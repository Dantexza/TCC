package com.example.bgabr.tcc;


import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.widget.TextView;
import android.widget.Toast;



import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SessionManagement session;
    //NFC
    NfcAdapter mNfcAdapter;
    private TextView messageText;
    byte statusByte;
    private String payload = "";
    //Dados da sessão
    HashMap<String, String> user;

    private static String TAG = MainActivity.class.getSimpleName();


    private ProgressDialog pDialog;
    TextView nome,ocupation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configuração do Toast personalizado
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,null);
        TextView text = (TextView) layout.findViewById(R.id.textToast);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        //Texto do perfil
        nome = (TextView) findViewById(R.id.textViewUser);
        ocupation = (TextView) findViewById(R.id.textViewOcupation);

        //Configuração da barra superior e barra de navegação
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final FloatingActionButton reload = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        //caixa de dialogo
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Carregando...");
        pDialog.setCancelable(false);


        //Drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.setNavigationItemSelectedListener(this);

        //Recuperando sessão
                session = new SessionManagement(getApplicationContext());
        //Configuração textView
        user = session.getUserDetails();
        String lblnome = user.get(SessionManagement.KEY_LOGIN);
        final String JsonArray = "http://4acess.online/infoUsuario.php?login="+lblnome;
        makeJsonArrayRequest(JsonArray);

        //Verificação se o celular possui NFC e se esta ativado
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "Seu dispositivo não possui NFC", Toast.LENGTH_LONG).show();
            finish();
        } else if (!mNfcAdapter.isEnabled()) {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Ative NFC e Android Beam nas configurações antes de iniciar  o asplicativo", Toast.LENGTH_LONG).show();
        }
        //Botão recarregar configuração do NFC
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeJsonArrayRequest(JsonArray);
                YoYo.with(Techniques.Landing).duration(500).playOn(reload);
                //YoYo.with(Techniques.ZoomIn).duration(200).playOn(findViewById(R.id.floatingActionButton));
                showToast("Aproxime o celular do terminal");
            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav = navigationView.getMenu();
        nav.findItem(R.id.nav_home).setChecked(true);
        user = session.getUserDetails();
        TextView name = (TextView) findViewById(R.id.sidebarName);
        TextView ocup = (TextView) findViewById(R.id.sidebarocup);
        name.setText(user.get(SessionManagement.KEY_NOME));
        ocup.setText(user.get(SessionManagement.KEY_OCUPATION));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            session.logoutUser();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        /// Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_acess) {
            Intent acesso = new Intent(getApplicationContext(), AcessoActivity.class);
            startActivity(acesso);
            finish();


        } else if (id == R.id.nav_request) {
            Intent req = new Intent(getApplicationContext(), RequestActivity.class);
            startActivity(req);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());

        }
        if ((!mNfcAdapter.isEnabled())) {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Ative NFC e Android Beam nas configurações antes de iniciar  o asplicativo", Toast.LENGTH_LONG).show();
        }
    }


    void processIntent(Intent intent) {


        NdefMessage[] messages = getNdefMessages(getIntent());
        for (int i = 0; i < messages.length; i++) {
            for (int j = 0; j < messages[0].getRecords().length; j++) {
                NdefRecord record = messages[i].getRecords()[j];
                statusByte = record.getPayload()[0];
                int languageCodeLength = statusByte & 0x3F; //mask value in order to find language code length
                int isUTF8 = statusByte - languageCodeLength;
                if (isUTF8 == 0x00) {
                    payload = new String(record.getPayload(), 1 + languageCodeLength, record.getPayload().length - 1 - languageCodeLength, Charset.forName("UTF-8"));
                } else if (isUTF8 == -0x80) {
                    payload = new String(record.getPayload(), 1 + languageCodeLength, record.getPayload().length - 1 - languageCodeLength, Charset.forName("UTF-16"));
                }
                messageText.setText("Text received: " + payload);
            }
        }
    }

    NdefMessage create_RTD_TEXT_NdefMessage(String inputText) {

        Locale locale = new Locale("en", "US");
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        boolean encodeInUtf8 = false;
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        byte status = (byte) (utfBit + langBytes.length);

        byte[] textBytes = inputText.getBytes(utfEncoding);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        NdefMessage message = new NdefMessage(new NdefRecord[]{textRecord});
        return message;

    }

    //Método nfc
    NdefMessage[] getNdefMessages(Intent intent) {

        NdefMessage[] msgs = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
            }
        } else {
            Log.d("Peer to Peer 2", "Unknown intent.");
            finish();
        }

        return msgs;
    }

    public void showToast(String msg){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,null);
        TextView text = (TextView) layout.findViewById(R.id.textToast);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 625);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        text.setText(msg);
        toast.show();

    }

    // Configuração do NFC e JSON
   private void makeJsonArrayRequest(String Array) {

        showpDialog();

        JsonArrayRequest req = new JsonArrayRequest(Array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject perfil = (JSONObject) response
                                        .get(0);

                                String name = perfil.getString("nome_completo");
                                String ocup = perfil.getString("nome");

                                nome.setText(name);
                                ocupation.setText(ocup);
                                session.userdata(name,ocup);
                            TextView nameTxt = (TextView) findViewById(R.id.sidebarName);
                            TextView ocuptxT = (TextView) findViewById(R.id.sidebarocup);
                            nameTxt.setText(name);
                            ocuptxT.setText(ocup);

                            if (mNfcAdapter.isEnabled()) {
                                //Mensagem a ser mandada por nfc
                                String lblnome = user.get(SessionManagement.KEY_LOGIN);
                                NdefMessage message = create_RTD_TEXT_NdefMessage(lblnome);
                                mNfcAdapter.setNdefPushMessage(message, MainActivity.this);
                                showToast("Aproxime o celular do terminal");
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(req);
    }

    //Método da caixa de dialogo
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

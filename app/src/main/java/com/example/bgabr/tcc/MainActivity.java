package com.example.bgabr.tcc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;
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
    private String payload="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assets
        TextView nome = (TextView) findViewById(R.id.textViewUser);
        TextView ocupation = (TextView) findViewById(R.id.textViewOcupation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        FloatingActionButton reload = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        //Recuperando sessão
        session = new SessionManagement(getApplicationContext());
        //Drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView.setNavigationItemSelectedListener(this);

        //Configuração textView
        HashMap<String, String> user = session.getUserDetails();
        String lblnome = user.get(SessionManagement.KEY_LOGIN);
        //String lblocupation = user.get(SessionManagement.);
        nome.setText(lblnome);
        //ocupation.setText(ocupation);

        //Configuração NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this,"Seu dispositivo não possui NFC",Toast.LENGTH_LONG).show();
            finish();
        }else if(!mNfcAdapter.isEnabled()){
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
            Toast.makeText(this,"Ative NFC e Android Beam nas configurações antes de iniciar o aplicativo ao aplicativo",Toast.LENGTH_LONG).show();
        }
        String nfcstring;
        NdefMessage message=create_RTD_TEXT_NdefMessage("Hello world");
        mNfcAdapter.setNdefPushMessage(message, this);
        Toast.makeText(this, "Aproxime o celular do terminal", Toast.LENGTH_SHORT).show();


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
        HashMap<String, String> user = session.getUserDetails();
        TextView name = (TextView) findViewById(R.id.sidebarName);
        String lblname=user.get(SessionManagement.KEY_LOGIN);
        name.setText(lblname);
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

        if (id == R.id.nav_camera) {
             // Handle the camera action

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

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
    }

    void processIntent(Intent intent) {


        NdefMessage[] messages = getNdefMessages(getIntent());
        for(int i=0;i<messages.length;i++){
            for(int j=0;j<messages[0].getRecords().length;j++){
                NdefRecord record = messages[i].getRecords()[j];
                statusByte=record.getPayload()[0];
                int languageCodeLength= statusByte & 0x3F; //mask value in order to find language code length
                int isUTF8=statusByte-languageCodeLength;
                if(isUTF8==0x00){
                    payload=new String(record.getPayload(),1+languageCodeLength,record.getPayload().length-1-languageCodeLength, Charset.forName("UTF-8"));
                }
                else if (isUTF8==-0x80){
                    payload=new String(record.getPayload(),1+languageCodeLength,record.getPayload().length-1-languageCodeLength,Charset.forName("UTF-16"));
                }
                messageText.setText("Text received: "+ payload);
            }
        }
    }
    NdefMessage create_RTD_TEXT_NdefMessage(String inputText){

        Locale locale= new Locale("en","US");
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        boolean encodeInUtf8=false;
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
        NdefMessage message= new NdefMessage(new NdefRecord[] { textRecord});
        return message;

    }
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
}

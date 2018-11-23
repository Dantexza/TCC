package com.example.bgabr.tcc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class RequestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemSelectedListener {
    SessionManagement session;
    Integer[] items = new Integer[]{1,2,3,4};

    HashMap<String, String> user;
    private int mGalleryCount=0;
    private boolean initializedView = false;
    //this counts how many Gallery's have been initialized
    private int mGalleryInitializedCount=0;

    private static String TAG = RequestActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //final Button makeRequest = (Button) findViewById(R.id.button);


        session = new SessionManagement(getApplicationContext());

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.setNavigationItemSelectedListener(this);

        user = session.getUserDetails();
        String lblnome = user.get(SessionManagement.KEY_LOGIN);
        final String JsonArray = "http://4acess.online/solicitacoesAcessos.php?login="+lblnome;
        makeJsonArrayRequest(JsonArray);

        //caixa de dialogo
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Carregando...");
        pDialog.setCancelable(false);


       //final Spinner porta = (Spinner) findViewById(R.id.spinnerPorta);
       // porta.setSelected(false);  // must
        //porta.setSelection(0,true);  //mu

      //  porta.setOnItemSelectedListener(this);








    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav = navigationView.getMenu();
        nav.findItem(R.id.nav_request).setChecked(true);
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

        if (id == R.id.nav_home) {
            Intent home = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(home);
            finish();


        } else if (id == R.id.nav_acess) {
            Intent acesso = new Intent(getApplicationContext(), AcessoActivity.class);
            startActivity(acesso);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void setSpinner (List<Object> arrayPredio,List<Object> arrayAndar,List<Object> arrayPorta){
        Spinner porta = (Spinner) findViewById(R.id.spinnerPorta);
        Spinner andar = (Spinner) findViewById(R.id.spinnerandar);
        Spinner predio = (Spinner) findViewById(R.id.spinnerpredio);
        ArrayAdapter<Object> adapterPredio = new ArrayAdapter<Object>(this,R.layout.snipper,arrayPredio);
        adapterPredio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<Object> adapterAndar = new ArrayAdapter<Object>(this,R.layout.snipper,arrayAndar);
        ArrayAdapter<Object> adapterPorta = new ArrayAdapter<Object>(this,R.layout.snipper,arrayPorta);
        porta.setAdapter(adapterPredio);
        andar.setAdapter(adapterAndar);
        predio.setAdapter(adapterPorta);}


        public void addRows(String data,String nPorta, String estado){
            TableLayout treq =(TableLayout) findViewById(R.id.tablereq);

            TableRow.LayoutParams size = new TableRow.LayoutParams(1,55);

            TableRow tr_head = new TableRow(this);
            tr_head.setId(View.generateViewId());
            tr_head.setPadding(0,0,0,20);
            tr_head.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            //Variavel predio
            TextView predio = new TextView(this);
            predio.setId(View.generateViewId());
            predio.setText(data);
            predio.setWidth(80);
            predio.setHeight(50);
            predio.setTextSize(20);
            //predio.setPadding(0,0,0,2);
            predio.setGravity(Gravity.CENTER);
            tr_head.addView(predio);

            ImageView img1 = new ImageView(this);
            img1.setImageResource(R.drawable.ic_column);
            img1.setLayoutParams(size);
            img1.setPadding(30,0,0,0);
            tr_head.addView(img1);
            //Variavel acesso
            TextView andar = new TextView(this);
            andar.setId(View.generateViewId());
            andar.setText(nPorta);
            andar.setTextSize(20);
            andar.setGravity(Gravity.CENTER);
            tr_head.addView(andar);

            ImageView img2 = new ImageView(this);
            img2.setImageResource(R.drawable.ic_column);
            img2.setLayoutParams(size);
            img2.setPadding(30,0,0,0);
            tr_head.addView(img2);
            //Variavel porta
            TextView porta = new TextView(this);
            porta.setId(View.generateViewId());
            if(estado.equals("0")){
                porta.setText("Em análise");
            }else if(estado.equals("1")){
                porta.setText("Aprovado");
            }else{
                porta.setText("Recusado");
            }

            porta.setWidth(60);
            porta.setHeight(50);
            porta.setTextSize(20);
            porta.setGravity(Gravity.CENTER);
            tr_head.addView(porta);


            treq.addView(tr_head);

    }






      // Configuração do NFC e JSON
    private void makeJsonArrayRequest(String Array) {

        JsonArrayRequest req = new JsonArrayRequest(Array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());



                        try {


                            for (int i = 0;i<response.length();i++) {
                                JSONObject valor = (JSONObject) response
                                        .get(i);
                                String data = valor.getString("data_solicitacao");
                                String porta = valor.getString("porta");
                                String status = valor.getString("status");


                               addRows(data,porta,status);




                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        AppController.getInstance().addToRequestQueue(req);
    }

    private static Integer[] arrRemove(Integer[] strArray) {
        Set<Integer> set = new HashSet<Integer>();
        set.addAll((List<Integer>) Arrays.asList(strArray));
        return (Integer[]) set.toArray(new Integer[set.size()]);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        if (initializedView ==  false)
        {Log.i(TAG, "selected item position = " + String.valueOf(position) );
            initializedView = true;

        }
        else
        {
            //only detect selection events that are not done whilst initializing
            Log.i(TAG, "selected item position = " + String.valueOf(position) );
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}




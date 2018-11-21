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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
        final Button makeRequest = (Button) findViewById(R.id.button);


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
        final String JsonArray = "http://4acess.online/acessoUsuarioPortas.php?login="+lblnome;
        makeJsonArrayRequest(JsonArray);

        //caixa de dialogo
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Carregando...");
        pDialog.setCancelable(false);


        final Spinner porta = (Spinner) findViewById(R.id.spinnerPorta);
       // porta.setSelected(false);  // must
        //porta.setSelection(0,true);  //mu

        porta.setOnItemSelectedListener(this);





       /* final boolean isSpinnerTouched = false;
        porta.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        porta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (!isSpinnerTouched) {


                    Toast.makeText(getApplicationContext(),porta.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
                        return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        makeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Landing).duration(500).playOn(makeRequest);
            }
        });

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
        String lblname = user.get(SessionManagement.KEY_NOME);
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
        predio.setAdapter(adapterPorta);




    }  // Configuração do NFC e JSON
    private void makeJsonArrayRequest(String Array) {

        JsonArrayRequest req = new JsonArrayRequest(Array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());



                        try {
                            List<Long> a = new  ArrayList<>();
                            List<Long> b = new ArrayList<>();
                            List<Long> c= new  ArrayList<>();

                            for (int i = 0;i<response.length();i++) {
                                JSONObject valor = (JSONObject) response
                                        .get(i);

                                a.add(valor.getLong("predio"));
                                b.add(valor.getLong("andar"));
                                c.add(valor.getLong("id"));

                            }

                            List<Object> predio = a.stream().distinct().collect(Collectors.toList());
                            List<Object> andar = b.stream().distinct().collect(Collectors.toList());
                            List<Object> id = c.stream().distinct().collect(Collectors.toList());

                            setSpinner(predio, andar, id);

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




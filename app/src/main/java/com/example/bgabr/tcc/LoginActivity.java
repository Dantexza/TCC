package com.example.bgabr.tcc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    //Sessão de login
    SessionManagement session;
    private ProgressDialog pDialog;
    private String jsonResponse;
    private EditText textLogin,textSenha;

    private static String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, MainActivity.class);
        setContentView(R.layout.activity_login);
        session = new SessionManagement(getApplicationContext());

         textLogin = (EditText) findViewById(R.id.login);
         textSenha = (EditText) findViewById(R.id.pass);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        //Começa main caso usuátio já estiver logado
        if (session.isLoggedIn()) {
            startActivity(intent);
        }


        final Button btnlogin = (Button) findViewById(R.id.btnLogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String login = textLogin.getText().toString();
                String senha = textSenha.getText().toString();

                if (login.trim().length() > 0 && senha.trim().length() > 0) {
                    String arrayLogin = "http://4acess.online/selectUser.php?login=" + login + "&senha=" + senha;
                    makeJsonArrayRequest(arrayLogin);



                } else {
                    Toast.makeText(getApplicationContext(), "Preencha os campos de Login e Senha", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private boolean makeJsonArrayRequest(String Array) {

        showpDialog();
        JsonArrayRequest req = new JsonArrayRequest(Array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object
                            jsonResponse = "";
                            JSONObject table = (JSONObject) response.get(0);
                            jsonResponse = table.getString("acesso");
                            if (jsonResponse.equals("1")) {

                                    // Creating user login session
                                    // For testing i am stroing name, email as follow
                                    // Use user real data
                                String login = textLogin.getText().toString();
                                String senha = textSenha.getText().toString();
                                session.createLoginSession(login, senha);

                                    // Staring MainActivity
                                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(main);
                                    finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Login e senha inválidos",
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





        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        return true;
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

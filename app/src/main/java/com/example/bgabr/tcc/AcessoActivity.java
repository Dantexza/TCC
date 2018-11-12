package com.example.bgabr.tcc;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

public class AcessoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SessionManagement session;


    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acesso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        session = new SessionManagement(getApplicationContext());

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        for (int i = 0 ;i<15;i++){
            addRows();
        }


        Button request = (Button) findViewById(R.id.buttonRequest);

        navigationView.setNavigationItemSelectedListener(this);





        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent request = new Intent(getApplicationContext(),RequestActivity.class);
                startActivity(request);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent main = new Intent(getApplicationContext(),MainActivity.class);
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
        nav.findItem(R.id.nav_acess).setChecked(true);
        user = session.getUserDetails();
        TextView name = (TextView) findViewById(R.id.sidebarName);
        String lblname=user.get(SessionManagement.KEY_NOME);
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
            Intent home = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(home);
            finish();


        } else if (id == R.id.nav_request) {
            Intent req = new Intent(getApplicationContext(),RequestActivity.class);
            startActivity(req);
            finish();


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void addRows(){
        TableLayout t2 =(TableLayout) findViewById(R.id.table2);

        TableRow.LayoutParams size = new TableRow.LayoutParams(1,55);

        TableRow tr_head = new TableRow(this);
        tr_head.setId(View.generateViewId());
        tr_head.setPadding(0,0,0,20);
        tr_head.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView predio = new TextView(this);
        predio.setId(View.generateViewId());
        predio.setText("1");
        predio.setTextSize(20);
        //predio.setPadding(0,0,0,2);
        predio.setGravity(Gravity.CENTER);
        tr_head.addView(predio);

        ImageView img1 = new ImageView(this);
        img1.setImageResource(R.drawable.ic_column);
        img1.setLayoutParams(size);
        img1.setPadding(30,0,0,0);
        tr_head.addView(img1);

        TextView andar = new TextView(this);
        andar.setId(View.generateViewId());
        andar.setText("1");
        andar.setTextSize(20);
        andar.setGravity(Gravity.CENTER);
        tr_head.addView(andar);

        ImageView img2 = new ImageView(this);
        img2.setImageResource(R.drawable.ic_column);
        img2.setLayoutParams(size);
        img2.setPadding(30,0,0,0);
        tr_head.addView(img2);

        TextView porta = new TextView(this);
        porta.setId(View.generateViewId());
        porta.setText("21");
        porta.setTextSize(20);
        porta.setGravity(Gravity.CENTER);
        tr_head.addView(porta);

        ImageView img3 = new ImageView(this);
        img3.setImageResource(R.drawable.ic_column);
        img3.setLayoutParams(size);
        tr_head.addView(img3);

        TableRow.LayoutParams acessoSize = new TableRow.LayoutParams(40,55);
        ImageView img4 = new ImageView(this);
        img4.setImageResource(R.drawable.ic_green_door);
        img4.setLayoutParams(acessoSize);
        tr_head.addView(img4);
        t2.addView(tr_head);

    }
}

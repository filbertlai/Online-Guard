package com.filbert.online_guard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // GUI element
    private BottomNavigationView bottomNavigationView;

    // Fragments
    private browser_fragment bf;
    private news_fragment nf;
    private info_fragment inf;
    private quiz_fragment qf;
    private blocklist_fragment blf;
    private help_fragment hf;
    private setting_fragment sf;

    // fragment that user is currently located in
    // 1: browser   2: news     3: info     4: quiz     5. blocklist    6. setting  7. help
    private Integer Current_Fragment = 1;

    // URL to load when the app is started
    private String url_to_go = "https://www.google.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            // User starts the app by clicking an URL
            Intent intent = getIntent();
            Uri data = intent.getData();
            url_to_go = data.toString();
            Toast.makeText(this, "Loading: " + url_to_go, Toast.LENGTH_LONG).show();
        }catch(Exception e){
            // User launches the app directly in the os
        }

        // Initialize fragments
        bf = new browser_fragment(url_to_go);
        nf = new news_fragment();
        inf = new info_fragment();
        qf = new quiz_fragment();
        blf = new blocklist_fragment();
        hf = new help_fragment();
        sf = new setting_fragment();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Starting page of the app is the browser page
        bottomNavigationView.setSelectedItemId(R.id.browser);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        // Function for switching to different pages

        switch (item.getItemId()){
            case R.id.browser:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,bf).commit();
                Current_Fragment = 1;
                return true;
            case R.id.news:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,nf).commit();
                Current_Fragment = 2;
                return true;
            case R.id.info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,inf).commit();
                Current_Fragment = 3;
                return true;
            case R.id.quiz:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,qf).commit();
                Current_Fragment = 4;
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Function to inflate the action bar when the user opens the menu for the first time
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        // Function to control the operations in action bar

        switch (item.getItemId()){
            case R.id.blocklist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,blf).commit();
                Current_Fragment = 5;
                return true;
            case R.id.setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,sf).commit();
                Current_Fragment = 6;
                return true;
            case R.id.help:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,hf).commit();
                Current_Fragment = 7;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Function for handling the back button

        // If user is currently in browser page
        if (Current_Fragment==1) {
            if (bf.onMyKeyDown(keyCode)){
                // No need bubble up to the default system behavior
                return true;
            }
        }

        if (Current_Fragment==2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl,nf).commit();
            return true;
        }

        // If user is currently in blocklist/help/info page -> back to browser page (App home page)
        if (Current_Fragment>=5){
            getSupportFragmentManager().beginTransaction().replace(R.id.fl,bf).commit();
            Current_Fragment = 1;
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
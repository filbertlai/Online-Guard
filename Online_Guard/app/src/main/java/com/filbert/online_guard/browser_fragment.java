package com.filbert.online_guard;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class browser_fragment extends Fragment {

    // GUI elements
    private EditText address_bar;
    private Button go_button;
    private Button block_button;
    private WebView myWebView;
    private RelativeLayout loading_panel;
    private CoordinatorLayout coordinator_layout;

    // URL to load on the webview
    private String webview_url;

    // An instance of another class <check_phishing>
    private check_phishing cp;

    private boolean force_to_load;

    // To be invoked after getting volley response
    public interface VolleyCallback{
        void onSuccess(String result);
    }

    // Public constructor
    public browser_fragment(String url) {
        webview_url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.browser_layout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This function will be invoked after onCreateView

        cp = new check_phishing(getActivity());

        force_to_load = false;

        // Initialize address bar
        address_bar = view.findViewById(R.id.address_bar);
        address_bar.setText(webview_url);

        // Initialize buttons
        go_button = view.findViewById(R.id.go_button);
        block_button = view.findViewById(R.id.block_button);

        // Initialize WebView
        myWebView = view.findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();

        loading_panel = view.findViewById(R.id.loadingPanel);
        coordinator_layout = view.findViewById(R.id.coordinator_layout);

        // Enable Google Safe Browsing Service in WebView
        webSettings.setSafeBrowsingEnabled(true);

        // If JavaScript is disabled, a lot of websites cannot work normally
        webSettings.setJavaScriptEnabled(true);

        // Load images automatically in WebView
        webSettings.setLoadsImagesAutomatically(true);

        // Load either the URL passed in the intent or google.com
        if (is_network_connected()){
            myWebView.loadUrl(webview_url);
        }

        // Set onclick listener for go button since public methods in fragment cannot be linked with the element in the xml
        go_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                address_bar_done();
            }
        });

        // Set onclick listener for block button
        block_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                add_current_url_to_blocklist(myWebView.getUrl());
            }
        });

        // Set listener for address bar to handle 'Search' key input
        address_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                boolean handled = false;

                // Pressing the search button on keyboard
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    address_bar_done();
                    handled = true;
                }

                return handled;
            }

        });

    }

    private class MyWebViewClient extends WebViewClient {
        // This class intended to handle the WebView, such as blocking URL
        // This class should not be separate in other file in order to access the WebView

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            loading_panel.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // function invoked before loading any URL

            if (force_to_load){
                force_to_load = false;
                return false;
            }

            // Check in self-defined blocklist
            if (get_setting_from_device("blocklist_usage") && is_url_in_blocklist(url)) {
                read_setting_and_vibrate();
                if (get_setting_from_device("blocklist_ask")){
                    show_warning_message("URL/domain name exists in the blocklist", url);
                }
                else{
                    Toast.makeText(getActivity(), "URL/domain name exists in the blocklist", Toast.LENGTH_LONG).show();
                }
                return true;
            }


            // Only check for phishing when the user is not searching on Google but entered an URL
            if (get_setting_from_device("api_usage") && !url.contains("https://www.google.com/search?q=")){
                // Check for phishing by the api in apivoid.com
                cp.is_url_safe_by_apivoid(url, new VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        if (result.length()>0){
                            back_if_possible();
                            read_setting_and_vibrate();
                            if (get_setting_from_device("api_ask")){
                                show_warning_message("Blocked by urlvoid API\n\n" + result, url);
                            }
                            else{
                                Toast.makeText(getActivity(), "Blocked by urlvoid API", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });


                // Check for phishing by the api in ipqualityscore.com
                cp.is_url_safe_by_ipqs(url, new VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        if (result.length()>0){
                            back_if_possible();
                            read_setting_and_vibrate();
                            if (get_setting_from_device("api_ask")){
                                show_warning_message("Blocked by IPQS API\n\n" + result, url);
                            }
                            else{
                                Toast.makeText(getActivity(), "Blocked by IPQS API", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

            }


            // Only check for phishing when the user is not searching on Google but entered an URL
            if (get_setting_from_device("ai_usage") && !url.contains("https://www.google.com/search?q=")){
                // Check for phishing by AI
                cp.is_url_safe_by_ai(url, new VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        // Toast.makeText(getActivity(), "OnSuccess called", Toast.LENGTH_SHORT).show();
                        if (result.length()>0){
                            back_if_possible();
                            read_setting_and_vibrate();
                            if (get_setting_from_device("ai_ask")){
                                show_warning_message("Detected as suspicious by AI", url);
                            }
                            else{
                                Toast.makeText(getActivity(), "Detected as suspicious by AI", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }

            // Update the address bar and save the url for the next fragment loading
            url_consistent(url);

            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Function for handling error in loading URL

            Toast.makeText(getActivity(), "Invalid URL: " + failingUrl, Toast.LENGTH_LONG).show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    public boolean onMyKeyDown(int keyCode) {
        // Function for handling back button

        // Check if the key event was the back button and if there is history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            if (is_network_connected()){
                myWebView.goBack();
                url_consistent(myWebView.getUrl());
            }

            // No need bubble up to the default system behavior
            return true;
        }
        // Bubble up to the default system behavior
        return false;
    }

    private void address_bar_done(){
        // Function to be invoked after user have inputted thing to search or URL in the address bar

        String url = String.valueOf(address_bar.getText());

        // Identify the thing entered in the address bar is a thing to search or an url
        if (! url.contains(".")){
            // Search in google since it is not an url
            url = "https://www.google.com/search?q=" + url;
            url_consistent(url);
        }

        // URL correction for lacking http(s)
        // No need adding for '.com' since not all URLs have '.com'
        else{
            if (! (url.contains("https://") || url.contains("http://")) ){
                url = "https://" + url;
            }
        }

        if (is_network_connected()){
            if (get_setting_from_device("blocklist_usage") && is_url_in_blocklist(url)){
                if (get_setting_from_device("blocklist_ask")){
                    show_warning_message("URL/domain name exists in the blocklist", url);
                }
                else{
                    Toast.makeText(getActivity(), "URL/domain name exists in the blocklist", Toast.LENGTH_LONG).show();
                }
            }
            else{
                myWebView.loadUrl(url);
            }
        }

        // Close keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private boolean is_network_connected(){
        // Function for checking the network state
        // Return true if network is connected, vice versa

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            Toast.makeText(getActivity(), "No internet connection!",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void url_consistent(String url){
        // Update address bar
        address_bar.setText(url);

        // Save the url for loading next time
        webview_url = url;
    }

    private boolean is_url_in_blocklist(String url){
        SharedPreferences blocklist_sp = getActivity().getSharedPreferences("blocklist", MODE_PRIVATE);
        String combined_url = String.valueOf(blocklist_sp.getString("url", "")); // default value is ""

        if (combined_url.length()==0){
            return false;
        }
        else{
            String[] url_array = combined_url.split("\\s+");
            for(int i = 0; i<url_array.length; i++)
            {
                // it is an URL
                if (url_array[i].contains("http") && url_array[i].contains("://")){
                    if (url.contains(url_array[i])){
                        return true;
                    }
                }

                // it is a domain name
                else{
                    // Not all URL have 'www.'
                    String text_to_check = "." + url_array[i] + ".";
                    if (url.contains(text_to_check)){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void show_warning_message(String reason, String url){
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setTitle("Website BLOCKED!");
        adb.setMessage("Reason: " + reason);

        adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                url_consistent(myWebView.getUrl());
            }});

        adb.setPositiveButton("Still Go", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                force_to_load=true;
                myWebView.loadUrl(url);
                show_report_button();
            }});

        adb.show();
    }

    private void read_setting_and_vibrate(){
        if (get_setting_from_device("vibrate")){
            Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(500);
        }
    }

    private void add_current_url_to_blocklist(String url){
        // This function adds the url to the blocklist in local storage

        SharedPreferences blocklist_sp = getActivity().getSharedPreferences("blocklist", MODE_PRIVATE);
        String combined_url = String.valueOf(blocklist_sp.getString("url", "")); // default value is ""
        final String amended_combined_url = combined_url + url + " ";
        ArrayList<String> url_arraylist = new ArrayList<String>();
        if (combined_url.length()>0){
            // Split by space
            String[] url_array = combined_url.split("\\s+");
            for(String u:url_array){
                url_arraylist.add(u);
            }
        }

        if (url.contains("google")){
            Toast.makeText(getActivity(), "Cannot add home URL to blocklist!", Toast.LENGTH_LONG).show();
        }
        else if (url_arraylist.contains(url)){
            Toast.makeText(getActivity(), "URL/keyword already exists in blocklist!", Toast.LENGTH_SHORT).show();
        }
        else{
            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            adb.setTitle("Add to blocklist?");
            adb.setMessage("Add URL to the blocklist:\n" + url);

            adb.setNegativeButton("Cancel", null);

            adb.setPositiveButton("Add", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    show_report_button();
                    blocklist_sp.edit().putString("url", amended_combined_url).commit();
                    Toast.makeText(getActivity(), url + " has been added to blocklist!", Toast.LENGTH_SHORT).show();
                    back_if_possible();
                }});

            adb.show();
        }
    }

    private boolean get_setting_from_device(String key){
        // This function gets the setting from local storage

        SharedPreferences setting_sp = getActivity().getSharedPreferences("setting", MODE_PRIVATE);
        Boolean result = setting_sp.getBoolean(key, true); // default value is true
        return result;
    }

    private void back_if_possible(){
        if (is_network_connected()){
            if (myWebView.canGoBack()){
                myWebView.goBack();
            }
            else{
                myWebView.loadUrl("https://www.google.com");
            }
            url_consistent(myWebView.getUrl());
        }
    }

    private void report_to_ai_model(String url, Boolean is_phishing){
        cp.report_to_ai_model(url, is_phishing, new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                if (result.contains("success")){
                    Toast.makeText(getActivity(), "Thanks for reporting!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void show_adb_for_reporting_current_url(){
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setTitle("Report URL:");
        adb.setMessage(webview_url);

        adb.setNegativeButton("Safe", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                report_to_ai_model(webview_url, false);
            }});

        adb.setPositiveButton("Phishing/Malicious", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                report_to_ai_model(webview_url, true);
            }});

        adb.show();
    }

    private void show_report_button(){
        // This function shows the snack bar for the users to report an URL as phishing/malicious or safe

        Snackbar snackbar = Snackbar
                .make(
                        coordinator_layout,
                        "Help us improve the AI model",
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(
                        "Report",

                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                show_adb_for_reporting_current_url();
                            }
                        });

        snackbar.show();
    }

}
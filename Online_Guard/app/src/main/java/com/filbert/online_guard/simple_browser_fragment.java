package com.filbert.online_guard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class simple_browser_fragment extends Fragment {
    /*
    This is a simplified version of browser_fragment

    No address bar, buttons and blocking functions.

    Used for browsing the news in news page.
     */

    // GUI elements
    private WebView myWebView;
    private RelativeLayout loading_panel;

    // URL to load on the webview
    private String webview_url;

    public simple_browser_fragment(String url) {
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
        return inflater.inflate(R.layout.simple_browser_layout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // Initialize WebView
        myWebView = view.findViewById(R.id.simple_webview);

        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                loading_panel.setVisibility(View.GONE);
                myWebView.setVisibility(View.VISIBLE);
            }
        });
        WebSettings webSettings = myWebView.getSettings();

        // Enable Google Safe Browsing Service in WebView
        webSettings.setSafeBrowsingEnabled(true);

        // If JavaScript is disabled, a lot of websites cannot work normally
        webSettings.setJavaScriptEnabled(true);

        // Load images automatically in WebView
        webSettings.setLoadsImagesAutomatically(true);

        loading_panel = view.findViewById(R.id.loadingPanel);

        // Load either the URL passed in the intent or google.com
        if (is_network_connected()){
            myWebView.loadUrl(webview_url);
        }

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
}
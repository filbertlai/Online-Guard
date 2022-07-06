package com.filbert.online_guard;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class check_phishing{
    // This class is used for checking website by AI and two APIs in urlvoid.com and ipqualityscore.com

    // activity of the calling class
    private Activity activity;

    // public constructor
    public check_phishing(Activity act){
        activity = act;
    }

    private boolean is_network_connected(){
        // Function for checking the network state
        // Return true if network is connected, vice versa

        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            Toast.makeText(activity, "No internet connection!",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private String url_standardize(String url){
        // Standardize the URL so that it can be searched in the two APIs

        url = url.replace(":","%3A");
        url = url.replace("/","%2F");

        return url;
    }

    public void is_url_safe_by_apivoid(String url, final browser_fragment.VolleyCallback callback) {
        // Check if the url is safe by the api in apivoid.com

        url = url_standardize(url);
        url = "https://endpoint.apivoid.com/urlrep/v1/pay-as-you-go/?key=14f5d389a2f9bfc0424db0578caff62c50813926&url=" + url;

        if (is_network_connected()){
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);

                        JSONObject data = object.getJSONObject("data");
                        JSONObject report = data.getJSONObject("report");
                        JSONObject domain_blacklist = report.getJSONObject("domain_blacklist");
                        Integer detections = domain_blacklist.getInt("detections");
                        JSONObject risk_score = report.getJSONObject("risk_score");
                        Integer result = risk_score.getInt("result");

                        if (detections==0 && result==0){
                            callback.onSuccess("");
                        }
                        else{
                            String text_return = "Risk score by urlvoid.com: " + result.toString();

                            text_return += "\n\nDetected as phishing by " + detections.toString() + " engine(s)";

                            JSONObject web_page = report.getJSONObject("web_page");
                            String title = web_page.getString("title");
                            text_return += "\n\nWebsite title:\n" + title;

                            callback.onSuccess(text_return);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "Error: "+error, Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue rq = Volley.newRequestQueue(activity);
            rq.add(request);
        }
    }

    public void is_url_safe_by_ipqs(String url, final browser_fragment.VolleyCallback callback) {
        // Check if the url is safe by the api in ipqualityscore.com

        url = url_standardize(url);
        url = "https://ipqualityscore.com/api/json/url/193PzlAKmjfNyRnNHyFi45AKxv78JCvN/" + url;

        if (is_network_connected()){
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);

                        Boolean unsafe = object.getBoolean("unsafe");
                        Integer risk_score = object.getInt("risk_score");
                        Boolean spamming = object.getBoolean("spamming");
                        Boolean malware = object.getBoolean("malware");
                        Boolean phishing = object.getBoolean("phishing");
                        Boolean suspicious = object.getBoolean("suspicious");
                        String category = object.getString("category");

                        if (!unsafe && risk_score==0){
                            callback.onSuccess("");
                        }
                        else{
                            String text_return = "Risk score by ipqualityscore.com: " + risk_score.toString();

                            text_return += "\n\nReasons: ";
                            Boolean first_reason = true;

                            if (spamming){
                                text_return += "spamming";
                                first_reason = false;
                            }

                            if (malware){
                                if (first_reason){
                                    text_return += "malware";
                                }
                                else{
                                    text_return += ",malware";
                                }
                                first_reason = false;
                            }

                            if (phishing){
                                if (first_reason){
                                    text_return += "phishing";
                                }
                                else{
                                    text_return += ",phishing";
                                }
                                first_reason = false;
                            }

                            if (suspicious){
                                if (first_reason){
                                    text_return += "suspicious";
                                }
                                else{
                                    text_return += ",suspicious";
                                }
                            }

                            text_return += "\n\nWebsite category:\n" + category;
                            callback.onSuccess(text_return);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "Error: "+error, Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue rq = Volley.newRequestQueue(activity);
            rq.add(request);
        }
    }

    public void is_url_safe_by_ai(String url, final browser_fragment.VolleyCallback callback) {
        // Check if the url is safe by artificial intelligence

        url = url_standardize(url);
        url = "http://fyp21013s1.cs.hku.hk:8080/predict?url=" + url;

        if (is_network_connected()){
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);

                        String result = object.getString("result");
                        //Toast.makeText(activity, "AI result: "+result, Toast.LENGTH_SHORT).show();

                        if (result.equals("0")){
                            callback.onSuccess("");
                        }
                        else{
                            callback.onSuccess("Detected as suspicious by AI");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "Error"+e, Toast.LENGTH_SHORT).show();
                        callback.onSuccess("");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "Error: "+error, Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue rq = Volley.newRequestQueue(activity);
            rq.add(request);
        }
    }

    public void report_to_ai_model(String url, Boolean is_phishing, final browser_fragment.VolleyCallback callback) {
        // Report URL to the AI model

        url = url_standardize(url);
        url = "http://fyp21013s1.cs.hku.hk:8080/feedback?url=" + url;
        if (is_phishing){
            url += "&label=1";
        }
        else{
            url += "&label=0";
        }

        if (is_network_connected()){
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);

                        String result = object.getString("result");
                        // Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();

                        callback.onSuccess(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "Error"+e, Toast.LENGTH_SHORT).show();
                        callback.onSuccess("failed");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "Error: "+error, Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue rq = Volley.newRequestQueue(activity);
            rq.add(request);
        }
    }

}

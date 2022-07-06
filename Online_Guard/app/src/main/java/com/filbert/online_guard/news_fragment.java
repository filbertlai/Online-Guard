package com.filbert.online_guard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class news_fragment extends Fragment {
    final ArrayList<NewsView> news_array = new ArrayList<NewsView>();
    private ListView lv;

    public news_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.news_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // Update the news from server
        get_news();

        lv = view.findViewById(R.id.news_list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Click an item in the listview to load the corresponding url
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String url_to_go = news_array.get(position).get_url();
                load_url(url_to_go);
            }
        });

        update_listview();

    }

    private void update_listview(){
        NewsViewAdapter nva = new NewsViewAdapter(getContext(), news_array);
        lv.setAdapter(nva);
    }

    private void add_news(String title, String url, String source){
        NewsView nv = new NewsView(title, url, source);
        for (NewsView n:news_array){
            if (n.get_title()==title){
                return;
            }
        }
        news_array.add(nv);
    }

    private void load_url(String url){
        simple_browser_fragment sbf = new simple_browser_fragment(url);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl,sbf).commit();
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

    private void get_news(){
        String API = "http://fyp21013s1.cs.hku.hk:8080/get_news";

        if (is_network_connected()){
            StringRequest request = new StringRequest(API, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);

                        JSONArray news_arr = object.getJSONArray("news");

                        for (int i=0;i<news_arr.length();i++){
                            JSONObject news = news_arr.getJSONObject(i);
                            String title = news.getString("Title");
                            String url = news.getString("URL");
                            String source = news.getString("Source");
                            add_news(title, url, source);
                        }

                        NewsViewAdapter nva = new NewsViewAdapter(getContext(), news_array);
                        ListView lv = getView().findViewById(R.id.news_list);

                        lv.setAdapter(nva);

                        update_listview();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Error: "+error, Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue rq = Volley.newRequestQueue(getActivity());
            rq.add(request);
        }
    }

}
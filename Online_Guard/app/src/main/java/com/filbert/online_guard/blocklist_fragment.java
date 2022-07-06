package com.filbert.online_guard;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class blocklist_fragment extends Fragment {

    // GUI elements
    private TextView info_panel;
    private EditText add_url_edittext;
    private Button add_button;
    private ListView listView;

    // Used to store URLs
    private ArrayList<String> url_arraylist;
    // Array is also needed since only array can be fed to the ArrayAdapter
    private String[] url_array;
    private ArrayAdapter adapter;

    public blocklist_fragment() {
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
        return inflater.inflate(R.layout.blocklist_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // This function will be invoked after onCreateView

        // Initialize GUI elements
        info_panel = view.findViewById(R.id.info_panel);
        add_url_edittext = view.findViewById(R.id.add_url_edittext);
        add_button = view.findViewById(R.id.add_url_button);
        listView = (ListView) view.findViewById(R.id.url_list);

        // Get data from local storage and initialize arraylist and array
        url_arraylist = new ArrayList<String>();
        get_blocklist_from_device();
        info_panel.setText("Tap any item to remove it  |  Number of items in blocklist: " + url_arraylist.size());
        url_array = url_arraylist.toArray(new String[0]);

        // Loading animation disappear
        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // Initialize adapter and set it to listview
        adapter = new ArrayAdapter<String>(getContext(),
                R.layout.blocklist_listview, url_array);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Click an item in the listview to remove it from both listview and local storage
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                adb.setTitle("Delete item?");
                adb.setMessage("Remove URL/domain name from your blocklist:\n" + url_arraylist.get(position) );
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Remove", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        url_arraylist.remove(position);
                        update_listview();
                        update_blocklist_to_device();
                    }});
                adb.show();
            }
        });

        // Add URL to both listview and local storage
        add_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                add_url_to_blocklist();
            }
        });

        add_url_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                boolean handled = false;

                // Pressing the enter button on keyboard
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    add_url_to_blocklist();
                    handled = true;
                }

                return handled;
            }
        });

        add_url_edittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Originally the backspace key on keyboard will act as a back button if the Edittext is empty
                if(keyCode == KeyEvent.KEYCODE_DEL && String.valueOf(add_url_edittext.getText()).length()==0) {
                    // Handled
                    return true;
                }
                return false;
            }
        });

    }

    private void add_url_to_blocklist(){
        // This function adds the url in EditText to the listview and local storage

        String url = String.valueOf(add_url_edittext.getText());

        if (url.length()==0){
            Toast.makeText(getActivity(), "Empty Input!", Toast.LENGTH_SHORT).show();
        }
        else if (url.contains(" ")){
            Toast.makeText(getActivity(), "URL/domain name should not contain space!", Toast.LENGTH_LONG).show();
        }
        else if (url.contains("google")){
            Toast.makeText(getActivity(), "Google cannot be blocked!", Toast.LENGTH_LONG).show();
        }
        else if (url_arraylist.contains(url)){
            Toast.makeText(getActivity(), "URL/domain name already exists!", Toast.LENGTH_SHORT).show();
        }
        else{
            url_arraylist.add(url);
            update_listview();
            update_blocklist_to_device();
        }
    }

    private void update_listview(){
        // This function updates the arraylist to listview

        // Convert arraylist to array in order to feed to the array adapter
        url_array = url_arraylist.toArray(new String[0]);
        ArrayAdapter<String> new_adapter = new ArrayAdapter<String>(getContext(),
                R.layout.blocklist_listview, url_array);
        listView.setAdapter(new_adapter);

        info_panel.setText("Tap any URL to remove it  |  Number of URL in blocklist: " + url_arraylist.size());
    }

    private String arraylist_to_string(){
        // This function combines the arraylist into a single string in order to store into local storage

        String combined_url = "";
        for (int i = 0; i<url_arraylist.size(); i++){
            combined_url += url_arraylist.get(i) + " ";
        }
        return combined_url;
    }

    private void update_blocklist_to_device(){
        // This function updates the arraylist to local storage

        SharedPreferences blocklist_sp = getActivity().getSharedPreferences("blocklist", MODE_PRIVATE);
        String combined_url = arraylist_to_string();
        blocklist_sp.edit().putString("url", combined_url).commit();
        Toast.makeText(getActivity(), "Changes Saved!", Toast.LENGTH_SHORT).show();
    }

    private void get_blocklist_from_device(){
        // This function gets the blocklist from local storage

        SharedPreferences blocklist_sp = getActivity().getSharedPreferences("blocklist", MODE_PRIVATE);
        String combined_url = String.valueOf(blocklist_sp.getString("url", "")); // default value is ""

        if (combined_url.length()>0){
            // Split by space
            url_array = combined_url.split("\\s+");
            for(String url:url_array){
                url_arraylist.add(url);
            }
        }

    }

}
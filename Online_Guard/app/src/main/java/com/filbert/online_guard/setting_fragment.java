package com.filbert.online_guard;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class setting_fragment extends Fragment {

    // GUI elements
    private Switch switch0;
    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private Switch switch4;
    private Switch switch5;
    private Switch switch6;
    private Button default_app_button;

    public setting_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.setting_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        switch0 = view.findViewById(R.id.switch0);
        switch1 = view.findViewById(R.id.switch1);
        switch2 = view.findViewById(R.id.switch2);
        switch3 = view.findViewById(R.id.switch3);
        switch4 = view.findViewById(R.id.switch4);
        switch5 = view.findViewById(R.id.switch5);
        switch6 = view.findViewById(R.id.switch6);

        default_app_button = view.findViewById(R.id.default_app_button);

        switch0.setChecked(get_setting_from_device("vibrate"));
        switch1.setChecked(get_setting_from_device("blocklist_usage"));
        switch2.setChecked(get_setting_from_device("blocklist_ask"));
        switch3.setChecked(get_setting_from_device("api_usage"));
        switch4.setChecked(get_setting_from_device("api_ask"));
        switch5.setChecked(get_setting_from_device("ai_usage"));
        switch6.setChecked(get_setting_from_device("ai_ask"));

        switch0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                update_setting_to_device("vibrate", isChecked);
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                update_setting_to_device("blocklist_usage", isChecked);
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                update_setting_to_device("blocklist_ask", isChecked);
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                update_setting_to_device("api_usage", isChecked);
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                update_setting_to_device("api_ask", isChecked);
            }
        });

        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                update_setting_to_device("ai_usage", isChecked);
            }
        });

        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                update_setting_to_device("ai_ask", isChecked);
            }
        });

        default_app_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(android.provider.Settings.	ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
            }
        });

    }

    private boolean get_setting_from_device(String key){
        // This function gets the setting from local storage

        SharedPreferences setting_sp = getActivity().getSharedPreferences("setting", MODE_PRIVATE);
        Boolean result = setting_sp.getBoolean(key, true); // default value is true
        return result;
    }

    private void update_setting_to_device(String key, Boolean value){
        // This function updates the setting to local storage

        SharedPreferences setting_sp = getActivity().getSharedPreferences("setting", MODE_PRIVATE);
        setting_sp.edit().putBoolean(key, value).commit();
        Toast.makeText(getActivity(), "Changes Saved!", Toast.LENGTH_SHORT).show();
    }

}
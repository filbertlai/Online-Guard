package com.filbert.online_guard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class quiz_fragment extends Fragment {

    // GUI elements
    private RadioGroup radio_group1;
    private RadioGroup radio_group2;
    private RadioGroup radio_group3;
    private RadioGroup radio_group4;
    private RadioGroup radio_group5;
    private RadioGroup radio_group6;
    private RadioGroup radio_group7;
    private RadioGroup radio_group8;
    private RadioGroup radio_group9;
    private RadioGroup radio_group10;
    private Button submit_button;
    private TextView scoreboard;

    // For holding the 10 radio groups
    private ArrayList<RadioGroup> radio_groups;
    // For holding the id of the radio button which contains the correct answer
    private ArrayList<Integer> solutions;

    public quiz_fragment() {
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
        return inflater.inflate(R.layout.quiz_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        radio_group1 = view.findViewById(R.id.rg1);
        radio_group2 = view.findViewById(R.id.rg2);
        radio_group3 = view.findViewById(R.id.rg3);
        radio_group4 = view.findViewById(R.id.rg4);
        radio_group5 = view.findViewById(R.id.rg5);
        radio_group6 = view.findViewById(R.id.rg6);
        radio_group7 = view.findViewById(R.id.rg7);
        radio_group8 = view.findViewById(R.id.rg8);
        radio_group9 = view.findViewById(R.id.rg9);
        radio_group10 = view.findViewById(R.id.rg10);

        submit_button = view.findViewById(R.id.submit_button);
        scoreboard = view.findViewById(R.id.scoreboard);

        radio_groups = new ArrayList<RadioGroup>();
        radio_groups.add(radio_group1);
        radio_groups.add(radio_group2);
        radio_groups.add(radio_group3);
        radio_groups.add(radio_group4);
        radio_groups.add(radio_group5);
        radio_groups.add(radio_group6);
        radio_groups.add(radio_group7);
        radio_groups.add(radio_group8);
        radio_groups.add(radio_group9);
        radio_groups.add(radio_group10);

        solutions = new ArrayList<Integer>();
        solutions.add(R.id.q1c3);
        solutions.add(R.id.q2c1);
        solutions.add(R.id.q3c4);
        solutions.add(R.id.q4c4);
        solutions.add(R.id.q5c2);
        solutions.add(R.id.q6c1);
        solutions.add(R.id.q7c1);
        solutions.add(R.id.q8c2);
        solutions.add(R.id.q9c3);
        solutions.add(R.id.q10c4);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit_quiz();
            }
        });
    }

    private boolean are_all_questions_answer(){
        // This function checks if all the questions are answered

        for (RadioGroup rg:radio_groups){
            // Nothing is selected
            if (rg.getCheckedRadioButtonId()==-1){
                return false;
            }
        }
        return true;
    }

    private int calculate_score(){
        // This function calculates the score of the quiz

        int score = 0;
        RadioGroup rg;
        int solution_id;

        for (int i=0;i<radio_groups.size();i++){
            rg = radio_groups.get(i);
            solution_id = solutions.get(i);
            if (rg.getCheckedRadioButtonId()==solution_id){
                score += 10;
            }
        }

        return score;
    }

    private void show_solution(){
        // This function highlights the answer in green if users answer it correctly
        // Alternatively, highlight the answer in red if users do not answer it correctly

        RadioGroup rg;
        RadioButton rd;
        int solution_id;

        for (int i=0;i<radio_groups.size();i++){
            solution_id = solutions.get(i);
            rd = getActivity().findViewById(solution_id);
            rg = radio_groups.get(i);

            if (rg.getCheckedRadioButtonId()==solution_id){
                rd.setBackgroundColor(Color.parseColor("#BFFFB3"));
            }
            else{
                rd.setBackgroundColor(Color.parseColor("#FFCCCC"));
            }
        }
    }

    private void disable_radio_buttons(){
        for (RadioGroup rg:radio_groups){
            for (int i = 0; i < rg.getChildCount(); i++) {
                rg.getChildAt(i).setEnabled(false);
            }
        }
    }

    private void submit_quiz(){
        // Check if all questions are answered
        if (!are_all_questions_answer()){
            Toast.makeText(getActivity(), "Please answer all questions!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate the score and show it
        int score = calculate_score();
        scoreboard.setVisibility(View.VISIBLE);
        scoreboard.setText("Your score: " + score + "/100");

        // Disable the buttons, show solutions and hide the submit buttons
        disable_radio_buttons();
        show_solution();
        submit_button.setVisibility(View.GONE);
    }
}
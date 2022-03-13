package com.example.distribution;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TaskDetailtsFragment extends Fragment {

    String taskName, taskDescription, taskExpDate, taskExpTime, taskWorker;
    TextView textTaskName, textTaskDescription, textTaskExpDate, textTaskExpTime, textTaskWorker;

    public TaskDetailtsFragment(String taskName, String taskDescription, String taskExpDate, String taskExpTime, String taskWorker) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskExpDate = taskExpDate;
        this.taskExpTime = taskExpTime;
        this.taskWorker = taskWorker;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detailts, container, false);

        textTaskName = view.findViewById(R.id.textTaskName);
        textTaskDescription = view.findViewById(R.id.textTaskDescription);
        textTaskExpDate = view.findViewById(R.id.textTaskExpDate);
        textTaskExpTime = view.findViewById(R.id.textTaskExpTime);
        textTaskWorker = view.findViewById(R.id.textTaskWorker);

        textTaskName.setText(taskName);
        textTaskDescription.setText(taskDescription);
        textTaskExpDate.setText(taskExpDate);
        textTaskExpTime.setText(taskExpTime);
        textTaskWorker.setText(taskWorker);

        return view;
    }
}
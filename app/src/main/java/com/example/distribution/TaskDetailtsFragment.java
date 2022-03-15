package com.example.distribution;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskDetailtsFragment extends Fragment {

    interface OnFragmentSendDetailsToEdit{
        void onSendDetailsToEdit(String taskName, String taskDescription, String taskExpDate, String taskExpTime);
        void onCloseTaskDetailsFragment();
    }

    private OnFragmentSendDetailsToEdit fragmentSendDetailsToEdit;

    String taskName, taskDescription, taskExpDate, taskExpTime, taskWorker;
    TextView textTaskName, textTaskDescription, textTaskExpDate, textTaskExpTime, textTaskWorker;
    Button buttonTaskSeen, buttonTaskCompleted, buttonEditTask, buttonCloseTask;

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";
    String userRole;

    DatabaseReference databaseReference;
    String DISTRIBUTION_KEY = "Distribution";

    public TaskDetailtsFragment(String taskName, String taskDescription, String taskExpDate, String taskExpTime, String taskWorker) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskExpDate = taskExpDate;
        this.taskExpTime = taskExpTime;
        this.taskWorker = taskWorker;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentSendDetailsToEdit = (OnFragmentSendDetailsToEdit) context;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(), "Interface error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserRole();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonTaskSeen = view.findViewById(R.id.buttonTaskSeen);
        buttonTaskCompleted = view.findViewById(R.id.buttonTaskCompleted);
        buttonEditTask = view.findViewById(R.id.buttonEditTask);
        buttonCloseTask = view.findViewById(R.id.buttonCloseTask);

        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);

        if (!userRole.equals("Worker")){
            buttonTaskSeen.setVisibility(View.GONE);
            buttonTaskCompleted.setVisibility(View.GONE);
            buttonEditTask.setVisibility(View.VISIBLE);
            buttonCloseTask.setVisibility(View.VISIBLE);
        }

        buttonEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSendDetailsToEdit.onSendDetailsToEdit(taskName, taskDescription, taskExpDate, taskExpTime);
            }
        });

        buttonCloseTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTask("Task closed");
            }
        });

        buttonTaskCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTask("Task marked as completed");
            }
        });

        buttonTaskSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSendDetailsToEdit.onCloseTaskDetailsFragment();
            }
        });
    }

    private void getUserRole(){
        userRole = getActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).getString(PREF_ROLE, "Worker");
    }

    private void removeTask(String toastToShow){
        databaseReference.child(taskName).removeValue();
        showToast(toastToShow);
        fragmentSendDetailsToEdit.onCloseTaskDetailsFragment();
    }

    private void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
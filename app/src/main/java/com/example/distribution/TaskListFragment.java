package com.example.distribution;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    public interface OnFragmentSendDataListener{
        void onAddNewTask();
        void onSendTaskDetails(String taskName, String taskDescription, String taskExpDate, String taskExpTime, String taskWorker);
    }

    private OnFragmentSendDataListener fragmentSendDataListener;

    private ProgressBar progressBar;
    private ListView listTasks;
    private Button buttonAddNewTask;
    private ArrayList<Distribution> distributions;
    private DistributionAdapter adapter;

    private DatabaseReference databaseReference;
    private String DISTRIBUTION_KEY = "Distribution";

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";
    private static final String PREF_WORKER_NAME = "";
    private String userRole;

    public TaskListFragment() { }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentSendDataListener = (OnFragmentSendDataListener) context;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(), "Interface error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);
        getUserRole();
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        buttonAddNewTask = view.findViewById(R.id.buttonAddNewTask);
        if (userRole.equals("Worker")){
            buttonAddNewTask.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBarTaskList);
        listTasks = view.findViewById(R.id.listTasks);
        distributions = new ArrayList<>();
        adapter = new DistributionAdapter(getActivity(), R.layout.tasks_list, distributions);
        listTasks.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Distribution distribution = (Distribution)parent.getItemAtPosition(position);
                fragmentSendDataListener.onSendTaskDetails(distribution.getTaskName(), distribution.getTaskDescription(), distribution.getTaskExpirationDate(), distribution.getTaskExpirationTime(), distribution.getTaskWorker());
            }
        };
        listTasks.setOnItemClickListener(itemClickListener);

        buttonAddNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSendDataListener.onAddNewTask();
            }
        });
    }

    private void getData(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(distributions.size() > 0) distributions.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Distribution distribution = dataSnapshot.getValue(Distribution.class);
                    distributions.add(new Distribution(distribution.getTaskName(), distribution.getTaskDescription(), "Until " + distribution.getTaskExpirationDate(), distribution.getTaskExpirationTime(), "To: " + distribution.getTaskWorker()));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        if (userRole.equals("Worker")){
            databaseReference.orderByChild("taskWorker").equalTo(getActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).getString(PREF_WORKER_NAME, "")).addValueEventListener(valueEventListener);
        }
        else{
            databaseReference.addValueEventListener(valueEventListener);
        }
    }

    private void getUserRole(){
        userRole = getActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).getString(PREF_ROLE, "Worker");
    }
}
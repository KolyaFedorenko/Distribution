package com.example.distribution;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    ListView listTasks;
    ArrayList<Distribution> distributions;
    DistributionAdapter adapter;

    DatabaseReference databaseReference;
    String DISTRIBUTION_KEY = "Distribution";

    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listTasks = view.findViewById(R.id.listTasks);
        distributions = new ArrayList<>();
        adapter = new DistributionAdapter(getActivity(), R.layout.tasks_list, distributions);
        listTasks.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Distribution distribution = (Distribution)parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), "Task: " + distribution.getTaskName(), Toast.LENGTH_SHORT).show();
            }
        };
        listTasks.setOnItemClickListener(itemClickListener);
    }

    private void getData(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(distributions.size() > 0) distributions.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Distribution distribution = dataSnapshot.getValue(Distribution.class);
                    distributions.add(new Distribution(distribution.taskName, distribution.taskDescription));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }
}
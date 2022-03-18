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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddTaskFragment extends Fragment {

    interface OnFragmentCloseListener{
        void onCloseAddTaskFragment();
    }

    private OnFragmentCloseListener fragmentCloseListener;

    EditText editTaskName, editTaskDescription, editExpirationDate, editExpirationTime;
    Button buttonAddTask;
    ListView listTaskTo;
    String taskName, taskDesc, taskExpDate, taskExpTime, taskTo;
    String oldTaskName;
    boolean filled = false;

    DatabaseReference databaseReference, databaseReferenceUsers;

    ArrayList<String> users;
    UserAdapter adapter;
    String DISTRIBUTION_KEY = "Distribution", USERS_KEY = "Users";

    public AddTaskFragment() {
        // Required empty public constructor
    }

    public AddTaskFragment(String taskName, String taskDescription, String taskExpDate, String taskExpTime){
        this.taskName = taskName;
        this.taskDesc = taskDescription;
        this.taskExpDate = taskExpDate;
        this.taskExpTime = taskExpTime;
        filled = true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentCloseListener = (OnFragmentCloseListener) context;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(), "Interface error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        listTaskTo = view.findViewById(R.id.listTaskTo);
        buttonAddTask = view.findViewById(R.id.buttonAddTask);

        editTaskName = view.findViewById(R.id.editTaskName);
        editTaskDescription = view.findViewById(R.id.editTaskDescription);
        editExpirationDate = view.findViewById(R.id.editExpirationDate);
        editExpirationTime = view.findViewById(R.id.editExpirationTime);

        if (filled){
            editTaskName.setText(taskName);
            editTaskDescription.setText(taskDesc);
            editExpirationDate.setText(taskExpDate);
            editExpirationTime.setText(taskExpTime);

            oldTaskName = editTaskName.getText().toString();

            editTaskName.setEnabled(false);
            buttonAddTask.setBackground(getActivity().getDrawable(R.drawable.rounded_secondary_action_item));
            buttonAddTask.setText("Edit task");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(USERS_KEY);

        users = new ArrayList<>();
        adapter = new UserAdapter(getActivity(), R.layout.users_list, users);
        getUsers();
        listTaskTo.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String worker = (String)parent.getItemAtPosition(position);
                taskTo = worker;
                showToast("Selected worker: " + worker);
            }
        };
        listTaskTo.setOnItemClickListener(itemClickListener);

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskName = editTaskName.getText().toString();
                taskDesc = editTaskDescription.getText().toString();
                taskExpDate = editExpirationDate.getText().toString();
                taskExpTime = editExpirationTime.getText().toString();
                if (!(taskName.equals("") || taskDesc.equals("") || taskExpDate.equals("") || taskExpTime.equals("") || taskTo == null)) {
                    Distribution distribution = new Distribution(taskName, taskDesc, taskExpDate, taskExpTime, taskTo);
                    databaseReference.child(taskName).setValue(distribution);
                    showToast("Successfully added");
                    fragmentCloseListener.onCloseAddTaskFragment();
                }
                else {
                    showToast("One or more fields is empty");
                }
            }
        });
    }

    public void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void getUsers(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (users.size() > 0) users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user.login);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReferenceUsers.orderByChild("role").equalTo("Worker").addValueEventListener(valueEventListener);
    }
}
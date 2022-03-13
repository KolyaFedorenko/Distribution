package com.example.distribution;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTaskFragment extends Fragment {

    interface OnFragmentCloseListener{
        void onCloseFragment();
    }

    private OnFragmentCloseListener fragmentCloseListener;

    EditText editTaskName, editTaskDescription, editExpirationDate, editExpirationTime;
    Button buttonAddTask;
    Spinner spinnerTaskTo;
    String taskName, taskDesc, taskExpDate, taskExpTime, taskTo;

    DatabaseReference databaseReference;

    String[] workers = {"Worker 1", "Worker 2", "Worker 3"};
    String DISTRIBUTION_KEY = "Distribution";

    public AddTaskFragment() {
        // Required empty public constructor
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

        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerTaskTo = view.findViewById(R.id.spinnerTaskTo);
        editTaskName = view.findViewById(R.id.editTaskName);
        editTaskDescription = view.findViewById(R.id.editTaskDescription);
        editExpirationDate = view.findViewById(R.id.editExpirationDate);
        editExpirationTime = view.findViewById(R.id.editExpirationTime);
        buttonAddTask = view.findViewById(R.id.buttonAddTask);

        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, workers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskTo.setAdapter(adapter);

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskName = editTaskName.getText().toString();
                taskDesc = editTaskDescription.getText().toString();
                taskExpDate = editExpirationDate.getText().toString();
                taskExpTime = editExpirationTime.getText().toString();
                taskTo = spinnerTaskTo.getSelectedItem().toString();
                if (!(taskName.equals("") || taskDesc.equals("") || taskExpDate.equals("") || taskExpTime.equals(""))){
                    Distribution distribution = new Distribution(taskName, taskDesc, taskExpDate, taskExpTime, taskTo);
                    databaseReference.push().setValue(distribution);
                    showToast("Successfully added");
                    fragmentCloseListener.onCloseFragment();
                }
                else{
                    showToast("One or more fields is empty");
                }
            }
        });
    }

    public void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
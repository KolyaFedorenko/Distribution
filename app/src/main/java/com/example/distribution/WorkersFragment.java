package com.example.distribution;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WorkersFragment extends Fragment implements WorkerAdapter.WorkerAdapterInterface{

    private ListView listWorkers;

    private WorkerAdapter workerAdapter;
    private DatabaseReference databaseReference;
    private String USERS_KEY = "Users";
    private ArrayList<User> workers;

    private PasswordHasher passwordHasher = new PasswordHasher();

    public WorkersFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workers, container, false);

        listWorkers = view.findViewById(R.id.listWorkers);
        workers = new ArrayList<>();
        workerAdapter = new WorkerAdapter(getActivity(), R.layout.workers_list, workers, this);
        listWorkers.setAdapter(workerAdapter);
        getData();

        return view;
    }

    @Override
    public void onDeleteWorker(String workerName) {
        databaseReference.child(workerName).removeValue();
        showToast("Работник " + workerName + " был успешно удален");
    }

    @Override
    public void onChangeRole(String workerName, String workerRole) {
        if (!workerRole.equals("Manager")) databaseReference.child(workerName).child("role").setValue("Manager");
        else databaseReference.child(workerName).child("role").setValue("Worker");
        showToast("Роль пользователя " + workerName + " была успешно изменена");
    }

    @Override
    public void onResetPassword(String workerName) {
        String password = "12345";
        try {password = passwordHasher.generatePasswordHash(password); }
        catch (Exception ignored) {}
        databaseReference.child(workerName).child("password").setValue(password);
        showToast("Пароль пользователя " + workerName + " был успешно изменен на \"12345\"");
    }

    private void getData(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(workers.size() > 0) workers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    workers.add(new User(user.getLogin(), user.getPassword(), user.getRole()));
                }
                workerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
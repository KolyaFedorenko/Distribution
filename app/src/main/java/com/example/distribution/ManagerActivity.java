package com.example.distribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManagerActivity extends AppCompatActivity implements TaskListFragment.OnFragmentSendDataListener,
        AddTaskFragment.OnFragmentCloseListener, TaskDetailtsFragment.OnFragmentSendDetailsToEdit,
        SettingsFragment.OnFragmentSignOut, AuthorizationFragment.OnFragmentSignIn{

    String TRACKING_KEY = "TaskTracking";
    DatabaseReference databaseReferenceTracking;
    Integer issued, seen, completed;

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";
    private static final boolean PREF_SIGNED_IN = false;
    private static final String PREF_WORKER_NAME = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private BottomNavigationView bottomNavigationView;
    Fragment activeFragment;

    Fragment addTaskFragment, taskDetailsFragment;
    Fragment settingsFragment = new SettingsFragment();
    Fragment trackingFragment;
    Fragment taskListFragment = new TaskListFragment();
    Fragment authorizationFragment = new AuthorizationFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        databaseReferenceTracking = FirebaseDatabase.getInstance().getReference(TRACKING_KEY);
        getStatistic();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);
        bottomNavigationView.getMenu().findItem(R.id.taskTracking).setEnabled(false);

        if (!getSharedPreferences(PREFS_FILE,MODE_PRIVATE).getBoolean(String.valueOf(PREF_SIGNED_IN), false)){
            bottomNavigationView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().add(R.id.container, authorizationFragment, "authorization").commit();
            activeFragment = authorizationFragment;
        }
        else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().add(R.id.container, taskListFragment, "taskListFragment").commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container, settingsFragment, "settingsFragment").hide(settingsFragment).commit();
            activeFragment = taskListFragment;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId())
            {
                case R.id.taskList:
                    if (activeFragment.equals(addTaskFragment)){
                        replaceFragment(addTaskFragment, taskListFragment);
                    }
                    if (activeFragment.equals(taskDetailsFragment)){
                        replaceFragment(taskDetailsFragment, taskListFragment);
                    }
                    if (activeFragment.equals(trackingFragment)){
                        replaceFragment(trackingFragment, taskListFragment);
                    }
                    else {
                        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(taskListFragment).commit();
                        activeFragment = taskListFragment;
                    }
                    break;

                case R.id.taskTracking:
                    trackingFragment = new TrackingFragment(issued, seen, completed);
                    if (activeFragment.equals(addTaskFragment)){
                        getSupportFragmentManager().beginTransaction().remove(activeFragment).add(R.id.container, trackingFragment, "trackingFragment").commit();
                    }
                    if (activeFragment.equals(taskDetailsFragment)){
                        getSupportFragmentManager().beginTransaction().remove(activeFragment).add(R.id.container, trackingFragment, "trackingFragment").commit();
                    }
                    if (activeFragment.equals(trackingFragment)){
                        getSupportFragmentManager().beginTransaction().remove(activeFragment).show(trackingFragment).commit();
                    }
                    if(!activeFragment.equals(addTaskFragment) && !activeFragment.equals(taskDetailsFragment)) {
                        getSupportFragmentManager().beginTransaction().hide(activeFragment).add(R.id.container, trackingFragment, "trackingFragment").commit();
                    }
                    activeFragment = trackingFragment;
                    break;

                case R.id.settings:
                    if (activeFragment.equals(addTaskFragment)){
                        replaceFragment(addTaskFragment, settingsFragment);
                    }
                    if (activeFragment.equals(taskDetailsFragment)){
                        replaceFragment(taskDetailsFragment, settingsFragment);
                    }
                    if (activeFragment.equals(trackingFragment)){
                        replaceFragment(trackingFragment, settingsFragment);
                    }
                    else {
                        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(settingsFragment).commit();
                        activeFragment = settingsFragment;
                    }
                    break;
            }

            return true;
        }
    };

    @Override
    public void onAddNewTask() {
        addTaskFragment = new AddTaskFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, addTaskFragment, "addTaskFragment").hide(activeFragment).commit();
        activeFragment = addTaskFragment;
    }

    @Override
    public void onCloseAddTaskFragment() {
        replaceFragment(addTaskFragment, taskListFragment);
    }

    @Override
    public void onSendTaskDetails(String taskName, String taskDescription, String taskExpDate, String taskExpTime, String taskWorker) {
        taskDetailsFragment = new TaskDetailtsFragment(taskName, taskDescription, taskExpDate, taskExpTime, taskWorker);
        getSupportFragmentManager().beginTransaction().add(R.id.container, taskDetailsFragment, "taskDetailsFragment").hide(activeFragment).commit();
        activeFragment = taskDetailsFragment;
    }

    public void replaceFragment(Fragment replaced, Fragment replacing){
        getSupportFragmentManager().beginTransaction().remove(replaced).show(replacing).commit();
        activeFragment = replacing;
    }

    @Override
    public void onSendDetailsToEdit(String taskName, String taskDescription, String taskExpDate, String taskExpTime) {
        addTaskFragment = new AddTaskFragment(taskName, taskDescription, taskExpDate, taskExpTime);
        getSupportFragmentManager().beginTransaction().add(R.id.container, addTaskFragment, "addTaskFragment").hide(activeFragment).commit();
        activeFragment = addTaskFragment;
    }

    @Override
    public void onCloseTaskDetailsFragment() {
        replaceFragment(taskDetailsFragment, taskListFragment);
    }

    @Override
    public void onSignIn(String role, String name) {
        sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PREF_ROLE, role);
        editor.putBoolean(String.valueOf(PREF_SIGNED_IN), true);
        editor.putString(PREF_WORKER_NAME, name);
        editor.apply();
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.taskList);
        taskListFragment = new TaskListFragment();
        settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, taskListFragment, "taskListFragment").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, settingsFragment, "settingsFragment").hide(settingsFragment).commit();
        replaceFragment(authorizationFragment, taskListFragment);
    }

    @Override
    public void onSignOut() {
        sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(String.valueOf(PREF_SIGNED_IN), false).apply();
        getSupportFragmentManager().beginTransaction().hide(settingsFragment).remove(settingsFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(taskListFragment).remove(taskListFragment).commit();
        bottomNavigationView.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().add(R.id.container, authorizationFragment, "authorization").commit();
        activeFragment = authorizationFragment;
        Toast.makeText(ManagerActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
    }

    private void getStatistic(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    TaskTracking taskTracking = dataSnapshot.getValue(TaskTracking.class);
                    issued = taskTracking.issued;
                    seen = taskTracking.seen;
                    completed = taskTracking.completed;
                }
                bottomNavigationView.getMenu().findItem(R.id.taskTracking).setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseReferenceTracking.addValueEventListener(valueEventListener);
    }
}
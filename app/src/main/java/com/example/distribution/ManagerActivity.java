package com.example.distribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManagerActivity extends AppCompatActivity implements TaskListFragment.OnFragmentSendDataListener,
        AddTaskFragment.OnFragmentCloseListener, TaskDetailtsFragment.OnFragmentSendDetailsToEdit,
        SettingsFragment.OnFragmentSignOut, AuthorizationFragment.OnFragmentSignIn{

    private String TRACKING_KEY = "TaskTracking";
    private DatabaseReference databaseReferenceTracking = FirebaseDatabase.getInstance().getReference(TRACKING_KEY);
    private Integer issued, seen, completed;

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";
    private static final boolean PREF_SIGNED_IN = false;
    private static final String PREF_WORKER_NAME = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private BottomNavigationView bottomNavigationView;
    private Fragment activeFragment;

    private Fragment addTaskFragment, taskDetailsFragment;
    private Fragment settingsFragment = new SettingsFragment();
    private Fragment trackingFragment;
    private Fragment taskListFragment = new TaskListFragment();
    private Fragment authorizationFragment = new AuthorizationFragment();
    private Fragment workersFragment = new WorkersFragment();
    private Fragment eventsFragment = new EventsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        getStatistic();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);
        bottomNavigationView.getMenu().findItem(R.id.taskTracking).setEnabled(false);

        if (!getSharedPreferences(PREFS_FILE, MODE_PRIVATE).getBoolean(String.valueOf(PREF_SIGNED_IN), false)){
            bottomNavigationView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().add(R.id.container, authorizationFragment, "authorization").commit();
            activeFragment = authorizationFragment;
        }
        else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            addTNWS();
            activeFragment = taskListFragment;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId())
            {
                case R.id.taskList:
                    showTNWS(taskListFragment);
                    break;

                case R.id.taskTracking:
                    showTaskTracking();
                    break;

                case R.id.events:
                    showTNWS(eventsFragment);
                    break;

                case R.id.users:
                    showTNWS(workersFragment);
                    break;

                case R.id.settings:
                    showTNWS(settingsFragment);
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

    @Override
    public void onSendDetailsToEdit(String taskName, String taskDescription, String taskExpDate, String taskExpTime) {
        addTaskFragment = new AddTaskFragment(taskName, taskDescription, taskExpDate, taskExpTime);
        getSupportFragmentManager().beginTransaction().add(R.id.container, addTaskFragment, "addTaskFragment").remove(activeFragment).commit();
        activeFragment = addTaskFragment;
    }

    @Override
    public void onCloseTaskDetailsFragment() {
        replaceFragment(taskDetailsFragment, taskListFragment);
    }

    @Override
    public void onSignIn(String role, String name) {
        getSpEditor();
        editor.putString(PREF_ROLE, role);
        editor.putBoolean(String.valueOf(PREF_SIGNED_IN), true);
        editor.putString(PREF_WORKER_NAME, name);
        editor.apply();
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.taskList);
        taskListFragment = new TaskListFragment();
        settingsFragment = new SettingsFragment();
        workersFragment = new WorkersFragment();
        eventsFragment = new EventsFragment();
        addTNWS();
        replaceFragment(authorizationFragment, taskListFragment);
    }

    @Override
    public void onSignOut() {
        getSpEditor();
        editor.putBoolean(String.valueOf(PREF_SIGNED_IN), false).apply();
        getSupportFragmentManager().beginTransaction().hide(settingsFragment).remove(settingsFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(taskListFragment).remove(taskListFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(workersFragment).remove(workersFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(eventsFragment).remove(eventsFragment).commit();
        bottomNavigationView.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().add(R.id.container, authorizationFragment, "authorization").commit();
        activeFragment = authorizationFragment;
        Toast.makeText(ManagerActivity.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
    }

    private void getStatistic(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    TaskTracking taskTracking = dataSnapshot.getValue(TaskTracking.class);
                    issued = taskTracking.getIssued();
                    seen = taskTracking.getSeen();
                    completed = taskTracking.getCompleted();
                }
                bottomNavigationView.getMenu().findItem(R.id.taskTracking).setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseReferenceTracking.addValueEventListener(valueEventListener);
    }

    private void replaceFragment(Fragment replaced, Fragment replacing){
        getSupportFragmentManager().beginTransaction().remove(replaced).show(replacing).commit();
        activeFragment = replacing;
    }

    private void getSpEditor(){
        sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void addTNWS(){
        getSupportFragmentManager().beginTransaction().add(R.id.container, taskListFragment, "taskListFragment").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, settingsFragment, "settingsFragment").hide(settingsFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, workersFragment, "workersFragment").hide(workersFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, eventsFragment, "eventsFragment").hide(eventsFragment).commit();
    }

    private void showTNWS(Fragment fragmentToShow){
        if (activeFragment.equals(addTaskFragment) || activeFragment.equals(taskDetailsFragment) || activeFragment.equals(trackingFragment)){
            replaceFragment(activeFragment, fragmentToShow);
        }
        else {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(fragmentToShow).commit();
            activeFragment = fragmentToShow;
        }
    }

    private void showTaskTracking(){
        trackingFragment = new TrackingFragment(issued, seen, completed);
        if (activeFragment.equals(addTaskFragment) || activeFragment.equals(taskDetailsFragment)){
            getSupportFragmentManager().beginTransaction().remove(activeFragment).add(R.id.container, trackingFragment, "trackingFragment").commit();
        }
        if (!activeFragment.equals(addTaskFragment) && !activeFragment.equals(taskDetailsFragment)) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).add(R.id.container, trackingFragment, "trackingFragment").commit();
        }
        if (activeFragment.equals(trackingFragment)){
            getSupportFragmentManager().beginTransaction().remove(activeFragment).show(trackingFragment).commit();
        }
        activeFragment = trackingFragment;
    }
}
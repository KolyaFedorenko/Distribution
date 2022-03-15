package com.example.distribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerActivity extends AppCompatActivity implements TaskListFragment.OnFragmentSendDataListener, AddTaskFragment.OnFragmentCloseListener, TaskDetailtsFragment.OnFragmentSendDetailsToEdit{

    private BottomNavigationView bottomNavigationView;
    Fragment activeFragment;

    Fragment addTaskFragment, taskDetailsFragment;
    Fragment settingsFragment = new SettingsFragment();
    Fragment taskListFragment = new TaskListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);

        getSupportFragmentManager().beginTransaction().add(R.id.container, taskListFragment, "taskListFragment").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, settingsFragment, "settingsFragment").hide(settingsFragment).commit();
        activeFragment = taskListFragment;
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
                    else {
                        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(taskListFragment).commit();
                        activeFragment = taskListFragment;
                    }
                    break;

                case R.id.settings:
                    if (activeFragment.equals(addTaskFragment)){
                        replaceFragment(addTaskFragment, settingsFragment);
                    }
                    if (activeFragment.equals(taskDetailsFragment)){
                        replaceFragment(taskDetailsFragment, settingsFragment);
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
}
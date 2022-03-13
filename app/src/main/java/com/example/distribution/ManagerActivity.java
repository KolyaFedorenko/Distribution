package com.example.distribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerActivity extends AppCompatActivity implements TaskListFragment.OnFragmentSendDataListener, AddTaskFragment.OnFragmentCloseListener{

    private BottomNavigationView bottomNavigationView;
    Fragment activeFragment;

    Fragment addTaskFragment;
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
                        replaceAddTaskFragment(taskListFragment);
                    }
                    else {
                        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(taskListFragment).commit();
                        activeFragment = taskListFragment;
                    }
                    break;

                case R.id.settings:
                    if (activeFragment.equals(addTaskFragment)){
                        replaceAddTaskFragment(settingsFragment);
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
    public void onSendData() {
        addTaskFragment = new AddTaskFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, addTaskFragment, "addTaskFragment").hide(activeFragment).commit();
        activeFragment = addTaskFragment;
    }

    @Override
    public void onCloseFragment() {
        replaceAddTaskFragment(taskListFragment);
    }

    public void replaceAddTaskFragment(Fragment replacing){
        getSupportFragmentManager().beginTransaction().remove(addTaskFragment).show(replacing).commit();
        activeFragment = replacing;
    }
}
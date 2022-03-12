package com.example.distribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    Fragment activeFragment;

    Fragment addTaskFragment = new AddTaskFragment();
    Fragment settingsFragment = new SettingsFragment();
    Fragment taskListFragment = new TaskListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);

        getSupportFragmentManager().beginTransaction().add(R.id.container, taskListFragment, "taskListFragment").hide(taskListFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, settingsFragment, "settingsFragment").hide(settingsFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, addTaskFragment, "addTaskFragment").commit();
        activeFragment = addTaskFragment;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId())
            {
                case R.id.addTask:
                    getSupportFragmentManager().beginTransaction().hide(activeFragment).show(addTaskFragment).commit();
                    activeFragment = addTaskFragment;
                    break;

                case R.id.taskList:
                    getSupportFragmentManager().beginTransaction().hide(activeFragment).show(taskListFragment).commit();
                    activeFragment = taskListFragment;
                    break;

                case R.id.settings:
                    getSupportFragmentManager().beginTransaction().hide(activeFragment).show(settingsFragment).commit();
                    activeFragment = settingsFragment;
                    break;
            }

            return true;
        }
    };
}
package com.example.distribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManagerActivity extends AppCompatActivity implements TaskListFragment.OnFragmentSendDataListener,
        AddTaskFragment.OnFragmentCloseListener, TaskDetailtsFragment.OnFragmentSendDetailsToEdit,
        SettingsFragment.OnFragmentSignOut, AuthorizationFragment.OnFragmentSignIn, EventsFragment.EventFragmentInterface,
        AddEventFragment.AddEventFragmentInterface {

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
    private Fragment addEventFragment;

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
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left_first, R.anim.slide_right_to_left).add(R.id.container, addTaskFragment, "addTaskFragment").hide(activeFragment).commit();
        activeFragment = addTaskFragment;
    }

    @Override
    public void onCloseAddTaskFragment() {
        replaceFragment(addTaskFragment, taskListFragment);
    }

    @Override
    public void onSendTaskDetails(String taskName, String taskDescription, String taskExpDate, String taskExpTime, String taskWorker) {
        taskDetailsFragment = new TaskDetailtsFragment(taskName, taskDescription, taskExpDate, taskExpTime, taskWorker);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left_first, R.anim.slide_right_to_left).add(R.id.container, taskDetailsFragment, "taskDetailsFragment").hide(activeFragment).commit();
        activeFragment = taskDetailsFragment;
    }

    @Override
    public void onSendDetailsToEdit(String taskName, String taskDescription, String taskExpDate, String taskExpTime) {
        addTaskFragment = new AddTaskFragment(taskName, taskDescription, taskExpDate, taskExpTime);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left_first, R.anim.slide_right_to_left).add(R.id.container, addTaskFragment, "addTaskFragment").remove(activeFragment).commit();
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

    @Override
    public void onAddNewEvent() {
        addEventFragment = new AddEventFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left_first, R.anim.slide_right_to_left).add(R.id.container, addEventFragment, "addEventFragment").hide(activeFragment).commit();
        activeFragment = addEventFragment;
    }

    @Override
    public void onCloseAddEventFragment() {
        replaceFragment(addEventFragment, eventsFragment);
    }

    @Override
    public void onShowInstruction() {
        showTNWS(taskListFragment);
        showInstruction();
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
        if (replacing.equals(taskListFragment) && !replaced.equals(trackingFragment)){
            animatedReplaceFragment(replaced, replacing);
        }
        else if (replaced.equals(addEventFragment) && replacing.equals(eventsFragment)){
            animatedReplaceFragment(replaced, replacing);
        }
        else {
            getSupportFragmentManager().beginTransaction().remove(replaced).show(replacing).commit();
        }
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
        if (activeFragment.equals(addTaskFragment) || activeFragment.equals(taskDetailsFragment) || activeFragment.equals(trackingFragment) || activeFragment.equals(addEventFragment)){
            replaceFragment(activeFragment, fragmentToShow);
        }
        else {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(fragmentToShow).commit();
            activeFragment = fragmentToShow;
        }
    }

    private void showTaskTracking(){
        trackingFragment = new TrackingFragment(issued, seen, completed);
        if (activeFragment.equals(addTaskFragment) || activeFragment.equals(taskDetailsFragment) || activeFragment.equals(addEventFragment)){
            getSupportFragmentManager().beginTransaction().remove(activeFragment).add(R.id.container, trackingFragment, "trackingFragment").commit();
        }
        if (!activeFragment.equals(addTaskFragment) && !activeFragment.equals(taskDetailsFragment) && !activeFragment.equals(addEventFragment)) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).add(R.id.container, trackingFragment, "trackingFragment").commit();
        }
        if (activeFragment.equals(trackingFragment)){
            getSupportFragmentManager().beginTransaction().remove(activeFragment).show(trackingFragment).commit();
        }
        activeFragment = trackingFragment;
    }

    private void animatedReplaceFragment(Fragment replaced, Fragment replacing){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.empty_animation, R.anim.slide_left_to_right)
                .remove(replaced)
                .setCustomAnimations(R.anim.slide_left_to_right_first, R.anim.slide_left_to_right)
                .show(replacing)
                .commit();
    }

    private void showInstruction(){
        try {
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.taskList), "Список задач", "В этой вкладке отображается список невыполненных задач").cancelable(false).outerCircleAlpha(0.8f),
                            TapTarget.forView(findViewById(R.id.buttonAddNewTask), "Добавление задачи", "Для добавления задачи можно нажать на эту кнопку").cancelable(false).tintTarget(false).outerCircleAlpha(0.8f).id(1),
                            TapTarget.forView(findViewById(R.id.taskTracking), "Отслеживание", "Если вы являетесь руководителем, здесь можно посмотреть информацию по количеству выданных/просмотренных/выполненных задач").cancelable(false).outerCircleAlpha(0.8f).id(2),
                            TapTarget.forView(findViewById(R.id.events), "События", "В этом разделе отображается список предстоящих событий").cancelable(false).outerCircleAlpha(0.8f),
                            TapTarget.forView(findViewById(R.id.buttonAddNewEvent), "Добавление события", "Для добавления события можно нажать на эту кнопку").cancelable(false).tintTarget(false).outerCircleAlpha(0.8f).id(3),
                            TapTarget.forView(findViewById(R.id.users), "Пользователи", "Здесь отображается список всех пользователей приложения").cancelable(false).outerCircleAlpha(0.8f).id(4),
                            TapTarget.forView(findViewById(R.id.settings), "Настройки", "В этом разделе расположен список настроек приложения").cancelable(false).outerCircleAlpha(0.8f),
                            TapTarget.forView(findViewById(R.id.editChangePassword), "Смена пароля", "Для смены пароля введите в это поле новый пароль и совершите длительное нажатие").cancelable(false).outerCircleAlpha(1f).targetRadius(70),
                            TapTarget.forView(findViewById(R.id.editPrivateReminder), "Создание личного напоминания", "Для добавления нового личного напоминания введите в это поле текст напоминания и совершите длительное нажатие").cancelable(false).outerCircleAlpha(1f).targetRadius(120),
                            TapTarget.forView(findViewById(R.id.textCheckPrivateReminders), "Просмотр списка напоминаний", "Для просмотра списка личных напоминаний совершите длительное нажатие на этот пункт настроек").cancelable(false).outerCircleAlpha(1f).targetRadius(110),
                            TapTarget.forView(findViewById(R.id.textClearPrivateReminders), "Очистка списка напоминаний", "Для очистки списка личных напоминаний совершите длительное нажатие на этот пункт настроек").cancelable(false).outerCircleAlpha(1f).targetRadius(120),
                            TapTarget.forView(findViewById(R.id.editAppReview), "Отзыв о приложении", "Чтобы добавить отзыв, введите в это поле его текст и совершите длительное нажатие").cancelable(false).outerCircleAlpha(1f).targetRadius(90),
                            TapTarget.forView(findViewById(R.id.textAppInformation), "Как использовать приложение?", "Чтобы узнать, как использовать приложение, совершите длительное нажатие на этот пункт настроек").cancelable(false).outerCircleAlpha(1f).targetRadius(110),
                            TapTarget.forView(findViewById(R.id.buttonSignOut), "Выход из аккаунта", "Чтобы выйти из аккаунта, нажмите на эту кнопку").cancelable(false).tintTarget(false).outerCircleAlpha(0.8f).targetRadius(60))
                    .listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            switch (lastTarget.id()) {
                                case 1:
                                    if (bottomNavigationView.getMenu().findItem(R.id.taskTracking).isEnabled()) showTaskTracking();
                                    break;
                                case 2:
                                    showTNWS(eventsFragment);
                                    break;
                                case 3:
                                    showTNWS(workersFragment);
                                    break;
                                case 4:
                                    showTNWS(settingsFragment);
                                    break;
                            }
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                        }
                    }).start();
        }
        catch (Exception e){
            Toast.makeText(this, "К сожалению, данная возможность сейчас недоступна", Toast.LENGTH_SHORT).show();
        }
    }
}
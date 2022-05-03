package com.example.distribution;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTaskFragment extends Fragment {

    public interface OnFragmentCloseListener{
        void onCloseAddTaskFragment();
    }
    private OnFragmentCloseListener fragmentCloseListener;

    private EditText editTaskName, editTaskDescription;
    private TextView textExpirationDate, textExpirationTime;
    private Button buttonAddTask;
    private ListView listTaskTo;
    private String taskName, taskDesc, taskExpDate, taskExpTime, taskTo;
    private boolean filled = false;

    private final Calendar calendar = Calendar.getInstance();
    private String day, month;
    private int year;

    private int nowHour = calendar.get(Calendar.HOUR_OF_DAY), nowMinute = calendar.get(Calendar.MINUTE);

    private DatabaseReference databaseReference, databaseReferenceUsers, databaseReferenceTracking;

    private ArrayList<String> users;
    private UserAdapter adapter;
    private String DISTRIBUTION_KEY = "Distribution", USERS_KEY = "Users", TRACKING_KEY = "TaskTracking";

    public AddTaskFragment() { }

    public AddTaskFragment(Distribution distribution){
        taskName = distribution.getTaskName();
        taskDesc = distribution.getTaskDescription();
        taskExpDate = distribution.getTaskExpirationDate();
        taskExpTime = distribution.getTaskExpirationTime();
        taskTo = distribution.getTaskWorker().substring(5);
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
        textExpirationDate = view.findViewById(R.id.textExpirationDate);
        textExpirationTime = view.findViewById(R.id.textExpirationTime);

        if (filled){
            editTaskName.setText(taskName);
            editTaskDescription.setText(taskDesc);
            textExpirationDate.setText(taskExpDate.substring(3));
            textExpirationTime.setText(taskExpTime);

            editTaskName.setEnabled(false);
            buttonAddTask.setBackground(getActivity().getDrawable(R.drawable.rounded_secondary_action_item));
            buttonAddTask.setText("Редактировать задачу");
        }
        else{
            day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
            year = calendar.get(Calendar.YEAR);
            textExpirationDate.setText(formatDate(day, month, year));
            textExpirationTime.setText(nowHour + ":" + formatMinute(String.valueOf(nowMinute)));
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(USERS_KEY);
        databaseReferenceTracking = FirebaseDatabase.getInstance().getReference(TRACKING_KEY).child("Tracking");

        users = new ArrayList<>();
        adapter = new UserAdapter(getActivity(), R.layout.users_list, users);
        getUsers();
        listTaskTo.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String worker = (String)parent.getItemAtPosition(position);
                taskTo = worker;
                showToast("Выбранный работник: " + worker);
            }
        };
        listTaskTo.setOnItemClickListener(itemClickListener);

        textExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        day = Integer.toString(dayOfMonth);
                        String strMonth = Integer.toString(month + 1);
                        textExpirationDate.setText(formatDate(day, strMonth, year));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        textExpirationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), R.style.MyDatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textExpirationTime.setText(hourOfDay + ":" + formatMinute(String.valueOf(minute)));
                    }
                }, nowHour, nowMinute, true).show();
            }
        });

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskName = editTaskName.getText().toString();
                taskDesc = editTaskDescription.getText().toString();
                taskExpDate = textExpirationDate.getText().toString();
                taskExpTime = textExpirationTime.getText().toString();
                if (!(taskName.equals("") || taskDesc.equals("") || taskExpDate.equals("") || taskExpTime.equals("") || taskTo == null)) {
                    if (taskName.matches("^[a-zA-Z0-9]+$")) {
                        Distribution distribution = new Distribution(taskName, taskDesc, taskExpDate, taskExpTime, taskTo);
                        databaseReference.child(taskName).setValue(distribution);
                        if (!filled) editIssuedTasksCount();
                        showToast("Успешно добавлено!");
                        fragmentCloseListener.onCloseAddTaskFragment();
                    }
                    else{
                        showToast("Имя задачи содержит недопустимые символы!");
                    }
                }
                else {
                    showToast("Необходимо заполнить все поля!");
                }
            }
        });
    }

    private void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void getUsers(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (users.size() > 0) users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user.getLogin());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReferenceUsers.orderByChild("role").equalTo("Worker").addValueEventListener(valueEventListener);
    }

    private String formatDate(String dayToFormat, String monthToFormat, int year){
        String formatDay = dayToFormat, formatMonth = monthToFormat;
        if (dayToFormat.length() == 1) formatDay = "0" + dayToFormat;
        if (monthToFormat.length() == 1) formatMonth = "0" + monthToFormat;
        return formatDay + "." + formatMonth + "." + year;
    }

    private String formatMinute(String minuteToFormat){
        String formatMinute = minuteToFormat;
        if (formatMinute.length() == 1) formatMinute = "0" + formatMinute;
        return formatMinute;
    }

    private void editIssuedTasksCount(){
        databaseReferenceTracking.child("issued").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentValue = currentData.getValue(Integer.class);
                if (currentValue == null) { currentData.setValue(0); }
                else { currentData.setValue(currentValue + 1); }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }
}
package com.example.distribution;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEventFragment extends Fragment {

    public interface AddEventFragmentInterface{
        void onCloseAddEventFragment();
    }
    private AddEventFragmentInterface addEventFragmentInterface;

    private EditText editEventName, editEventDescription;
    private TextView textEventDate;
    private ListView listEventWorkers;
    private ChipGroup chipGroupEventWorkers;
    private Button buttonAddEvent;

    private DatabaseReference databaseReference;

    private ArrayList<String> users;
    private UserAdapter adapter;

    private final Calendar calendar = Calendar.getInstance();


    private Event event;
    private boolean filled = false;
    private String[] workers;

    public AddEventFragment() { }

    public AddEventFragment(Event event){
        this.event = event;
        workers = event.getEventWorkers().split(", ");
        filled = true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addEventFragmentInterface = (AddEventFragmentInterface) context;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(), "Interface error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textEventDate = view.findViewById(R.id.textEventDate);
        editEventName = view.findViewById(R.id.editEventName);
        editEventDescription = view.findViewById(R.id.editEventDescription);
        listEventWorkers = view.findViewById(R.id.listEventWorkers);
        chipGroupEventWorkers = view.findViewById(R.id.chipGroupEventWorkers);
        buttonAddEvent = view.findViewById(R.id.buttonAddEvent);

        if (filled){
            buttonAddEvent.setBackground(getActivity().getDrawable(R.drawable.rounded_secondary_action_item));
            buttonAddEvent.setText("Редактировать событие");
            editEventName.setEnabled(false);
            editEventName.setText(event.getEventName());
            editEventDescription.setText(event.getEventDescription());
            textEventDate.setText(event.getEventDate());
            for (String s : workers) {
                addChip(s);
            }
        }

        users = new ArrayList<>();
        adapter = new UserAdapter(getActivity(), R.layout.users_list, users);
        getUsers();
        listEventWorkers.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addChip((String)parent.getItemAtPosition(position));
            }
        };
        listEventWorkers.setOnItemClickListener(itemClickListener);

        textEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String day = Integer.toString(dayOfMonth);
                        String strMonth = Integer.toString(month + 1);
                        textEventDate.setText(formatDate(day, strMonth, year));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buttonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String eventName = editEventName.getText().toString();
                    String eventDescription = editEventDescription.getText().toString();
                    String eventDate = textEventDate.getText().toString();
                    String eventWorkers = getSelectedWorkers();
                    if (!eventName.equals("") && !eventDescription.equals("") && !eventWorkers.equals("") && !eventDate.equals("Дата события")) {
                        if (eventName.matches("^[a-zA-Z0-9]+$")) {
                            Event event = new Event(eventName, eventDescription, eventWorkers, eventDate);
                            databaseReference.child("Events").child(eventName).setValue(event);
                            addEventFragmentInterface.onCloseAddEventFragment();
                        } else {
                            Toast.makeText(getActivity(), "Имя события содержит недопустимые символы!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Необходимо заполнить все поля!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Необходимо выбрать работников!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        databaseReference.child("Users").orderByChild("role").equalTo("Worker").addValueEventListener(valueEventListener);
    }

    private void handleChipCloseIconClicked(Chip chip) {
        ChipGroup parent = (ChipGroup) chip.getParent();
        parent.removeView(chip);
    }

    private String getSelectedWorkers(){
        int count = chipGroupEventWorkers.getChildCount();
        String selectedWorkers = null;

        for (int i = 0; i < count; i++){
            Chip child = (Chip) chipGroupEventWorkers.getChildAt(i);
            if (selectedWorkers == null) selectedWorkers = child.getText().toString();
            else selectedWorkers += ", " + child.getText().toString();
        }
        return selectedWorkers;
    }

    private String formatDate(String dayToFormat, String monthToFormat, int year){
        String formatDay = dayToFormat, formatMonth = monthToFormat;
        if (dayToFormat.length() == 1) formatDay = "0" + dayToFormat;
        if (monthToFormat.length() == 1) formatMonth = "0" + monthToFormat;
        return formatDay + "." + formatMonth + "." + year;
    }

    private void addChip(String chipText){
        try{
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Chip chip = (Chip) inflater.inflate(R.layout.chip_entry, chipGroupEventWorkers, false);
            chip.setText(chipText);
            chip.setTextIsSelectable(false);
            chipGroupEventWorkers.addView(chip);

            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleChipCloseIconClicked((Chip) v);
                }
            });
        }
        catch (Exception e) {
            Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
        }
    }
}
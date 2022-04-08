package com.example.distribution;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddEventFragment extends Fragment {

    public interface AddEventFragmentInterface{
        void onCloseAddEventFragment();
    }
    private AddEventFragmentInterface addEventFragmentInterface;

    private EditText editEventName, editEventDescription;
    private ListView listEventWorkers;
    private ChipGroup chipGroupEventWorkers;
    private Button buttonAddEvent;

    private DatabaseReference databaseReference;

    private ArrayList<String> users;
    private UserAdapter adapter;

    public AddEventFragment() { }

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

        editEventName = view.findViewById(R.id.editEventName);
        editEventDescription = view.findViewById(R.id.editEventDescription);
        listEventWorkers = view.findViewById(R.id.listEventWorkers);
        chipGroupEventWorkers = view.findViewById(R.id.chipGroupEventWorkers);
        buttonAddEvent = view.findViewById(R.id.buttonAddEvent);

        users = new ArrayList<>();
        adapter = new UserAdapter(getActivity(), R.layout.users_list, users);
        getUsers();
        listEventWorkers.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String worker = (String)parent.getItemAtPosition(position);
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    Chip chip = (Chip) inflater.inflate(R.layout.chip_entry, chipGroupEventWorkers, false);
                    chip.setText(worker);
                    chip.setTextIsSelectable(false);
                    chipGroupEventWorkers.addView(chip);

                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleChipCloseIconClicked((Chip) v);
                        }
                    });
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        };
        listEventWorkers.setOnItemClickListener(itemClickListener);

        buttonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = editEventName.getText().toString();
                String eventDescription = editEventDescription.getText().toString();
                String eventWorkers = getSelectedWorkers();
                if (!eventName.equals("") && !eventDescription.equals("") && !eventWorkers.equals("")){
                    Event event = new Event(eventName, eventDescription, eventWorkers);
                    databaseReference.child("Events").child(eventName).setValue(event);
                    addEventFragmentInterface.onCloseAddEventFragment();
                }
                else{
                    Toast.makeText(getActivity(), "Необходимо заполнить все поля!", Toast.LENGTH_SHORT).show();
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
}
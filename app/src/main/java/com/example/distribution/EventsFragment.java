package com.example.distribution;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsFragment extends Fragment {

    public interface EventFragmentInterface{
        void onAddNewEvent();
    }
    private EventFragmentInterface eventFragmentInterface;

    private Button buttonAddNewEvent;
    private RecyclerView recyclerViewEvents;

    private DatabaseReference databaseReference;
    private ArrayList<Event> events;
    private EventAdapter adapter;

    public EventsFragment() { }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            eventFragmentInterface = (EventFragmentInterface) context;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(), "Interface error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonAddNewEvent = view.findViewById(R.id.buttonAddNewEvent);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewEvents.setLayoutManager(layoutManager);

        events = new ArrayList<>();
        adapter = new EventAdapter(getActivity(), events);
        getEvents();
        recyclerViewEvents.setAdapter(adapter);

        buttonAddNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventFragmentInterface.onAddNewEvent();
            }
        });
    }

    private void getEvents(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (events.size() > 0) events.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Event event = dataSnapshot.getValue(Event.class);
                    events.add(new Event(event.getEventName(), event.getEventDescription(), event.getEventWorkers(), event.getEventDate()));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }
}
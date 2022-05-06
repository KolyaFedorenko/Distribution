package com.example.distribution;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class EventsFragment extends Fragment {

    public interface EventFragmentInterface{
        void onAddNewEvent();
        void onEditEvent(Event event);
    }
    private EventFragmentInterface eventFragmentInterface;

    private Button buttonAddNewEvent, buttonEditEvent, buttonDeleteEvent;
    private RecyclerView recyclerViewEvents;
    private ProgressBar progressBarEvents;
    private TextView eventsListEmpty;

    private DatabaseReference databaseReference;
    private ArrayList<Event> events;
    private EventAdapter adapter;

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";

    private boolean menuShowed = false;

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

        buttonEditEvent = view.findViewById(R.id.buttonEditEvent);
        buttonDeleteEvent = view.findViewById(R.id.buttonDeleteEvent);
        buttonAddNewEvent = view.findViewById(R.id.buttonAddNewEvent);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        progressBarEvents = view.findViewById(R.id.progressBarEvents);
        eventsListEmpty = view.findViewById(R.id.eventsListEmpty);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewEvents.setLayoutManager(layoutManager);

        events = new ArrayList<>();
        adapter = new EventAdapter(getActivity(), events);
        getEvents();
        recyclerViewEvents.setAdapter(adapter);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewEvents);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewEvents);

        if (!getUserRole().equals("Manager")){
            buttonAddNewEvent.setVisibility(View.INVISIBLE);
            buttonEditEvent.setVisibility(View.INVISIBLE);
            buttonDeleteEvent.setVisibility(View.INVISIBLE);
        }

        buttonAddNewEvent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!menuShowed) {
                    updateMenu(false, 160f, -225f, 1f, R.drawable.rounded_hidden_event_menu);
                }
                return true;
            }
        });

        buttonAddNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuShowed){
                    updateMenu(true, 0f, 0f, 0f, R.drawable.rounded_delete_event);
                    } else {
                    eventFragmentInterface.onAddNewEvent();
                }
            }
        });

        buttonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    databaseReference.child(getEvent().getEventName()).removeValue();
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "На экране нет ни одного события", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eventFragmentInterface.onEditEvent(getEvent());
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "На экране нет ни одного события", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(adapter.events, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(adapter.events, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }
    };

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
                progressBarEvents.setVisibility(View.GONE);
                if (events.size() == 0) eventsListEmpty.setVisibility(View.VISIBLE);
                else eventsListEmpty.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private String getUserRole(){
        return getActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).getString(PREF_ROLE, "Worker");
    }

    private void updateMenu(boolean menuShowed, float translation, float rotation, float alpha, int buttonBackground){
        this.menuShowed = !menuShowed;
        buttonEditEvent.animate().translationX(translation).setDuration(500).alpha(alpha).setDuration(500).start();
        buttonDeleteEvent.animate().translationX(0-translation).setDuration(500).alpha(alpha).setDuration(500).start();
        buttonAddNewEvent.animate().rotation(rotation).setDuration(500).start();
        buttonAddNewEvent.setBackground(getActivity().getDrawable(buttonBackground));
    }

    private Event getEvent(){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewEvents.getLayoutManager();
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        return adapter.getItemAtPosition(position);
    }
}
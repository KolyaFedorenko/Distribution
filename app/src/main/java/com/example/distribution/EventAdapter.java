package com.example.distribution;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<Event> events;
    private Context context;

    public EventAdapter(Context context, List<Event> events){
        this.events = events;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.events_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textEventName.setText(event.getEventName());
        holder.textEventDescription.setText(event.getEventDescription());
        holder.textEventWorkers.setText(event.getEventWorkers());
        holder.textEventDateCard.setText(event.getEventDate());

        if (event.getEventDate().substring(0,1).equals("1") || event.getEventDate().substring(0,1).equals("3")){
            holder.constraintLayoutEvents.setBackground(context.getDrawable(R.drawable.event_card_first));
        }
        else if (event.getEventDate().substring(0,1).equals("0")){
            holder.constraintLayoutEvents.setBackground(context.getDrawable(R.drawable.event_card_second));
        }
        else{
            holder.constraintLayoutEvents.setBackground(context.getDrawable(R.drawable.event_card_third));
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView textEventName, textEventDescription, textEventWorkers, textEventDateCard;
        final ConstraintLayout constraintLayoutEvents;
        ViewHolder(View view){
            super(view);
            textEventName = view.findViewById(R.id.textEventName);
            textEventDescription = view.findViewById(R.id.textEventDescription);
            textEventWorkers = view.findViewById(R.id.textEventWorkers);
            textEventDateCard = view.findViewById(R.id.textEventDateCard);
            constraintLayoutEvents = view.findViewById(R.id.constraintLayoutEvents);
        }
    }
}

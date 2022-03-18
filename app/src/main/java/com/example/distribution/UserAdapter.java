package com.example.distribution;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private int layout;
    private List<String> users;

    public UserAdapter(Context context, int resource, List<String> users){
        super(context, resource, users);
        this.users = users;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View view = inflater.inflate(this.layout, parent, false);
        TextView workerName = view.findViewById(R.id.workerName);

        String user = users.get(position);
        workerName.setText(user);

        return view;
    }
}

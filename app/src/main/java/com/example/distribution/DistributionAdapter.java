package com.example.distribution;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DistributionAdapter extends ArrayAdapter<Distribution> {

    private LayoutInflater inflater;
    private int layout;
    private List<Distribution> distributions;

    public DistributionAdapter(Context context, int resource, List<Distribution> distributions){
        super(context, resource, distributions);
        this.distributions = distributions;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view=inflater.inflate(this.layout, parent, false);

        TextView taskNameView = view.findViewById(R.id.tlTaskName);
        TextView taskDescriptionView = view.findViewById(R.id.tlTaskDescription);

        Distribution distribution = distributions.get(position);
        taskNameView.setText(distribution.getTaskName());
        taskDescriptionView.setText(distribution.getTaskDescription());

        return view;
    }
}

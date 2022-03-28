package com.example.distribution;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;

public class TrackingFragment extends Fragment {

    private int issued, seen, completed;

    private PieChart pieChartAllStats;
    private TextView textChartUnavailable;

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";

    public TrackingFragment() { }

    public TrackingFragment(int issued, int seen, int completed){
        this.issued = issued;
        this.seen = seen;
        this.completed = completed;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChartAllStats = view.findViewById(R.id.pieChartAllStats);
        textChartUnavailable = view.findViewById(R.id.textChartUnavailable);

        if (getUserRole().equals("Manager")) {
            setupPieChart();
            loadPieChartData();
        }
        else{
            pieChartAllStats.setVisibility(View.GONE);
            textChartUnavailable.setVisibility(View.VISIBLE);
        }
    }

    private void setupPieChart(){
        pieChartAllStats.setDrawHoleEnabled(true);
        pieChartAllStats.setEntryLabelTextSize(12);
        pieChartAllStats.setEntryLabelColor(Color.BLACK);
        pieChartAllStats.setCenterText("Task stats");
        pieChartAllStats.setCenterTextSize(18);
        pieChartAllStats.getDescription().setEnabled(false);

        Legend legend = pieChartAllStats.getLegend();
        legend.setEnabled(false);
    }

    private void loadPieChartData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(issued, "Issued"));
        entries.add(new PieEntry(seen, "Seen"));
        entries.add(new PieEntry(completed, "Completed"));

        PieDataSet dataSet = new PieDataSet(entries, "Legend");
        dataSet.setColors(new int[]{R.color.colorIssued, R.color.colorSeen, R.color.colorCompleted}, getActivity());

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new DefaultValueFormatter(0));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChartAllStats.setData(data);
        pieChartAllStats.invalidate();
        pieChartAllStats.animateY(1400, Easing.EaseInOutQuart);
    }

    private String getUserRole(){
        return getActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).getString(PREF_ROLE, "Worker");
    }
}
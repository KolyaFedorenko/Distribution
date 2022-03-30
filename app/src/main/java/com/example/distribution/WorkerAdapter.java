package com.example.distribution;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WorkerAdapter extends ArrayAdapter<User> {

    public interface WorkerAdapterInterface{
        void onDeleteWorker(String workerName);
        void onResetPassword(String workerName);
        void onChangeRole(String workerName, String workerRole);
    }
    private WorkerAdapterInterface workerAdapterInterface;

    private LayoutInflater inflater;
    private int layout;
    private List<User> users;
    private Context context;

    public WorkerAdapter(Context context, int resource, List<User> users, WorkerAdapterInterface workerAdapterInterface){
        super(context, resource, users);
        this.users = users;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.workerAdapterInterface = workerAdapterInterface;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view = inflater.inflate(this.layout, parent, false);

        TextView textWorkerName = view.findViewById(R.id.textWorkerName);
        ImageView imageWorkerManager = view.findViewById(R.id.imageWorkerManager);
        Button buttonDeleteWorker = view.findViewById(R.id.buttonDeleteWorker);
        Button buttonResetPassword = view.findViewById(R.id.buttonResetPassword);
        Button buttonChangeRole = view.findViewById(R.id.buttonChangeRole);

        User user = users.get(position);
        textWorkerName.setText(user.getLogin());
        if (user.getRole().equals("Manager")){
            imageWorkerManager.setVisibility(View.VISIBLE);
            buttonChangeRole.setBackground(context.getDrawable(R.drawable.remove_manager_role_gradient));
        }

        buttonDeleteWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workerAdapterInterface.onDeleteWorker(user.getLogin());
            }
        });

        buttonChangeRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workerAdapterInterface.onChangeRole(user.getLogin(), user.getRole());
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workerAdapterInterface.onResetPassword(user.getLogin());
            }
        });

        return view;
    }
}

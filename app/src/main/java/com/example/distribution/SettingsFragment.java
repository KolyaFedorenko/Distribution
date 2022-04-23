package com.example.distribution;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsFragment extends Fragment {

    public interface OnFragmentSignOut{
        void onSignOut();
        void onShowInstruction();
    }
    private OnFragmentSignOut fragmentSignOut;

    private Button buttonSignOut;
    private TextView textSignedAs, textCheckPrivateReminders, textClearPrivateReminders, textAppInformation;
    private EditText editPrivateReminder, editAppReview, editChangePassword;
    private ImageView imageManager;

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";
    private static final String PREF_WORKER_NAME = "";

    private static final String FILE_NAME = "PrivateReminders.txt";

    private PasswordHasher passwordHasher = new PasswordHasher();
    private String password;

    private DatabaseReference databaseReference;

    public SettingsFragment() { }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentSignOut = (OnFragmentSignOut) context;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(), "Interface error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSignOut = view.findViewById(R.id.buttonSignOut);
        textSignedAs = view.findViewById(R.id.textSignedAs);
        imageManager = view.findViewById(R.id.imageManager);

        textAppInformation = view.findViewById(R.id.textAppInformation);
        textCheckPrivateReminders = view.findViewById(R.id.textCheckPrivateReminders);
        textClearPrivateReminders = view.findViewById(R.id.textClearPrivateReminders);
        editChangePassword = view.findViewById(R.id.editChangePassword);
        editPrivateReminder = view.findViewById(R.id.editPrivateReminder);
        editAppReview = view.findViewById(R.id.editAppReview);

        textSignedAs.setText(textSignedAs.getText().toString() + " " +  getUserLogin());
        if (getUserRole().equals("Manager")) imageManager.setVisibility(View.VISIBLE);

        editChangePassword.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                password = editChangePassword.getText().toString();
                try {password = passwordHasher.generatePasswordHash(password); }
                catch (Exception ignored) {}
                databaseReference.child("Users").child(getUserLogin()).child("password").setValue(password);
                showToast("Пароль был успешно изменен");
                editChangePassword.setText("");
                return true;
            }
        });

        editAppReview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                databaseReference.child("Reviews").child("From " + getUserLogin()).setValue(editAppReview.getText().toString());
                showToast("Отзыв успешно отправлен");
                editAppReview.setText("");
                return true;
            }
        });

        editPrivateReminder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addPrivateReminder(editPrivateReminder.getText().toString() + "\n", Context.MODE_APPEND);
                showToast("Добавлено новое личное напоминание");
                editPrivateReminder.setText("");
                return true;
            }
        });

        textCheckPrivateReminders.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPrivateReminders();
                return true;
            }
        });

        textClearPrivateReminders.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addPrivateReminder("", Context.MODE_PRIVATE);
                showToast("Список личных напоминаний был очищен");
                return true;
            }
        });

        textAppInformation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fragmentSignOut.onShowInstruction();
                return true;
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSignOut.onSignOut();
            }
        });
    }

    private String getUserRole(){
        return getActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).getString(PREF_ROLE, "Worker");
    }

    private String getUserLogin(){
        return getActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).getString(PREF_WORKER_NAME, "");
    }

    private void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void checkPrivateReminders(){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = getActivity().openFileInput(FILE_NAME);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            String privateReminders = new String(bytes);
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme).create();
            alertDialog.setTitle("Личные напоминания");
            alertDialog.setMessage(privateReminders);
            alertDialog.show();
        }
        catch (Exception e) {
            showToast("Произошла ошибка!");
        }
        finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            }
            catch (Exception e) {
                showToast("Произошла ошибка!");
            }
        }
    }

    private void addPrivateReminder(String reminderToAdd, int mode){
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = getActivity().openFileOutput(FILE_NAME, mode);
            fileOutputStream.write(reminderToAdd.getBytes());
        }
        catch (Exception e){
            showToast("Произошла ошибка!");
        }
        finally {
            try {
                if (fileOutputStream!=null) fileOutputStream.close();
            }
            catch (Exception e){
                showToast("Произошла ошибка!");
            }
        }
    }
}
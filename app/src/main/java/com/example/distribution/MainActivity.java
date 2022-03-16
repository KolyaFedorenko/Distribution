package com.example.distribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_FILE = "Account";
    private static final String PREF_ROLE = "Worker";
    SharedPreferences sharedPreferences;

    String login, password, gettedLogin, gettedPassword, role;
    User gettedUser;

    DatabaseReference databaseReference;
    String DISTRIBUTION_KEY = "Users";

    EditText editLogin, editPassword;
    Button buttonSignUp, buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        editLogin = findViewById(R.id.editLogin);
        editPassword = findViewById(R.id.editPassword);

        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoginAndPassword();
                if (!login.equals("") && !password.equals("")) {
                    getFirebaseLoginAndPassword();
                    if (login.equals(gettedLogin) && !password.equals(gettedPassword)){
                        showToast("Incorrect password!");
                    }
                    if (login.equals(gettedLogin) && password.equals(gettedPassword)){
                        showToast("You have been signed in!");
                        setRole(role);
                        Intent intent = new Intent(getApplicationContext(), ManagerActivity.class);
                        startActivity(intent);
                    }
                }
                else {
                    showToast("One or more fields is empty");
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoginAndPassword();
                if (!login.equals("") && !password.equals("")) {
                    User user = new User(login, password, "Worker");
                    databaseReference.child(login).setValue(user);
                    showToast("You have been signed up!");
                    setRole("Worker");
                }
                else{
                    showToast("One or more fields is empty!");
                }
            }
        });
    }

    private void setRole(String role){
        sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_ROLE, role).apply();
    }

    private void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void getLoginAndPassword(){
        login = editLogin.getText().toString();
        password = editPassword.getText().toString();
    }

    private void getFirebaseLoginAndPassword(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    gettedUser = snapshot.child(login).getValue(User.class);
                    gettedLogin = gettedUser.login;
                    gettedPassword = gettedUser.password;
                    role = gettedUser.role;
                }
                catch (Exception e){
                    showToast("You don't signed up! Please sign up now!");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
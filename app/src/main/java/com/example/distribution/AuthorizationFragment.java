package com.example.distribution;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthorizationFragment extends Fragment {

    public interface OnFragmentSignIn{
        void onSignIn(String role, String name);
    }
    private OnFragmentSignIn fragmentSignIn;

    private String login = "", password, receivedLogin, receivedPassword, receivedRole;
    private User receivedUser;

    private DatabaseReference databaseReference;
    private String DISTRIBUTION_KEY = "Users";
    private boolean userExist = false;
    private PasswordHasher passwordHasher = new PasswordHasher();

    private EditText editLogin, editPassword;
    private Button buttonSignUp, buttonSignIn;

    public AuthorizationFragment() { }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentSignIn = (OnFragmentSignIn) context;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(), "Interface error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorization, container, false);

        buttonSignIn = view.findViewById(R.id.buttonSignIn);
        buttonSignUp = view.findViewById(R.id.buttonSignUp);
        editLogin = view.findViewById(R.id.editLogin);
        editPassword = view.findViewById(R.id.editPassword);

        databaseReference = FirebaseDatabase.getInstance().getReference(DISTRIBUTION_KEY);

        editLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) getFirebaseLoginAndPassword();
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoginAndPassword();
                if (!login.equals("") && !password.equals("")) {
                    try {
                        if (login.equals(receivedLogin) && !passwordHasher.validatePassword(password, receivedPassword)) {
                            showToast("Incorrect password!");
                        }
                        if (login.equals(receivedLogin) && passwordHasher.validatePassword(password, receivedPassword)) {
                            showToast("You have been signed in!");
                            fragmentSignIn.onSignIn(receivedRole, receivedLogin);
                        }
                    }
                    catch (Exception e) {
                        showToast("Password decrypt error");
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
                    if (!userExist) {
                        try {password = passwordHasher.generatePasswordHash(password); }
                        catch (Exception ignored) {}
                        User user = new User(login, password, "Worker");
                        databaseReference.child(login).setValue(user);
                        showToast("You have been signed up!");
                    }
                    else{
                        showToast("User with this name is already registered");
                    }
                }
                else{
                    showToast("One or more fields is empty!");
                }
            }
        });

        return view;
    }

    private void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void getLoginAndPassword(){
        login = editLogin.getText().toString();
        password = editPassword.getText().toString();
    }

    private void getFirebaseLoginAndPassword(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    receivedUser = snapshot.child(login).getValue(User.class);
                    receivedLogin = receivedUser.getLogin();
                    receivedPassword = receivedUser.getPassword();
                    receivedRole = receivedUser.getRole();
                    userExist = true;
                }
                catch (Exception e){
                    showToast("You don't signed up! Please sign up now!");
                    userExist = false;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }
}
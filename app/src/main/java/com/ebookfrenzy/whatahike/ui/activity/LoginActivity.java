package com.ebookfrenzy.whatahike.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            onAuthCompleted(User.getCurrentUser());
                        } else {
                            Exception exception = task.getException();
                            makeToast(exception.getMessage());
                        }
                    }
                });
    }

    private void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            onAuthCompleted(User.getCurrentUser());
                        } else {
                            Exception exception = task.getException();
                            makeToast(exception.getMessage());
                        }
                    }
                });
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void onAuthCompleted(User user) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void onClick(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        if (emailText.length() == 0) {
            Toast.makeText(this, "please input email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordText.length() == 0) {
            Toast.makeText(this, "please input password", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.loginBtn:
                login(emailText, passwordText);
                break;
            case R.id.registerBtn:
                register(emailText, passwordText);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
package com.kase.wallyapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kase.wallyapp.R;

public class EnterOtpActivity extends AppCompatActivity {

    String otp;
    Button otpSubmit;
    Context mContext;
    SharedPreferences sharedPref;
    EditText otpText;
    FirebaseAuth auth;
    String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        mContext = this;
        otpText = findViewById(R.id.otp);
        auth = FirebaseAuth.getInstance();
        otpText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                otpSubmit.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {
                otp = otpText.getText().toString();
                if(otp!=null){
                    if(!otp.isEmpty() && otp.length()==6){
                        otpSubmit.setEnabled(true);
                    }
                }else {
                    Log.e(getString(R.string.otp_validation), getString(R.string.otp_null));
                }

            }
        });




        sharedPref = getSharedPreferences(
                getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        verificationCode = sharedPref.getString("code","");


        otpSubmit = findViewById(R.id.otp_submit_button);

        otpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(otpSubmit.getWindowToken(), 0);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                signinWithPhone(credential);

            }
        });
    }

    private void signinWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent imageIntent = new Intent(mContext, ScrollingActivity.class);
                            startActivity(imageIntent);
                        } else {
                            Toast.makeText(EnterOtpActivity.this,R.string.wrong_otp,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

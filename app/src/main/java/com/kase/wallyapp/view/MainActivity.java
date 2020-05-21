package com.kase.wallyapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kase.wallyapp.util.Config;
import com.kase.wallyapp.R;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    String phoneNumber;
    EditText phoneNumberText;
    Button requestOtp;
    Context mContext;
    String countryCode;
    private FirebaseAuth mAuth;
    private Config  config = new Config();
    PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        mContext = this;
        phoneNumberText = findViewById(R.id.phone_number);
        requestOtp = findViewById(R.id.otp_request_button);
        phoneNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                requestOtp.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneNumber = phoneNumberText.getText().toString();
                Patterns.PHONE.matcher(phoneNumber).matches();
                if(!phoneNumber.isEmpty() && phoneNumber.length()==10){
                    setRequestOtp();
                }

            }
        });
    }

    private void setRequestOtp(){
        countryCode = ((TextView)findViewById(R.id.country_code)).getText().toString();
        phoneNumber = countryCode+phoneNumberText.getText().toString();
        requestOtp.setEnabled(true);
        requestOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(requestOtp.getWindowToken(), 0);
                showProgress();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        (Activity) mContext,
                        onVerificationStateChangedCallbacks

                );
            }
        });



        onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Intent imageIntent = new Intent(getApplicationContext(),EnterOtpActivity.class);
                imageIntent.putExtra("phone_creds",phoneAuthCredential);
                hideProgress();
                startActivity(imageIntent);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(mContext, "Verification Failed : "+e, Toast.LENGTH_LONG).show();
                hideProgress();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE).edit().putString("code", s).commit();
            }
        };
    }

    private void hideProgress() {
        if(mProgressDialog!=null){
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }
    }

    private void showProgress() {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle(getString(R.string.validating));
        mProgressDialog.setMessage(getString(R.string.wait));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }


}

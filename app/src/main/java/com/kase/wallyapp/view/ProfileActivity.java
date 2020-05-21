package com.kase.wallyapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kase.wallyapp.R;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    SharedPreferences sharedPref;
    private FirebaseAuth firebaseAuth;

    EditText nameText;
    EditText phoneNumber;
    EditText timeText;

    String name;
    String phone;
    String dob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        nameText = findViewById(R.id.profile_name);
        phoneNumber = findViewById(R.id.profile_phone);
        timeText = findViewById(R.id.time_text);
        getData();
    }

    private void getData() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        for(UserInfo userInfo : user.getProviderData()){


            if(userInfo!=null){
                System.out.println("name : "+userInfo.getDisplayName());
                System.out.println("phone : "+userInfo.getPhoneNumber());
                if(userInfo.getDisplayName()!=null){
                    name = userInfo.getDisplayName();
                }else{
                    name = "";
                }

                if(userInfo.getPhoneNumber()!=null){
                    phone = userInfo.getPhoneNumber();
                }else {
                    phone = "";
                }
            }

        }
        nameText.setText(name);
        phoneNumber.setText(phone);
    }
}

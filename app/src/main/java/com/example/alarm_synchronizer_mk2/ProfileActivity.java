package com.example.alarm_synchronizer_mk2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    EditText mName,mEmail,mMobileNo,mHostel,mRoomNo,mBranch;
    Button mSave;
    FirebaseAuth fAuth;
    ProgressBar mProgressBar;
    FirebaseFirestore fStore;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mName=findViewById(R.id.profileName);
        mEmail=findViewById(R.id.profileEmail);
        mMobileNo=findViewById(R.id.profileNumber);
        mHostel=findViewById(R.id.profileHostel);
        mRoomNo=findViewById(R.id.profileRoomNumber);
        mBranch=findViewById(R.id.profileBranch);
        mSave=findViewById(R.id.buttonSave);
        mProgressBar=findViewById(R.id.progressBarSave);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=mName.getText().toString();
                String email=mEmail.getText().toString();
                String branch=mBranch.getText().toString();
                String mobileno=mMobileNo.getText().toString();
                String hostel=mHostel.getText().toString();
                String roomno=mRoomNo.getText().toString();

                if(name.isEmpty()){
                    mName.setError("Name Is Required");
                    mName.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    mEmail.setError("Email Is Required");
                    mEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Enter Valid Email");
                    mEmail.requestFocus();
                    return;
                }
                if(branch.isEmpty()){
                    mName.setError("Branch Is Required");
                    mName.requestFocus();
                    return;
                }
                if(mobileno.isEmpty()){
                    mName.setError("Mobile Number Is Required");
                    mName.requestFocus();
                    return;
                }
                if(mobileno.length()!=10){
                    mName.setError("Invalid Mobile Number");
                    mName.requestFocus();
                    return;
                }
                if(hostel.isEmpty()){
                    mName.setError("Hostel Name Is Required");
                    mName.requestFocus();
                    return;
                }
                if(roomno.isEmpty()){
                    mName.setError("Room Number Is Required");
                    mName.requestFocus();
                    return;
                }
                if(roomno.length()<=0){
                    mName.setError("Room Number can't be negative");
                    mName.requestFocus();
                    return;
                }

                else{

                    mProgressBar.setVisibility(View.VISIBLE);

                    HashMap<String,Object> map=new HashMap<>();
                    map.put("UserName",name);
                    map.put("EmailID",email);
                    map.put("Branch",branch);
                    map.put("MobileNumber",mobileno);
                    map.put("Hostel",hostel);
                    map.put("RoomNo",roomno);

                    UserID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Users").document(UserID);
                    documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Success","onSuccess:Successful Storing"+UserID);
                            Toast.makeText(ProfileActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failed","Failed:"+e.toString());
                            Toast.makeText(ProfileActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

}


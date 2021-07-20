package com.example.alarm_synchronizer_mk2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView dashName, dashEmail, dashNumber,dashImageTitle;
    private ImageView dashImage;
    private Button mLogoutBtn;
    public Uri uri;
    FirebaseFirestore fStore;
    String UserID;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    Task<Uri> downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        mLogoutBtn = findViewById(R.id.logout_btn);
        dashName = findViewById(R.id.dashName);
        dashEmail = findViewById(R.id.dashEmail);
        dashNumber = findViewById(R.id.dashNumber);
        dashImage = findViewById(R.id.dashImage);
        dashImageTitle = findViewById(R.id.dashImageTitle);
        fStore = FirebaseFirestore.getInstance();

//        if(mCurrentUser!=null) {
//            Glide
//                    .with(MainActivity.this)
//                    .load(mCurrentUser.getPhotoUrl()).listener(new RequestListener<Drawable>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    return false;
//                }
//            })
//                    .into(dashImage);
//        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.getValue(String.class);
                Glide.with(MainActivity.this).load(imageUrl).into(dashImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadUserInformation();

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                sendUserToLogin();

            }
        });

        dashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,CHOOSE_IMAGE);
            }
        });


    }

    private void loadUserInformation() {
        if(mCurrentUser != null){

            UserID=mCurrentUser.getUid();
            final DocumentReference documentReference=fStore.collection("Users").document(UserID);

            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    dashName.setText(value.getString("UserName"));
                    dashNumber.setText(value.getString("MobileNumber"));
                    dashEmail.setText(value.getString("EmailID"));
                }
            });

//            StorageReference Ref = storageReference.child(System.currentTimeMillis()+"");
//            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Picasso.get().load(uri).into(dashImage);
//                }
//            });
//
//            if(myRef!=null){
//                myRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        if(uri!= null) {
//                            Glide.with(MainActivity.this)
//                                    .load(uri).listener(new RequestListener<Drawable>() {
//                                @Override
//                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                    return false;
//                                }
//
//                                @Override
//                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                    return false;
//                                }
//                            })
//                                    .into(dashImage);
//                        }
//                    }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(MainActivity.this, "Failed To Load", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                dashImageTitle.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
                uri=data.getData();

                dashImage.setImageURI(uri);
                dashImageTitle.setVisibility(View.GONE);

                uploadImageToFirebase();
        }
    }

//    private String getExtension(Uri uri){
//        ContentResolver cr = getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
//    }

    private void uploadImageToFirebase() {
        StorageReference Ref = storageReference.child(System.currentTimeMillis()+"");
        Ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        downloadUrl = Ref.getDownloadUrl();
//                        myRef = database.getReference("ProfileImage");
//                        myRef.setValue(downloadUrl);
                        Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser == null){
            sendUserToLogin();
        }
    }

    private void sendUserToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
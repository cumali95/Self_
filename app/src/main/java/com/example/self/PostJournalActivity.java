package com.example.self;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import model.Journal;
import util.JournalApi;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;
    private EditText titleEditText;
    private EditText thoughtsEditText;
    private TextView currenUserTextView;
    private ImageView imageview;


    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();
        progressBar = findViewById(R.id.post_progressbar);
        titleEditText = findViewById(R.id.post_title_et);
        thoughtsEditText = findViewById(R.id.post_thoughts_et);
        currenUserTextView = findViewById(R.id.post_username_textview);
        saveButton = findViewById(R.id.post_save_journal);
        addPhotoButton = findViewById(R.id.post_camera_imageview);
        imageview = findViewById(R.id.postimageview);

        getSupportActionBar().setElevation(0);

        addPhotoButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        progressBar.setVisibility(View.INVISIBLE);

        if (JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUsername();
            currenUserTextView.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {


                } else {


                }
            }
        };

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_save_journal:
                saveJournal();
                break;


            case R.id.post_camera_imageview:
                Intent galerryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galerryIntent.setType("image/*");
                startActivityForResult(galerryIntent, GALLERY_CODE);

                break;

        }

    }

    private void saveJournal() {
        final String title = titleEditText.getText().toString().trim();
        final String thoughts = thoughtsEditText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);


        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null) {

            final StorageReference filepath = storageReference
                    .child("journal_images")
                    .child("my_image_" + Timestamp.now().getSeconds());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressBar.setVisibility(View.INVISIBLE);
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            //create a journal object
                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThought(thoughts);
                            journal.setImageUrl(imageUrl);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setUserName(currentUserName);
                            journal.setUserId(currentUserId);

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostJournalActivity.this, JournalListActivity.class));
                                    finish();

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("hata3", "onFailure: " + e.getMessage());

                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("hata4", "onFailure: "+e.getMessage());

                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });


        } else {

            progressBar.setVisibility(View.INVISIBLE);

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                imageview.setImageURI(imageUri);

            }


        }
    }
}

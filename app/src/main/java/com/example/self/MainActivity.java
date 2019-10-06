package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import util.JournalApi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button getStartedButton;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentuser;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStartedButton=findViewById(R.id.startButton);
        getSupportActionBar().setElevation(0);

        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {

                currentuser=firebaseAuth.getCurrentUser();
                if(currentuser!=null)
                {
                    currentuser=firebaseAuth.getCurrentUser();
                    final String currentUserId=currentuser.getUid();
                     collectionReference.whereEqualTo("userId",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                         @Override
                         public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                             if(e!=null)
                             {
                                 return;

                             }
                             String name;
                             if (!queryDocumentSnapshots.isEmpty())
                             {
                                 for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots)
                                 {

                                     JournalApi journalApi=JournalApi.getInstance();
                                     journalApi.setUserId(snapshot.getString("userId"));
                                     journalApi.setUsername(snapshot.getString("username"));

                                     startActivity(new Intent(MainActivity.this, JournalListActivity.class
                                     ));
                                     finish();


                                 }

                             }

                         }
                     });

                }else
                {


                }
            }
        };

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);

        }
    }
}
    
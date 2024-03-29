package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import model.Journal;
import util.JournalApi;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Button createAccountButton;

    private AutoCompleteTextView email;
    private EditText password;
  private ProgressBar progressBar;

     private FirebaseAuth firebaseAuth;
     private FirebaseAuth.AuthStateListener authStateListener;
      private FirebaseUser currentUser;

      private  FirebaseFirestore db=FirebaseFirestore.getInstance();
      private CollectionReference  collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);

        firebaseAuth=FirebaseAuth.getInstance();


        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

          progressBar=findViewById(R.id.login_progress);

        loginButton=findViewById(R.id.email_sign_in_button);
        createAccountButton=findViewById(R.id.create_account_button_login);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreatAccountActivity.class));
            }
        });

         loginButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 loginEmailPasswordUser(email.getText().toString().trim(),password.getText().toString().trim());
             }
         });

    }

    private void loginEmailPasswordUser(String email,String psw)
    {
           progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(psw))
        {
            firebaseAuth.signInWithEmailAndPassword(email,psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    assert user!=null;
                    final String currentUserId=user.getUid();


                    collectionReference.whereEqualTo("userId",currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                    if (e!=null)
                                    {
                                       return;

                                    }
                                    if (!queryDocumentSnapshots.isEmpty())
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots)
                                        {
                                            JournalApi journalApi=JournalApi.getInstance();
                                            journalApi.setUsername(snapshot.getString("username"));
                                            journalApi.setUserId(snapshot.getString("userId"));

                                            startActivity(new Intent(LoginActivity.this,PostJournalActivity.class));

                                        }

                                    }



                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });

        }else
        {
            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(LoginActivity.this,"Please enter email or password",Toast.LENGTH_LONG).show();

        }

    }


}

package com.example.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import util.JournalApi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreatAccountActivity extends AppCompatActivity {

    private Button loginButton;
    private Button createAcctButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //FireStore connection
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");

    private EditText emailEditText;
    private  EditText passwordEditText;
    private ProgressBar progressBar;
    private Button createAccountButton;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_account);
        getSupportActionBar().setElevation(0);

        firebaseAuth=FirebaseAuth.getInstance();

        createAccountButton=findViewById(R.id.create_account_button);
        progressBar=findViewById(R.id.create_acct_progress);
        emailEditText=findViewById(R.id.email_account);
        passwordEditText=findViewById(R.id.password_account);
        userNameEditText=findViewById(R.id.username_account);
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser=firebaseAuth.getCurrentUser();
                if(currentUser!=null)
                {

                }else
                {

                }
            }
        };


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(emailEditText.getText().toString()) &&
                        !TextUtils.isEmpty(passwordEditText.getText().toString()) &&
                        !TextUtils.isEmpty(userNameEditText.getText().toString())) {
                      String email=emailEditText.getText().toString();
                      String password = passwordEditText.getText().toString();
                      String username=userNameEditText.getText().toString();

                    createUserEmailAccount(email, password, username);
                }else
                {
                    Toast.makeText(CreatAccountActivity.this,"  Empty Fields not Allowed",Toast.LENGTH_LONG).show();


                }
            }
        });





    }
    private  void  createUserEmailAccount(String email, String password, final String username)
    {
           if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(username))
           {
               progressBar.setVisibility(View.VISIBLE);
               firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful())
                       {
                           currentUser=firebaseAuth.getCurrentUser();
                           final String currentUserId=currentUser.getUid();

                           Map<String,String> userObj=new HashMap<>();
                           userObj.put("userId",currentUserId);
                           userObj.put("username",username);

                           collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                               @Override
                               public void onSuccess(DocumentReference documentReference)
                               {

                                   documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                           if(Objects.requireNonNull(task.getResult()).exists())
                                           {

                                                progressBar.setVisibility(View.INVISIBLE);
                                                String name=task.getResult().getString("username");

                                               JournalApi journalApi=JournalApi.getInstance();
                                               journalApi.setUserId(currentUserId);
                                               journalApi.setUsername(name);


                                               Intent intent=new Intent(CreatAccountActivity.this,PostJournalActivity.class);
                                               intent.putExtra("username",name);
                                               intent.putExtra("userId",currentUserId);
                                               startActivity(intent);

                                           }else
                                           {
                                               progressBar.setVisibility(View.INVISIBLE);

                                           }
                                       }
                                   });

                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Log.d("hatasuc", "onFailure: +"+ e);

                               }
                           });


                       }else
                       {

                       }

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d("hata2", "onFailure: "+ e);

                   }
               });
           }
           else
           {


           }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}

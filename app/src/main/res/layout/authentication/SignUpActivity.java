package com.carusoft.squashmind.ui.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.carusoft.squashmind.MainActivity;
import com.carusoft.squashmind.R;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SignUpActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressBar progressBar;

    CFAlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                // Launch Sign In
                progressBar.setVisibility(View.VISIBLE);
                signInToGoogle();
            }
        });
        // Configure Google Client
        configureGoogleClient();

        Button signin = (Button) findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(start);
            }
        });

        Button signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                TextView emailTxt = (TextView) findViewById(R.id.email);
                TextView passwordTxt = (TextView) findViewById(R.id.password);
                TextView nameTxt = (TextView) findViewById(R.id.name);
                TextView cityTxt = (TextView) findViewById(R.id.city);
                String email = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                String name = nameTxt.getText().toString();
                String city = cityTxt.getText().toString();

                if (!(emailTxt.getText().equals("")) && !(passwordTxt.getText().equals(""))  && !(nameTxt.getText().equals(""))  && !(cityTxt.getText().equals(""))){
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                    prefsEditor.putString("name", name);
                                    prefsEditor.putString("email", email);
                                    prefsEditor.putString("city", city);

                                    Boolean commited = prefsEditor.commit();
                                    Log.d("CCOMMITTED", String.valueOf(commited));
                                    progressBar.setVisibility(View.GONE);

                                    ClassUser usuario = new ClassUser();
                                    usuario.setName(name);
                                    usuario.setPlatform("Android");
                                    usuario.setEmail(email);
                                    usuario.setCity(city);

                                    String currentDate = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault()).format(new Date());
                                    usuario.setCreated(currentDate);

                                    mDatabase.child("users").child(user.getUid()).setValue(usuario);
                                    Intent start = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(start);


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("FAIL", "createUserWithEmail:failure", task.getException());
                                    progressBar.setVisibility(View.GONE);
                                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignUpActivity.this)
                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                            .setTitle("Error")
                                            .setMessage(task.getException().getMessage())
                                            .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                                dialog.dismiss();
                                            });
                                    alertDialog = builder.show();

                                }


                            }
                        });
                }else {
                    progressBar.setVisibility(View.GONE);
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignUpActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Enter All Fields")
                            .setMessage("Please enter all fields")
                            .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                            });
                    alertDialog = builder.show();

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Log.d("PRESS BACK","PRESS BACK");
        return;

    }

 // START GOOGLE SIGN IN PROCEESS - TUTORIAL: https://stackoverflow.com/questions/47437678/why-do-i-get-com-google-android-gms-common-api-apiexception-10
    private void configureGoogleClient() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }



    public void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //showToastMessage("Google Sign in Succeeded");

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                showToastMessage("Google Sign in Failed " + e);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d("TAG", "signInWithCredential:success: currentUser: " + user.getEmail());
                            //showToastMessage("Firebase Authentication Succeeded ");
                            launchMainActivity(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            showToastMessage("Firebase Authentication failed:" + task.getException());
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void showToastMessage(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
    }
    private void launchMainActivity(FirebaseUser user) {

        if (user != null) {



            Query dbQuery = mDatabase.child("users").child(user.getUid());
            dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    if (dataSnapshot.exists()){
                        ClassUser user = dataSnapshot.getValue(ClassUser.class);

                        SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();

                        prefsEditor.putString("name", user.getName());
                        prefsEditor.putString("email", user.getEmail());
                        prefsEditor.putString("city", user.getCity());
                        prefsEditor.putString("created", user.getCreated());

                        prefsEditor.commit();

                        Intent start = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(start);

                        progressBar.setVisibility(View.GONE);
                    }else{
                        FirebaseUser user = mAuth.getCurrentUser();
                        SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();

                        prefsEditor.putString("name", user.getDisplayName());
                        prefsEditor.putString("email", user.getEmail());

                        Boolean commited = prefsEditor.commit();
                        Log.d("CCOMMITTED", String.valueOf(commited));
                        progressBar.setVisibility(View.GONE);

                        ClassUser usuario = new ClassUser();
                        usuario.setName(user.getDisplayName());
                        usuario.setPlatform("Android");
                        usuario.setEmail(user.getEmail());
                        String currentDate = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault()).format(new Date());
                        usuario.setCreated(currentDate);

                        mDatabase.child("users").child(user.getUid()).setValue(usuario);
                        Intent start = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(start);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ERROR", "Error while reading data");
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }



}

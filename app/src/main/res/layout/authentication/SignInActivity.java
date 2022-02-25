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
import android.widget.ProgressBar;
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
import com.github.ybq.android.spinkit.style.Wave;
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


public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressBar progressBar;

    CFAlertDialog alertDialog;

    private static final int RC_SIGN_IN = 1001;
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

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

        Button signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent start = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(start);
            }
        });

        Button forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.email);
                if (!email.getText().toString().equals("")){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignInActivity.this)
                                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                .setTitle("Email sent")
                                                .setMessage("Please check your INBOX for instructions")
                                                .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                                    dialog.dismiss();
                                                });
                                        alertDialog = builder.show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignInActivity.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                    .setTitle("Problem")
                                    .setMessage(e.getMessage().toString())
                                    .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                        dialog.dismiss();
                                    });
                            alertDialog = builder.show();
                        }
                    });
                }else{
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignInActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Atention")
                            .setMessage("Please enter your Email.")
                            .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                            });
                    alertDialog = builder.show();
                }


            }
        });


        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("login", "login");
                EditText email = findViewById(R.id.email);
                EditText password = findViewById(R.id.password);
                Sprite wanderingCubes = new Wave();

                progressBar.setIndeterminateDrawable(wanderingCubes);
                progressBar.setVisibility(View.VISIBLE);

                if (!(email.getText().equals("")) && !(password.getText().equals(""))){

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("SUCCESS", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        final String userId = user.getUid();
                                        Log.d("USER ID",userId);

                                        Query dbQuery = mDatabase.child("users").child(userId);
                                        dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ClassUser user = dataSnapshot.getValue(ClassUser.class);
                                                Log.d("userSnapshot", String.valueOf(user));


                                                SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
                                                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                                prefsEditor.putString("name", user.getName());
                                                prefsEditor.putString("email", user.getEmail());
                                                prefsEditor.putString("city", user.getCity());
                                                prefsEditor.putString("created", user.getCreated());


                                                Boolean commited = prefsEditor.commit();

                                                progressBar.setVisibility(View.GONE);
                                                Intent start = new Intent(SignInActivity.this, MainActivity.class);
                                                startActivity(start);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("ERROR", "Error while reading data");
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });

                                    } else {
                                        Log.w("FAIL", "signInWithEmail:failure", task.getException());
                                        progressBar.setVisibility(View.GONE);
                                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignInActivity.this)
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
                }else{
                    progressBar.setVisibility(View.GONE);
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignInActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Enter All Fields")
                            .setMessage("Please enter email and password.")
                            .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                            });
                    alertDialog = builder.show();

                }
            }
        });
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


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Log.d("PRESS BACK","PRESS BACK");
        return;

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
        progressBar.setVisibility(View.GONE);
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_LONG).show();
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

                        Intent start = new Intent(SignInActivity.this, MainActivity.class);
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
                        Intent start = new Intent(SignInActivity.this, MainActivity.class);
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

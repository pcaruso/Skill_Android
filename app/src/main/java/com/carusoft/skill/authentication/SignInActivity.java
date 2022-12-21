package com.carusoft.skill.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carusoft.skill.ClassNegocio;
import com.carusoft.skill.HomeActivity;
import com.carusoft.skill.NewProduct;
import com.carusoft.skill.NewPurchase;
import com.carusoft.skill.R;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.sprite.Sprite;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private LottieAnimationView loader;

    CFAlertDialog alertDialog;

    private static final int RC_SIGN_IN = 1001;
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private RelativeLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Button contacto = (Button) findViewById(R.id.contacto);
        contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    /*Intent intent = new Intent(Intent.ACTION_MAIN);
                    Uri data = Uri.parse("mailto:panelskill@gmail.com");
                    intent.setData(data);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    startActivity(intent);*/

                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "panelskill@gmail.com"});
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, ""));

                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(SignInActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("login", "login");
                EditText email = findViewById(R.id.email);
                EditText password = findViewById(R.id.password);
                startLoader();

                if (!(email.getText().equals("")) && !(password.getText().equals(""))){


                    String packagesUrl = "http://skill-ca.com/api/login.php";

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.getCache().clear();
                    //startLoader();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("RESPONSE", response);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(response);
                                stopLoader();



                                if (json.getInt("code") == 1) {
                                    JSONObject resultado = json.getJSONObject("result");

                                    SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    prefsEditor.putString("idHogar", String.valueOf(resultado.get("idHogar")));
                                    prefsEditor.putString("grupo", String.valueOf(resultado.get("grupo")));
                                    prefsEditor.putString("estado", String.valueOf(resultado.get("estado")));
                                    prefsEditor.putString("municipio", String.valueOf(resultado.get("municipio")));
                                    prefsEditor.putString("ciudad", String.valueOf(resultado.get("ciudad")));

                                    Boolean commited = prefsEditor.commit();
                                    Intent start = new Intent(SignInActivity.this, NewPurchase.class);
                                    startActivity(start);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {


                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d("VOLLey error", volleyError.getMessage());
                            if (volleyError instanceof TimeoutError) {
                                stopLoader();
                            }
                        }
                    }) {
                        @Override
                        public Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("usuario", email.getText().toString());
                            params.put("password", password.getText().toString());

                            return params;
                        }

                        @Override
                        public Request.Priority getPriority() {
                            return Request.Priority.IMMEDIATE;
                        }
                    };

                    stringRequest.setShouldCache(false);
                    queue.add(stringRequest);

                }else{
                    stopLoader();

                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SignInActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Atención")
                            .setMessage("Por favor ingrese todos los campos.")
                            .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                            });
                    alertDialog = builder.show();

                }
            }
        });
    }

    private void startLoader() {
        loader = findViewById(R.id.animation_view);
        overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        loader.setRenderMode(RenderMode.HARDWARE);
    }

    private void stopLoader(){
        loader.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Log.d("PRESS BACK","PRESS BACK");
        return;

    }



}

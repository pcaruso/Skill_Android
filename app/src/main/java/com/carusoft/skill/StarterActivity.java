package com.carusoft.skill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.carusoft.skill.authentication.SignInActivity;

public class StarterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                setTheme(R.style.AppTheme);

                ActionBar actionBar = getSupportActionBar();
                actionBar.hide();

                SharedPreferences mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                String idHogar = mPrefs.getString("idHogar", "");
                if (!idHogar.equals("")) {
                    Intent i = new Intent(getBaseContext(), NewPurchase.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(getBaseContext(), SignInActivity.class);
                    startActivity(i);
                }
            }
        }, 5000);



    }
}

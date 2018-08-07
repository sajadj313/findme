package com.cubosystems.findme;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.LocaleList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.cubosystems.findme.Models.Helper;

import java.io.File;
import java.util.Locale;

public class ActivityLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checking if the registration file is existing
        String ff = getFilesDir().getAbsolutePath() + "/" + Helper.USER_INFO_FILE_NAME;
        File file = new File(ff);

        if(file.exists()){
            //if the user file is existing, then redirect to the main app activity
            startActivity(new Intent(ActivityLauncher.this,MainActivity.class));
        }else{
            //if the file is not existing, then redirect to the registration page
            startActivity(new Intent(ActivityLauncher.this,RegistrationActivity.class));
        }


        //setContentView(R.layout.activity_launcher);
    }
}

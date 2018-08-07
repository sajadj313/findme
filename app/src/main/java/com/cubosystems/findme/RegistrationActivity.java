package com.cubosystems.findme;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cubosystems.findme.Models.Guard;
import com.cubosystems.findme.Models.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {
    Button btnRegister;
    EditText txtName, txtContact;
    ArrayList<CheckBox> languagesCheckBoxesList;
    CheckBox chk1, chk2, chk3, chk4, chk5, chk6;
    LinearLayout linearLayout;
    RadioButton radioButton1, radioButton2;
    RadioGroup appLanguageRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLocale(Helper.getAppLang(RegistrationActivity.this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        radioButton1 = (RadioButton)findViewById(R.id.registration_app_lang_en);
        radioButton2 = (RadioButton)findViewById(R.id.registration_app_lang_ar);
        appLanguageRadioGroup = (RadioGroup)findViewById(R.id.language_radio_group1);

        //setting up the spoken languages
        languagesCheckBoxesList = new ArrayList<>(6);
        chk1 = new CheckBox(RegistrationActivity.this);
        chk1.setText("English");
        chk1.setChecked(true);
        chk2 = new CheckBox(RegistrationActivity.this);
        chk2.setText("Arabic");
        chk2.setChecked(true);
        chk3 = new CheckBox(RegistrationActivity.this);
        chk3.setText("Hindi");
        chk4 = new CheckBox(RegistrationActivity.this);
        chk4.setText("Spanish");
        chk5 = new CheckBox(RegistrationActivity.this);
        chk5.setText("Urdu");
        chk6 = new CheckBox(RegistrationActivity.this);
        chk6.setText("French");

        languagesCheckBoxesList.add(chk1);
        languagesCheckBoxesList.add(chk2);
        languagesCheckBoxesList.add(chk3);
        languagesCheckBoxesList.add(chk4);
        languagesCheckBoxesList.add(chk5);
        languagesCheckBoxesList.add(chk6);

        linearLayout = (LinearLayout)findViewById(R.id.registration_spoken_lang_area);
        linearLayout.removeAllViews();
        for(CheckBox c : languagesCheckBoxesList){
            linearLayout.addView(c);
        }
        btnRegister = (Button)findViewById(R.id.register_btn);
        txtName = (EditText)findViewById(R.id.register_name);
        txtContact = (EditText)findViewById(R.id.register_phone);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a model for the guard and fetching details
                Guard guard = new Guard();
                guard.setName(txtName.getText().toString().trim());
                guard.setAppLang(radioButton1.isChecked() ? radioButton1.getText().toString() : radioButton2.getText().toString());
                guard.setSpokenLangs(getSpokenLanguages(languagesCheckBoxesList));
                guard.setPhoneNo(txtContact.getText().toString().trim());

                //performing database tasks
                final String guardID = Helper.generateID("guard");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("Guards").child(guardID).setValue(guard).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //storing the guard ID in the file
                        if(Helper.storeIDInFile(guardID,RegistrationActivity.this)){
                            Toast.makeText(RegistrationActivity.this, R.string.Add_Guard_Success, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                        }else{
                            Toast.makeText(RegistrationActivity.this, "unexpected error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });


            }
        });

        showLanguagePreferenceDialogBox();
    }

    private String getSpokenLanguages(List<CheckBox> checkBoxList){
        StringBuilder sb = new StringBuilder();
        for(CheckBox c : checkBoxList){
            if(c.isChecked()){
                sb.append(c.getText() + ",");
            }
        }

        return sb.toString().substring(0,sb.toString().length()-1);
    }

    private void showLanguagePreferenceDialogBox(){
        //if the user has a lang file, then show an alert dialog. otherwise dont show
        if(Helper.getAppLang(RegistrationActivity.this) == ""){
            AlertDialog alertDialog = new AlertDialog.Builder(RegistrationActivity.this)
                    .setTitle("Choose the app language\nاختر لغة التطبيق")
                    .setCancelable(false)
                    .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            RadioButton r1;
                            r1 = (RadioButton)((AlertDialog)dialogInterface).findViewById(R.id.language_dialog_radio1);

                            String selectedLanguage = r1.isChecked() ? "en" : "ar";
                            setLocale(selectedLanguage);
                            Helper.setAppLang(RegistrationActivity.this,selectedLanguage);
                            dialogInterface.dismiss();
                            finish();
                            startActivity(new Intent(RegistrationActivity.this,RegistrationActivity.class));
                        }
                    })
                    .create();


            View view = getLayoutInflater().inflate(R.layout.app_language_dialog,null);
            alertDialog.setView(view);
            alertDialog.show();
        }

    }

    private void setLocale(String lang){
        Locale myLocale = new Locale(lang);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = myLocale;
        resources.updateConfiguration(configuration,displayMetrics);

    }
}

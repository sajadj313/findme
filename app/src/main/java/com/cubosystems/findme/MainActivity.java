package com.cubosystems.findme;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cubosystems.findme.Helper.BroadcastModel;
import com.cubosystems.findme.Helper.CameraView;
import com.cubosystems.findme.Helper.FindMeService;
import com.cubosystems.findme.Models.Guard;
import com.cubosystems.findme.Models.Helper;
import com.cubosystems.findme.Models.LostPerson;
import com.cubosystems.findme.Models.MyAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    EditText txtPersonName, txtContactNumber, txtCountry, txtGroupName;
    Spinner spnnrStationName;
    SurfaceView cameraPreview;
    Button btnLang;
    final ArrayList<LostPerson> arr = new ArrayList<>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    RelativeLayout lytExitSearchMode;
    Uri imageUri;

    //QR Code related object creations
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lytExitSearchMode = (RelativeLayout)findViewById(R.id.main_l_layout2);

        lytExitSearchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSearchTerm();
            }
        });

        startService(new Intent(MainActivity.this, FindMeService.class));
        Log.d("USER ID","service started");
        Log.d("USER ID",Helper.getGuardID(MainActivity.this));

        Button btnLost = (Button) findViewById(R.id.main_btn_lost);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        /*LostPerson lp = new LostPerson();
        lp.setPersonName("Sajad Jaward");
        lp.setStationName("Station B");
        lp.setContactPerson("0766674770");
        arr.add(lp);*/
        adapter = new MyAdapter(arr, MainActivity.this);
        recyclerView.setAdapter(adapter);

        database.getReference("LostPeople").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LostPerson lp = dataSnapshot.getValue(LostPerson.class);
                if(!lp.getStatus().equals("found")){
                    arr.add(lp);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /*//Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                LostPerson lostPerson = dataSnapshot.getValue(LostPerson.class);
                if(lostPerson.getStatus().equals("found")){
                    //loop through data set and remove the item
                    int i=0;
                    for(LostPerson lp : arr){
                        i++;
                        if(lp.getId().equals(lostPerson.getId())){
                            arr.remove(lostPerson);
                            adapter.notifyItemRemoved(i-1);
                            break;
                        }
                    }
                    arr.remove(lostPerson);
                    adapter.notifyDataSetChanged();
                }else{
                    arr.add(lostPerson);
                    adapter.notifyDataSetChanged();
                }*/
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        /*.setCancelable(false)*/
                        .create();

                View v = getLayoutInflater().inflate(R.layout.lost_person_layout, null);
                dialog.setView(v);
                dialog.show();

                txtPersonName = (EditText) dialog.findViewById(R.id.lost_person_name);
                spnnrStationName = (Spinner) dialog.findViewById(R.id.lost_person_station_name);
                txtContactNumber = (EditText) dialog.findViewById(R.id.lost_person_contact_person);
                txtCountry = (EditText) dialog.findViewById(R.id.lost_person_country);
                txtGroupName = (EditText) dialog.findViewById(R.id.lost_person_group_name);

                barcodeDetector = new BarcodeDetector.Builder(MainActivity.this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
                cameraSource = new CameraSource.Builder(MainActivity.this, barcodeDetector)
                        .setAutoFocusEnabled(true)
                        .setRequestedPreviewSize(144, 176)
                        .build();

                //getting the reference to the surface view
                cameraPreview = (SurfaceView) dialog.findViewById(R.id.lost_person_camera);
                cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder surfaceHolder) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        try{
                            cameraSource.start(cameraPreview.getHolder());
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                        cameraSource.stop();
                    }
                });

                barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                    @Override
                    public void release() {

                    }

                    @Override
                    public void receiveDetections(Detector.Detections<Barcode> detections) {
                        final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                        if(qrcodes.size() > 0){
                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    String qrCodeDisplayValue = qrcodes.valueAt(0).displayValue;
                                    cameraSource.stop();

                                    txtPersonName.setText(qrCodeDisplayValue.split("\n")[0].split(":")[1].trim());
                                    txtContactNumber.setText(qrCodeDisplayValue.split("\n")[1].split(":")[1].trim());
                                    txtCountry.setText(qrCodeDisplayValue.split("\n")[4].split(":")[1].trim());
                                    txtGroupName.setText(qrCodeDisplayValue.split("\n")[9].split(":")[1].trim());
                                }
                            });
                        }
                    }
                });

                Button b = (Button)dialog.findViewById(R.id.btn_lost_person_done);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uniqueID = Helper.generateID("person");

                        LostPerson lp = new LostPerson();
                        lp.setId(uniqueID);
                        lp.setPersonName(txtPersonName.getText().toString().trim());
                        lp.setStationName(spnnrStationName.getSelectedItem().toString().trim());
                        lp.setContactPerson(txtContactNumber.getText().toString().trim());
                        lp.setGuardID(Helper.getGuardID(MainActivity.this));
                        lp.setLostTime(new Date().getTime());
                        lp.setStatus("not found");

                        //database stuff
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference("LostPeople").child(uniqueID).setValue(lp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
        });

        btnLang = (Button)findViewById(R.id.main_btn_changeLang);
        btnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting a list of guard IDs
                final ArrayList<String> guards = new ArrayList<String>();
                final String thisGuardID = Helper.getGuardID(MainActivity.this);

                database.getReference("Guards").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                if(!ds.getKey().equals(thisGuardID)){
                                    guards.add(ds.getKey());
                                }
                            }

                            //sending broadcast for each guard
                            BroadcastModel broadcastModel = new BroadcastModel();
                            for(String s:guards){
                                broadcastModel.setLostPersonID("123wqe");
                                broadcastModel.setGuardID(thisGuardID);
                                broadcastModel.setHasOpened(false);
                                database.getReference("Broadcast").child(s).push().setValue(broadcastModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item1:
                //Toast.makeText(this, "menu item 1", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(),"sajad.jpg");
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                imageUri = Uri.fromFile(file);
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),999);
                return true;
            case R.id.main_menu_item2:
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                return true;
            case R.id.main_menu_item3:
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Enter your search term")
                        .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //making the exit search mode text visible
                                lytExitSearchMode.setVisibility(View.VISIBLE);

                                //search for the term
                                final EditText txtSearch = (EditText)((AlertDialog)dialogInterface).findViewById(R.id.search_txt);
                                database.getReference("LostPeople").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChildren()){
                                            ArrayList<LostPerson> lostPeopleArrayList = new ArrayList<LostPerson>();
                                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                                lostPeopleArrayList.add(ds.getValue(LostPerson.class));
                                            }

                                            //arr.clear();

                                            ArrayList<LostPerson> searchResultArrayList = new ArrayList<LostPerson>();
                                            String searchTerm = txtSearch.getText().toString().trim();
                                            for(LostPerson lp : lostPeopleArrayList){
                                                if(!lp.getStatus().toLowerCase().equals("found") &&  (lp.getPersonName().toLowerCase().contains(searchTerm) || lp.getContactPerson().contains(searchTerm) || lp.getStationName().toLowerCase().contains(searchTerm))){
                                                    searchResultArrayList.add(lp);
                                                }
                                            }
                                            //adapter = new MyAdapter()
                                            RecyclerView.Adapter adapter1 = new MyAdapter(searchResultArrayList,MainActivity.this);
                                            recyclerView.swapAdapter(adapter1,false);
                                            //adapter.notifyDataSetChanged();
                                        }else{
                                            Toast.makeText(MainActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetSearchTerm();
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                View view = getLayoutInflater().inflate(R.layout.search_dialog,null);
                alertDialog.setView(view);
                alertDialog.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999 && resultCode == RESULT_OK){
            Bitmap image = (Bitmap)data.getExtras().get("data");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,bos);
            byte[] imageByteArray = bos.toByteArray();

            InputStream is = new ByteArrayInputStream(imageByteArray);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference("images").child("img.jpg").putStream(is).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void resetSearchTerm(){
        lytExitSearchMode.setVisibility(View.GONE);
        recyclerView.swapAdapter(adapter,false);
    }


}

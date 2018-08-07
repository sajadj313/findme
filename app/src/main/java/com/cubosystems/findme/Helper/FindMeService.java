package com.cubosystems.findme.Helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.cubosystems.findme.Models.Helper;
import com.cubosystems.findme.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Administrator on 7/27/2018.
 */

public class FindMeService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Getting the database reference to the guard ID under the Broadcast node
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Broadcast").child(Helper.getGuardID(getApplicationContext())).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                Notification notification = new Notification.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                        .setContentText("Broadcast message")
                        .setSubText("content")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("You have a broadcast message")
                        .build();

                manager.notify(100,notification);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

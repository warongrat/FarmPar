package project.farmpar.Notifications.FiSho;

/**
 * Created by best on 7/11/2560.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import project.farmpar.MainActivity;
import project.farmpar.R;

public class Notification_AFood extends Service {
    private DatabaseReference myRef;
    private int notification_id;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private String secret;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MyActivity", "In the FirstPlant service");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idc = prefs.getString("IDC", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(idc).child("Fish").child("FeedSet");
        myRef.keepSynced(true);
        myRef.orderByValue().limitToLast(1);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);

        notification_id = (int) System.currentTimeMillis();

        Intent notification_intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification_intent, 0);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_small)
                .setContentTitle("Auto Feed")
                .setContentText("System : Success")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(Notification.PRIORITY_MAX);


        Map<String, Object> sec = new HashMap<String, Object>();
        sec.put("Secret", "0");
        myRef.updateChildren(sec);

        //notificationManager.notify(notification_id, builder.build());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = new HashMap<String, Object>();
                String notification = dataSnapshot.child("Notification").getValue(String.class);
                String alert1 = dataSnapshot.child("Alert1").getValue(String.class);
                String alert2 = dataSnapshot.child("Alert2").getValue(String.class);
                String alert3 = dataSnapshot.child("Alert3").getValue(String.class);
                secret = dataSnapshot.child("Secret").getValue(String.class);
                if ((alert1.equals("Enable")||(alert2.equals("Enable")) || alert3.equals("Enable")) && secret.equals("0")) {
                    value.put("Status", "Enable");
                    myRef.updateChildren(value);

                }
                if (notification.equals("Enable")) {
                    notificationManager.notify(notification_id, builder.build());
                    value.put("Secret", "1");
                    myRef.updateChildren(value);
                    //System.exit(0);
                } else ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed
    }

}
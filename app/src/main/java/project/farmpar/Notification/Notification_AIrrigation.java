package project.farmpar.Notification;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import project.farmpar.MainActivity;
import project.farmpar.R;

/**
 * Created by waron on 12/9/2560.
 */

public class Notification_AIrrigation extends Service {
    private DatabaseReference myRef;
    private int notification_id;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idc = prefs.getString("IDC", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(idc).child("AutoIrrigation");
        myRef.keepSynced(true);
        myRef.orderByValue().limitToLast(1);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);

        notification_id = (int) System.currentTimeMillis();

        Intent notification_intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification_intent, 0);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_small)
                .setContentTitle("Auto Irrigation")
                .setContentText("System : Disable")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(Notification.PRIORITY_MAX);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String value = "Disable";
                value = String.valueOf(map.get("Status"));
                String notification = dataSnapshot.child("Notification").getValue(String.class);
                if (notification.equals("Enable"))
                    notificationManager.notify(notification_id, builder.build());
                if (notification.equals("Disable")) ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed
    }
}

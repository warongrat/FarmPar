package project.farmpar.FirstPlant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import project.farmpar.Notifications.FirstPlant.Notification_AFertilization;
import project.farmpar.R;
import project.farmpar.Service.AlarmReceiverPlant;

public class SetTimeFragment extends Fragment {
    private TimePicker timePicker;
    private Button start, start2, stop;
    private DatabaseReference myRef;
    private TextView tv;
    AlarmManager alarmManager;
    private PendingIntent pending_intent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String idc = prefs.getString("IDC", "");
        View view = inflater.inflate(R.layout.fragment_set_time, container, false);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        start = (Button) view.findViewById(R.id.start);
        start2 = (Button) view.findViewById(R.id.alarm2);
        stop = (Button) view.findViewById(R.id.stop);
        tv = (TextView) view.findViewById(R.id.TextViewSetTime);
        getActivity().setTitle("Set Time Irrigation");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(idc).child("SetTime");
        myRef.keepSynced(true);
        myRef.orderByValue().limitToLast(1);
        final Intent myIntent = new Intent(getActivity(), AlarmReceiverPlant.class);
        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        timePicker.setIs24HourView(true);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String value, value1, value2;
                value = String.valueOf(map.get("Alert"));
                value2 = String.valueOf(map.get("Alert2"));
                value1 = String.valueOf(map.get("Notification"));
                if ((value.equals("Enable")) || value2.equals("Enable"))
                    tv.setText(getResources().getString(R.string.Status) + ":" + getResources().getString(R.string.Timer));
                if (value.equals("Disable"))
                    tv.setText(getResources().getString(R.string.Status) + " : " + getResources().getString(R.string.Disable));
                if (value.equals("Disable") && value1.equals("Enable")) {
                    alarmManager.cancel(pending_intent);
                    getActivity().stopService(new Intent(getActivity(), Notification_AFertilization.class));
                    tv.setText(getResources().getString(R.string.Status) + " : " + getResources().getString(R.string.Disable));
                } else ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();

                        calendar.add(Calendar.SECOND, 3);
                        final int hour = timePicker.getCurrentHour();
                        final int minute = timePicker.getCurrentMinute();
                        Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        pending_intent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        value.put("Alert", "Enable");
                        value.put("Secret", "1");
                        myRef.updateChildren(value);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 24 * 60 * 60, pending_intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();

                        calendar.add(Calendar.SECOND, 3);
                        final int hour = timePicker.getCurrentHour();
                        final int minute = timePicker.getCurrentMinute();
                        Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                        pending_intent = PendingIntent.getBroadcast(getActivity(), 1, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        value.put("Alert2", "Enable");
                        value.put("Secret", "1");
                        myRef.updateChildren(value);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 24 * 60 * 60, pending_intent);
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmManager.cancel(pending_intent);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();
                        value.put("Secret", "0");
                        value.put("Alert", "Disable");
                        value.put("Alert2", "Disable");
                        myRef.updateChildren(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

}
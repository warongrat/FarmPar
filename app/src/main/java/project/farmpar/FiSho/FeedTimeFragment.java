package project.farmpar.FiSho;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import project.farmpar.Notifications.FiSho.Notification_AFood;
import project.farmpar.R;
import project.farmpar.Service.AlarmReceiverFish;

/**
 * Created by best on 5/4/2560.
 */

public class FeedTimeFragment extends Fragment {
    private TimePicker timefood;
    private Button start1,start2,start3,stop;
    private EditText fm;
    private TextView tv;
    private DatabaseReference time;
    public String a;
    AlarmManager alarmManager;
    private PendingIntent pending_intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_time, container, false);
        timefood = (TimePicker) view.findViewById(R.id.TimeFood);
        start1 = (Button) view.findViewById(R.id.start1);
        start2 = (Button) view.findViewById(R.id.start2);
        start3 = (Button) view.findViewById(R.id.start3);
        stop = (Button) view.findViewById(R.id.stop);
        fm = (EditText) view.findViewById(R.id.minute);
        tv = (TextView) view.findViewById(R.id.TextViewSetTime);
        getActivity().setTitle("Feed Time");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String idc = prefs.getString("IDC", "");

        time = database.getReference(idc).child("FeedSet");
        time.keepSynced(true);
        time.orderByValue().limitToLast(1);
        final Intent myIntent = new Intent(getActivity(), AlarmReceiverFish.class);
        //final Intent myIntent = new Intent(getActivity(), Notification_AFood.class);
        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        timefood.setIs24HourView(true);

        time.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String value, value1;
                value = String.valueOf(map.get("Alert1"));
                value1 = String.valueOf(map.get("Notification"));
                if (value.equals("Enable"))
                    //tv.setText("Set time: "  + String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()));
                    tv.setText("System : Set Time");
                if (value.equals("Disable"))
                    tv.setText("System : Disable");
                if (value.equals("Disable") && value1.equals("Enable")) {
                    alarmManager.cancel(pending_intent);
                    getActivity().stopService(new Intent(getActivity(), Notification_AFood.class));
                    tv.setText("System : Disable");
                } else ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();

                        calendar.add(Calendar.SECOND, 3);
                        final int hour = timefood.getCurrentHour();
                        final int minute = timefood.getCurrentMinute();
                        Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, timefood.getCurrentHour());
                        calendar.set(Calendar.MINUTE, timefood.getCurrentMinute());
                        pending_intent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        if (fm.getText().toString().equals("")) {
                            fm.setError("Please enter minute");
                            value.put("Alert1", "Disable");
                        } else {
                            value.put("Alert1", "Enable");
                            value.put("Minute", Integer.parseInt(fm.getText().toString()));
                            value.put("Secret", "1");
                            time.updateChildren(value);
                            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*24*60*60, pending_intent);
                        }
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
                time.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();

                        calendar.add(Calendar.SECOND, 3);
                        final int hour = timefood.getCurrentHour();
                        final int minute = timefood.getCurrentMinute();
                        Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, timefood.getCurrentHour());
                        calendar.set(Calendar.MINUTE, timefood.getCurrentMinute());

                        pending_intent = PendingIntent.getBroadcast(getActivity(), 1, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        if (fm.getText().toString().equals("")) {
                            fm.setError("Please enter minute");
                            value.put("Alert2", "Disable");
                        } else {
                            value.put("Alert2", "Enable");
                            value.put("Minute", Integer.parseInt(fm.getText().toString()));
                            value.put("Secret", "1");
                            time.updateChildren(value);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        start3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();

                        calendar.add(Calendar.SECOND, 3);
                        final int hour = timefood.getCurrentHour();
                        final int minute = timefood.getCurrentMinute();
                        Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, timefood.getCurrentHour());
                        calendar.set(Calendar.MINUTE, timefood.getCurrentMinute());

                        pending_intent = PendingIntent.getBroadcast(getActivity(), 2, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        if (fm.getText().toString().equals("")) {
                            fm.setError("Please enter minute");
                            value.put("Alert3", "Disable");
                        } else {
                            value.put("Alert3", "Enable");
                            value.put("Minute", Integer.parseInt(fm.getText().toString()));
                            value.put("Secret", "1");
                            time.updateChildren(value);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        time.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = new HashMap<String, Object>();
                String alert1 = dataSnapshot.child("Alert1").getValue(String.class);
                String alert2 = dataSnapshot.child("Alert2").getValue(String.class);
                String alert3 = dataSnapshot.child("Alert3").getValue(String.class);
                a = alert1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a.equals("Enable")){
                    alarmManager.cancel(pending_intent);
                }
                time.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();

                        value.put("Secret", "0");
                        value.put("Alert1", "Disable");
                        value.put("Alert2", "Disable");
                        value.put("Alert3", "Disable");
                        //value.put("Status", "Disable");
                        time.updateChildren(value);

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
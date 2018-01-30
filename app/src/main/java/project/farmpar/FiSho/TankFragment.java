package project.farmpar.FiSho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import project.farmpar.Notifications.FiSho.Notification_WDang;
import project.farmpar.Notifications.FiSho.Notification_WLow;
import project.farmpar.R;

/**
 * Created by best on 29/3/2560.
 */

public class TankFragment extends Fragment {

    private DatabaseReference gpond,gnoti;
    private TextView WLevel,OS,PSO,PSI;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tank_layout,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Pond");


        WLevel = (TextView) view.findViewById(R.id.WLevel);
        OS = (TextView) view.findViewById(R.id.OxygenS);
        PSO = (TextView) view.findViewById(R.id.PumpSOut);
        PSI = (TextView) view.findViewById(R.id.PumpSIn);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String idc = prefs.getString("IDC", "");
        // TempWater
        gpond = database.getReference(idc).child("Fish").child("Tank");
        gpond.keepSynced(true);
        gpond.orderByValue().limitToLast(1);
        gpond.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String valueWLevel = String.valueOf(map.get("WaterLevel"));
                String valueOxygen = String.valueOf(map.get("Oxygen"));
                String valuePump = String.valueOf(map.get("Pump"));
                String valuePumpOut = String.valueOf(map.get("PumpOut"));

                WLevel.setText("Water Level : " + valueWLevel);
                PSI.setText("Pumping Water : " + valuePump);
                PSO.setText("Pumping Out : " + valuePumpOut);
                OS.setText("Oxygen : " + valueOxygen);


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gnoti = database.getReference(idc).child("FiSho").child("Tank");
        gnoti.keepSynced(true);
        gnoti.orderByValue().limitToLast(1);
        gnoti.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String WaterLevel = String.valueOf(map.get("WaterLevel"));

                //////////// Turbidity High ///////////////////////
                if (WaterLevel.equals("Low Level")) {
                    getActivity().startService(new Intent(getActivity(), Notification_WLow.class));
                } else {
                    getActivity().stopService(new Intent(getActivity(), Notification_WLow.class));
                }
                //////////// Turbidity Low ///////////////////////
                if (WaterLevel.equals("Dangerous")) {
                    getActivity().startService(new Intent(getActivity(), Notification_WDang.class));
                } else {
                    getActivity().stopService(new Intent(getActivity(), Notification_WDang.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return  view;
    }


}

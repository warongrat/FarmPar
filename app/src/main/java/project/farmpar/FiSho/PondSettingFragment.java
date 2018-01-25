package project.farmpar.FiSho;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import project.farmpar.R;

/**
 * Created by best on 4/4/2560.
 */

public class PondSettingFragment extends Fragment {

    private DatabaseReference foodset,oxygenset;
    private EditText SFL,SO,Contoller;
    private Button BFL,BO,Clear,Select;
    private TextView Tset;
    public static String idc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pondsetting_layout, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Pond Set");
        SFL = (EditText) view.findViewById(R.id.TSFL);
        BFL = (Button) view.findViewById(R.id.BSFL);
        SO =  (EditText) view.findViewById(R.id.TSO);
        BO = (Button) view.findViewById(R.id.BSO);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String idc = prefs.getString("IDC", "");

        foodset = database.getReference(idc).child("FiSho").child("FoodLevel");
        foodset.keepSynced(true);
        foodset.orderByValue().limitToLast(1);

        BFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodset.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value1 = new HashMap<String, Object>();
                        if (SFL.getText().toString().equals("")) {
                            SFL.setError("Please enter Food Level (cm)");
                        } else {
                            value1.put("LevelSet", Float.parseFloat(SFL.getText().toString()));
                        }
                        foodset.updateChildren(value1);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        oxygenset = database.getReference(idc).child("FiSho").child("TankSet");
        oxygenset.keepSynced(true);
        oxygenset.orderByValue().limitToLast(1);
        BO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oxygenset.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value2 = new HashMap<String, Object>();
                        if (SO.getText().toString().equals("")) {
                            SO.setError("Please enter Oxygen time on-off (minute)");
                        } else {
                            value2.put("TimeOxygen", Float.parseFloat(SO.getText().toString()));
                        }
                        oxygenset.updateChildren(value2);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });



        return view;
    }
    public static String getIDC() {
        return idc;
    }
}
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
 * Created by best on 12/9/2560.
 */

public class QualitySet extends Fragment {

    DatabaseReference Qset;
    EditText tmax, tmin, pHmax, pHmin, trmax, trmin;
    Button ASet,Set;
    TextView TextS;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qualityset_layout, container, false);
        getActivity().setTitle("Quality Set");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String idc = prefs.getString("IDC", "");

        Qset = database.getReference(idc).child("FiSho").child("Setting");
        Qset.keepSynced(true);
        Qset.orderByValue().limitToLast(1);
        tmax = (EditText) view.findViewById(R.id.TempMax);
        tmin = (EditText) view.findViewById(R.id.TempMin);
        pHmax = (EditText) view.findViewById(R.id.pHMax);
        pHmin = (EditText) view.findViewById(R.id.pHMin);
        trmax = (EditText) view.findViewById(R.id.TurMax);
        trmin = (EditText) view.findViewById(R.id.TurMin);
        TextS = (TextView) view.findViewById(R.id.TextQS);
        ASet = (Button) view.findViewById(R.id.ASetBQ);
        Set = (Button) view.findViewById(R.id.SetBQ);

        Qset.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String value = "Disable";
                value = String.valueOf(map.get("Status"));
                if (value.equals("Manual")) {
                    TextS.setText("Status : Manual");
                }
                if (value.equals("Auto")) {
                    TextS.setText("Status : Auto");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qset.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();
                        String val = dataSnapshot.child("Status").getValue(String.class);

                        if (val.equals("Manual") || val.equals("Auto")) {
                            value.put("Status", "Manual");
                            if (tmax.getText().toString().equals("")) {
                                tmax.setError("Please enter High Temp");
                            } else {
                                value.put("TempH", Integer.parseInt(tmax.getText().toString()));
                            }
                            if (tmin.getText().toString().equals("")) {
                                tmin.setError("Please enter Low Temp");
                            } else {
                                value.put("TempL", Integer.parseInt(tmin.getText().toString()));
                            }
                            if (pHmax.getText().toString().equals("")) {
                                pHmax.setError("Please enter High pH");
                            } else {
                                value.put("pHH", Integer.parseInt(pHmax.getText().toString()));
                            }
                            if (pHmin.getText().toString().equals("")) {
                                pHmin.setError("Please enter Low pH");
                            } else {
                                value.put("pHL", Integer.parseInt(pHmin.getText().toString()));
                            }
                            if (trmax.getText().toString().equals("")) {
                                trmax.setError("Please enter High Turbidity");
                            } else {
                                value.put("TurH", Integer.parseInt(trmax.getText().toString()));
                            }
                            if (trmin.getText().toString().equals("")) {
                                trmin.setError("Please enter Low Turbidity");
                            } else {
                                value.put("TurL", Integer.parseInt(trmin.getText().toString()));
                            }
                            Qset.updateChildren(value);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        ASet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qset.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();
                        String val = dataSnapshot.child("Status").getValue(String.class);

                        if (val.equals("Manual") || val.equals("Auto")) {
                            value.put("Status", "Auto");
                            value.put("TempH", 40);
                            value.put("TempL", 20);
                            value.put("pHH", 11);
                            value.put("pHL", 4);
                            value.put("TurH", 80);
                            value.put("TurL", 10);
                            Qset.updateChildren(value);
                        }
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
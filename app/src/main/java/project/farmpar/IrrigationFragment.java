package project.farmpar;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import project.farmpar.Notification.Notification_Irrigation;

public class IrrigationFragment extends Fragment {
    private Button status;
    private ImageView Grow;
    private DatabaseReference myRef;
    private TextView tv;
    private EditText et;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String idc = prefs.getString("IDC", "");
        View view = inflater.inflate(R.layout.irrigation, container, false);
        Grow = (ImageView) view.findViewById(R.id.grow_i);
        tv = (TextView) view.findViewById(R.id.textViewIrrigation);
        et = (EditText) view.findViewById(R.id.et_irrigation);
        status = (Button) view.findViewById(R.id.btn_IStatus);
        getActivity().setTitle(R.string.Irrigation);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(idc).child("Irrigation");
        myRef.keepSynced(true);
        myRef.orderByValue().limitToLast(1);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String value = "Disable";
                value = String.valueOf(map.get("Status"));
                if (value.equals("Disable")) {
                    status.setText(R.string.Enable);
                    Grow.setImageResource(R.drawable.growdie);
                    status.setBackgroundResource(R.color.green);
                    tv.setText(getResources().getString(R.string.Status) + " : " + getResources().getString(R.string.Disable));
                }
                if (value.equals("Enable")) {
                    Grow.setImageResource(R.drawable.grow_normal);
                    status.setBackgroundResource(R.color.colorRed);
                    status.setText(R.string.Disable);
                    tv.setText(getResources().getString(R.string.Status) + " : " + getResources().getString(R.string.Enable));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> value = new HashMap<String, Object>();
                        String val = dataSnapshot.child("Status").getValue(String.class);

                        if (val.equals("Enable")) {
                            value.put("Status", "Disable");
                            Grow.setImageResource(R.drawable.grow_normal);
                            status.setBackgroundResource(R.color.green);
                            myRef.updateChildren(value);
                            getActivity().stopService(new Intent(getActivity(), Notification_Irrigation.class));
                        }
                        if (val.equals("Disable")) {
                            value.put("Status", "Enable");
                            if (et.getText().toString().equals("")) {
                                et.setError("Please enter time");
                                value.put("Status", "Disable");
                            } else {
                                value.put("Time", Integer.parseInt(et.getText().toString()));
                                Grow.setImageResource(R.drawable.growdie);
                                status.setBackgroundResource(R.color.colorRed);
                                myRef.updateChildren(value);
                                getActivity().startService(new Intent(getActivity(), Notification_Irrigation.class));
                            }
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


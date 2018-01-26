package project.farmpar.FirstPlant;

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

import project.farmpar.Notifications.FirstPlant.Notification_Fertilization;
import project.farmpar.R;

public class FertilizationFragment extends Fragment {
    Button status;
    ImageView Grow;
    DatabaseReference myRef;
    TextView tv;
    EditText et;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String idc = prefs.getString("IDC", "");
        View view = inflater.inflate(R.layout.fertilization, container, false);
        Grow = (ImageView) view.findViewById(R.id.grow_f);
        status = (Button) view.findViewById(R.id.btn_FStatus);
        tv = (TextView) view.findViewById(R.id.textViewFertilizer);
        et = (EditText) view.findViewById(R.id.et_fertilization);
        getActivity().setTitle(R.string.Fertilization);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(idc).child("FirstPlant").child("Fertilization");
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
                    status.setBackgroundResource(R.color.green);
                    Grow.setImageResource(R.drawable.grow_normal);
                    tv.setText(getResources().getString(R.string.Status) + " : " + getResources().getString(R.string.Disable));
                }
                if (value.equals("Enable")) {
                    Grow.setImageResource(R.drawable.grow_f);
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
                            Grow.setImageResource(R.drawable.grow_f);
                            status.setBackgroundResource(R.color.green);
                            myRef.updateChildren(value);
                            getActivity().stopService(new Intent(getActivity(), Notification_Fertilization.class));
                        }
                        if (val.equals("Disable")) {
                            value.put("Status", "Enable");
                            if (et.getText().toString().equals("")) {
                                et.setError("Please enter volume");
                                value.put("Status", "Disable");
                            } else {
                                value.put("Volume", Integer.parseInt(et.getText().toString()));
                                Grow.setImageResource(R.drawable.grow_normal);
                                status.setBackgroundResource(R.color.colorRed);
                                myRef.updateChildren(value);
                                getActivity().startService(new Intent(getActivity(), Notification_Fertilization.class));
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

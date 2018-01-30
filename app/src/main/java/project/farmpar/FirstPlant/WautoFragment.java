package project.farmpar.FirstPlant;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import project.farmpar.R;


public class WautoFragment extends Fragment {
    private Button status;
    private ImageView Auto;
    private DatabaseReference myRef;
    private TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String idc = prefs.getString("IDC", "");
        View view = inflater.inflate(R.layout.fragment_wauto, container, false);
        Auto = (ImageView) view.findViewById(R.id.auto);
        tv = (TextView) view.findViewById(R.id.textViewAIrrigation);
        status = (Button) view.findViewById(R.id.btn_AIStatus);
        getActivity().setTitle(R.string.AutoIrrigation);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(idc).child("Plant").child("AutoIrrigation");
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
                    Auto.setImageResource(R.drawable.f_irrigation);
                    status.setBackgroundResource(R.color.green);
                    tv.setText(getResources().getString(R.string.Status) + " : " + getResources().getString(R.string.Disable));
                }
                if (value.equals("Enable")) {
                    Auto.setImageResource(R.drawable.o_irrigation);
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
                            value.put("Warning", 0);
                            Auto.setImageResource(R.drawable.o_irrigation);
                            status.setBackgroundResource(R.color.green);
                            myRef.updateChildren(value);
                            //getActivity().stopService(new Intent(getActivity(), Notification_AIrrigation.class));
                        }
                        if (val.equals("Disable")) {
                            value.put("Status", "Enable");
                            Auto.setImageResource(R.drawable.f_irrigation);
                            status.setBackgroundResource(R.color.colorRed);
                            myRef.updateChildren(value);
                            //getActivity().startService(new Intent(getActivity(), Notification_AIrrigation.class));
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

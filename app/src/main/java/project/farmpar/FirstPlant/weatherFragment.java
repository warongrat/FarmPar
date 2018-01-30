package project.farmpar.FirstPlant;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import project.farmpar.R;


public class weatherFragment extends Fragment {
    private TextView textViewResult, textViewTitle;
    private ImageView wIcon;
    private DatabaseReference myRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String idc = prefs.getString("IDC", "");
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        textViewResult = (TextView) view.findViewById(R.id.result);
        textViewTitle = (TextView) view.findViewById(R.id.title);
        wIcon = (ImageView) view.findViewById(R.id.weatherIcon);
        getActivity().setTitle(R.string.Weather);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(idc).child("Plant").child("Weather");
        myRef.keepSynced(true);
        myRef.orderByValue().limitToLast(1);

        myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Map map = (Map) dataSnapshot.getValue();
                                            String temp = String.valueOf(map.get("Temperature"));
                                            String hum = String.valueOf(map.get("Humidity"));
                                            String heat = String.valueOf(map.get("Heatindex"));
                                            String rain = String.valueOf(map.get("Raindrop"));
                                            String sun = String.valueOf(map.get("Sunlight"));

                                            if (sun.equals("1") && rain.equals("1")) {
                                                wIcon.setImageResource(R.drawable.sun_rain);
                                                textViewTitle.setText(R.string.Sun_Rain);
                                            } else if (sun.equals("0") && rain.equals("1")) {
                                                wIcon.setImageResource(R.drawable.rainy);
                                                textViewTitle.setText(R.string.Rainy);
                                            } else if (sun.equals("1") && rain.equals("0")) {
                                                wIcon.setImageResource(R.drawable.sunny);
                                                textViewTitle.setText(R.string.Sunny);
                                            } else {
                                                wIcon.setImageResource(R.drawable.cloudy);
                                                textViewTitle.setText(R.string.Cloudy);
                                            }
                                            textViewResult.setText(getResources().getString(R.string.Temp) + " : " + temp + "\n" + getResources().getString(R.string.Hump) + " : " + hum + "\n" + getResources().getString(R.string.Heat) + " : " + heat);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }
        );
        return view;
    }
}


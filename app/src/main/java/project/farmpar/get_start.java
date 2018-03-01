package project.farmpar;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import project.farmpar.data.StaticConfig;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.TourGuide;

public class get_start extends Fragment {
    public TourGuide mTutorialHandler, mTutorialHandler2;
    private Button get_start;
    private DatabaseReference myRef, userDB, Ref;
    private TextView Tset, Tfarm;
    private String username;
    List<String> stringlist;
    ArrayAdapter<String> adapter, adapter_type;
    private String[] type = {"Choose a Type...", "Plant", "Fish"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_get_start, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        get_start = (Button) view.findViewById(R.id.get_start);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("name");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Name", username);
                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Overlay overlay = new Overlay()
                // Note: disable click has no effect when setOnClickListener is used, this is here for demo purpose
                // if setOnClickListener is not used, disableClick() will take effect
                .disableClick(true)
                .disableClickThroughHole(false)
                .setStyle(Overlay.Style.ROUNDED_RECTANGLE)
                .setRoundedCornerRadius(8);

        mTutorialHandler = TourGuide.init(getActivity()).with(TourGuide.Technique.CLICK)
                .setPointer(new Pointer())
                .setOverlay(overlay)
                .playOn(get_start);

        get_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //mTutorialHandler.cleanUp();
                View dView = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog, null);
                View sView = getActivity().getLayoutInflater().inflate(R.layout.activity_setting, null);
                Tset = (TextView) sView.findViewById(R.id.Tset);
                Tfarm = (TextView) sView.findViewById(R.id.Tfarm);
                final EditText farmname = (EditText) dView.findViewById(R.id.farmname);
                final EditText controller = (EditText) dView.findViewById(R.id.controller);
                final Spinner t_spinner = (Spinner) dView.findViewById(R.id.t_spinner);
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, type);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                t_spinner.setAdapter(adapter);
                myRef = FirebaseDatabase.getInstance().getReference("users").child(prefs.getString("Name", ""));
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setView(dView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mTutorialHandler.cleanUp();
                        SharedPreferences.Editor editor = prefs.edit();
                        // FirstPlant
                        if (!t_spinner.getSelectedItem().toString().equals("Choose a Type...")) {
                            if (farmname.getText().toString().equals("") || (controller.getText().toString().equals(""))) {
                            } else {

                                if (t_spinner.getSelectedItem().toString().equals("Plant")) {
                                    myRef.child("Plant").child(farmname.getText().toString()).setValue(controller.getText().toString());
                                    Ref = FirebaseDatabase.getInstance().getReference(controller.getText().toString()).child("Plant");
                                    Ref.child("AutoFertilization").child("Alert").setValue("Disable");
                                    Ref.child("AutoFertilization").child("Alert2").setValue("Disable");
                                    Ref.child("AutoFertilization").child("Notification").setValue("Disable");
                                    Ref.child("AutoFertilization").child("Secret").setValue("0");
                                    Ref.child("AutoFertilization").child("Status").setValue("Disable");
                                    Ref.child("AutoFertilization").child("Volume").setValue(0);
                                    Ref.child("AutoIrrigation").child("Notification").setValue("Disable");
                                    Ref.child("AutoIrrigation").child("Status").setValue("Disable");
                                    Ref.child("AutoIrrigation").child("Warning").setValue(0);
                                    Ref.child("Fertilization").child("Status").setValue("Disable");
                                    Ref.child("Fertilization").child("Notification").setValue("Disable");
                                    Ref.child("Fertilization").child("Volume").setValue(0);
                                    Ref.child("Irrigation").child("Time").setValue(0);
                                    Ref.child("Irrigation").child("Status").setValue("Disable");
                                    Ref.child("Irrigation").child("Notification").setValue("Disable");
                                    Ref.child("Weather").child("Temperature").setValue(0);
                                    Ref.child("Weather").child("Humidity").setValue(0);
                                    Ref.child("Weather").child("Heatindex").setValue(0);
                                    Ref.child("Weather").child("Raindrop").setValue(0);
                                    Ref.child("Weather").child("Sunlight").setValue(0);
                                    editor.putString("Type", "Plant");
                                    editor.putString("Farm", farmname.getText().toString());
                                }

                                //FiSho
                                else if (t_spinner.getSelectedItem().toString().equals("Fish")) {
                                    myRef.child("Fish").child(farmname.getText().toString()).setValue(controller.getText().toString());
                                    Ref = FirebaseDatabase.getInstance().getReference(controller.getText().toString()).child("Fish");
                                    Ref.child("FeedSet").child("Alert1").setValue("Disable");
                                    Ref.child("FeedSet").child("Alert2").setValue("Disable");
                                    Ref.child("FeedSet").child("Alert3").setValue("Disable");
                                    Ref.child("FeedSet").child("Minute").setValue(0);
                                    Ref.child("FeedSet").child("Notification").setValue("Disable");
                                    Ref.child("FeedSet").child("Secret").setValue(0);
                                    Ref.child("FeedSet").child("Status").setValue("Disable");
                                    Ref.child("FoodLevel").child("Level").setValue(0);
                                    Ref.child("FoodLevel").child("LevelSet").setValue(100);
                                    Ref.child("Setting").child("Status").setValue("Auto");
                                    Ref.child("Setting").child("TempH").setValue(40);
                                    Ref.child("Setting").child("TempL").setValue(20);
                                    Ref.child("Setting").child("TurH").setValue(100);
                                    Ref.child("Setting").child("TurL").setValue(20);
                                    Ref.child("Setting").child("pHH").setValue(13);
                                    Ref.child("Setting").child("pHL").setValue(4);
                                    Ref.child("Tank").child("Oxygen").setValue("Disable");
                                    Ref.child("Tank").child("Pump").setValue("Disable");
                                    Ref.child("Tank").child("PumpOut").setValue("Disable");
                                    Ref.child("Tank").child("WaterLevel").setValue("Normal");
                                    Ref.child("TankSet").child("TimeOxygen").setValue(0);
                                    Ref.child("WaterQuality").child("Temp").setValue(0);
                                    Ref.child("WaterQuality").child("Turbidity").setValue(0);
                                    Ref.child("WaterQuality").child("pH").setValue(0);
                                    editor.putString("Type", "Fish");
                                    editor.putString("Farm", farmname.getText().toString());
                                }
                            }
                        }
                        String value = controller.getText().toString();
                        editor.putString("First", "1");
                        editor.putString("IDC", value);
                        editor.commit();
                        Tset.setText(getResources().getString(R.string.Current_ID) + " : " + prefs.getString("IDC", ""));
                        Tfarm.setText("Farm name: "+ prefs.getString("Farm", ""));
                        dialog.dismiss();
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
        });

        return view;

    }
}

package project.farmpar;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import project.farmpar.Service.ServiceUtils;
import project.farmpar.data.FriendDB;
import project.farmpar.data.GroupDB;
import project.farmpar.data.StaticConfig;

public class Setting extends Fragment {

    private DatabaseReference myRef, userDB, Ref;
    private Button select, signout;
    private EditText controller;
    private TextView Tset;
    public static String idc;
    List<String> stringlist;
    ArrayAdapter<String> adapter;
    private String username;
private String [] type = {"Choose a Type...", "FirstPlant", "FiSho"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);
        signout = (Button) view.findViewById(R.id.signout);
        select = (Button) view.findViewById(R.id.select);
        Tset = (TextView) view.findViewById(R.id.Tset);
        getActivity().setTitle(R.string.Settings);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Tset.setText(getResources().getString(R.string.Current_ID) + " : " + prefs.getString("IDC", ""));
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("name");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                mBuilder.setTitle("Farm name");
                myRef = FirebaseDatabase.getInstance().getReference("users").child(username);
                final Spinner spinner = (Spinner) mView.findViewById(R.id.spinner);
                stringlist = new ArrayList<>();
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stringlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        stringlist.clear();
                        stringlist.add("Choose a Farm...");
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey().toString();
                            stringlist.add(key);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                mBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String value;
                        myRef = FirebaseDatabase.getInstance().getReference("users").child(username);
                        if (!spinner.getSelectedItem().toString().equals("Choose a Farm...")) {
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String value = dataSnapshot.child(spinner.getSelectedItem().toString()).getValue(String.class);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("IDC", value);
                                    editor.commit();
                                    Tset.setText(getResources().getString(R.string.Current_ID) + " : " + prefs.getString("IDC", ""));
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                });

                mBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Sure..!!!");
                alertDialogBuilder.setMessage("Are you sure,You want to sign out ?");
                alertDialogBuilder.setIcon(R.drawable.question);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.commit();
                        FirebaseAuth.getInstance().signOut();
                        FriendDB.getInstance(getContext()).dropDB();
                        GroupDB.getInstance(getContext()).dropDB();
                        ServiceUtils.stopServiceFriendChat(getContext().getApplicationContext(), true);
                        getActivity().finish();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                View dView = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog, null);
                final EditText farmname = (EditText) dView.findViewById(R.id.farmname);
                final EditText controller = (EditText) dView.findViewById(R.id.controller);
                final Spinner t_spinner = (Spinner) dView.findViewById(R.id.t_spinner);
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, type);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                t_spinner.setAdapter(adapter);
                myRef = FirebaseDatabase.getInstance().getReference("users").child(username);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setView(dView);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // FirstPlant
                        if (!t_spinner.getSelectedItem().toString().equals("Choose a Type...")) {
                            myRef.child(farmname.getText().toString()).setValue(controller.getText().toString());
                            if (t_spinner.getSelectedItem().toString().equals("FirstPlant")) {
                                Ref = FirebaseDatabase.getInstance().getReference(controller.getText().toString()).child("FirstPlant");
                                Ref.child("SetTime").child("Alert").setValue("Disable");
                                Ref.child("SetTime").child("Alert2").setValue("Disable");
                                Ref.child("SetTime").child("Notification").setValue("Disable");
                                Ref.child("SetTime").child("Secret").setValue("0");
                                Ref.child("SetTime").child("Status").setValue("Disable");
                                Ref.child("AutoIrrigation").child("Notification").setValue("Disable");
                                Ref.child("AutoIrrigation").child("Status").setValue("Disable");
                                Ref.child("AutoIrrigation").child("Warning").setValue(0);
                                Ref.child("Irrigation").child("Time").setValue(0);
                                Ref.child("Irrigation").child("Status").setValue("Disable");
                                Ref.child("Irrigation").child("Notification").setValue("Disable");
                                Ref.child("Weather").child("Temperature").setValue(0);
                                Ref.child("Weather").child("Humidity").setValue(0);
                                Ref.child("Weather").child("Heatindex").setValue(0);
                                Ref.child("Weather").child("Raindrop").setValue(0);
                                Ref.child("Weather").child("Sunlight").setValue(0);
                            }

                            //FiSho
                            else if (t_spinner.getSelectedItem().toString().equals("FiSho")) {
                                Ref = FirebaseDatabase.getInstance().getReference(controller.getText().toString()).child("FiSho");
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
                            }
                            //FarmPar
                        }
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


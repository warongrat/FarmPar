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
                        if (!spinner.getSelectedItem().toString().equals("Choose a controller ID...")) {
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
                myRef = FirebaseDatabase.getInstance().getReference("users").child(username);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setView(dView);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        myRef.child(farmname.getText().toString()).setValue(controller.getText().toString());
                        Ref = FirebaseDatabase.getInstance().getReference(controller.getText().toString());
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


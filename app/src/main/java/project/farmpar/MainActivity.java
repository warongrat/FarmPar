package project.farmpar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.farmpar.FiSho.FeedTimeFragment;
import project.farmpar.FiSho.FoodLevelFragment;
import project.farmpar.FiSho.PondSettingFragment;
import project.farmpar.FiSho.QualitySet;
import project.farmpar.FiSho.TankFragment;
import project.farmpar.FiSho.WaterQualityFragment;
import project.farmpar.FirstPlant.FertilizationFragment;
import project.farmpar.FirstPlant.IrrigationFragment;
import project.farmpar.FirstPlant.SetTimeFragment;
import project.farmpar.FirstPlant.WautoFragment;
import project.farmpar.FirstPlant.weatherFragment;
import project.farmpar.data.StaticConfig;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int notification_id;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private DatabaseReference userDB;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);

        notification_id = (int) System.currentTimeMillis();

        Intent notification_intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification_intent, 0);

        builder.setSmallIcon(R.drawable.icon_small)
                .setAutoCancel(true)
                .setContentTitle("Welcome")
                .setContentText("Farming for Everyone")
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(Notification.PRIORITY_MAX);

        //notificationManager.notify(notification_id, builder.build());
        //notificationManager.cancel(notification_id);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

        if(prefs.getString("Type", "").toString().equals("Plant"))
            nav_Menu.setGroupVisible(R.id.group_fish, false);
        else if(prefs.getString("Type", "").toString().equals("Fish"))
            nav_Menu.setGroupVisible(R.id.group_plant, false);
        else{
            nav_Menu.setGroupVisible(R.id.group_fish, false);
            nav_Menu.setGroupVisible(R.id.group_plant, false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            setTitle("FarmPar");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.action_exit) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Confirm Exit..!!!");
            alertDialogBuilder.setMessage("Are you sure,You want to exit ?");
            alertDialogBuilder.setIcon(R.drawable.question);
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                    System.exit(0);
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
            return true;
        }
        if (id == R.id.action_chat) {
            startActivity(new Intent(MainActivity.this, MainChat.class));
            return true;
        }if (id == R.id.action_about) {
            fragment = new About();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.mainFrame, fragment).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_weather) {
            fragment = new weatherFragment();
        } else if (id == R.id.nav_water) {
            fragment = new IrrigationFragment();
        } else if (id == R.id.nav_Wauto) {
            fragment = new WautoFragment();
        } else if (id == R.id.nav_time) {
            fragment = new SetTimeFragment();
        }else if (id == R.id.nav_fertilizer) {
            fragment = new FertilizationFragment();
        } else if (id == R.id.setting) {
            fragment = new Setting();
        } else if (id == R.id.nav_quality) {
            fragment = new WaterQualityFragment();
        } else if (id == R.id.nav_qualityset) {
            fragment = new QualitySet();
        } else if (id == R.id.nav_Intake) {
            fragment = new FoodLevelFragment();
        } else if (id == R.id.nav_feedtime) {
            fragment = new FeedTimeFragment();
        } else if (id == R.id.nav_tank) {
            fragment = new TankFragment();
        } else if (id == R.id.nav_tankset) {
            fragment = new PondSettingFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.mainFrame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import project.farmpar.R;

/**
 * Created by best on 5/4/2560.
 */

public class FoodLevelFragment extends Fragment {

    private DatabaseReference gfood;
    private TextView FL;
    private ImageView PFL;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.foodintake_layout,container,false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Food Level");

        FL = (TextView) view.findViewById(R.id.TFL);
        PFL = (ImageView) view.findViewById(R.id.FL);


        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String idc = prefs.getString("IDC", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gfood = database.getReference(idc).child("FiSho").child("FoodLevel");
        gfood.keepSynced(true);
        gfood.orderByValue().limitToLast(1);
        gfood.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String FoodLevel = String.valueOf(map.get("Level"));
                if (FoodLevel.equals("Error") ) {
                    FL.setText("Food Level : " + FoodLevel);
                    PFL.setImageResource(R.drawable.food_er);
                }
                else {
                    FL.setText("Food Level : " + FoodLevel + "%");
                    if(Float.parseFloat(FoodLevel) == 100){
                        PFL.setImageResource(R.drawable.food_100);
                    }
                    else if(Float.parseFloat(FoodLevel) <= 75 && Float.parseFloat(FoodLevel) > 50 ){
                        PFL.setImageResource(R.drawable.food_75);
                    }
                    else if(Float.parseFloat(FoodLevel) <= 50 && Float.parseFloat(FoodLevel) > 25 ){
                        PFL.setImageResource(R.drawable.food_50);
                    }
                    else if(Float.parseFloat(FoodLevel) <= 25 && Float.parseFloat(FoodLevel) > 5 ){
                        PFL.setImageResource(R.drawable.food_25);
                    }
                    else if(Float.parseFloat(FoodLevel) <= 5 && Float.parseFloat(FoodLevel) > 0 ){
                        PFL.setImageResource(R.drawable.food_0);
                    }
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
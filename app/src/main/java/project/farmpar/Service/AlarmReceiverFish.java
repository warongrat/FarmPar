package project.farmpar.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import project.farmpar.Notifications.FiSho.Notification_AFood;

/**
 * Created by best on 13/9/2560.
 */

public class AlarmReceiverFish extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context,Notification_AFood.class);
        context.startService(serviceIntent);
    }
}
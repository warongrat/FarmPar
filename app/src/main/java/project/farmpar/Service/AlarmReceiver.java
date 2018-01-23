package project.farmpar.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import project.farmpar.Notification.Notification_AFertilization;

/**
 * Created by waron on 10/9/2560.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context,Notification_AFertilization.class);
                    context.startService(serviceIntent);
    }
}


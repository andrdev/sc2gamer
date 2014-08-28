package com.github.andrdev.sc2gamer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**BootAlarmReceiver class is a class, that responsible for the tracking
 * BOOT_COMPLETE intent, and informing AlarmCreatorService about it.
 * */
public class BootAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, AlarmCreatorService.class);
        in.putExtra(AlarmCreatorService.ALARM_EVENT, AlarmCreatorService.BOOT);
        context.startService(in);
    }
}

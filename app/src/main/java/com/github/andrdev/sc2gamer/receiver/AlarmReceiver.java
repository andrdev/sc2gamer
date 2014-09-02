package com.github.andrdev.sc2gamer.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.andrdev.sc2gamer.activity.AlarmDialogActivity;
import com.github.andrdev.sc2gamer.service.AlarmCreatorService;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmDialogActivity.class);
        i.putExtra(AlarmCreatorService.ALARM_ID, intent.getIntExtra(AlarmCreatorService.ALARM_ID, 1));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}

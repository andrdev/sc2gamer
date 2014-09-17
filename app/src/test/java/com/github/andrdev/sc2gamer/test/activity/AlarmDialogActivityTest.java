package com.github.andrdev.sc2gamer.test.activity;

import android.app.Activity;

import com.github.andrdev.sc2gamer.activity.AlarmDialogActivity;
import com.github.andrdev.sc2gamer.activity.MainActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class AlarmDialogActivityTest {
    @Before
    public void setup() {
//do whatever is necessary before every test
    }
    @Test
    public void testActivityFound() {
        Activity activity = Robolectric.buildActivity(AlarmDialogActivity.class).create().get();
        Assert.assertNotNull(activity);
    }
    @Test
    public void testLayout() {
        Activity activity = Robolectric.buildActivity(AlarmDialogActivity.class).create().get();
        Assert.assertNotNull(activity);
    }
}

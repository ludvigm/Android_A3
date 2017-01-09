package com.example.ludvig.assignment3.CallHistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;



public class IncomingCallListener extends BroadcastReceiver {

    File dir;
    private static String lastState;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (null == bundle) {
            return;
        }
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (!state.equals(lastState)) {
            lastState = state;                                                                   //Theres a known bug where this receiver gets triggered twice for each phonecall(Saving two numbers per call)
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {                  //This work-around avoids that. Credit: http://stackoverflow.com/questions/35478848/android-broadcastreceiver-onreceive-called-twice-on-android-5-1-1-even-after-o
                String phonenumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                dir = context.getExternalFilesDir(null);
                savePhoneNumber(phonenumber);
            }
        }


    }

    /**
     * Save phonenumber to file using Google Guava. Separate numbers by comma.
     * @param number
     */

    private void savePhoneNumber(String number) {
        File file = new File(dir, "phoneNumbers");
        try {
            Files.append(number + ",", file, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("number saved: " + number);
    }
}

package com.example.user.sqlite;

import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 2015/7/31.
 */
public class CommonUtilities {
    // give your server registration url here
    static final String SERVER_URL = "http://104.215.188.35/keepintouch/gcm_server_php/register.php";

    // Google project id
    static final String SENDER_ID = "540275285217";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.example.user.sqlite.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

}

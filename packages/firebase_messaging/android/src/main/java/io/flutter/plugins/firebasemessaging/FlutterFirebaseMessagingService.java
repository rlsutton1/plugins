// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebasemessaging;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;

import android.app.NotificationChannel;
import android.content.Context;
import android.net.Uri;
import android.media.RingtoneManager;
import android.media.AudioAttributes;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification;

import android.app.Notification.Action;
import android.util.Log;


public class FlutterFirebaseMessagingService extends FirebaseMessagingService {

    public static final String ACTION_FOREGROUND_REMOTE_MESSAGE =
            "io.flutter.plugins.firebasemessaging.FOREGROUND_NOTIFICATION";
    public static final String ACTION_BACKGROUND_REMOTE_MESSAGE =
            "io.flutter.plugins.firebasemessaging.BACKGROUND_NOTIFICATION";
    public static final String EXTRA_REMOTE_MESSAGE = "notification";

    public static final String ACTION_TOKEN = "io.flutter.plugins.firebasemessaging.TOKEN";
    public static final String EXTRA_TOKEN = "token";

    /**
     * true, if application receive messages with type "Data messages"
     * About FCM messages {@https://firebase.google.com/docs/cloud-messaging/concept-options}
     */
    private boolean isDataMessages;

    @Override
    public void onCreate() {
        super.onCreate();
        isDataMessages = FlutterFirebaseMessagingUtils.isDataMessages(this);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.w("TestFBM", "onMessageReceived");

        if (!isDataMessages) {
            sendForegroundBroadcast(remoteMessage);
        }

        boolean applicationForeground = FlutterFirebaseMessagingUtils.isApplicationForeground(getApplicationContext());

        if (applicationForeground) {
            sendForegroundBroadcast(remoteMessage);
        } else {
            handleBackgroundMessage(remoteMessage);
        }
    }

    private void sendForegroundBroadcast(RemoteMessage remoteMessage) {
        Intent intent = new Intent(ACTION_FOREGROUND_REMOTE_MESSAGE);
        intent.putExtra(EXTRA_REMOTE_MESSAGE, remoteMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Called when a new token for the default Firebase project is generated.
     *
     * @param token The token used for sending messages to this application instance. This token is
     *              the same as the one retrieved by getInstanceId().
     */
    @Override
    public void onNewToken(String token) {
        Intent intent = new Intent(ACTION_TOKEN);
        intent.putExtra(EXTRA_TOKEN, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void handleBackgroundMessage(RemoteMessage remoteMessage) {
        try {
            Class<BackgroundMessageHandlerIfc> messageHandlerIfc = (Class<BackgroundMessageHandlerIfc>) Class.forName("io.flutter.plugins.firebasemessaging.BackgroundMessageHandler");
            BackgroundMessageHandlerIfc messageHandler = messageHandlerIfc.newInstance();
            messageHandler.handleBackgroundMessage(this, remoteMessage);
        } catch (Exception e) {
            Log.w("TestFBM", e.getMessage());
        }
    }


}

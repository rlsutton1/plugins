package io.flutter.plugins.firebasemessaging;

import com.google.firebase.messaging.RemoteMessage;

interface BackgroundMessageHandlerIfc
{
    public void handleBackgroundMessage(FlutterFirebaseMessagingService service,RemoteMessage remoteMessage);
}
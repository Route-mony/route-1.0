package com.beyondthehorizon.route.utils;


import android.content.SharedPreferences;
import android.util.Log;

import com.beyondthehorizon.route.databases.NotificationCount;
import com.beyondthehorizon.route.repository.RouteRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "MESSAGING";
    private RouteRepository chatsRepository;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    SharedPreferences pref; // 0 - for private mode
//    private boolean appStatus;

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        /* There are two types of messages data messages and notification messages.
        Data messages are handled here in onMessageReceived whether the app is in the foreground or background.
        Data messages are the type traditionally used with GCM.
        Notification messages are only received here in onMessageReceived when the app is in the foreground.
        When the app is in the background an automatically generated notification is displayed. */

        chatsRepository = new RouteRepository(getApplication());
        pref = getApplication().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("notifCount", pref.getInt("notifCount", 0));
        editor.apply();

        chatsRepository.insertChat(new NotificationCount(String.valueOf(pref.getInt("notifCount", 0))));

//        final String dataSender, dataMessage, phoneNumber, senderUID, newGroup,
//                receiverUID, timestamp, type, profileImage, imageUrl, groupName, message_key;

        // GestureDetector if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage);
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            if (remoteMessage.getData().get("message").contains("delivery_status")) {
//                return;
        }
//            dataSender = remoteMessage.getData().get("sender");
//            dataMessage = remoteMessage.getData().get("message");
//            phoneNumber = remoteMessage.getData().get("phoneNumber");
//            senderUID = remoteMessage.getData().get("senderUID");
//            timestamp = remoteMessage.getData().get("timestamp");
//            receiverUID = remoteMessage.getData().get("receiverUID");
//            type = remoteMessage.getData().get("type");
//            imageUrl = remoteMessage.getData().get("imageUrl");
//            profileImage = remoteMessage.getData().get("profileUrl");
//            groupName = remoteMessage.getData().get("groupname");
//            newGroup = remoteMessage.getData().get("newGroup");
//            message_key = remoteMessage.getData().get("message_key");
//
//            final String finalDataTitle = dataSender;
//            final String finalDataMessage = dataMessage;
//
//            if (imageUrl.contains(NothingToSend)) {
//                saveDataLocally(dataSender, dataMessage, phoneNumber, senderUID,
//                        timestamp, receiverUID, type, imageUrl, remoteMessage,
//                        profileImage, groupName, newGroup, message_key);
//            } else {
//                try {
//                    URL url = new URL(imageUrl);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setDoInput(true);
//                    connection.connect();
//                    InputStream input = connection.getInputStream();
//                    Bitmap bitmap = BitmapFactory.decodeStream(input);
//                    File mydir = new File(Environment.getExternalStorageDirectory() + "/Associates/Media/Associates Images");
//                    if (!mydir.exists()) {
//                        mydir.mkdirs();
//                    }
//                    String time = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
//                    String fileUri = mydir.getAbsolutePath() + File.separator + "IMG-" + time + ".jpg";
//                    FileOutputStream outputStream = new FileOutputStream(fileUri);
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                    outputStream.flush();
//                    outputStream.close();
//
//                    saveDataLocally(dataSender, dataMessage, phoneNumber, senderUID,
//                            timestamp, receiverUID, type, fileUri, remoteMessage,
//                            profileImage, groupName, newGroup, message_key);
//
//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(fileUri))));
//                    Log.d(TAG, "onImageReceived: " + fileUri);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            Log.d(TAG, "onMessageReceived: " + AppController.getInstance().appStatus);
//
//            if (AppController.getInstance().appStatus) {
//                // Also if you intend on generating your own notifications as a result of a received FCM
//                // message, here is where that should be initiated. See sendNotification method below.
//                sendNotification(finalDataTitle, finalDataMessage, senderUID);
//            }
//        }
////        // GestureDetector if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
////            notificationTitle = remoteMessage.getNotification().getTitle();
////            notificationBody = remoteMessage.getNotification().getBody();
        }
    }

    private void saveDataLocally(String dataSender, String dataMessage, String phoneNumber, String senderUID,
                                 String timestamp, String receiverUID, String type, String imageUrl, RemoteMessage remoteMessage,
                                 String profileImage, String groupName, String newGroup, String message_key) {

//        String audioUrl = remoteMessage.getData().get("audioUrl");
//        String videoUrl = remoteMessage.getData().get("videoUrl");
//        String fileUrl = remoteMessage.getData().get("fileUrl");
//        String docName = remoteMessage.getData().get("docName");
//
//        if (type.contains("Room")) {
//            if (newGroup.contains("NewGroup")) {
//                if (dataMessage.contains(currentUser.getPhoneNumber())) {
//                    dataMessage = "You created " + groupName;
//                } else {
//                    dataMessage = dataMessage + " added You";
//                }
//                dataSender = "";
//            }
//            if (!senderUID.contains(currentUser.getUid())) {
//                chatsRepository.insertLatestChat(new RecentChatModel(
//                        receiverUID, //Group unique Id
//                        groupName,
//                        dataMessage,
//                        timestamp,
//                        type,
//                        profileImage
//                ));
//
//                chatsRepository.insertChat(new ChatModel(
//                        message_key,
//                        dataSender,
//                        dataMessage,
//                        "0",
//                        phoneNumber,
//                        receiverUID, //Chats are found using this senderUID(this is senderUID field)
//                        timestamp,
//                        receiverUID,
//                        profileImage,
//                        docName,
//                        imageUrl,
//                        videoUrl,
//                        audioUrl,
//                        fileUrl,
//                        type,
//                        Delivered
//                ));
//
//                chatsRepository.insertComment(new CommentsModel(
//                        message_key,
//                        message_key,
//                        dataSender,
//                        dataMessage,
//                        phoneNumber,
//                        senderUID,
//                        timestamp,
//                        docName,
//                        imageUrl,
//                        videoUrl,
//                        audioUrl,
//                        fileUrl,
//                        Delivered,
//                        type
//                ));
//            }
//        } else if (type.contains("Single")) {
//
//            chatsRepository.insertLatestChat(new RecentChatModel(
//                    senderUID,
//                    dataSender,
//                    dataMessage,
//                    timestamp,
//                    type,
//                    profileImage
//            ));
//
//            chatsRepository.insertChat(new ChatModel(
//                    message_key,
//                    dataSender,
//                    dataMessage,
//                    "0",
//                    phoneNumber,
//                    senderUID,
//                    timestamp,
//                    receiverUID,
//                    profileImage,
//                    docName,
//                    imageUrl,
//                    videoUrl,
//                    audioUrl,
//                    fileUrl,
//                    type,
//                    Sent
//            ));
//        } else if (type.contains("Comment")) {
//            String total_num_comments = remoteMessage.getData().get("totalComments");
////            String parent_message_key = remoteMessage.getData().get("parent_message_key");
//            if (!senderUID.contains(currentUser.getUid())) {
//                chatsRepository.insertComment(new CommentsModel(
//                        message_key, // parent_message_key
//                        message_key,// the comment message
//                        dataSender,
//                        dataMessage,
//                        phoneNumber,
//                        senderUID,
//                        timestamp,
//                        docName,
//                        imageUrl,
//                        videoUrl,
//                        audioUrl,
//                        fileUrl,
//                        Delivered,
//                        type
//                ));
//            }
//            chatsRepository.updateNumberOfComments(message_key, total_num_comments);
//        }
    }

    /**
     * //     * Create and show a simple notification containing the received FCM message.
     * //
     */
//    private void sendNotification(String dataTitle, String dataMessage, String senderUID) {
//
//        Intent intentAction = new Intent(this, ActionReceiver.class);
//        intentAction.putExtra("action", "action1");
//        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 1,
//                intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.putExtra("title", dataTitle);
//        intent.putExtra("message", dataMessage);
//        intent.putExtra("myFriend", dataTitle);
//        intent.putExtra("friendUID", senderUID);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        // This is the Notification Channel ID. More about this in the next section
//        final String NOTIFICATION_CHANNEL_ID = "channel_id";
//        //User visible Channel Name
//        final String CHANNEL_NAME = "Notification Channel";
//
//        //Notification channel should only be created for devices running Android 26
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Importance applicable to all the notifications in this Channel
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
//            //Boolean value to set if lights are enabled for Notifications from this Channel
//            notificationChannel.enableLights(true);
//            //Boolean value to set if vibration are enabled for Notifications from this Channel
//            notificationChannel.enableVibration(true);
//            //Sets the color of Notification Light
//            notificationChannel.setLightColor(Color.GREEN);
//            //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
//            notificationChannel.setVibrationPattern(new long[]{
//                    500,
//                    500,
//                    500,
//                    500,
//                    500
//            });
//            //Sets whether notifications from these Channel should be visible on Lockscreen or not
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
////            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////            notificationManager.createNotificationChannel(notificationChannel);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_account)
//                .setContentTitle(dataTitle)
//                .setContentText(dataMessage)
//                .setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true)
//                .addAction(R.drawable.ic_account, "Later", pendingIntent2)
//                .addAction(R.drawable.ic_send, "Reply", pendingIntent2);
//
//        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(101, builder.build());
//    }
}
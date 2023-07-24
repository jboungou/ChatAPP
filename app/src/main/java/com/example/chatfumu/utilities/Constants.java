package com.example.chatfumu.utilities;

import java.util.HashMap;

public class Constants {

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGN_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID ="senderid";
    public static final String KEY_RECEIVER_ID = "receiverid";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME ="receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_COLLECTION_POSTS = "Posts";
    public static final String KEY_COLLECTION_FAVORITES= "Favories";
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_PHOTO1 = "Photo1";
    public static final String KEY_PHOTO2 = "Photo2";
    public static final String KEY_PHOTO3 = "Photo3";
    public static final String KEY_PSEUDO = "Pseudo";
    public static final String KEY_PRIX = "Prix";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_FAVORITES_ID = "Favorites";
    public static final String KEY_ITEM = "item";
    public static final String KEY_COLLECTION_SEARCHED_ITEM = "search_item";
    public static final String KEY_COLLECTION_ALERT_ITEM = "alert_item";
    public static final String KEY_PRIX_MAX = "prix_max";
    public static final String KEY_PRIX_MIN = "prix_min";
    public static final String KEY_ARTICLE = "article";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_RECEIVER = "receiver";

    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders(){
        if (remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAHImXIrY:APA91bH3RBZZe9VJGSmyhUmmbODtVYdYo8imxFKi1pGatEO-zW7zZ8YNk0w7m8t6j99_kw7ZK-kz036BZBLPY4n1mdTQViimZp0rXXcmnvRyfVa5ifjCW-ZrKQVGNyVqYx_-s4jbvSjE"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }


}

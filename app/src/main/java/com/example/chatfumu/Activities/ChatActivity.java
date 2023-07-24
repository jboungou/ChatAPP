package com.example.chatfumu.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.chatfumu.Models.ChatMessage;
import com.example.chatfumu.Models.Item;
import com.example.chatfumu.Models.User;
import com.example.chatfumu.Network.ApiClient;
import com.example.chatfumu.Network.ApiService;
import com.example.chatfumu.adapter.ChatAdapter;
import com.example.chatfumu.databinding.ActivityChatBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;
    private Item item;
    private User receiverId_user;
    private User senderId_user;
    private String message;
    private User receiver;
    private User sender;
    private String receiver_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        item = (Item) getIntent().getSerializableExtra(Constants.KEY_ITEM);
        LookForUser();
        message = item.photo1 + "\n" + item.photo2 + "\n" + item.photo3 + "\n" + item.description + "\n" + item.prix;
        if (!message.isEmpty()){
            settleMessage();
        }
        setListeners();
        loadReceiverDetails();
        Init();
        listenMessage();

    }

    private void Init(){
        if (receiverUser != null) {

            preferenceManager = new PreferenceManager(getApplicationContext());
            chatMessages = new ArrayList<>();
            chatAdapter = new ChatAdapter(
                    chatMessages,
                    getBitmapFromEncodedString(receiverUser.image),
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
            binding.chatRecycleView.setAdapter(chatAdapter);
            database = FirebaseFirestore.getInstance();
        }
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiver.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecycleView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void LookForUser(){

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_NAME, item.pseudo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null){
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            receiverId_user = new User();
                            receiverId_user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            receiverId_user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            receiverId_user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            receiverId_user.id = queryDocumentSnapshot.getId();
                        }

                    }/* to be completed*/

                });
    }


    private void settleMessage() {

        sender = (User) getIntent().getSerializableExtra(Constants.KEY_SENDER);
        receiver_name = receiver.name;
        String receiverImage = item.photo1;
        sendAlertMessage(message, sender.id, sender.name, receiver.id, sender.image, receiverImage, receiver.token);
    }



    public void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversionId != null){
            updateConversion(binding.inputMessage.getText().toString());
        }else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }

        if (!isReceiverAvailable){
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
                //sendNotification((String) body.get(message.toString()));
            }catch (Exception exception){
                showToast(exception.getMessage());
            }
        }
        binding.inputMessage.setText(null);
    }

    Item alertitem;

    public void sendAlertMessage(String msg, String sender_id, String sender_name, String receiver_id, String sender_image, String receiverImage, String receiver_token){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, sender_id);
        message.put(Constants.KEY_RECEIVER_ID, receiver_id);
        message.put(Constants.KEY_MESSAGE, msg);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversionId != null){
            updateConversion(msg);
        }else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, sender_id);
            conversion.put(Constants.KEY_SENDER_NAME, sender_name);
            conversion.put(Constants.KEY_SENDER_IMAGE, sender_image);
            conversion.put(Constants.KEY_RECEIVER_ID, receiver_id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiver_name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverImage);
            conversion.put(Constants.KEY_LAST_MESSAGE, msg);
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }

        if (!isReceiverAvailable){
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiver_token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, msg);

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
                //sendNotification((String) body.get(message.toString()));
            }catch (Exception exception){
                showToast(exception.getMessage());
            }
        }
        binding.inputMessage.setText(null);

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    try {
                        if (response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    showToast("Notification sent successfully");
                }else{
                    showToast("Error: " + response.code());
                    //showToast("Notification sent successfully");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void listenAvailabityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receiverUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if(error != null){
                return;
            }
            if (value != null){
                if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                    int availability = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable = availability == 1;
                }
                receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                if (receiverUser.image == null){
                    receiverUser.image = value.getString(Constants.KEY_IMAGE);
                    chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image));
                    chatAdapter.notifyItemRangeChanged(0, chatMessages.size());
                }
            }
            if (isReceiverAvailable){
                binding.textAvailability.setVisibility(View.VISIBLE);
            }else{
                binding.textAvailability.setVisibility(View.GONE);
            }

        });
    }

    private void listenMessage(){

        if (receiverUser != null){
            database.collection(Constants.KEY_COLLECTION_CHAT)
                    .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                    .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                    .addSnapshotListener(eventListener);
            database.collection(Constants.KEY_COLLECTION_CHAT)
                    .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                    .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                    .addSnapshotListener(eventListener);
        }

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, sender.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiver.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiver.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, sender.id)
                .addSnapshotListener(eventListener);

    }
    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if (encodedImage != null){
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }else{
            return null;
        }
   }

    private final EventListener<QuerySnapshot> eventListener = (value, error)->{
        if (error != null){
            return;
        }
        if (value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTme(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(chatMessages, Comparator.comparing(obj -> obj.dateObject));
            }
            if (count == 0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecycleView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecycleView.setVisibility(View.VISIBLE);
        }
        binding.progressbar.setVisibility(View.GONE);
        if (conversionId == null){
            checkForConversion();
        }
    };

    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        if(receiverUser != null){
            binding.textName.setText(receiverUser.name);
        }
        if(!receiver_name.isEmpty()){
            binding.textName.setText(receiver_name);
        }
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(view-> onBackPressed());
        binding.layoutSend.setOnClickListener(view -> sendMessage());
    }

    private String getReadableDateTme(Date date){
        return new SimpleDateFormat("yyyy MM dd - hh:mm", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void updateConversion(String message){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }
    private void checkForConversion(){
        if (chatMessages.size() != 0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);

    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabityOfReceiver();
    }
}
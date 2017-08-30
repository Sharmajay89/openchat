package com.service.openchat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.service.openchat.LoginActivity;
import com.service.openchat.R;
import com.service.openchat.Util;
import com.service.openchat.adapter.ChatFirebaseAdapter;
import com.service.openchat.adapter.ClickListenerChatFirebase;
import com.service.openchat.model.ChatModel;
import com.service.openchat.model.UserModel;

import java.util.Calendar;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
/*
This is use as a chat screen in openchat application
 created by avishvakarma
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener , ClickListenerChatFirebase {
    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    static final String CHAT_REFERENCE = "chatmodel";

    //CLass Model
    private UserModel userModel;

    //Views UI
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btSendMessage,btEmoji;
    private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (!Util.verificaConexao(this)){
            Util.initToast(this,"Você não tem conexão com internet");
            finish();
        }else{
            bindViews();
            verifyLogedInUser();
        }
    }
    /**
     * Vincular views com Java API
     */
    private void bindViews(){
        contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText)findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView)findViewById(R.id.buttonMessage);
        btSendMessage.setOnClickListener(ChatActivity.this);
        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this,contentRoot,edMessage,btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView)findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
    }


    public void verifyLogedInUser(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            String uEmailId = mFirebaseUser.getEmail();
            String uname = null;
            String uId= null;
            String uProfilePic= null;
            if(mFirebaseUser.getDisplayName()!= null){
                uname = mFirebaseUser.getDisplayName();
            }
           if(mFirebaseUser.getUid()!= null){
                uId = mFirebaseUser.getUid();
            }
            if(mFirebaseUser.getPhotoUrl()!= null){
                uProfilePic = mFirebaseUser.getPhotoUrl().toString();
            }
            userModel = new UserModel(uname!= null?uname:uEmailId,uProfilePic != null?uProfilePic:"https://www.google.co.in/imgres?imgurl=https%3A%2F%2Fmedia.licdn.com%2Fmpr%2Fmpr%2Fshrinknp_200_200%2FAAEAAQAAAAAAAAdCAAAAJDQyMmY0OWJjLWMyOTAtNDlkYS1iNThmLTRjODg0YzY1NmM2NQ.jpg&imgrefurl=https%3A%2F%2Fin.linkedin.com%2Fin%2Fajaya-vishvakarma-84538954&docid=OAu_nYNbcdF0HM&tbnid=uz9si_1iQ6docM%3A&vet=10ahUKEwiB3cqQ2e_UAhUBuI8KHS27BmQQMwgiKAAwAA..i&w=200&h=200&itg=1&bih=810&biw=1440&q=ajaya%20vishvakarma&ved=0ahUKEwiB3cqQ2e_UAhUBuI8KHS27BmQQMwgiKAAwAA&iact=mrc&uact=8", uId!= null?uId:"531265374779312ID" );
            fetchMessagensFirebase();
        }
    }

    /**
     * fetch collections chatmodel Firebase
     */
    private void fetchMessagensFirebase(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final ChatFirebaseAdapter firebaseAdapter = new ChatFirebaseAdapter(mFirebaseDatabaseReference.child(CHAT_REFERENCE),userModel.getName(),this);
        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
    }
    /**
     * Enviar msg de texto simples para chat
     */
    private void sendMessageFirebase(){
        ChatModel model = new ChatModel(userModel,edMessage.getText().toString(), Calendar.getInstance().getTime().getTime()+"",null);
        mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(model);
        edMessage.setText(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
        }
    }

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }
}

package com.stephen.websocket.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.stephen.websocket.MyEvent;
import com.stephen.websocket.R;
import com.stephen.websocket.adapter.ChatAppMsgAdapter;
import com.stephen.websocket.adapter.ChatAppMsgDTO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private WebSocket mSocket;
    private EventBus eventBus;
    private String address = "wss://echo.websocket.org";
    private RecyclerView msgRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<ChatAppMsgDTO> msgDtoList;
    private ChatAppMsgAdapter chatAppMsgAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = MainActivity.this;
        init();
        findView();
        setListener();
    }

    public void init() {
        // EventBus
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // Get RecyclerView object.
        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);

        // Set RecyclerView layout manager.
        linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        // Create the initial data list.
        msgDtoList = new ArrayList<ChatAppMsgDTO>();
        ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, "hello");
        msgDtoList.add(msgDto);

        // Create the data adapter with above data list.
        chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);

        // Set data adapter to RecyclerView.
        msgRecyclerView.setAdapter(chatAppMsgAdapter);
    }

    public void findView() {
        getConnectButtonView().setEnabled(true);
        getConnectButtonView().setTextColor(getResources().getColor(R.color.colorBlack));
        getDisconnectButtonView().setEnabled(false);
        getDisconnectButtonView().setTextColor(getResources().getColor(R.color.colorWhite));
        getSendMessageButtonView().setEnabled(false);
        getSendMessageButtonView().setTextColor(getResources().getColor(R.color.colorWhite));
    }


    private void setListener() {
        getConnectButtonView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                connectSocket();
                getConnectButtonView().setEnabled(false);
                getConnectButtonView().setTextColor(getResources().getColor(R.color.colorWhite));
                getDisconnectButtonView().setEnabled(true);
                getDisconnectButtonView().setTextColor(getResources().getColor(R.color.colorBlack));
                getSendMessageButtonView().setEnabled(true);
                getSendMessageButtonView().setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });

        getDisconnectButtonView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.close(1000,"連線結束,感謝您的提問！");
                getConnectButtonView().setEnabled(true);
                getConnectButtonView().setTextColor(getResources().getColor(R.color.colorBlack));
                getDisconnectButtonView().setEnabled(false);
                getDisconnectButtonView().setTextColor(getResources().getColor(R.color.colorWhite));
                getSendMessageButtonView().setEnabled(false);
                getSendMessageButtonView().setTextColor(getResources().getColor(R.color.colorWhite));
                getSendMessageEditView().setText("");
            }
        });

        getSendMessageButtonView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.send(getSendMessageEditView().getText().toString());
                String msgContent = getSendMessageEditView().getText().toString();
                if (!TextUtils.isEmpty(msgContent)) {
                    // Add a new sent message to the list.
                    ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, msgContent);
                    msgDtoList.add(msgDto);

                    int newMsgPosition = msgDtoList.size() - 1;

                    // Notify recycler view insert one new data.
                    chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

                    // Scroll RecyclerView to the last message.
                    msgRecyclerView.scrollToPosition(newMsgPosition);

                    // Empty the input edit text box.
                    getSendMessageEditView().setText("");
                }
            }
        });
    }

    private void connectSocket(){
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Request request = new Request.Builder().url(address).build();
        EchoWebSocketListener socketListener = new EchoWebSocketListener();
        mOkHttpClient.newWebSocket(request, socketListener);
        mOkHttpClient.dispatcher().executorService().shutdown();

    }

    private final class EchoWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mSocket = webSocket;
            String openid = "1";
            //连接成功后，发送登录信息
//            String message = "{\"type\":\"login\",\"user_id\":\"" + openid + "\"}";
//            mSocket.send(message);
//            output("连接成功！");
            eventBus.post(new MyEvent("您好，請問有什麼可以幫助您呢？"));

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
//            output("receive bytes:" + bytes.hex());
            eventBus.post(new MyEvent(bytes.hex()));
        }

        @Override
        public void onMessage(WebSocket webSocket, String string) {
            super.onMessage(webSocket, string);
//            output("receive text:" + text);
            //收到服务器端发送来的信息后，每隔25秒发送一次心跳包
//            final String message = "{\"type\":\"heartbeat\",\"user_id\":\"heartbeat\"}";
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    mSocket.send(message);
//                }
//            }, 25000);
            eventBus.post(new MyEvent(string));
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
//            output("closing:" + reason);
//            eventBus.post(new MyEvent(reason));
            Log.d(TAG, "onClosing: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
//            output("failure:" + t.getMessage());
            Log.d(TAG, "onFailure: " + t.getMessage());
        }


        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
//            output("closed:" + reason);
            eventBus.post(new MyEvent(reason));
        }
    }

    private void output(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, text);
                msgDtoList.add(msgDto);
                int newMsgPosition = msgDtoList.size() - 1;

                // Notify recycler view insert one new data.
                chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

                // Scroll RecyclerView to the last message.
                msgRecyclerView.scrollToPosition(newMsgPosition);
            }
        });
    }

    //Thread mode 有 ASYN, MAIN, POSTING, BACKGROUND 三種選擇，主線程才能修改UI，因此這裡選 MAIN
    //這 onEvent 方法，當有人呼叫 EventBus.getDefault().post(Object event)方法時，就會觸發，並把附帶的資料傳入
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MyEvent event){
        ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, event.getMessage());
        msgDtoList.add(msgDto);
        int newMsgPosition = msgDtoList.size() - 1;

        // Notify recycler view insert one new data.
        chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

        // Scroll RecyclerView to the last message.
        msgRecyclerView.scrollToPosition(newMsgPosition);
    }


    private EditText getSendMessageEditView() {
        return (EditText) findViewById(R.id.edit);
    }

    private Button getConnectButtonView() {
        return (Button) findViewById(R.id.button_connect);
    }

    private Button getDisconnectButtonView() {
        return (Button) findViewById(R.id.button_disconnect);
    }

    private Button getSendMessageButtonView() {
        return (Button) findViewById(R.id.button_send);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //一定要記得取消註冊釋放資源
        eventBus.unregister(this);
    }
}

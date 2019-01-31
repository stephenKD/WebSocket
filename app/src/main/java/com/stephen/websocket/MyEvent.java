package com.stephen.websocket;

public class MyEvent {
    private static final String TAG = "MyEvent";
    //此類別用來當作 EventBus 中傳遞的參數物件型別，可在這裡面定義要傳遞的資料。
    private String message;

    public String getMessage() {
//        Log.d(TAG, "getMessage: ");
        return message;
    }

    public MyEvent(String message){
//        Log.d(TAG, "MyEvent: ");
        this.message = message;
    }
}

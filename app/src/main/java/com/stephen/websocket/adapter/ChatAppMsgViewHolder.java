package com.stephen.websocket.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stephen.websocket.R;

public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

    LinearLayout leftMsgLayout;

    LinearLayout rightMsgLayout;

    TextView leftMsgTextView;

    ImageView leftImageView;

    TextView rightMsgTextView;

    ImageView rightImageView;

    public ChatAppMsgViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_msg_layout);

            leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);

            leftImageView = (ImageView) itemView.findViewById(R.id.imageLeft);

            rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);

            rightImageView = (ImageView) itemView.findViewById(R.id.imageRight);
        }
    }
}

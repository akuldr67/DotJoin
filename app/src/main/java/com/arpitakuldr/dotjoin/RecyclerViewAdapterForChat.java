package com.arpitakuldr.dotjoin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterForChat extends RecyclerView.Adapter<RecyclerViewAdapterForChat.ViewHolder>{

    private  ArrayList<Message> mMessages = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapterForChat(Context mContext, ArrayList<Message> mMessages) {

        this.mMessages = mMessages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("check"," onBindViewHolder called");

        holder.SenderName.setText(mMessages.get(position).getSenderName());
        holder.SentMessage.setText(mMessages.get(position).getMessage());

        holder.chatParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check"," inside onClickListener");
                Toast.makeText(mContext, mMessages.get(position).getSenderName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView SenderName;
        TextView SentMessage;
        RelativeLayout chatParentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            SenderName = itemView.findViewById(R.id.MessageName);
            SentMessage = itemView.findViewById(R.id.MessageText);
            chatParentLayout = itemView.findViewById(R.id.chatItemParentLayout);
        }
    }
}

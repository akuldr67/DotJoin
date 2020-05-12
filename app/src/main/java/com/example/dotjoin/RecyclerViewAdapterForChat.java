package com.example.dotjoin;

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

    private ArrayList<String> mSenderName = new ArrayList<>();
    private ArrayList<String> mSentMessage = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapterForChat(Context mContext, ArrayList<String> mSenderName, ArrayList<String> mSentMessage) {
        this.mSenderName = mSenderName;
        this.mSentMessage = mSentMessage;
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

        holder.SenderName.setText(mSenderName.get(position));

        holder.SentMessage.setText(mSentMessage.get(position));

        holder.chatParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check"," inside onClickListener");
                Toast.makeText(mContext, mSenderName.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSenderName.size();
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

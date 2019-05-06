package com.example.self_chat;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class MessageRecyclerUtils {
    static class MessageCallback
            extends DiffUtil.ItemCallback<Message> {

        @Override
        public boolean areItemsTheSame(@NonNull Message message, @NonNull Message t1) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message message, @NonNull Message t1) {
            return false;
        }
    }



    interface MessageClickCallback {
        void onMessageClick(Message message);
    }


    static class MessageAdapter
            extends ListAdapter<Message, MessageHolder> {

        MessageAdapter() {
            super(new MessageCallback());
        }

        MessageClickCallback callback;

        @NonNull @Override
        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            View itemView =
                    LayoutInflater.from(context)
                            .inflate(R.layout.single_message, parent, false);
            final MessageHolder holder = new MessageHolder(itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = getItem(holder.getAdapterPosition());
                    if (callback != null)
                        callback.onMessageClick(message);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MessageHolder msgHolder, int position) {
            Message message = getItem(position);
            msgHolder.text.setText(message.content);
        }
    }



    static class MessageHolder
            extends RecyclerView.ViewHolder {

        final TextView text;
        MessageHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.msg_content);
        }
    }
}

package com.example.ollamaandroid;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_BOT = 1;
    private static final int VIEW_TYPE_ERROR = 2;
    private static final int VIEW_TYPE_INFO = 3; // 新增：信息类型视图

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == VIEW_TYPE_USER) {
            view = inflater.inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_ERROR) {
            view = inflater.inflate(R.layout.item_error_message, parent, false);
            return new ErrorMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_INFO) { // 新增信息类型判断
            view = inflater.inflate(R.layout.item_info_message, parent, false);
            return new InfoMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).messageTextView.setText(message.getMessage());
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).messageTextView.setText(message.getMessage());
        } else if (holder instanceof ErrorMessageViewHolder) {
            ((ErrorMessageViewHolder) holder).messageTextView.setText(message.getMessage());
        } else if (holder instanceof InfoMessageViewHolder) { // 绑定信息类型视图
            ((InfoMessageViewHolder) holder).messageTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatMessages.get(position).getType()) {
            case ChatMessage.TYPE_USER:
                return VIEW_TYPE_USER;
            case ChatMessage.TYPE_BOT:
                return VIEW_TYPE_BOT;
            case ChatMessage.TYPE_ERROR:
                return VIEW_TYPE_ERROR;
            case ChatMessage.TYPE_INFO: // 新增类型映射
                return VIEW_TYPE_INFO;
            default:
                return VIEW_TYPE_BOT;
        }
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }

    public static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }

    public static class ErrorMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public ErrorMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageTextView.setTextColor(Color.RED);
        }
    }

    // 新增：信息类型ViewHolder
    public static class InfoMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public InfoMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.infoMessageTextView);
            messageTextView.setTextColor(Color.GRAY); // 信息类型文字设为灰色
        }
    }
}
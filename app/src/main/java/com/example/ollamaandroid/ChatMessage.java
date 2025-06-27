package com.example.ollamaandroid;

public class ChatMessage {
    public static final int TYPE_USER = 0;           // 用户消息
    public static final int TYPE_BOT = 1;            // AI回复
    public static final int TYPE_ERROR = 2;          // 一般错误
    public static final int TYPE_INFO = 3;           // 普通通知
    public static final int TYPE_UPLOADING = 4;      // 文件上传中
    public static final int TYPE_UPLOAD_SUCCESS = 5; // 文件上传成功
    public static final int TYPE_UPLOAD_FAILURE = 6; // 文件上传失败

    private String message;
    private int type;

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
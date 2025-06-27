package com.example.ollamaandroid;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_FILE = 1001;
    private static final int REQUEST_PERMISSION = 1002;
    private static final int REQUEST_MANAGE_ALL_FILES = 1003;

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton attachButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private String currentUser;
    private UserManager userManager;
    private final String modelName = "deepseek-r1:70b";
    private final String ollamaUrl = "http://172.16.24.136:11434/api/generate";
    private final String uploadUrl = "http://172.16.24.136:11434/api/upload";
    private final ObjectMapper mapper = new ObjectMapper();

    // 是否显示过权限解释对话框
    private boolean shownPermissionExplanation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userManager = new UserManager(this);
        currentUser = getIntent().getStringExtra("username");
        if (currentUser == null) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupChatAdapter();
        setupSendButton();
        setupAttachButton();
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        attachButton = findViewById(R.id.attachButton);
        setTitle("Ollama 聊天 - " + currentUser);
    }

    private void setupChatAdapter() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(ChatActivity.this, "请输入消息", Toast.LENGTH_SHORT).show();
                return;
            }

            sendMessage(message);
            messageEditText.setText("");
        });
    }

    private void setupAttachButton() {
        attachButton.setOnClickListener(v -> {
            if (hasFileAccessPermission()) {
                openFilePicker();
            } else {
                requestFileAccessPermission();
            }
        });
    }

    private boolean hasFileAccessPermission() {
        // Android 14+ 需要选择性媒体访问权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    == PackageManager.PERMISSION_GRANTED;
        }
        // Android 13+ 需要所有媒体权限
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        }
        // Android 11+ 可能需要特殊存储访问
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                return false;
            }
        }
        // 其他版本需要外部存储权限
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestFileAccessPermission() {
        // 检查是否需要显示权限解释
        if (!shownPermissionExplanation) {
            showPermissionExplanation();
            return;
        }

        // Android 14+ (API 34) 处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // 请求选择性媒体访问权限
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED},
                    REQUEST_PERMISSION);
        }
        // Android 13+ (API 33) 处理
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 请求媒体权限
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.READ_MEDIA_IMAGES,
                            android.Manifest.permission.READ_MEDIA_VIDEO,
                            android.Manifest.permission.READ_MEDIA_AUDIO
                    },
                    REQUEST_PERMISSION);
        }
        // Android 11+ (API 30) 处理
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 请求特殊存储访问权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES);
        }
        // 其他版本处理
        else {
            // 请求标准外部存储权限
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }

    private void showPermissionExplanation() {
        new AlertDialog.Builder(this)
                .setTitle("需要文件访问权限")
                .setMessage("请允许访问设备文件以上传附件。我们只会访问您选择的文件，不会访问其他文件。")
                .setPositiveButton("同意", (dialog, which) -> {
                    shownPermissionExplanation = true;
                    requestFileAccessPermission();
                })
                .setNegativeButton("取消", null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                handlePermissionDenied();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MANAGE_ALL_FILES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 特殊存储访问结果处理
            if (Environment.isExternalStorageManager()) {
                openFilePicker();
            } else {
                handlePermissionDenied();
            }
        }
        else if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri == null) return;

            // 获取文件的持久访问权限
            try {
                getContentResolver().takePersistableUriPermission(fileUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                Log.e("FileAccess", "获取文件持久权限失败", e);
            }

            // 处理文件上传
            handleFileUpload(fileUri);
        }
    }

    private void handlePermissionDenied() {
        new AlertDialog.Builder(this)
                .setTitle("权限被拒绝")
                .setMessage("文件访问权限被拒绝。您需要在设置中启用权限才能上传附件。")
                .setPositiveButton("打开设置", (dialog, which) -> openAppSettings())
                .setNegativeButton("取消", null)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // 仅选择单个文件
        startActivityForResult(Intent.createChooser(intent, "选择文件"), REQUEST_PICK_FILE);
    }

    private void handleFileUpload(Uri fileUri) {
        String fileName = getFileName(fileUri);
        showInfoMessage("正在上传: " + fileName, ChatMessage.TYPE_UPLOADING);

        new Thread(() -> {
            try {
                uploadFile(fileUri);

                runOnUiThread(() -> {
                    removeLastUploadingMessage();
                    showInfoMessage("上传成功: " + fileName, ChatMessage.TYPE_UPLOAD_SUCCESS);

                    // 自动添加附件标记
                    String currentText = messageEditText.getText().toString();
                    messageEditText.setText("[附件:" + fileName + "] " + currentText);
                });
            } catch (Exception e) {
                Log.e("FileUpload", "上传失败", e);
                runOnUiThread(() -> {
                    removeLastUploadingMessage();
                    showInfoMessage("上传失败: " + e.getMessage(), ChatMessage.TYPE_UPLOAD_FAILURE);
                });
            }
        }).start();
    }

    private void showInfoMessage(String text, int type) {
        chatMessages.add(new ChatMessage(text, type));
        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
    }

    private void removeLastUploadingMessage() {
        if (!chatMessages.isEmpty() &&
                chatMessages.get(chatMessages.size() - 1).getType() == ChatMessage.TYPE_UPLOADING) {
            chatMessages.remove(chatMessages.size() - 1);
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void uploadFile(Uri fileUri) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             InputStream inputStream = getContentResolver().openInputStream(fileUri)) {

            if (inputStream == null) {
                throw new IOException("无法打开文件流");
            }

            String fileName = getFileName(fileUri);

            HttpPost request = new HttpPost(uploadUrl);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", inputStream, ContentType.APPLICATION_OCTET_STREAM, fileName);
            builder.addTextBody("username", currentUser);

            request.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    throw new IOException("服务器响应错误: " + statusCode);
                }

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new IOException("服务器无返回内容");
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;

        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                Log.w("FileName", "无法从内容提供器获取文件名", e);
            }
        }

        // 从URI路径解析文件名
        if (fileName == null) {
            String path = uri.getPath();
            if (path != null) {
                int slashIndex = path.lastIndexOf('/');
                if (slashIndex != -1) {
                    fileName = path.substring(slashIndex + 1);
                }
            }
        }

        return fileName != null ? fileName : "未命名文件";
    }

    private void sendMessage(String message) {
        ChatMessage userMessage = new ChatMessage(message, ChatMessage.TYPE_USER);
        chatMessages.add(userMessage);
        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);

        new Thread(() -> {
            try {
                String response = callOllamaModel(message);
                ChatMessage botMessage = new ChatMessage(response, ChatMessage.TYPE_BOT);

                runOnUiThread(() -> {
                    chatMessages.add(botMessage);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                });
            } catch (Exception e) {
                Log.e("APICall", "调用API失败", e);
                runOnUiThread(() -> {
                    ChatMessage errorMessage = new ChatMessage("错误: " + e.getMessage(), ChatMessage.TYPE_ERROR);
                    chatMessages.add(errorMessage);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                });
            }
        }).start();
    }

    private String callOllamaModel(String prompt) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(ollamaUrl);

            // 构造JSON请求
            String requestBody = String.format("{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":false}",
                    modelName, escapeJson(prompt));

            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) return "空响应";

                String result = EntityUtils.toString(entity);
                JsonNode resultNode = mapper.readTree(result);
                return resultNode.path("response").asText("无响应内容");
            } catch (ParseException e) {
                throw new IOException("解析响应失败", e);
            }
        } catch (Exception e) {
            throw new IOException("请求失败: " + e.getMessage(), e);
        }
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_password) {
            Intent intent = new Intent(ChatActivity.this, ChangePasswordActivity.class);
            intent.putExtra("username", currentUser);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Toast.makeText(this, "已登出", Toast.LENGTH_SHORT).show();
        finish();
    }
}
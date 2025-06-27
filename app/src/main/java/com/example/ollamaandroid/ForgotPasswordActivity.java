package com.example.ollamaandroid; // 根据你的项目包名修改

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputLayout emailTextInputLayout;
    private MaterialButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // 初始化视图组件
        emailEditText = findViewById(R.id.emailEditText);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        sendButton = findViewById(R.id.sendButton);

        // 设置返回按钮点击事件
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 设置发送按钮点击事件
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail()) {
                    sendResetEmail();
                }
            }
        });
    }

    // 验证邮箱格式
    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailTextInputLayout.setError("请输入邮箱地址");
            emailEditText.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.setError("请输入有效的邮箱地址");
            emailEditText.requestFocus();
            return false;
        } else {
            emailTextInputLayout.setError(null);
            return true;
        }
    }

    // 发送重置邮件（示例逻辑）
    private void sendResetEmail() {
        String email = emailEditText.getText().toString().trim();

        // 显示加载状态
        sendButton.setEnabled(false);
        sendButton.setText("发送中...");

        // 模拟网络请求延迟
        emailEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 恢复按钮状态
                sendButton.setEnabled(true);
                sendButton.setText("发送重置邮件");

                // 显示成功消息（实际应用中应根据服务器响应显示）
                Toast.makeText(
                        ForgotPasswordActivity.this,
                        "重置密码邮件已发送至 " + email + "，请查收",
                        Toast.LENGTH_LONG
                ).show();

                // 返回登录页面
                finish();
            }
        }, 2000);

        // 实际应用中，这里应该有真实的网络请求代码
        // 例如使用 Firebase Authentication:
        /*
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "重置密码邮件已发送", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "发送失败: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        */
    }
}
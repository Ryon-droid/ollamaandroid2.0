package com.example.ollamaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private Button cancelButton;
    private Button loginButton; // 新增：登录按钮
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG, "onCreate: 注册页面启动");

        userManager = new UserManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        cancelButton = findViewById(R.id.cancelButton);
        loginButton = findViewById(R.id.loginButton); // 新增：查找登录按钮
    }

    private void setupListeners() {
        // 注册按钮逻辑保持不变
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "registerButton 点击");

                    String username = usernameEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                    // 表单验证
                    if (username.isEmpty()) {
                        usernameEditText.setError("用户名不能为空");
                        usernameEditText.requestFocus();
                        return;
                    }

                    if (password.isEmpty()) {
                        passwordEditText.setError("密码不能为空");
                        passwordEditText.requestFocus();
                        return;
                    }

                    if (password.length() < 6) {
                        passwordEditText.setError("密码长度至少6位");
                        passwordEditText.requestFocus();
                        return;
                    }

                    if (!password.equals(confirmPassword)) {
                        confirmPasswordEditText.setError("两次输入的密码不一致");
                        confirmPasswordEditText.requestFocus();
                        return;
                    }

                    // 执行注册
                    Log.d(TAG, "尝试注册用户: " + username);
                    boolean success = userManager.register(username, password);

                    if (success) {
                        Log.d(TAG, "注册成功");
                        Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.d(TAG, "注册失败");
                        Toast.makeText(RegisterActivity.this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "注册异常: " + e.getMessage(), e);
                    Toast.makeText(RegisterActivity.this, "注册异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 取消按钮逻辑保持不变
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 新增：登录按钮逻辑
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "loginButton 点击，返回登录界面");
                finish(); // 关闭当前注册界面，返回上一个界面（登录界面）
            }
        });
    }
}
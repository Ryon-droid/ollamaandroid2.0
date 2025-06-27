package com.example.ollamaandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = new UserManager(this);

        // 绑定视图组件
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // 绑定忘记密码文本视图
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // 设置登录按钮点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    // 显示加载状态
                    setLoginButtonLoading(true);

                    // 模拟登录过程（实际应用中应该是异步操作）
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 模拟网络延迟
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            final boolean loginSuccess = userManager.login(username, password);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 隐藏加载状态
                                    setLoginButtonLoading(false);

                                    if (loginSuccess) {
                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                                        intent.putExtra("username", username);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        // 设置注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // 验证输入
    private boolean validateInputs() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            usernameTextInputLayout.setError("请输入用户名");
            isValid = false;
        } else {
            usernameTextInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordTextInputLayout.setError("请输入密码");
            isValid = false;
        } else if (password.length() < 6) {
            passwordTextInputLayout.setError("密码长度至少为6位");
            isValid = false;
        } else {
            passwordTextInputLayout.setError(null);
        }

        return isValid;
    }

    // 设置登录按钮加载状态
    private void setLoginButtonLoading(boolean isLoading) {
        if (isLoading) {
            loginButton.setText("登录中...");
            loginButton.setEnabled(false);
        } else {
            loginButton.setText("登录");
            loginButton.setEnabled(true);
        }
    }
}
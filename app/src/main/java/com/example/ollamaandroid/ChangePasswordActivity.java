package com.example.ollamaandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button changeButton;
    private Button cancelButton;
    private UserManager userManager;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userManager = new UserManager(this);
        username = getIntent().getStringExtra("username");

        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changeButton = findViewById(R.id.changeButton);
        cancelButton = findViewById(R.id.cancelButton);





        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userManager.changePassword(username, oldPassword, newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finishAffinity(); // 关闭所有Activity
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "原密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
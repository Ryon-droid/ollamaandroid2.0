package com.example.ollamaandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.Toast;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserManager {
    private static final String TAG = "UserManager";
    private static final String USERS_PREFS = "ollama_users";
    private static final String KEY_ALIAS = "ollama_key";
    private SharedPreferences encryptedPrefs;
    private Context context;

    public UserManager(Context context) {
        this.context = context;
        Log.d(TAG, "初始化 UserManager");
        try {
            initEncryptedStorage();
        } catch (Exception e) {
            Log.e(TAG, "构造函数异常: " + e.getMessage(), e);
            Toast.makeText(context, "用户管理初始化失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 初始化加密存储
    private void initEncryptedStorage() {
        try {
            Log.d(TAG, "开始初始化加密存储");

            // 检查 Android 版本，低于 API 23 的设备不支持
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                throw new RuntimeException("设备系统版本过低，不支持加密存储");
            }

            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build();

            String masterKeyAlias = MasterKeys.getOrCreate(spec);
            Log.d(TAG, "MasterKey 生成成功: " + masterKeyAlias);

            encryptedPrefs = EncryptedSharedPreferences.create(
                    USERS_PREFS,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            Log.d(TAG, "加密存储初始化成功");

            // 验证存储是否可用
            encryptedPrefs.edit().putString("test_key", "test_value").apply();
            String testValue = encryptedPrefs.getString("test_key", null);
            if (testValue == null) {
                throw new RuntimeException("加密存储写入/读取失败");
            }

        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "加密存储初始化异常: " + e.getMessage(), e);
            Toast.makeText(context, "加密存储错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // 回退到普通 SharedPreferences（仅用于调试）
            encryptedPrefs = context.getSharedPreferences(USERS_PREFS, Context.MODE_PRIVATE);
            Log.w(TAG, "已回退到普通 SharedPreferences");
        }
    }

    // 注册新用户
    public boolean register(String username, String password) {
        if (encryptedPrefs == null) {
            Log.e(TAG, "register: 加密存储未初始化");
            return false;
        }

        if (username.isEmpty() || password.isEmpty()) {
            Log.e(TAG, "register: 用户名或密码为空");
            return false;
        }

        if (password.length() < 6) {
            Log.e(TAG, "register: 密码长度不足");
            return false;
        }

        try {
            if (encryptedPrefs.contains(username)) {
                Log.d(TAG, "register: 用户名已存在: " + username);
                return false;
            }

            String encryptedPassword = encryptPassword(password);
            encryptedPrefs.edit().putString(username, encryptedPassword).apply();
            Log.d(TAG, "register: 用户注册成功: " + username);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "register: 注册异常: " + e.getMessage(), e);
            Toast.makeText(context, "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 用户登录
    public boolean login(String username, String password) {
        if (encryptedPrefs == null) {
            Log.e(TAG, "login: 加密存储未初始化");
            return false;
        }

        try {
            String storedPassword = encryptedPrefs.getString(username, null);
            if (storedPassword == null) {
                Log.d(TAG, "login: 用户不存在: " + username);
                return false;
            }
            boolean result = storedPassword.equals(encryptPassword(password));
            Log.d(TAG, "login: 登录结果: " + result + ", 用户: " + username);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "login: 登录异常: " + e.getMessage(), e);
            Toast.makeText(context, "登录失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 修改密码
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (encryptedPrefs == null) {
            Log.e(TAG, "changePassword: 加密存储未初始化");
            return false;
        }

        try {
            String storedPassword = encryptedPrefs.getString(username, null);
            if (storedPassword == null || !storedPassword.equals(encryptPassword(oldPassword))) {
                Log.d(TAG, "changePassword: 原密码错误: " + username);
                return false;
            }

            encryptedPrefs.edit().putString(username, encryptPassword(newPassword)).apply();
            Log.d(TAG, "changePassword: 密码修改成功: " + username);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "changePassword: 修改密码异常: " + e.getMessage(), e);
            Toast.makeText(context, "修改密码失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 密码加密（SHA-256）
    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "encryptPassword: 加密算法异常: " + e.getMessage(), e);
            throw new RuntimeException("密码加密失败", e);
        }
    }
}
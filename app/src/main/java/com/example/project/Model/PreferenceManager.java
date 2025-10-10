package com.example.project.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("chatAPPPreference", Context.MODE_PRIVATE);//chỉ có thể được truy cập bởi ứng dụng của bạn và không được chia sẻ với các ứng dụng khác.
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }
    public void putDouble(String key, double value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, String.valueOf(value));
        editor.apply();
    }

    public double getDouble(String key) {
        String value = sharedPreferences.getString(key, null);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0.0; // giá trị mặc định nếu chưa có
    }
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

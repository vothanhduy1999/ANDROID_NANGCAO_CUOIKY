package com.example.vothanhduy_1706020015;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//service làm đăng xuất app khi xóa tác vụ lướt qua//
@SuppressLint("Registered")
public class Clean_App extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        LoginActivity.mGoogleSignInClient.signOut();
        stopSelf();
    }
}

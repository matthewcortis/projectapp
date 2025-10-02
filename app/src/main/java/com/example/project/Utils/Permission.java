package com.example.project.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.project.R;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class Permission {
    private final Context mContext;
    private final Activity mActivity;

    public Permission(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity;
    }

    public Permission(Fragment fragment) {
        this.mActivity = fragment.requireActivity();
        this.mContext = fragment.requireContext();
    }

    public void checkConnectInternet(ImageView internet_status, TextView text_status_internet){
        boolean isConnect = isConnectToInternet();

        if (isConnect){
            internet_status.setVisibility(View.GONE);
            text_status_internet.setVisibility(View.GONE);
        }else {
            internet_status.setVisibility(View.VISIBLE);
            text_status_internet.setVisibility(View.VISIBLE);
        }
    }

    private boolean isConnectToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    public long getFileSize(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
        assert pfd != null;
        FileDescriptor fd = pfd.getFileDescriptor();
        FileInputStream fis = new FileInputStream(fd);
        long fileSize = fis.getChannel().size();
        fis.close();
        pfd.close();
        return fileSize;
    }
    public  String formatFileSize(long bytes, boolean inMB) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        if (inMB) {
            double fileSizeInMB = bytes / (1024.0 * 1024);
            return decimalFormat.format(fileSizeInMB) + " MB";
        } else {

            double fileSizeInKB = bytes / 1024.0;
            return decimalFormat.format(fileSizeInKB) + " KB";
        }
    }

}

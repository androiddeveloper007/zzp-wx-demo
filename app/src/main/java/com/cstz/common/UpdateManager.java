package com.cstz.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.cstz.common.view.MyProgressDialog;
import com.cstz.tools.FileTools;
import com.cstz.tools.ToastMakeText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateManager {
    private Context mContext;
    // 返回的安装包url
    private String apkUrl = null;
    /* 下载包安装路径 */
    private static String savePath = "";///sdcard/sp2pAndroid/
    private static String saveFileName = "";//savePath+ "cstz_android.apk";
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int SHUT_DOWN = 3;
    private int progress;
    private Thread downLoadThread;
    private MyProgressDialog mDialog;
    private boolean interceptFlag = false;
    private boolean forceUpdate = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mDialog.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                case SHUT_DOWN:
                    SysApplication.getInstance().exit();
                    break;
                default:break;
            }
        }

        ;
    };

    public UpdateManager(Context context, String apkUrl) {
        this.mContext = context;
        this.apkUrl = apkUrl;
    }

    public UpdateManager(Context context, String apkUrl, boolean forceUpdate) {
        this.mContext = context;
        this.apkUrl = apkUrl;
        this.forceUpdate = forceUpdate;
    }

    public void start() {
        mDialog = new MyProgressDialog(mContext);
        mDialog.setCallback(new MyProgressDialog.ProgressCallback() {
            @Override
            public void callback() {
                downloadApk();
            }
            @Override
            public void cancel() {
                interceptFlag = true;
                mDialog.dismiss();
                if(forceUpdate)
                    mHandler.sendEmptyMessage(SHUT_DOWN);//强制更新时，关闭下载界面，直接退出app
            }
        });
        mDialog.show();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                String path = "";
                File file;
                if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
                    file = mContext.getExternalCacheDir();
                } else {
                    path = mContext.getFilesDir().getPath();
                    file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
                String apkFile = "/cstz_android.apk";
                saveFileName = mContext.getExternalCacheDir() + apkFile;
                if (FileTools.FileExists(saveFileName)) {
                    FileTools.deleteFile(saveFileName);
                }
                File ApkFile = new File(file, apkFile);
                ApkFile.createNewFile();
                if (!ApkFile.isFile())
                    return;
                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                ToastMakeText.showToast((Activity) mContext, "下载出错！请至官网下载最新版", 3000);
                e.printStackTrace();
            } catch (IOException e) {
                ToastMakeText.showToast((Activity) mContext, "下载出错！请至官网下载最新版", 3000);
                e.printStackTrace();
            }
        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//added by ZhuZhiPeng安装之后直接打开
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
        mDialog.dismiss();
        if (forceUpdate)
            mHandler.sendEmptyMessageDelayed(SHUT_DOWN,500);
    }
}

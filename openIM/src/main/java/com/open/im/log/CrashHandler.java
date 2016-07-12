package com.open.im.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.open.im.utils.MyLog;
import com.open.im.utils.MyNetUtils;
import com.open.im.utils.ThreadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
    private String receiveEmail;
    private static final String TAG = "OpenIM";
    private UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler mInstance = new CrashHandler();
    private Context mContext;
    private Map<String, String> mLogInfo = new HashMap();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return mInstance;
    }

    public void init(Context paramContext, String receiveEmail) {
        this.receiveEmail = receiveEmail;
        this.mContext = paramContext;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 捕获异常
     *
     * @param paramThread
     * @param paramThrowable
     */
    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
        if (this.handleException(paramThrowable)) {
            if (this.mDefaultHandler != null) {
                // 系统处理异常
                this.mDefaultHandler.uncaughtException(paramThread, paramThrowable);
            } else {
                Process.killProcess(Process.myPid());
                //不为零表示非正常退出
                System.exit(1);
            }
        }
    }

    /**
     * 如果有异常 则返回true 如果没异常 则返回false
     *
     * @param paramThrowable
     * @return
     */
    public boolean handleException(final Throwable paramThrowable) {
        if (paramThrowable == null) {
            return false;
        } else {
            ThreadUtil.runOnBackThread(new Runnable() {
                @Override
                public void run() {
                    CrashHandler.this.getDeviceInfo(CrashHandler.this.mContext);
                    MyLog.showLog("保存日志前");
                    CrashHandler.this.saveCrashLogToFile(paramThrowable);
                    MyLog.showLog("发送邮件");
                }
            });
            return true;
        }
    }

    /**
     * 保存异常到本地log 同时发送邮件到指定的邮箱
     *
     * @param ex
     * @return
     */
    private String saveCrashLogToFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        long timestamp = System.currentTimeMillis();
        String time = mSimpleDateFormat.format(new Date());
        String logBegin = "crash---" + time + "---" + timestamp + "---";
        String logEnd = "---crash---end---";
        sb.append(logBegin + "\n");
        for (Map.Entry<String, String> entry : mLogInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        sb.append(logEnd + "\n");
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exiu/crash/";
//                String dirPath = Environment.getDataDirectory().getAbsolutePath() + "/";
//                String dirPath = mContext.getCacheDir().getAbsolutePath() + "/log/";
//                MyLog.showLog("path::" + dirPath);
                String logFileName = "crash-log.txt";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(dirPath + logFileName, true);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return time;
        } catch (Exception e) {
            Log.e(TAG, "保存日志时出现错误", e);
        } finally {
            if (MyNetUtils.isNetworkConnected(mContext)) {
                MyLog.showLog("发送邮件前");
                sentEmail(sb.toString());
                MyLog.showLog("发送邮件后");
            }
        }
        return null;
    }

    /**
     * 方法 发送邮件
     *
     * @param paramThrowable
     */
    public void sentEmail(String paramThrowable) {
        try {
            MailSenderInfo e = new MailSenderInfo();
            e.setMailServerHost("smtp.qq.com");
            MyLog.showLog("到这儿没");
            e.setMailServerPort("25");
            e.setValidate(true);
//            e.setUserName("2171565576@qq.com");
//            e.setPassword("69yaya");
//            e.setFromAddress("2171565576@qq.com");
            e.setUserName("480474041@qq.com");
            e.setPassword("qwer159632");
            e.setFromAddress("480474041@qq.com");
            e.setToAddress(this.receiveEmail);
            // TODO
            e.setSubject("OpenIM have bugs  " + this.mSimpleDateFormat.format(new Date()));
            e.setContent(paramThrowable);
            SimpleMailSender sms = new SimpleMailSender();
            sms.sendTextMail(e);
        } catch (Exception var4) {
            Log.e("SendMail", var4.getMessage(), var4);
        }

    }
    /**
     * 方法 获取设备信息
     *
     * @param ctx
     */
    private void getDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mLogInfo.put("versionName", versionName);
                mLogInfo.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "获取设备信息时出错", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mLogInfo.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "获取缓存信息时出错", e);
            }
        }
    }

//    /**
//     * 方法 获取设备的信息
//     *
//     * @param paramContext
//     */
//    public void getDeviceInfo(Context paramContext) {
//        try {
//            PackageManager mFields = paramContext.getPackageManager();
//            PackageInfo field = mFields.getPackageInfo(paramContext.getPackageName(), 1);
//            if (field != null) {
//                String versionName = field.versionName == null ? "null" : field.versionName;
//                String versionCode = String.valueOf(field.versionCode);
//                this.mLogInfo.put("versionName", versionName);
//                this.mLogInfo.put("versionCode", versionCode);
//            }
//        } catch (NameNotFoundException var10) {
//            var10.printStackTrace();
//        }
//
//        Field[] var11 = Build.class.getDeclaredFields();
//        Field[] var6 = var11;
//        int var14 = var11.length;
//
//        for (int var13 = 0; var13 < var14; ++var13) {
//            Field var12 = var6[var13];
//
//            try {
//                var12.setAccessible(true);
//                this.mLogInfo.put(var12.getName(), var12.get("").toString());
//                Log.d("OpenIM", var12.getName() + ":" + var12.get(""));
//            } catch (IllegalArgumentException var8) {
//                var8.printStackTrace();
//            } catch (IllegalAccessException var9) {
//                var9.printStackTrace();
//            }
//        }
//    }

//    private String saveCrashLogToFile(Throwable paramThrowable) {
//        StringBuffer mStringBuffer = new StringBuffer();
//        Iterator mPrintWriter = this.mLogInfo.entrySet().iterator();
//
//        String mResult;
//        while(mPrintWriter.hasNext()) {
//            Entry mWriter = (Entry)mPrintWriter.next();
//            String mThrowable = (String)mWriter.getKey();
//            mResult = (String)mWriter.getValue();
//            mStringBuffer.append(mThrowable + "=" + mResult + "\r\n");
//        }
//
//        StringWriter mWriter1 = new StringWriter();
//        PrintWriter mPrintWriter1 = new PrintWriter(mWriter1);
//        paramThrowable.printStackTrace(mPrintWriter1);
//        paramThrowable.printStackTrace();
//
//        for(Throwable mThrowable1 = paramThrowable.getCause(); mThrowable1 != null; mThrowable1 = mThrowable1.getCause()) {
//            mThrowable1.printStackTrace(mPrintWriter1);
//            mPrintWriter1.append("\r\n");
//        }
//
//        mPrintWriter1.close();
//        mResult = mWriter1.toString();
//        mStringBuffer.append(mResult);
//        this.sentEmail(mStringBuffer.toString());
//        return null;
//    }
}

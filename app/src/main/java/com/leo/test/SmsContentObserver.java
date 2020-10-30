package com.leo.test;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: leo zhangwei
 * @Description: java类作用描述
 * @CreateDate: 2020/10/30 2:32 PM
 */
public class SmsContentObserver extends ContentObserver {

    private SmsListener listener;
    private Context context;
    private String code;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsContentObserver(Context context, Handler handler, SmsListener listener) {
        super(handler);
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, @Nullable Uri uri) {
        super.onChange(selfChange, uri);
        // 第一次回调 不是我们想要的 直接返回
        if (uri.toString().contains("content://sms/raw")) {
            return;
        }
        // 第二次回调 查询收件箱里的内容
        Uri inboxUri = Uri.parse("content://sms/inbox");
        // 按时间顺序排序短信数据库
        Cursor cursor = context.getContentResolver().query(inboxUri, null, null,
                null, "date desc");


        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int index_Address = cursor.getColumnIndex("address");
                int index_Person = cursor.getColumnIndex("person");
                int index_Body = cursor.getColumnIndex("body");
                int index_Date = cursor.getColumnIndex("date");
                int index_Type = cursor.getColumnIndex("type");

                do {
                    // 获取手机号
                    String address = cursor.getString(index_Address);
                    Log.e("ADDRESS", address);
                    // 获取短信内容
                    String body = cursor.getString(index_Body);
                    Log.e("ADDRESS", body);
                    // 判断手机号是否为目标号码，服务号号码不固定请用正则表达式判断前几位。
                    //加上这个判断必须知道发送方的电话号码，局限性比较高
//                if (!address.equals(mPhone)) {
//                    return;
//                }

                    String startString = body.substring(0, 10);
                    if (!startString.contains("农业银行")) {
                        continue;
                    }

                    // 正则表达式截取短信中的6位验证码
                    String regEx = "(?<![0-9])([0-9]{" + 6 + "})(?![0-9])";
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(body);
                    // 如果找到通过Handler发送给主线程
                    while (matcher.find()) {
                        code = matcher.group();
                        if (listener != null) {
                            listener.onResult(code);
                            cursor.close();
                            return;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    /**
     * 短信回调接口
     */
    public interface SmsListener {
        /**
         * @param result 回调内容（验证码）
         */
        void onResult(String result);
    }
}

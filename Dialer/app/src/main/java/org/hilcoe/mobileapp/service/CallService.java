package org.hilcoe.mobileapp.service;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import org.hilcoe.mobileapp.Util.Consumer;
import org.hilcoe.mobileapp.domainmodel.CallLog;
import org.hilcoe.mobileapp.domainmodel.Callable;
import org.hilcoe.mobileapp.domainmodel.Contact;
import org.hilcoe.mobileapp.domainmodel.RawNumber;

import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallService
{
    private static String CHECK_BALANCE_URI="*804#";
    private static String  RECHARGE_BALANCE_STRING="*805*%s#";
    private static String SEND_CALL_ME_BACK_STRING="*807*%s#";
    private static String TRANSFER_BALANCE_STRING="*806*%s*%s#";
    private static CallService callService;
    private ContactService contactService;
    private List<CallLog> callLogList;
    private long logVersion=1;
    private long myVersion=0;
    private OnCallLogChangeListener onCallLogChangeListener;
    private ExecutorService executorService;
    public interface OnCallLogChangeListener
    {
        void onUpdate();
    }
    static
    {
        callService=new CallService();
    }
    private CallService()
    {
        executorService= Executors.newSingleThreadExecutor();
        contactService= ContactService.getInstance();
    }
    public static CallService getInstance()
    {
        return callService;
    }
    private CallLog.CALL_TYPE convertCallType(int callType)
    {
        switch (callType)
        {
            case android.provider.CallLog.Calls.MISSED_TYPE: return CallLog.CALL_TYPE.MISSED_CALL;
            case android.provider.CallLog.Calls.REJECTED_TYPE: return CallLog.CALL_TYPE.REJECTED;
            case android.provider.CallLog.Calls.OUTGOING_TYPE: return CallLog.CALL_TYPE.OUTGOING_CALL;
            case android.provider.CallLog.Calls.INCOMING_TYPE: return CallLog.CALL_TYPE.INCOMING_CALL;
            default:throw new IllegalArgumentException("unknown call type "+callType);
        }
    }
    private synchronized void loadList(Context context)
    {
        if(myVersion!=logVersion)
        {
            this.callLogList = new ArrayList<>();
            ContentResolver cr = context.getContentResolver();
            contactService.getContactByPoneNumber(new RawNumber("1123123123"),context);//to load the contacts on a separate thread
            Cursor cursor = cr.query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
            int phoneNumberIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
            int callTypeIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
            int dateIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
            Contact temp;
            while (cursor.moveToNext())
            {
                try
                {
                    temp=contactService.getContactByPoneNumber(new RawNumber(cursor.getString(phoneNumberIdx)),context).get();
                    this.callLogList.add(new CallLog(cursor.getString(phoneNumberIdx).replaceAll(" ",""),
                            convertCallType(cursor.getInt(callTypeIdx)),
                            new Date(Long.valueOf(cursor.getString(dateIdx))),
                            temp==null?null:temp.getName()));
                }
                catch (Exception e)
                {
                    //log
                }
            }
            cursor.close();
            myVersion++;
            cr.registerContentObserver(android.provider.CallLog.Calls.CONTENT_URI, true, new LogChangeObserver(new Handler(Looper.getMainLooper())));
        }
    }
    private void call(Context context, String data)
    {
        call(context,Uri.parse("tel:"+Uri.encode(data)));
    }
    private void call(Context context, Uri data)
    {
        Intent intent=new Intent(Intent.ACTION_CALL,data);
        context.startActivity(intent);
    }
    public void setUpdateListener(OnCallLogChangeListener listener)
    {
        this.onCallLogChangeListener=listener;
    }
    public void requestBalance(Context context)
    {
        call(context,CHECK_BALANCE_URI);
    }
    public void call(Context context, Callable number)
    {
        if(number.getPhoneNumber().length()==14&&number.getPhoneNumber().matches("[0-9]*"))
        {
            //probably recharging balance
            call(context,new Formatter().format(RECHARGE_BALANCE_STRING,number.getPhoneNumber()).toString());
        }
        else
        {
            call(context, number.getPhoneNumber());
        }
    }
    public void sendCallMeBack(Context context, Callable number)
    {
        call(context, new Formatter().format(SEND_CALL_ME_BACK_STRING,number.getPhoneNumberWithoutCode()).toString());
    }
    public void transfer(Context context, Callable number, int amount)
    {
        call(context, new Formatter().format(TRANSFER_BALANCE_STRING,number.getPhoneNumberWithoutCode(),amount).toString());
    }
    public void addToContact(Context context, Callable number)
    {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if(number!=null)
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, number.getPhoneNumber());
        context.startActivity(intent);
    }
    public void sendMessage(Context context, Callable number)
    {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", number.getPhoneNumber());
        context.startActivity(smsIntent);
    }
    private List<CallLog> getCallLogs(Context context)
    {
        if(this.myVersion!=logVersion)
        {
            loadList(context);
            myVersion=logVersion;
        }
        return callLogList;
    }
    public  void getCallLogsAsync(Context context, Consumer<List<CallLog>> callback)
    {
        this.executorService.submit(() -> {
            callback.accept(getCallLogs(context));
        });
    }
    private class LogChangeObserver extends ContentObserver
    {
        public LogChangeObserver(Handler handler)
        {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange, @Nullable Uri uri)
        {
            CallService.this.logVersion++;
            if(CallService.this.onCallLogChangeListener!=null)
            {
                CallService.this.onCallLogChangeListener.onUpdate();
            }
        }
    }
}

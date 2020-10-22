package org.hilcoe.mobileapp.domainmodel;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallLog implements Callable
{
    private static final String UNSAVED_PHONE_NUMBER="Unknown";
    public enum CALL_TYPE
    {
        MISSED_CALL, OUTGOING_CALL, INCOMING_CALL, REJECTED
    }
    private  CALL_TYPE type;
    private String phoneNumber;
    private Date dateTime;
    private String name;
    public CallLog(String phoneNumber, CALL_TYPE type, Date dateTime, String name)
    {
        this.dateTime=dateTime;
        this.name=name;
        this.phoneNumber=phoneNumber;
        this.type=type;
    }
    public CallLog(String phoneNumber, CALL_TYPE type, Date dateTime)
    {
        this(phoneNumber,type,dateTime,null);
    }

    @Override
    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }
    public String getCallTime()
    {
        Date now = new Date();
        String result;
        SimpleDateFormat format=new SimpleDateFormat();
        if(now.getYear()==this.dateTime.getYear())
        {
            if(now.getMonth()==this.dateTime.getMonth())
            {
                if(now.getDate()==this.dateTime.getDate())
                {
                    format.applyPattern("hh:mm");
                    return "Today "+format.format(this.dateTime);
                }
                else if((now.getDate()-this.dateTime.getDate())==1)
                {
                    format.applyPattern("hh:mm");
                    return "Yesterday "+format.format(this.dateTime);
                }
                else
                {
                    format.applyPattern("dd/hh:mm");
                }
            }
            else
            {
                format.applyPattern("MM/dd hh:mm");
            }
        }
        else
        {
            format=new SimpleDateFormat("YY/MM/dd hh:mm");
        }
        return format.format(this.dateTime);
    }
    @Override
    public boolean isSaved()
    {
        return this.name!=null;
    }
    public String getName()
    {
        return isSaved()?this.name:UNSAVED_PHONE_NUMBER;
    }
    public CALL_TYPE getCallType()
    {
        return this.type;
    }

    @NonNull
    @Override
    public String toString()
    {
        return this.name;
    }
}

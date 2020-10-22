package org.hilcoe.mobileapp.domainmodel;

public interface Callable
{
    String getPhoneNumber();
    boolean isSaved();
    default String getPhoneNumberWithoutCode()
    {
        String phoneNumber=getPhoneNumber();
        if(phoneNumber.startsWith("+")&&phoneNumber.length()>3)
        {
            phoneNumber="0"+phoneNumber.substring(4);
        }
        return phoneNumber;
    }
}

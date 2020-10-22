package org.hilcoe.mobileapp.domainmodel;

public class RawNumber implements Callable
{
    private String phoneNumber;

    public RawNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }

    @Override
    public boolean isSaved()
    {
        return false;
    }
}

package org.hilcoe.mobileapp.domainmodel;

public class Contact implements Callable
{
    private String phoneNumber,name;
    public Contact(String phoneNumber,String name)
    {
        this.phoneNumber=phoneNumber;
        this.name=name;
    }
    @Override
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean isSaved()
    {
        return true;
    }
}

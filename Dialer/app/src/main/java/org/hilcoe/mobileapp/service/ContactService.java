package org.hilcoe.mobileapp.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import org.hilcoe.mobileapp.Util.Consumer;
import org.hilcoe.mobileapp.domainmodel.Callable;
import org.hilcoe.mobileapp.domainmodel.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ContactService
{
    private static ContactService contactService;
    private ExecutorService executorService;
    static
    {
        contactService=new ContactService();
    }
    private List<Contact> contactList;
    private Map<String, Contact> contactMap;
    private ContactService()
    {
        executorService= Executors.newSingleThreadExecutor();
        contactMap=new HashMap<>();
    }
    public static ContactService getInstance()
    {
        return contactService;
    }
    public void getContactListAsync(Context context, Consumer<List<Contact>> callback)
    {
        executorService.submit(() -> {
            callback.accept(getContactList(context));
        });
    }
    public List<Contact> getContactList(Context context)
    {
        if(contactList==null)loadList(context);
        return contactList;
    }
    //this may block, so return Future
    public Future<Contact> getContactByPoneNumber(Callable callable,Context context)
    {
        return executorService.submit(() -> {
            if(contactList==null)loadList(context);
            return contactMap.get(callable.getPhoneNumberWithoutCode());
        });
    }
    private synchronized void loadList(Context context)
    {
        if(contactList==null)
        {
            contactList = new ArrayList<>();
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cur == null) return;
            int nameIdx = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int hasPhoneIdx = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int idIdx = cur.getColumnIndex(ContactsContract.Contacts._ID);
            Contact tempContact;
            while (cur.moveToNext())
            {
                if (cur.getInt(hasPhoneIdx) > 0)
                {
                    //has phone number;
                    Cursor numberCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{cur.getString(idIdx)}, null);
                    if (numberCursor != null && numberCursor.moveToNext())
                    {
                        String phoneNumber = numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        tempContact = new Contact(phoneNumber.replaceAll(" ",""), cur.getString(nameIdx));
                        contactList.add(tempContact);
                        numberCursor.close();
                        contactMap.put(tempContact.getPhoneNumberWithoutCode(), tempContact);
                    }
                }
            }
            cur.close();
            Collections.sort(contactList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        }
    }
}

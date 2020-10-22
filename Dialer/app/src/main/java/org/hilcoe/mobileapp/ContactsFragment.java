package org.hilcoe.mobileapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.hilcoe.mobileapp.Util.CallPopupMenuManager;
import org.hilcoe.mobileapp.adapter.ContactAdapter;
import org.hilcoe.mobileapp.domainmodel.Contact;
import org.hilcoe.mobileapp.service.CallService;
import org.hilcoe.mobileapp.service.ContactService;

import java.util.List;

public class ContactsFragment extends Fragment
{
    private CallPopupMenuManager popupMenuManager=new CallPopupMenuManager();
    private ContactService contactService=ContactService.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.contacts_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        load();
        getView().findViewById(R.id.add_contact_fab).setOnClickListener(v -> CallService.getInstance().addToContact(v.getContext(),null));
    }
    private void load()
    {
        this.contactService.getContactListAsync(this.getContext(),contacts -> {
            new Handler(Looper.getMainLooper()).post(() -> updateRecyclerView(contacts));
        });
    }
    private void updateRecyclerView(List<Contact> contacts)
    {
        RecyclerView rv=ContactsFragment.this.getView().findViewById(R.id.contact_list);
        ContactAdapter contactAdapter=new ContactAdapter(contacts);
        rv.setAdapter(contactAdapter);
        contactAdapter.setOnClickListener((view, contact) -> popupMenuManager.showMenu(ContactsFragment.this.getActivity(),view,contact));
        rv.setLayoutManager(new LinearLayoutManager(ContactsFragment.this.getContext()));
    }
}

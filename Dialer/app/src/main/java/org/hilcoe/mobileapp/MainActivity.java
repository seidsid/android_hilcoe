package org.hilcoe.mobileapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import org.hilcoe.mobileapp.domainmodel.CallLog;
import org.hilcoe.mobileapp.domainmodel.Contact;
import org.hilcoe.mobileapp.service.CallService;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    CallService callService;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        callService=CallService.getInstance();
        setToolbar();
    }
    private void init()
    {
        ViewPager vp=findViewById(R.id.view_pager);
        vp.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        ((TabLayout)findViewById(R.id.tab_layout)).setupWithViewPager(vp);
    }
    private void setToolbar()
    {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId()==R.id.check_balance_menu)
        {
            this.callService.requestBalance(this);
        }
        return true;
    }

    private void requestPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},101);
        }
        else
        {
            init();
        }
    }
    private class PagerAdapter extends FragmentPagerAdapter
    {
        private String[] titles={"Calls","Contacts"};
        PagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles[position];
        }

        @Override
        public Fragment getItem(int i)
        {
            switch (i)
            {
                case 0: return new CallLogFragment();
                case 1: return new ContactsFragment();
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 2;
        }
    }
}
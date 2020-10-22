package org.hilcoe.mobileapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.hilcoe.mobileapp.Util.CallPopupMenuManager;
import org.hilcoe.mobileapp.adapter.CallLogAdapter;
import org.hilcoe.mobileapp.domainmodel.CallLog;
import org.hilcoe.mobileapp.service.CallService;

import java.util.List;

public class CallLogFragment extends Fragment
{
    private CallService callService= CallService.getInstance();
    private CallPopupMenuManager callPopupMenuManager =new CallPopupMenuManager();
    private long myCallLogVersion=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.call_log_fragment,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        load();
        callService.setUpdateListener(() -> load());
        getView().findViewById(R.id.dail_pad_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(),Dialer.class));
        });
    }
    private void load()
    {
        callService.getCallLogsAsync(this.getContext(),logs -> {
            new Handler(Looper.getMainLooper()).post(() -> loadRecyclerView(logs));
        });
    }
    private void loadRecyclerView(List<CallLog> logs)
    {
        RecyclerView rv=((RecyclerView)CallLogFragment.this.getView().findViewById(R.id.call_log_recycler_view));
        CallLogAdapter adapter=(CallLogAdapter)rv.getAdapter();
        if(adapter==null)
        {
            adapter=new CallLogAdapter(logs);
            adapter.setOnClickListner((view, callLog) -> callPopupMenuManager.showMenu(CallLogFragment.this.getActivity(),view,callLog));
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(CallLogFragment.this.getView().getContext()));
        }
        else
        {
            adapter.changeList(logs);
            adapter.notifyDataSetChanged();
        }
    }
}

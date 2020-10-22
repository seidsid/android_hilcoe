package org.hilcoe.mobileapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hilcoe.mobileapp.R;
import org.hilcoe.mobileapp.domainmodel.CallLog;

import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>
{
    @FunctionalInterface
    public interface OnClickListener
    {
        void onClick(View view, CallLog callLog);
    }
    private List<CallLog> callLogs;
    private OnClickListener clickListener;
    public CallLogAdapter(List<CallLog> callLogs)
    {
        this.callLogs=callLogs;
    }
    public void changeList(List<CallLog> newList)
    {
        this.callLogs=newList;
    }
    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new CallLogViewHolder((CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calllog_cardview,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder callLogViewHolder, int i)
    {
        callLogViewHolder.bind(this.callLogs.get(i));
    }

    @Override
    public int getItemCount()
    {
        return this.callLogs.size();
    }
    public void setOnClickListner(OnClickListener listener)
    {
        this.clickListener=listener;
    }
    public class CallLogViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameTextView;
        private TextView callTimeTextView;
        private TextView phoneNumberTextView;
        private ImageView callTypeImageView;
        private CardView cardView;
        public CallLogViewHolder(@NonNull CardView itemView)
        {
            super(itemView);
            this.nameTextView=itemView.findViewById(R.id.name);
            this.callTimeTextView=itemView.findViewById(R.id.call_time);
            this.phoneNumberTextView=itemView.findViewById(R.id.phone_number);
            this.callTypeImageView=itemView.findViewById(R.id.call_type);
            this.cardView=itemView;
        }
        public void bind(CallLog log)
        {
            this.phoneNumberTextView.setText(log.getPhoneNumber());
            this.callTimeTextView.setText(log.getCallTime());
            this.callTypeImageView.setImageResource(getCallTypeResource(log.getCallType()));
            this.nameTextView.setText(log.getName());
            this.cardView.setOnClickListener(v ->
            {
                if(CallLogAdapter.this.clickListener!=null)
                    CallLogAdapter.this.clickListener.onClick(v,log);
            });
        }
        private int getCallTypeResource(CallLog.CALL_TYPE type)
        {
            switch (type)
            {
                case REJECTED:return R.drawable.rejected_call;
                case MISSED_CALL:return R.drawable.missed_call;
                case INCOMING_CALL:return R.drawable.incoming_call;
                case OUTGOING_CALL:return R.drawable.outgoing_call;
            }
            return R.drawable.incoming_call;
        }
    }
}

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
import org.hilcoe.mobileapp.domainmodel.Contact;

import java.util.List;
import java.util.Random;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
{
    private List<Contact> contactList;
    private Random random;
    private OnClickListener onClickListener;
    public interface OnClickListener
    {
        void onClick(View view, Contact contact);
    }
    public ContactAdapter(List<Contact> contactList)
    {
        this.contactList = contactList;
        random=new Random(System.currentTimeMillis());
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new ContactViewHolder((CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_cardview,viewGroup,false));
    }
    public void setOnClickListener(OnClickListener listener)
    {
        this.onClickListener=listener;
    }
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i)
    {
        contactViewHolder.bind(contactList.get(i));
    }

    @Override
    public int getItemCount()
    {
        return contactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder
    {
        private final int[] images={R.drawable.contact_avatar_0,R.drawable.contact_avatar_1,R.drawable.contact_avatar_2,R.drawable.contact_avatar_3,R.drawable.contact_avatar_4};
        private TextView nameTextView;
        private TextView phoneNumberTextView;
        private ImageView avatarImageView;
        private CardView cardView;
        public ContactViewHolder(@NonNull CardView itemView)
        {
            super(itemView);
            this.cardView=itemView;
            this.nameTextView=itemView.findViewById(R.id.contact_name);
            this.avatarImageView=itemView.findViewById(R.id.contact_avatar);
            this.phoneNumberTextView=itemView.findViewById(R.id.contact_phone_number);
        }
        public void bind(Contact contact)
        {
            this.avatarImageView.setImageResource(images[random.nextInt(images.length)]);
            this.phoneNumberTextView.setText(contact.getPhoneNumber());
            this.nameTextView.setText(contact.getName());
            this.cardView.setOnClickListener(v -> {
                if(ContactAdapter.this.onClickListener!=null)
                {
                    ContactAdapter.this.onClickListener.onClick(v,contact);
                }
            });
        }
    }
}

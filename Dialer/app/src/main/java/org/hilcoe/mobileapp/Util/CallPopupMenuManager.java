package org.hilcoe.mobileapp.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.hilcoe.mobileapp.R;
import org.hilcoe.mobileapp.domainmodel.Callable;
import org.hilcoe.mobileapp.service.CallService;

public class CallPopupMenuManager
{
    private CallService callService=CallService.getInstance();
    public void showMenu(Activity activity,View view, Callable callable)
    {
        showPopup(activity,view,callable);
    }
    private void createDefaultPopupMenu(PopupMenu popupMenu)
    {
        popupMenu.getMenuInflater().inflate(R.menu.default_call_log_popup_menu,popupMenu.getMenu());
    }
    private void createAddContactMenu(PopupMenu popupMenu)
    {
        createDefaultPopupMenu(popupMenu);
        popupMenu.getMenuInflater().inflate(R.menu.add_to_contact_popup_menu,popupMenu.getMenu());
    }
    private void showPopup(Activity activity,View view,Callable callable)
    {
        PopupMenu popupMenu=new PopupMenu(activity,view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            popupMenu.setGravity(Gravity.END);
        }
        if(callable.isSaved())
        {
            createDefaultPopupMenu(popupMenu);
            popupMenu.setOnMenuItemClickListener(new DefaultPopupMenuClickListener(activity,callable));
        }
        else
        {
            createAddContactMenu(popupMenu);
            popupMenu.setOnMenuItemClickListener(new AddContactPopupMenuClickListener(activity,callable));
        }
        popupMenu.show();
    }
    private class DefaultPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener
    {
        private Activity activity;
        private Callable number;
        DefaultPopupMenuClickListener(Activity activity, Callable number)
        {
            this.activity=activity;
            this.number=number;
        }
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            if(item.getItemId()==R.id.call_popup_menu)
            {
                CallPopupMenuManager.this.callService.call(activity,number);
            }
            else if(item.getItemId()==R.id.send_message_popup_menu)
            {
                CallPopupMenuManager.this.callService.sendMessage(activity,number);
            }
            else if(item.getItemId()==R.id.call_me_back_popup_menu)
            {
                CallPopupMenuManager.this.callService.sendCallMeBack(activity,number);
            }
            else if(item.getItemId()==R.id.transfer_popup_menu)
            {
                final EditText input = new EditText(activity);
                LinearLayout container = new LinearLayout(activity);
                container.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(40,5,40, 5);
                input.setLayoutParams(lp);
                input.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                container.addView(input);
                new AlertDialog.Builder(activity)
                    .setTitle("Transfer Balance")
                    .setMessage("to: "+number.getPhoneNumber())
                    .setView(container)
                    .setPositiveButton("Send", (dialog, which) -> {
                        try
                        {
                            int amount=Integer.parseInt(input.getText().toString());
                            CallPopupMenuManager.this.callService.transfer(activity,number,amount);
                        }
                        catch (NumberFormatException e)
                        {
                            Toast.makeText(activity,"invalid input!",Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton("Cancel",(dialog, which) -> {}).show();
            }
            return true;
        }

        public Activity getActivity()
        {
            return activity;
        }

        public Callable getNumber()
        {
            return number;
        }
    }
    private class AddContactPopupMenuClickListener extends DefaultPopupMenuClickListener
    {
        AddContactPopupMenuClickListener(Activity activity, Callable number)
        {
            super(activity,number);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            if(item.getItemId()==R.id.add_contact_popup_menu)
            {
                CallPopupMenuManager.this.callService.addToContact(getActivity(),getNumber());
                return true;
            }
            return super.onMenuItemClick(item);
        }
    }
}

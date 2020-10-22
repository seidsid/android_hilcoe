package org.hilcoe.mobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.hilcoe.mobileapp.Util.CallPopupMenuManager;
import org.hilcoe.mobileapp.domainmodel.RawNumber;
import org.hilcoe.mobileapp.service.CallService;

public class Dialer extends AppCompatActivity
{
    private StringBuilder stringBuilder=new StringBuilder();
    private CallService callService=CallService.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        findViewById(R.id.backspace_button).setOnLongClickListener(v -> {
            stringBuilder.delete(0,stringBuilder.length());
            updateTextView();
            return true;
        });
    }
    public void onKeyClick(View view)
    {
        Button button=(Button)view;
        stringBuilder.append(button.getText());
        updateTextView();
    }
    public void onBackspaceClick(View view)
    {
        if(stringBuilder.length()!=0)
        {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            updateTextView();
        }
    }
    public void onOptionClick(View view)
    {
        if(stringBuilder.length()>0)
        {
            new CallPopupMenuManager().showMenu(this,view,new RawNumber(stringBuilder.toString()));
        }
    }
    public void onCallButtonClick(View view)
    {
        if(stringBuilder.length()>0)
        {
            this.callService.call(this,new RawNumber(stringBuilder.toString()));
        }
    }
    private void updateTextView()
    {
        TextView textView=(TextView)findViewById(R.id.phone_number_text_view);
        textView.setText(stringBuilder.toString());
    }
}
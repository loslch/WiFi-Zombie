package com.wifi_zombie;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener {
    
    private TextView txtView;
    private Button btnOk;
    protected ListFragment mFrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtView = (TextView)findViewById(R.id.editText1);
        btnOk = (Button)findViewById(R.id.button1);
        btnOk.setOnClickListener(this);
        
        setSlidingActionBarEnabled(true);
    }
    
    @Override
    public void onClick(View v) {
        this.setTitle(txtView.getText());
    }

}

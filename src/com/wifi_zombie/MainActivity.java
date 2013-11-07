package com.wifi_zombie;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    Activity root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        root = this;
        final TextView txtView = (TextView)findViewById(R.id.editText1);
        Button btnOk = (Button)findViewById(R.id.button1);
        btnOk.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                root.setTitle(txtView.getText());
            }
        });
    }

}

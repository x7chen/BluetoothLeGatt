package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SerialPortAssistant extends Activity {
    public static String TAG = SerialPortAssistant.class.getSimpleName();
    public final static String DEVICE_DOES_NOT_SUPPORT_UART =
                                                "com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";
    public static final UUID TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID FIRMWARE_REVISON_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID NUS_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_assistant);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Button bt_send = (Button)findViewById(R.id.button2);
        final EditText et = (EditText)findViewById(R.id.editText);
        final CheckBox cb = (CheckBox)findViewById(R.id.checkBox);
        final GridLayout gridLayout =(GridLayout)findViewById(R.id.grid_root);
        final Intent intent = new Intent(BluetoothLeService.ACTION_GATT_HANDLE);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("cmd", R.integer.nordic_ble_nus_write_characteristic);
                intent.putExtra("data", et.getText().toString());
                sendBroadcast(intent);
                Log.i(TAG, "Button Send clicked");

            }
        });
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gridLayout.setVisibility(View.VISIBLE);
                } else {
                    gridLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        String[] chars =new String[]
                {
                        "0","1","2","3","4","5","6","7","<",
                        "8","9","A","B","C","D","E","F"," "
                };
        for(int i=0;i<18;i++){
            final Button bt = new Button(this);
            bt.setText(chars[i]);
            bt.setTextSize(20);
            //bt.setPadding(1, 1, 1, 1);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bt.getText().toString().equals("<")){

                    }
                    else{
                        et.append(bt.getText().toString());
                    }

                }
            });

            GridLayout.Spec rowspec = GridLayout.spec(i/9);
            GridLayout.Spec columnspec = GridLayout.spec(i%9);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowspec,columnspec);
            params.width = 110;
            params.height = 150;
            params.setMargins(1,1,1,1);
            params.setGravity(Gravity.LEFT);
            gridLayout.addView(bt,params);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(MyReceiver, MyIntentFilter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serial_port_assistant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final TextView tv = (TextView)findViewById(R.id.textView);
            final String action = intent.getAction();
            String data;
            if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                tv.append(data);
                Log.i(TAG,data);
            }
        }
    };
    private static IntentFilter MyIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}

package com.cwt.coolpot.mainpart;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cwt.coolpot.R;
import com.cwt.coolpot.network.MessageString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;


/**
 * Created by 曹吵吵 on 2017/8/20 0020.
 */

public class MainFragment extends Fragment  {
    private Handler handler;
    private final static String refreshOrder="1",rotateOrder="2",waterOrder="3",autoWaterOrder="4";
    TextView temperatureText,lightText,humidityText,connectedDeviceName;
    View view;
    //EditText msgEditText;
    Button refreshButton,waterButton,rotateButton;
    CheckBox autoWaterOptionCheck;
    BluetoothSocket btSocket=null;
    Animation refreshAnimation;
    boolean isWatering=false,isRotating=false;
    public final static int MESSAGE_READ = 3,MESSAGE_SEND=4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_fragment,container,false);
        init();
        Log.e("Bluetooth","true id"+getId());
        return view;
    }

    private void init(){
        initHandler();
        initFunc();
    }

    public void startThread(){
        new MsgHandleThread().start();
    }

    public class MsgHandleThread extends Thread {
        //private BluetoothSocket btSocket;
        private InputStream inputStream;

        public void run() {
            try {
                inputStream = btSocket.getInputStream();
                int len;
                String result = "";
                while (true) {
                    len=inputStream.available();
                    if (len<=0 ) {
                        continue;
                    } else {
                        try {
                            Thread.sleep(500);  //等待0.5秒,数据接收完整
                            len=inputStream.available();
                            byte data[]=new byte[len];
                            int readCount = 0; // 已经成功读取的字节的个数
                            while (readCount < len) {
                                readCount += inputStream.read(data, readCount, len - readCount);
                            }
                            //inputStream.read(data);
                            result = MessageString.bytesToHexString(data);
                            result = MessageString.HexAscToString(result);
                            Log.e("单片机蓝牙信息:", "result " + result);
                            Message msg = new Message();
                            msg.what = MESSAGE_READ;
                            msg.obj = result;
                            handler.sendMessage(msg);
                            Log.e("单片机蓝牙信息:", "byte长度 " + len);

                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
                inputStream.close();
                Log.e("Bluetooth", "关闭inputStream");
                if (btSocket != null) {
                    btSocket.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.toString());
            }
        }
    }

    public Boolean setBtSocket(BluetoothSocket btSocket){
        this.btSocket=btSocket;
        Log.e("Bluetooth","setBtSocket OK");
        BluetoothDevice device=btSocket.getRemoteDevice();
        connectedDeviceName.setText(device.getName());
        return true;
    }

    public void deleteBtSocket(){
        btSocket=null;
        connectedDeviceName.setText("暂无");
    }

    private void startRefreshAnim(){
        refreshAnimation.reset();
        refreshButton.clearAnimation();
        refreshButton.startAnimation(refreshAnimation);
    }

    private void stopRefreshAnim(){
        refreshAnimation.reset();
        refreshButton.clearAnimation();
    }

    private boolean sendOrderToBT(String order){
        if (btSocket==null){
            Toast.makeText(view.getContext(),"请先连接设备！",Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            OutputStream outputStream;
            outputStream=MainFragment.this.btSocket.getOutputStream();
            outputStream.write(order.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(),"发送命令出错！",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        String result = (String) msg.obj;
                        String data[] = dealData(result);
                        Log.e("单片机蓝牙信息:", "data num:" + data.length);
                        for (String a:data)
                            Log.e("单片机蓝牙信息:", "data:" + a);
                        //TODO 蓝牙收到数据之后对信息进行更新
                        stopRefreshAnim();
                        if (data.length>1) {
                            temperatureText.setText(data[0] + "℃");
                            humidityText.setText(data[1].split("%")[0] + "%");
                        }
                        break;
                }
            }
        };
    }

    private void initFunc(){
        waterButton=(Button)view.findViewById(R.id.water_pot_button);
        rotateButton=(Button)view.findViewById(R.id.rotate_pot_button);
        autoWaterOptionCheck=(CheckBox)view.findViewById(R.id.auto_water_option_check);
        refreshButton =(Button)view.findViewById(R.id.refresh_data_button);
        temperatureText=(TextView)view.findViewById(R.id.temperatureMain_text);
        lightText=(TextView)view.findViewById(R.id.lightMain_text);
        humidityText=(TextView)view.findViewById(R.id.humidityMain_text);
        connectedDeviceName=(TextView)view.findViewById(R.id.connected_device_name_text);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRefreshAnim();
                boolean flag=sendOrderToBT(refreshOrder);
                if (flag==false) {
                    Toast.makeText(view.getContext(), "数据刷新失败！", Toast.LENGTH_SHORT).show();
                    stopRefreshAnim();
                } else {
                    Toast.makeText(view.getContext(), "数据刷新成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag;
                if (autoWaterOptionCheck.isChecked())
                    flag=sendOrderToBT(autoWaterOrder);
                else
                    flag=sendOrderToBT(waterOrder);
                if (flag){
                    if (isWatering){
                        waterButton.setText("开始浇水");
                        if (autoWaterOptionCheck.isChecked())
                            Toast.makeText(view.getContext(),"已关闭自动浇水！",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(view.getContext(),"已停止浇水！",Toast.LENGTH_SHORT).show();
                    } else {
                        waterButton.setText("停止浇水");
                        if (autoWaterOptionCheck.isChecked())
                            Toast.makeText(view.getContext(),"已开启自动浇水！",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(view.getContext(),"正在浇水！",Toast.LENGTH_SHORT).show();
                    }
                    isWatering=!isWatering;
                }
            }
        });
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag=sendOrderToBT(rotateOrder);
                if (flag){
                    if (isRotating){
                        rotateButton.setText("开始旋转");
                        Toast.makeText(view.getContext(),"已结束旋转！",Toast.LENGTH_SHORT).show();
                    } else {
                        rotateButton.setText("结束旋转");
                        Toast.makeText(view.getContext(),"正在旋转！",Toast.LENGTH_SHORT).show();
                    }
                    isRotating=!isRotating;
                }
            }
        });
        autoWaterOptionCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    if (isWatering){
                        autoWaterOptionCheck.setChecked(false);
                        Toast.makeText(view.getContext(),"请先停止浇水！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(view.getContext(),"点击开始浇水按钮以开始自动浇水程序。",Toast.LENGTH_SHORT).show();
                } else {
                    if (isWatering){
                        autoWaterOptionCheck.setChecked(true);
                        Toast.makeText(view.getContext(),"请先结束自动浇水程序！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        refreshAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.rotate_refresh);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isWatering)
            waterButton.callOnClick();
        if (isRotating)
            rotateButton.callOnClick();
    }

    private String[] dealData(String data){
        return data.split("\\s+");
    }
}

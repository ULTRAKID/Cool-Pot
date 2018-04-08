package com.cwt.coolpot.mainpart;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cwt.coolpot.R;
import com.cwt.coolpot.network.MessageString;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.ChartFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 主功能界面
 * 花盆控制功能
 * 花盆状态显示
 * Created by 曹吵吵 on 2017/8/20 0020.
 */

public class MainFragment extends Fragment  {
    private Handler handler;
    private final static String refreshOrder="1",rotateOrder="2",waterOrder="3",autoWaterOrder="4",autoRotateOrder="5";
    private XYMultipleSeriesDataset mDataset;             // 多个系列的数据集合,即多条线的数据集合
    private XYSeries tempSeries,humiditySeries;         // 一个系列的数据，即一条线的数据集合
    private XYMultipleSeriesRenderer allRenderer;        // 多个系列的环境渲染，即整个画折线的区域
    private XYSeriesRenderer tempRenderer,humidityRenderer;                // 一个系列的环境渲染，即一条线的环境渲染
    TextView temperatureText,lightText,humidityText,connectedDeviceName;
    View view;
    //EditText msgEditText;
    Button refreshButton,waterButton,rotateButton;
    CheckBox autoWaterOptionCheck,autoRotateOptionCheck;
    BluetoothSocket btSocket=null;
    Animation refreshAnimation;
    GraphicalView lineChartView;
    LinearLayout lineChartLayout;
    boolean isWatering=false,isRotating=false,isRefreshing=false;
    List<Float> tempData,humidityData;
    float lastX,lastY;
    Timer timer;TimerTask animTask,refreshTask;
    public final static int MESSAGE_READ = 3,MESSAGE_SEND=4,TIME_LIMITED_EXCEEDED=5;
    private final static int MAX_POINT_NUM=10, MAX_WAITING_TIME =5000,REFRESH_INTERVAL_TIME=10000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_fragment,container,false);
        init();
        //initDataForTest();
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
                            if (!isRefreshing)
                                continue;
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
                        if (data.length==2) {
                            String temp=data[0],humidity=data[1].split("%")[0],light=data[1].split("%")[1];
                            light=light.split("lx")[0];
                            int lightN=Integer.valueOf(light);
                            temperatureText.setText(temp + "℃");
                            humidityText.setText(humidity + "%");
                            lightText.setText(lightN + "Lux");
                            Toast.makeText(view.getContext(), "数据刷新成功！", Toast.LENGTH_SHORT).show();
                            if (animTask!=null)
                                animTask.cancel();
                            isRefreshing=false;
                            if (tempData.size()>=5*MAX_POINT_NUM){
                                tempData.clear();humidityData.clear();
                            }
                            float a=Float.valueOf(temp);
                            tempData.add(a);
                            a=Float.valueOf(humidity);
                            humidityData.add(a);
                            updateChartData();
                            if (refreshTask!=null)
                                refreshTask.cancel();
                            refreshTask=new TimerTask() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshButton.callOnClick();
                                        }
                                    });
                                }
                            };
                            timer.schedule(refreshTask,REFRESH_INTERVAL_TIME);
                        }
                        break;
                    case TIME_LIMITED_EXCEEDED:
                        stopRefreshAnim();
                        isRefreshing=false;
                        Toast.makeText(view.getContext(),"刷新超时",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    private void initFunc(){
        initChart();
        timer=new Timer();
        waterButton=(Button)view.findViewById(R.id.water_pot_button);
        rotateButton=(Button)view.findViewById(R.id.rotate_pot_button);
        autoWaterOptionCheck=(CheckBox)view.findViewById(R.id.auto_water_option_check);
        autoRotateOptionCheck=(CheckBox)view.findViewById(R.id.auto_rotate_option_check);
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
                animTask =new TimerTask() {
                    @Override
                    public void run() {
                        Message message=new Message();
                        message.what=TIME_LIMITED_EXCEEDED;
                        handler.sendMessage(message);
                    }
                };
                timer.schedule(animTask, MAX_WAITING_TIME);
                /*Random random=new Random();
                if (tempData.size()>=5*MAX_POINT_NUM){
                    tempData.clear();humidityData.clear();
                }
                tempData.add(random.nextFloat()*50);
                humidityData.add(random.nextFloat()*100);
                updateChartData();*/
                if (flag==false) {
                    Toast.makeText(view.getContext(), "数据刷新失败！", Toast.LENGTH_SHORT).show();
                    stopRefreshAnim();
                } else {
                    isRefreshing=true;
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
                            Toast.makeText(view.getContext(),"已开启自动浇水（湿度不宜时）！",Toast.LENGTH_SHORT).show();
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
                boolean flag;
                if (autoRotateOptionCheck.isChecked())
                    flag=sendOrderToBT(autoRotateOrder);
                else
                    flag=sendOrderToBT(rotateOrder);
                if (flag){
                    if (isRotating){
                        rotateButton.setText("开始旋转");
                        if (autoRotateOptionCheck.isChecked())
                            Toast.makeText(view.getContext(),"已关闭自动旋转！",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(view.getContext(),"已停止旋转！",Toast.LENGTH_SHORT).show();
                    } else {
                        rotateButton.setText("结束旋转");
                        if (autoRotateOptionCheck.isChecked())
                            Toast.makeText(view.getContext(),"已开启自动旋转（光照不宜时）！",Toast.LENGTH_SHORT).show();
                        else
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
                    Toast.makeText(view.getContext(),"点击开始浇水按钮以开始（湿度不宜时）自动浇水程序。",Toast.LENGTH_SHORT).show();
                } else {
                    if (isWatering){
                        autoWaterOptionCheck.setChecked(true);
                        Toast.makeText(view.getContext(),"请先结束自动浇水程序！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        autoRotateOptionCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    if (isRotating){
                        autoRotateOptionCheck.setChecked(false);
                        Toast.makeText(view.getContext(),"请先停止旋转！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(view.getContext(),"点击开始旋转按钮以开始（光照不宜时）自动旋转程序。",Toast.LENGTH_SHORT).show();
                } else {
                    if (isRotating){
                        autoRotateOptionCheck.setChecked(true);
                        Toast.makeText(view.getContext(),"请先结束自动旋转程序！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        refreshAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.rotate_refresh);

    }

    private void initChart(){
        tempData=new ArrayList<>();humidityData=new ArrayList<>();
        mDataset=new XYMultipleSeriesDataset();
        tempSeries=new XYSeries("温度",0);humiditySeries=new XYSeries("湿度",1);
        mDataset.addSeries(tempSeries);mDataset.addSeries(humiditySeries);
        allRenderer=new XYMultipleSeriesRenderer(2);
        //allRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        allRenderer.setXTitle("时间");
        allRenderer.setYTitle("温度",0);
        allRenderer.setYTitle("湿度",1);
        allRenderer.setYAxisAlign(Paint.Align.RIGHT,1);
        allRenderer.setYLabelsAlign(Paint.Align.RIGHT,0);
        allRenderer.setYLabelsAlign(Paint.Align.LEFT,1);
        allRenderer.setAxisTitleTextSize(20);// 设置轴标题文本大小
        allRenderer.setChartTitle("盆栽状态折线图");// 设置图表标题
        allRenderer.setChartTitleTextSize(30);// 设置图表标题文字的大小
        allRenderer.setLabelsTextSize(30);// 设置标签的文字大小
        allRenderer.setLegendTextSize(30);// 设置图例文本大小
        allRenderer.setPointSize(10f);// 设置点的大小
        allRenderer.setYAxisMin(0,0);// 设置y轴最小值是0
        allRenderer.setYAxisMax(50,0);
        allRenderer.setYAxisMin(0,1);
        allRenderer.setYAxisMax(100,1);
        allRenderer.setXAxisMin(0,0);
        allRenderer.setXAxisMax(MAX_POINT_NUM,0);
        allRenderer.setXAxisMin(0,1);
        allRenderer.setXAxisMax(MAX_POINT_NUM,1);
        allRenderer.setLabelsColor(Color.BLACK);
        allRenderer.setYLabels(10);// 设置Y轴刻度个数
        allRenderer.setXLabels(0);//不显示X标签刻度
        allRenderer.setXAxisMax(MAX_POINT_NUM);
        allRenderer.setMargins(new int[] { 80, 45, 10, 60 });// 设置图表的外边框(上/左/下/右)
        allRenderer.setPanEnabled(true,false);
        tempRenderer=new XYSeriesRenderer();humidityRenderer=new XYSeriesRenderer();
        tempRenderer.setColor(Color.RED);humidityRenderer.setColor(Color.BLUE);
        tempRenderer.setPointStyle(PointStyle.CIRCLE);humidityRenderer.setPointStyle(PointStyle.DIAMOND);
        tempRenderer.setFillPoints(true);humidityRenderer.setFillPoints(true);
        tempRenderer.setDisplayChartValues(true);humidityRenderer.setDisplayChartValues(true);
        tempRenderer.setChartValuesTextSize(25);humidityRenderer.setChartValuesTextSize(25);
        tempRenderer.setLineWidth(3);humidityRenderer.setLineWidth(3);
        allRenderer.addSeriesRenderer(0,tempRenderer);
        allRenderer.addSeriesRenderer(1,humidityRenderer);
        allRenderer.setMarginsColor(android.R.color.transparent);
        lineChartView=ChartFactory.getLineChartView(view.getContext(),mDataset,allRenderer);
        lineChartLayout=(LinearLayout)view.findViewById(R.id.lineChart_layout);
        lineChartLayout.addView(lineChartView);
        lineChartView.repaint();
        lineChartView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                lineChartView.getParent().requestDisallowInterceptTouchEvent(true);
                float x=motionEvent.getRawX();float y=motionEvent.getRawY();

                switch (motionEvent.getAction()){
                    case  MotionEvent.ACTION_DOWN:
                        lastX=x;
                        lastY=y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dy=y-lastY;
                        float dx=x-lastX;
                        float viewX=lineChartView.getX();
                        float viewY=lineChartView.getY();
                        if (Math.abs(dy)<Math.abs(dx)&&lastX>viewX&&lastY>viewY&&btSocket!=null&&!humidityData.isEmpty())
                            lineChartView.getParent().requestDisallowInterceptTouchEvent(true);
                        else
                            lineChartView.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initDataForTest(){
        Random random=new Random();
        if (tempData.size()>=5*MAX_POINT_NUM){
            tempData.clear();humidityData.clear();
        }
        int i=0;
        while (i<30){
            tempData.add(random.nextFloat()*50);
            humidityData.add(random.nextFloat()*100);
            updateChartData();
            i++;
        }
        temperatureText.setText("30℃");
        lightText.setText("1200Lux");
        humidityText.setText("78%");
    }

    private void updateChartData(){
        int i;
        tempSeries.clear();humiditySeries.clear();
        for (i=0;i<tempData.size();i++){
            tempSeries.add(i,(int)changeNumPoint(tempData.get(i),2));
            humiditySeries.add(i,(int)changeNumPoint(humidityData.get(i),2));
            //Log.e("tempData:",changeNumPoint(tempData.get(i),2)+" ");
        }
        if (i<MAX_POINT_NUM){
            allRenderer.setXAxisMin(0);
            allRenderer.setXAxisMax(MAX_POINT_NUM);
        } else {
            allRenderer.setXAxisMin(i-MAX_POINT_NUM);
            allRenderer.setXAxisMax(i);
        }
        lineChartView.repaint();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refreshTask!=null)
            refreshTask.cancel();
        if (animTask!=null)
            animTask.cancel();
        if (isWatering)
            waterButton.callOnClick();
        if (isRotating)
            rotateButton.callOnClick();
    }

    private String[] dealData(String data){
        return data.split("\\s+");
    }

    private float changeNumPoint(float a,int num){
        BigDecimal b = new BigDecimal(a);
        return b.setScale(num,BigDecimal.ROUND_HALF_UP).floatValue();
    }


}

package com.cwt.coolpot.conpart;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cwt.coolpot.R;
import com.cwt.coolpot.mainpart.MainFragment;
import com.cwt.coolpot.network.MBluetooth;


import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by 曹吵吵 on 2017/8/20 0020.
 */

public class ConFragment extends Fragment{
    private final int REQUEST_PERMISSION_ACCESS_LOCATION=1;
    private BluetoothSocket btSocket;
    private List<BluetoothDevice> bondedDevices=null,onlineDevices=null;
    private BluetoothAdapter btAdapter=BluetoothAdapter.getDefaultAdapter();
    private MBluetoothRecyclerAdapter onlineDevicesAdapter,bondedDevicesAdapter;
    private BroadcastReceiver receiver;
    private MainFragment fragment;
    RecyclerView bondedDevicesRV,onlineDevicesRV;
    TextView onlineDevicesRVtitle,bondedDevicesRVtitle;
    Button searchDevices;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.con_fragment,container,false);
        init();
        return view;
    }

    private void init(){
        fragment =(MainFragment)getActivity().getSupportFragmentManager().getFragments().get(1);
        //(MainFragment) mPagerAdapter.getItem(1);
        Log.e("Bluetooth","getID:"+fragment.getId());
        initBondedDevices();
        initOnlineDevices();
        initButton();
        regReciver();
    }

    private void initButton(){
        searchDevices=(Button)view.findViewById(R.id.bt_search_button);
        searchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MBluetooth.openBluetooth(view,btAdapter);
                int stat=btAdapter.getState();
                Log.e("Bluetooth","蓝牙state:"+stat);
                if (stat==BluetoothAdapter.STATE_OFF)
                    return;
                Log.e("Bluetooth","蓝牙已打开");
                //initBondedDevices();
                boolean hasPermission=checkPermission();
                Log.e("Bluetooth","权限："+hasPermission);
                if (!hasPermission)
                    ConFragment.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSION_ACCESS_LOCATION);
                searchOnlineDevices();
            }
        });
    }

    private void initBondedDevices(){
        bondedDevicesRVtitle=(TextView)view.findViewById(R.id.bt_bond_rv_title);
        Set<BluetoothDevice> devices=btAdapter.getBondedDevices();
        bondedDevices=new ArrayList<>(devices);
        bondedDevicesAdapter=new MBluetoothRecyclerAdapter(bondedDevices);
        if (btAdapter.getState()==BluetoothAdapter.STATE_OFF){
            bondedDevicesRVtitle.setText("蓝牙未打开");
            bondedDevices.clear();
            bondedDevicesAdapter.notifyDataSetChanged();
            Log.e("Bluetooth","蓝牙未打开");
            return;
        }
        bondedDevicesRVtitle.setText("已配对设备");
        bondedDevicesRV=(RecyclerView)view.findViewById(R.id.bt_bond_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        bondedDevicesRV.setLayoutManager(layoutManager);
        bondedDevicesRV.setAdapter(bondedDevicesAdapter);
    }

    private void initOnlineDevices(){
        onlineDevices=new ArrayList<>();
        onlineDevicesRVtitle=(TextView)view.findViewById(R.id.bt_search_rv_title);
        onlineDevicesRV=(RecyclerView)view.findViewById(R.id.bt_search_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        onlineDevicesRV.setLayoutManager(layoutManager);
        onlineDevicesAdapter=new MBluetoothRecyclerAdapter(onlineDevices);
        onlineDevicesRV.setAdapter(onlineDevicesAdapter);
        if (onlineDevices.size()==0){
            onlineDevicesRV.setVisibility(View.INVISIBLE);
            onlineDevicesRVtitle.setVisibility(View.INVISIBLE);
        }
    }

    private boolean searchOnlineDevices(){
        onlineDevices.clear();
        onlineDevicesRVtitle.setVisibility(View.VISIBLE);
        onlineDevicesRV.setVisibility(View.VISIBLE);
        if (btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();
        boolean stat=btAdapter.startDiscovery();
        return stat;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkAccessFinePermission = ActivityCompat
                    .checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    searchOnlineDevices();
                } else {
                    Toast.makeText(view.getContext(),
                            "没有蓝牙权限",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void regReciver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        onlineDevices.add(device);
                        onlineDevicesAdapter.notifyDataSetChanged();
                        Log.e("Bluetooth",device.getName()+ "  数量  "+onlineDevices.size()
                                +"\nRV项目数量："+onlineDevicesRV.getAdapter().getItemCount());

                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        initBondedDevices();
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        Toast.makeText(view.getContext(),
                                "正在搜索蓝牙设备",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        Toast.makeText(view.getContext(),
                                "搜索蓝牙设备结束",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.STATE_OFF);
                        initBondedDevices();
                        initOnlineDevices();
                        if (state==BluetoothAdapter.STATE_TURNING_OFF||state==BluetoothAdapter.STATE_OFF)
                            fragment.deleteBtSocket();
                        break;
                }
            }
        };
        view.getContext().registerReceiver(receiver, intentFilter);
    }

    public void connectDevice(String macAddress){
        btAdapter.cancelDiscovery();
        BluetoothDevice clientDevice=btAdapter.getRemoteDevice(macAddress);
        Method clientMethod = null;
        try {
            clientMethod = clientDevice.getClass()
                    .getMethod("createRfcommSocket", new Class[]{int.class});
            btSocket = (BluetoothSocket) clientMethod.invoke(clientDevice, 1);
            connect(btSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void connect(BluetoothSocket btSocket) {
        try {
            btSocket.connect();//连接
            if (btSocket.isConnected()) {
                Log.e("Bluetooth", "连接成功");
                Toast.makeText(view.getContext(), "蓝牙连接成功", Toast.LENGTH_SHORT).show();
                //TextView testText=(TextView)view.findViewById(R.id.info_test_text);
                //new MainFragment.MsgHandleThread(btSocket,testText).start();

            } else {
                Toast.makeText(view.getContext(), "蓝牙连接失败", Toast.LENGTH_SHORT).show();
                btSocket.close();
                Log.e("Bluetooth", "连接关闭");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Bluetooth","即将可以开始传输数据");
        //MPagerAdapter mPagerAdapter = (MPagerAdapter)ConFragment.this.getArguments().getSerializable("MPagerAdapter");
        if (fragment==null){
            Log.e("Bluetooth","MainFragment is null");
            return;
        }
        if (fragment.getView()==null){
            Log.e("Bluetooth","MainFragment's view is null");
            return;
        }
        //Log.e("Bluetooth","即将可以开始传输数据");
        fragment.setBtSocket(btSocket);
        fragment.startThread();
        Log.e("Bluetooth","可以开始传输数据");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view.getContext().unregisterReceiver(receiver);
    }

    public BluetoothSocket getBtSocket(){
        return btSocket;
    }

    public class MBluetoothRecyclerAdapter extends RecyclerView.Adapter<MBluetoothRecyclerAdapter.ViewHolder> implements Serializable{
        private static final long serialVersionUID = -4949266590758772924L;
        private List<BluetoothDevice> btList;

        public MBluetoothRecyclerAdapter(List<BluetoothDevice> btList){
            this.btList=btList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bluetooth_item,parent,false);
            final ViewHolder viewHolder=new ViewHolder(view);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=viewHolder.getAdapterPosition();
                    final BluetoothDevice device=btList.get(position);
                    AlertDialog.Builder dialog=new AlertDialog.Builder(view.getContext());
                    dialog.setItems(new String[]{"连接"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case 0:
                                    ConFragment.this.connectDevice(device.getAddress());
                                    break;
                            }
                        }
                    }).show();

                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BluetoothDevice device=btList.get(position);
            holder.btName.setText(device.getName());
            holder.btAddr.setText("MAC地址："+device.getAddress());
        }

        @Override
        public int getItemCount() {
            return btList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView btName,btAddr;
            View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view=itemView;
                btName=(TextView)itemView.findViewById(R.id.bt_name_text);
                btAddr=(TextView)itemView.findViewById(R.id.bt_addr_text);
            }

        }
    }
}

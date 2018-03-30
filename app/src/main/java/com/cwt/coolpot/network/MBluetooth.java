package com.cwt.coolpot.network;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.cwt.coolpot.R;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

/**
 * 蓝牙各种功能
 * Created by 曹吵吵 on 2018/3/9 0009.
 */

public class MBluetooth {
    //private static BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    //public String name;
    //public String address;

    //public final static int BT_OPENED=1,BT_CLOSED=0;

    public static int openBluetooth(final View view, BluetoothAdapter mBluetoothAdapter){
        if (mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON)
            return mBluetoothAdapter.getState();
        CharSequence charSequence;
        String ms="<font color='black'>蓝牙已关闭，是否打开</font>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            charSequence = Html.fromHtml(ms,Html.FROM_HTML_MODE_LEGACY);
        } else {
            charSequence = Html.fromHtml(ms);
        }
        AlertDialog alertDialog=new AlertDialog.Builder(view.getContext(), R.style.CoolPotDialog)
                .setMessage(charSequence)
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        view.getContext().startActivity(intent);
                    }
                })
                .create();
        alertDialog.show();
        return mBluetoothAdapter.getState();
    }



}

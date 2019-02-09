package com.qintingfm.explayer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.qintingfm.explayer.R;

public class MainActivity extends AppCompatActivity {
    String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int PERMISSIONS_REQUEST_CODE=123;
    public MainActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断当前系统的版本
            int hasPermission=1;
            for (String permission:permissions) {
                int i = ContextCompat.checkSelfPermission(MainActivity.this, permission);
                if (i == PackageManager.PERMISSION_DENIED) {
                    hasPermission=0;
                }
            }
            if(hasPermission==0){
                final AlertDialog.Builder normalDialog =new AlertDialog.Builder(this);
                normalDialog.setMessage("系统功能正常运行需要读写存储权限，请在接下来对话框点击允许");
                normalDialog.setTitle("权限申请");
                normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSIONS_REQUEST_CODE);
                    }

                });
                normalDialog.show();
            }else {
                Intent intent=new Intent(this,NavActivity.class);
                intent.setData(getIntent().getData());
                intent.putExtra("play_source","Launcher");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }else {
            Intent intent=new Intent(this,NavActivity.class);
            intent.setData(getIntent().getData());
            intent.putExtra("play_source","Launcher");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@android.support.annotation.NonNull String[] permissions,@android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSIONS_REQUEST_CODE == requestCode) {
            int grantAll=1;
            for (int x : grantResults) {
                if (x == PackageManager.PERMISSION_DENIED) {
                    grantAll=0;
                }
            }
            if(grantAll==0){
                Toast.makeText(this,"没有获取到权限,请重新点击程序进行授权。",Toast.LENGTH_LONG).show();
                finish();
            }else{
                Intent intent=new Intent(this,NavActivity.class);
                startActivity(intent);
            }
        }
    }
}

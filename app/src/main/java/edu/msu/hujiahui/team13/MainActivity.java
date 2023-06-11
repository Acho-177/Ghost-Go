package edu.msu.hujiahui.team13;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private String username = "";

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLogin(View view){
        LogDlg dlg1 = new LogDlg();
        dlg1.show(getFragmentManager(), "log");
    }

    public void onSignup(View view){
        SignDlg dlg1 = new SignDlg();
        dlg1.show(getSupportFragmentManager(), "sign");
    }

    public void onStart(View view){
        if (username=="")
            onLogin(view);
        else
            startGhostActivity();
    }
    public void startGhostActivity(){
        Intent intent = new Intent(this,GhostActivity.class);
        startActivity(intent);
    }
}

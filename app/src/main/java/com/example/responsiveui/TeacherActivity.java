package com.example.responsiveui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.util.Scanner;

public class TeacherActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_dashboard);

        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherActivity.this, AttendancePage.class));
            }
        });

        Button button1 = findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherActivity.this, RecordsActivity.class));
            }
        });

        Button button2 = findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherActivity.this, DownloadData.class));
            }
        });

        Button button3 = findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherActivity.this, OutActivity.class).putExtra("User", "Teacher"));
            }
        });

        TextView textView1 = findViewById(R.id.textView1);
        textView1.setText(returnAccount_Info("Name"));
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText(returnAccount_Info("Designation"));
        TextView textView3 = findViewById(R.id.textView3);
        textView3.setText(returnAccount_Info("Department"));
    }

    public String returnAccount_Info(String key) {
        String returnString  = null;
        try {
            Scanner scanner = new Scanner(openFileInput("account_info.txt"));
            scanner.nextLine();
            scanner.nextLine();
            String line = scanner.nextLine();
            JSONObject jsonObject = new JSONObject(line);
            returnString = jsonObject.getString(key);
        } catch (Exception e) {
        }
        return returnString;
    }
}

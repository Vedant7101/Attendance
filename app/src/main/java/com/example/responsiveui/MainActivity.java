package com.example.responsiveui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        File file = new File("/data/data/com.example.responsiveui/files/account_info.txt");
        try {
            if (!file.exists()) {
                createFile();
            } else {
                Scanner scanner = new Scanner(openFileInput("account_info.txt"));
                JSONObject jsonObject = new JSONObject(scanner.nextLine());
                if (!jsonObject.get("login").equals("No")) {
                    JSONObject jsonObject1 = new JSONObject(scanner.nextLine());
                    if (jsonObject1.get("user").equals("Student")) {
                        startActivity(new Intent(MainActivity.this, StudentActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, TeacherActivity.class));
                    }
                }
            }
        } catch (Exception e) {}

        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra("User", "Teacher");
                startActivity(i);
            }
        });

        Button button1 = findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra("User", "Student");
                startActivity(i);
            }
        });
    }

    void createFile() throws Exception {
        FileOutputStream fout = openFileOutput("account_info.txt", MODE_PRIVATE);
        fout.write(("{\'login\':\'No\'}").getBytes());
        fout.close();
    }
}

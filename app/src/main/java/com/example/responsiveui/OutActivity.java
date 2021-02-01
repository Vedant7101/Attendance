package com.example.responsiveui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class OutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout);

        try {
            if (getIntent().getStringExtra("User").equals("Student")) {
                TextView textView1 = findViewById(R.id.textView1);
                textView1.setText(returnAccount_Info("first_name") + " " + returnAccount_Info("last_name"));
                TextView textView2 = findViewById(R.id.textView2);
                textView2.setText(returnAccount_Info("grno"));
            } else {
                TextView textView1 = findViewById(R.id.textView1);
                textView1.setText(returnAccount_Info("Name"));
                TextView textView2 = findViewById(R.id.textView2);
                textView2.setText(returnAccount_Info("Designation"));
                TextView textView3 = findViewById(R.id.textView3);
                textView3.setText(returnAccount_Info("Department"));
            }
        } catch (Exception e) {}
    }

    public void clickButton(View view) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Do you really want to sign out?");
        dialog.setTitle("Sign Out");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FileWriter fileWriter = new FileWriter(new File("/data/data/com.example.responsiveui/files/account_info.txt"));
                            fileWriter.write("{\"login\":\"No\"}\n");
                            fileWriter = new FileWriter(new File("/data/data/com.example.responsiveui/files/teacher_subject.txt"));
                            fileWriter.write("");
                            fileWriter.close();
                            startActivity(new Intent(OutActivity.this, MainActivity.class));
                        } catch (IOException e) {
                        }
                    }
                });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    public String returnAccount_Info(String key) throws FileNotFoundException, JSONException {
        Scanner scanner = new Scanner(openFileInput("account_info.txt"));
        scanner.nextLine();
        scanner.nextLine();
        String line = scanner.nextLine();
        JSONObject jsonObject = new JSONObject(line);
        return jsonObject.getString(key);
    }
}

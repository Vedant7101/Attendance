package com.example.responsiveui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final String user = getIntent().getStringExtra("User");

        try {
            Scanner scanner = new Scanner(openFileInput("account_info.txt"));
            JSONObject jsonObject = new JSONObject(scanner.nextLine());
            if (jsonObject.get("login").equals("Yes")) {
                startActivity(new Intent(this, MainActivity.class));
            }
        } catch (Exception e) {}

        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = findViewById(R.id.edit1);
                EditText editText1 = findViewById(R.id.edit2);

                String para[] = new String[2];
                para[0] = editText.getText().toString();
                para[1] = editText1.getText().toString();

                if (para[0].equals("") || para[1].equals("")) {
                    Toast.makeText(LoginActivity.this, "Please fill all the fields.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    if (user.equals("Student")) {
                        if (new myAsyncTask().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from StudentInfo where user='" + para[0] + "' and pass='" + para[1] + "'").get()) {
                            Toast.makeText(LoginActivity.this, "Success.", Toast.LENGTH_LONG).show();

                            ArrayList<String> tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from StudentInfo WHERE user='" + para[0] + "' and pass='" + para[1] + "'").get();
                            FileWriter fileWriter = new FileWriter(new File("/data/data/com.example.responsiveui/files/account_info.txt"));
                            fileWriter.write("{\"login\":\"Yes\"}\n" + "{\"user\":\"Student\"}\n" + tempList.get(0));
                            fileWriter.close();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Fail.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (new myAsyncTask().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Teacher_Info WHERE user='" + para[0] + "' and password='" + para[1] + "'").get()) {
                            Toast.makeText(LoginActivity.this, "Success.", Toast.LENGTH_LONG).show();

                            ArrayList<String> tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Teacher_Info WHERE user='" + para[0] + "' and password='" + para[1] + "'").get();
                            ArrayList<String> tempList1 = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Subject_Info WHERE user='" + para[0] + "'").get();
                            FileWriter fileWriter = new FileWriter(new File("/data/data/com.example.responsiveui/files/account_info.txt"));
                            fileWriter.write("{\"login\":\"Yes\"}\n" + "{\"user\":\"Teacher\"}\n" + tempList.get(0));
                            fileWriter.close();
                            FileWriter fileWriter1 = new FileWriter(new File("/data/data/com.example.responsiveui/files/teacher_subject.txt"));
                            fileWriter1.write(tempList1.toString());
                            fileWriter1.close();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Fail.", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this,  e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}

class myAsyncTask extends AsyncTask<String, Void, Boolean> {

    Boolean verification = true;

    @Override
    protected Boolean doInBackground(String... para) {
        String result = null;
        try {

            String link = para[0];
            link = link.replace(" ", "%20");
            link = link.replace(",", "%27");
            URL url = new URL(link);
            Scanner scanner = new Scanner(url.openStream());
            result = scanner.next();
            if (result.equalsIgnoreCase("No")) {
                verification = false;
            }

        } catch (Exception e) {
            verification = false;
        }
        return verification;
    }
}
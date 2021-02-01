package com.example.responsiveui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RecordsActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(RecordsActivity.this, TeacherActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records);

        try {

            String user = returnAccount_Info("user");
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            ArrayList<String> tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Attendance WHERE user='" + user + "' order by Date").get();
            String displayDate = "";

            for (final String temp : tempList) {

                JSONObject jsonObject = new JSONObject(temp);
                String department = jsonObject.getString("Department");
                String className = jsonObject.getString("Class");
                String date = jsonObject.getString("Date");
                String subject = jsonObject.getString("Subject");
                String time = jsonObject.getString("StartTime");
                int practical = jsonObject.getInt("Practical");

                if (practical == 1) {
                    subject += "\n(Practical)";
                }

                if (!displayDate.equals(date)) {
                    TextView textView1 = new Button(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, convertDpToPx(25), 0, convertDpToPx(25));
                    textView1.setLayoutParams(params);
                    textView1.setBackgroundColor(Color.WHITE);
                    textView1.setGravity(Gravity.CENTER_VERTICAL);
                    textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView1.setTextColor( Color.BLACK);
                    textView1.setTextSize(convertDpToPx(10));
                    textView1.setText(date);
                    Typeface face = ResourcesCompat.getFont(this, R.font.abeezee);
                    textView1.setTypeface(face);
                    linearLayout.addView(textView1);
                    displayDate = date;
                }

                Button textView1 = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPx(150));
                params.setMargins(0, 0, 0, convertDpToPx(15));
                textView1.setLayoutParams(params);
                textView1.setPadding(convertDpToPx(15), convertDpToPx(15), convertDpToPx(15), convertDpToPx(15));
                textView1.setTextColor(Color.BLACK);
                textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView1.setGravity(Gravity.CENTER_VERTICAL);
                textView1.setText(subject + "\n\n" + department + " - " + className + "\n\n" + date + " " + time);
                textView1.setTextSize(convertDpToPx(8));
                Typeface face = ResourcesCompat.getFont(this, R.font.abeezee);
                textView1.setTypeface(face);
                textView1.setBackgroundResource(R.drawable.teacher_button);
                textView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(new Intent(RecordsActivity.this, ShowAttendance.class));
                        intent.putExtra("Data", temp);
                        startActivity(intent);
                    }
                });
                linearLayout.addView(textView1);

            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    int convertDpToPx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
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

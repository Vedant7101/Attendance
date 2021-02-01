package com.example.responsiveui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class SubjectActivity extends AppCompatActivity {

    private String department=null, className = null, rollno = null, batch = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_repre);

        try {
            department = returnAccount_Info("department");
            className = returnAccount_Info("class");
            rollno = returnAccount_Info("rollno");
            batch = returnAccount_Info("batch");
        } catch (Exception e) {}

        try {

            Intent intent = getIntent();
            String subject = intent.getStringExtra("subject");
            int practical = Integer.parseInt(intent.getStringExtra("practical"));
            String batch1 = "";
            if (practical == 1) {
                batch1 = batch;
            }

            ArrayList<String> tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select count(*) as count from Attendance WHERE Department='" + department + "' and Class='" + className + "' and Subject='" + subject + "' and Practical=" + practical + " and Batch='" + batch1 + "'").get();
            JSONObject jsonObject = new JSONObject(tempList.get(0));
            int total = jsonObject.getInt("count");
            tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select count(*) as count from Attendance WHERE locate('" + rollno +",', Present) and Department='" + department +"' and Class='" + className +"' and Subject='" + subject + "' and Practical=" + practical + " and Batch='" + batch1 + "'").get();
            jsonObject = new JSONObject(tempList.get(0));
            int attended = jsonObject.getInt("count");
            int attendance = 0;
            if (total != 0)
                attendance = Math.round((attended * 100) / total);

            TextView textView = findViewById(R.id.textView1);
            textView.setText(subject);

            PieChartView pieChartView = findViewById(R.id.pie1);
            pieChartView.setChartRotation(attendance / 100 * 360 - 90, false);
            pieChartView.setChartRotationEnabled(false);

            List pieData = new ArrayList<>();
            pieData.add(new SliceValue(attendance, Color.parseColor("#f80000")));
            pieData.add(new SliceValue(100 - attendance, Color.parseColor("#f9d7b5")));

            PieChartData pieChartData = new PieChartData(pieData);
            pieChartData.setHasCenterCircle(true).setCenterText1(attendance + "%").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#706551"));
            pieChartView.setPieChartData(pieChartData);

            if (total > 0) {

                LinearLayout linearLayout = findViewById(R.id.linearLayout);
                tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Attendance WHERE Department='" + department + "' and Class='" + className + "' and Subject='" + subject + "' and Practical=" + practical + " and Batch='" + batch1 + "'").get();

                for (String temp : tempList) {

                    jsonObject = new JSONObject(temp);
                    String receivedDate = jsonObject.getString("Date");
                    String convertedDate = receivedDate.substring(8, 10) + "/" + receivedDate.substring(5, 7) + "/" + receivedDate.substring(0, 4);
                    String present = jsonObject.getString("Present");

                    TextView textView1 = new TextView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, convertDpToPx(20));
                    textView1.setLayoutParams(params);
                    textView1.setHeight(convertDpToPx(50));
                    textView1.setText(convertedDate);
                    textView1.setPadding(convertDpToPx(15), 0, convertDpToPx(15), 0);
                    textView1.setTextColor(Color.BLACK);
                    textView1.setGravity(Gravity.CENTER_VERTICAL);
                    if (present.contains(rollno + ",")) {
                        textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green_25dp, 0);
                    } else {
                        textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_red_24dp, 0);
                    }
                    textView1.setBackgroundColor(Color.WHITE);
                    linearLayout.addView(textView1);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
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
package com.example.responsiveui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class StudentActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String department=null, className = null, name = null, grno = null, rollno = null, batch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_student);

        try {
            department = returnAccount_Info("department");
            className = returnAccount_Info("class");
            name = returnAccount_Info("first_name") + " " + returnAccount_Info("last_name");
            grno = returnAccount_Info("grno");
            rollno = returnAccount_Info("rollno");
            batch = returnAccount_Info("batch");
        } catch (Exception e) {
        }

        TextView textView = findViewById(R.id.textView1);
        textView.setText(name);
        TextView textView1 = findViewById(R.id.textView2);
        textView1.setText(grno);

        ImageView imageView = findViewById(R.id.imageView1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentActivity.this, OutActivity.class).putExtra("User", "Student"));
            }
        });

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.refreshLayout1);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    addData();

                } catch (Exception e) {
                }
                pullToRefresh.setRefreshing(false);
            }
        });

        try {
            addData();
        } catch (Exception e) {
        }
    }

    void addPopup() {

        try {

            final ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Temporary where Department='" + department + "' and Class='" + className + "'").get();
            if (!arrayList.get(0).equals("No data")) {
                for (String temp : arrayList) {
                    JSONObject jsonObject = new JSONObject(temp);
                    final int practical = jsonObject.getInt("Practical");
                    String subject = jsonObject.getString("Subject");
                    String time1 = jsonObject.getString("StartTime");
                    String time2 = jsonObject.getString("EndTime");
                    String teacher = jsonObject.getString("Teacher");
                    final String batch1 = jsonObject.getString("Batch");
                    ArrayList<String> arrayList1 = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select COUNT(*) as count from Temporary where LOCATE('" + rollno + ",', Present) and Department='" + department + "' and Class='" + className + "' and Subject='" + subject + "' and Practical=" + practical + " and Batch='" + batch1 + "'").get();
                    jsonObject = new JSONObject(arrayList1.get(0));
                    if ((practical == 1 && batch1.equals(batch)) || practical == 0) {
                        if (jsonObject.getInt("count") > 0)
                            continue;
                        if (practical == 1)
                            subject += "\n(Practical)";

                        final LinearLayout linearLayout = findViewById(R.id.linearLayout1);

                        final LinearLayout linearLayout1 = new LinearLayout(this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        linearLayout1.setLayoutParams(params);
                        linearLayout1.setOrientation(LinearLayout.VERTICAL);
                        linearLayout1.setPadding(convertDpToPx(10), 0, convertDpToPx(10), convertDpToPx(20));
                        linearLayout1.setBackgroundResource(R.drawable.subject_button);
                        linearLayout.addView(linearLayout1, 0);

                        TextView textView = new TextView(this);
                        textView.setText(subject);
                        textView.setTextColor(Color.parseColor("#000000"));
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
                        textView.setLayoutParams(params1);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        Typeface face = ResourcesCompat.getFont(this, R.font.abeezee);
                        textView.setTypeface(face);
                        textView.setPadding(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
                        textView.setTextSize(17);
                        linearLayout1.addView(textView);

                        TextView textView1 = new TextView(this);
                        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
                        textView1.setLayoutParams(params3);
                        textView1.setText("Time : " + time1 + " - " + time2);
                        textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView1.setTextColor(Color.BLACK);
                        textView1.setTextSize(15);
                        textView1.setTypeface(face);
                        linearLayout1.addView(textView1);

                        TextView textView2 = new TextView(this);
                        textView2.setLayoutParams(params3);
                        textView2.setText("Teacher : " + teacher);
                        textView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView2.setTextColor(Color.BLACK);
                        textView2.setTextSize(15);
                        textView2.setTypeface(face);
                        linearLayout1.addView(textView2);

                        Button button = new Button(this);
                        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params4.setMargins(0, convertDpToPx(20), 0, 0);
                        params4.gravity = Gravity.CENTER_HORIZONTAL;
                        button.setPadding(convertDpToPx(15), convertDpToPx(15), convertDpToPx(15), convertDpToPx(15));
                        button.setAllCaps(false);
                        button.setText("Mark Attendance");
                        button.setTextSize(17);
                        button.setTypeface(face);
                        button.setLayoutParams(params4);
                        button.setTextColor(Color.parseColor("#000000"));
                        button.setBackgroundResource(R.drawable.mark_button);
                        final String finalSubject = subject;
                        final int finalPractical = practical, finalNumber = Integer.parseInt(rollno);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    String subject = null;
                                    if (finalPractical == 1) {
                                        subject = finalSubject.substring(0, finalSubject.indexOf("\n(Practical)"));
                                    } else {
                                        subject = finalSubject;
                                    }
                                    ArrayList<String> arrayList1 = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/Manipulate.php?query=update Temporary set Present=CONCAT(Present, '" + finalNumber + ",') where Department='" + department + "' and Class='" + className + "' and Subject='" + subject + "' and Practical=" + finalPractical + " and Batch='" + batch1 + "'").get();
                                    if (arrayList1.get(0).equals("Success")) {
                                        Toast.makeText(StudentActivity.this, "Attendance marked.", Toast.LENGTH_SHORT).show();
                                        linearLayout.removeView(linearLayout1);
                                    } else
                                        Toast.makeText(StudentActivity.this, arrayList1.get(0), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                }
                            }
                        });
                        linearLayout1.addView(button);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    void addData() {

        try {

            LinearLayout linearLayout = findViewById(R.id.linearLayout1);
            linearLayout.removeAllViews();
            ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Subject_Info where branch='" + department + "' and class='" + className + "'").get();
            for (String temp : arrayList) {
                temp = temp.trim();
                JSONObject jsonObject = new JSONObject(temp);
                int practical = jsonObject.getInt("practical");
                final String batch1 = jsonObject.getString("batch");
                if ((practical == 1 && batch1.equals(batch)) || practical == 0) {
                    String subject = jsonObject.get("subject").toString();
                    ArrayList<String> tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select count(*) as count from Attendance WHERE Department='" + department + "' and Class='" + className + "' and Subject='" + subject + "' and Practical=" + practical + " and Batch='" + batch1 + "'").get();
                    jsonObject = new JSONObject(tempList.get(0));
                    int total = jsonObject.getInt("count");
                    tempList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select count(*) as count from Attendance WHERE locate('" + rollno + ",', Present) and Department='" + department + "' and Class='" + className + "' and Subject='" + subject + "' and Practical=" + practical + " and Batch='" + batch1 + "'").get();
                    jsonObject = new JSONObject(tempList.get(0));
                    int attended = jsonObject.getInt("count");
                    int attendance = 0;
                    if (total != 0)
                        attendance = Math.round((attended * 100) / total);

                    LinearLayout linearLayout1 = new LinearLayout(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, convertDpToPx(25), 0, 0);
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);
                    linearLayout1.setLayoutParams(params);
                    linearLayout1.setBackgroundResource(R.drawable.subject_button);

                    TextView textView = new TextView(this);
                    if (practical == 1) {
                        textView.setText(subject + "\n(Practical)");
                    } else {
                        textView.setText(subject);
                    }
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(params1);
                    textView.setBackgroundResource(R.drawable.subject_text);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    Typeface face = ResourcesCompat.getFont(this, R.font.abeezee);
                    textView.setTypeface(face);
                    textView.setPadding(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
                    textView.setTextSize(17);
                    linearLayout1.addView(textView);

                    PieChartView pieChartView = new PieChartView(this);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params2.height = convertDpToPx(150);
                    params2.setMargins(convertDpToPx(15), convertDpToPx(15), convertDpToPx(15), convertDpToPx(15));
                    pieChartView.setLayoutParams(params2);
                    pieChartView.setChartRotation(attendance / 100 * 360 - 90, false);
                    pieChartView.setChartRotationEnabled(false);

                    List pieData = new ArrayList<>();
                    pieData.add(new SliceValue(attendance, Color.parseColor("#f88017")));
                    pieData.add(new SliceValue(100 - attendance, Color.parseColor("#f9d7b5")));

                    final String finalSubject = subject;
                    final int finalPractical = practical;
                    PieChartData pieChartData = new PieChartData(pieData);
                    pieChartData.setHasCenterCircle(true).setCenterText1(attendance + "%").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#706551"));
                    pieChartView.setPieChartData(pieChartData);
                    pieChartView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(StudentActivity.this, SubjectActivity.class);
                            intent.putExtra("subject", finalSubject);
                            intent.putExtra("practical", String.valueOf(finalPractical));
                            startActivity(intent);
                        }
                    });
                    linearLayout1.addView(pieChartView);
                    linearLayout.addView(linearLayout1);
                }
            }
            addPopup();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    int convertDpToPx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
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
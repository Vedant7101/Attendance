package com.example.responsiveui;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class AttendancePage extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(AttendancePage.this, TeacherActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.attendance_page);

        try {
            final Spinner spinner = findViewById(R.id.edit1);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getData("class"));
            spinner.setAdapter(arrayAdapter);

            final Spinner spinner1 = findViewById(R.id.edit2);
            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_item, getData("subject"));
            spinner1.setAdapter(arrayAdapter1);

            final EditText editText = findViewById(R.id.edit3);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TimePickerDialog timePickerDialog = new TimePickerDialog(AttendancePage.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            editText.setText(hourOfDay + ":" + minutes);
                        }
                    }, 0, 0, false);
                    timePickerDialog.show();
                }
            });

            final EditText editText1 = findViewById(R.id.edit4);
            editText1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TimePickerDialog timePickerDialog = new TimePickerDialog(AttendancePage.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            editText1.setText(hourOfDay + ":" + minutes);
                        }
                    }, 0, 0, false);
                    timePickerDialog.show();
                }
            });

            Button button = findViewById(R.id.button1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String department = spinner.getSelectedItem().toString().split("-")[0].trim();
                    String className = spinner.getSelectedItem().toString().split("-")[1].trim();
                    String subject = spinner1.getSelectedItem().toString();
                    String user = returnAccount_Info("user");
                    String practical = "0";
                    String name = returnAccount_Info("Name");
                    String batch = "";
                    if (subject.contains("(Practical)")) {
                        subject = subject.substring(0, subject.indexOf(" (Practical)"));
                        practical = "1";
                        try {
                            batch = spinner.getSelectedItem().toString().split("-")[2].trim();
                        } catch (Exception e) {
                        }
                    }
                    String start = editText.getText().toString();
                    String end = editText1.getText().toString();
                    String date =  new SimpleDateFormat("yyyy/MM/dd").format(new Date());

                    try {
                        if (!(practical.equals("1") && batch.equals(""))) {
                            ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/Manipulate.php?query=insert into Temporary values ('" + department + "', '" + className + "', '" + name + "', '" + user + "', '" + subject + "', '" + practical + "','" + batch + "','" + date + "', '" + start + "', '" + end + "', '')").get();
                            if (arrayList.get(0).equals("Success")) {
                                Toast.makeText(AttendancePage.this, "Attendance Started.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AttendancePage.this, TeacherActivity.class));
                            } else
                                Toast.makeText(AttendancePage.this, arrayList.get(0), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AttendancePage.this, "Please select batch", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
        }

        addPopup();
        super.onCreate(savedInstanceState);
    }

    void addPopup() {

        try {

            final ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Temporary where User='" + returnAccount_Info("user") + "'").get();
            if (!arrayList.get(0).equals("No data")) {
                for (String temp : arrayList) {
                    JSONObject jsonObject = new JSONObject(temp);
                    final String department = jsonObject.getString("Department");
                    final String className = jsonObject.getString("Class");
                    final String teacher = jsonObject.getString("Teacher");
                    final String date = jsonObject.getString("Date");
                    final String user = jsonObject.getString("User");
                    final int practical = jsonObject.getInt("Practical");
                    final String batch = jsonObject.getString("Batch");
                    String subject = jsonObject.getString("Subject");
                    final String time1 = jsonObject.getString("StartTime");
                    final String time2 = jsonObject.getString("EndTime");
                    final String present = jsonObject.getString("Present");
                    if (practical == 1) {
                        subject += "\n(Practical)";
                    }

                    ScrollView scrollView = new ScrollView(this);
                    scrollView.setScrollBarSize(0);

                    final LinearLayout linearLayout = findViewById(R.id.linearLayout);
                    linearLayout.removeAllViews();

                    final LinearLayout linearLayout1 = new LinearLayout(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearLayout1.setLayoutParams(params);
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);
                    linearLayout1.setPadding(convertDpToPx(10), 0, convertDpToPx(10), convertDpToPx(20));
                    linearLayout1.setBackgroundResource(R.drawable.subject_button);

                    final TextView textView = new TextView(this);
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
                    textView1.setText("Time : " + time1 + " - " + time2 + "\n\n________________________\n\n Students Joined\n");
                    textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView1.setTextColor(Color.BLACK);
                    textView1.setTextSize(15);
                    textView1.setTypeface(face);
                    linearLayout1.addView(textView1);

                    LinearLayout linearLayout2 = new LinearLayout(this);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(convertDpToPx(150), ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.gravity = Gravity.CENTER_HORIZONTAL;
                    linearLayout2.setGravity(Gravity.CENTER_HORIZONTAL);
                    linearLayout2.setOrientation(LinearLayout.VERTICAL);
                    linearLayout2.setLayoutParams(params2);
                    linearLayout2.setPadding(0, convertDpToPx(15), 0, convertDpToPx(15));

                    final Set<String> set = new TreeSet<>();
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    if (!present.equals(",")) {
                        arrayList1.addAll(Arrays.asList(present.split(",")));
                        int i = 0;
                        for (String no : arrayList1) {
                            if (no.equals("") || no.equals(null))
                                continue;
                            CheckBox checkBox = new CheckBox(AttendancePage.this);
                            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params4.setMargins(0, 0, 0, convertDpToPx(10));
                            checkBox.setTextSize(convertDpToPx(10));
                            checkBox.setTextColor(Color.BLACK);
                            checkBox.setText(no);
                            checkBox.setId(i++);
                            checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                            checkBox.setLayoutParams(params4);
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        set.add(buttonView.getText().toString());
                                    } else {
                                        set.remove(buttonView.getText().toString());
                                    }
                                }
                            });
                            linearLayout2.addView(checkBox);
                        }
                    }

                    linearLayout1.addView(linearLayout2);

                    Button button = new Button(this);
                    LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params4.setMargins(0, convertDpToPx(20), 0, 0);
                    params4.gravity = Gravity.CENTER_HORIZONTAL;
                    button.setPadding(convertDpToPx(15), convertDpToPx(15), convertDpToPx(15), convertDpToPx(15));
                    button.setAllCaps(false);
                    button.setText("Submit");
                    button.setTextSize(17);
                    button.setTypeface(face);
                    button.setLayoutParams(params4);
                    button.setTextColor(Color.parseColor("#000000"));
                    button.setBackgroundResource(R.drawable.mark_button);
                    final String finalSubject = subject;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String present = set.toString().substring(1, set.toString().length() - 1);
                            AlertDialog.Builder builder = new AlertDialog.Builder(AttendancePage.this);
                            builder.setMessage("Do you want to save this attendance?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                String subject = null;
                                                if (practical == 1) {
                                                    subject = finalSubject.substring(0, finalSubject.indexOf("\n(Practical)"));
                                                } else {
                                                    subject = finalSubject;
                                                }
                                                ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/Manipulate.php?query=insert into Attendance values ('" + department + "', '" + className + "', '" + teacher + "', '" + user + "', '" + subject + "', '" + practical + "','" + batch + "','" + date + "', '" + time1 + "', '" + time2 + "', '" + present + ",')").get();
                                                ArrayList<String> arrayList1 = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/Manipulate.php?query=delete from Temporary where Department='" + department + "' and Class='" + className + "' and User='" + user + "' and  Subject='" + subject + "' and Practical='" + practical + "' and Date='" + date + "' and Batch='" + batch + "'").get();
                                                if (arrayList.get(0).equals("Success") && arrayList1.get(0).equals("Success")) {
                                                    Toast.makeText(AttendancePage.this, "Attendance saved successfully.", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(AttendancePage.this, arrayList.get(0), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AttendancePage.this, TeacherActivity.class));
                                            } catch (Exception e) {
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Save Attendance");
                            alert.show();
                        }
                    });
                    linearLayout1.addView(button);
                    scrollView.addView(linearLayout1);
                    linearLayout.addView(scrollView);
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    ArrayList<String> getData(String id) throws Exception {
        Scanner scanner = new Scanner(openFileInput("teacher_subject.txt"));
        ArrayList<String> arrayList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(scanner.nextLine());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String subject = jsonObject.getString(id);
            if (jsonObject.getString("practical").equals("1") && !id.equals("class"))
                subject += " (Practical)";
            if (id.equals("class")) {
                subject = jsonObject.getString("branch") + " - " + subject;
                if (!jsonObject.getString("batch").equals(""))
                    subject += " - " + jsonObject.getString("batch");
            }
            arrayList.add(subject);
        }
        Set<String> set = new TreeSet<>(arrayList);
        arrayList.clear();
        arrayList.addAll(set);
        return arrayList;
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


    int convertDpToPx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}
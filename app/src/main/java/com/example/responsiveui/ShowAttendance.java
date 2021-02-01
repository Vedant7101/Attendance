package com.example.responsiveui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class ShowAttendance extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(ShowAttendance.this, RecordsActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_attendance);

        try {

            String data = getIntent().getStringExtra("Data");

            JSONObject jsonObject = new JSONObject(data);
            final String Department = jsonObject.getString("Department");
            final String Class = jsonObject.getString("Class");
            final String Subject = jsonObject.getString("Subject");
            final String User = jsonObject.getString("User");
            final String Date = jsonObject.getString("Date");
            final String StartTime = jsonObject.getString("StartTime");
            String tempSubject = jsonObject.getString("Present");
            final String Present = tempSubject.substring(0, tempSubject.length()-1);
            final int Practical = jsonObject.getInt("Practical");
            final String Batch = jsonObject.getString("Batch");

            TextView textView = findViewById(R.id.text1);
            if (Practical == 1) {
                textView.setText(Subject + "\n(Practical)");
            } else {
                textView.setText(Subject);
            }
            TextView textView2 = findViewById(R.id.text2);
            textView2.setText(Class);
            TextView textView3 = findViewById(R.id.text3);
            textView3.setText(Date + " " + StartTime);

            ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select eligible from Subject_Info WHERE branch='" +Department + "' and class='" + Class + "' and subject='" + Subject + "' and practical=" + Practical + " and Batch='" + Batch + "'").get();
            jsonObject = new JSONObject(arrayList.get(0));
            String getelegible = jsonObject.getString("eligible");
            String[] eligible = getelegible.split("-" );

            LinearLayout linearLayout = findViewById(R.id.linearLayout);

            final ArrayList<String> arrayList1 = new ArrayList<>();
            if (!Present.equals(",,")) {
                arrayList1.addAll(Arrays.asList(Present.split(",")));
            }

            final Set<String> set = new TreeSet<>();
            for (int i = Integer.parseInt(eligible[0]), j = 0; i <= Integer.parseInt(eligible[1]); i++) {
                CheckBox checkBox = new CheckBox(ShowAttendance.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, convertDpToPx(10));
                checkBox.setTextSize(convertDpToPx(10));
                checkBox.setTextColor(Color.BLACK);
                checkBox.setText(String.valueOf(i));
                checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                checkBox.setId(i % 100);
                checkBox.setLayoutParams(params);
                if (j != arrayList1.size()) {
                    System.out.println("In condition 1 " + i + " " + arrayList1.get(j));
                    if (String.valueOf(i).equals(arrayList1.get(j).trim())) {
                        System.out.println("In condition 2");
                        checkBox.setChecked(true);
                        set.add(String.valueOf(i));
                        j++;
                    }
                }
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
                linearLayout.addView(checkBox);
            }

            Button button = findViewById(R.id.button1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String present = set.toString().substring(1, set.toString().length() - 1);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowAttendance.this);
                    builder.setMessage("Do you want to save this attendance?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        final String present = set.toString().substring(1, set.toString().length() - 1);
                                        ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/Manipulate.php?query=update Attendance set present = '" + present + ",' where Department='" + Department + "' and Class='" + Class + "' and User='" + User + "' and  Subject='" + Subject + "' and Practical='" + Practical + "' and Date='" + Date + "' and Batch='" + Batch + "'").get();
                                        if (arrayList.get(0).equals("Success")) {
                                            Toast.makeText(ShowAttendance.this, "Attendance updated successfully.", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(ShowAttendance.this, arrayList.get(0), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ShowAttendance.this, RecordsActivity.class));
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
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    int convertDpToPx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}

package com.example.responsiveui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DownloadData extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(DownloadData.this, TeacherActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.download_data);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);

            Scanner scanner = new Scanner(openFileInput("teacher_subject.txt"));
            String subjectList = scanner.nextLine();
            JSONArray jsonArray = new JSONArray(subjectList);
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                String Department = jsonObject.getString("branch");
                String Subject = jsonObject.getString("subject");
                int Practical = jsonObject.getInt("practical");
                if (Practical == 1)
                    Subject += "\n(Practical)";
                String Class = jsonObject.getString("class");

                Button textView1 = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPx(100));
                params.setMargins(0, 0, 0, convertDpToPx(20));
                textView1.setLayoutParams(params);
                textView1.setPadding(convertDpToPx(15), convertDpToPx(15), convertDpToPx(15), convertDpToPx(15));
                textView1.setTextColor(Color.BLACK);
                textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView1.setGravity(Gravity.CENTER_VERTICAL);
                textView1.setText(Subject + "\n\n" + Department + " - " + Class);
                textView1.setTextSize(convertDpToPx(8));
                Typeface face = ResourcesCompat.getFont(this, R.font.abeezee);
                textView1.setTypeface(face);
                textView1.setBackgroundResource(R.drawable.teacher_button);
                textView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DownloadData.this);
                        builder.setMessage("Do you want to download attendance?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            downloadPDF(jsonObject);
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
                        alert.setTitle("Download Attendance");
                        alert.show();
                    }
                });
                linearLayout.addView(textView1);
            }

        } catch (Exception e){}
    }

    void downloadPDF(JSONObject jsonObject) {

        try {

            String Department = jsonObject.getString("branch");
            String user = jsonObject.getString("user");
            String Subject = jsonObject.getString("subject");
            int Practical = jsonObject.getInt("practical");
            String Class = jsonObject.getString("class");
            String Batch = jsonObject.getString("batch");
            String eligible[] = jsonObject.getString("eligible").split("-");

            PdfWriter writer = new PdfWriter(Environment.getExternalStorageDirectory() + "/record.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            float displayData[] = {100F, 400F};
            Table info = new Table(displayData);
            info.addCell(new Cell().add("Class").setFontSize(15F));
            info.addCell(new Cell().add(Department + " - " + Class).setFontSize(15F));
            info.addCell(new Cell().add("Subject").setFontSize(15F));
            info.addCell(new Cell().add(Subject)).setFontSize(15F);
            info.setMarginBottom(convertDpToPx(15));
            info.setMarginLeft(convertDpToPx(7));
            doc.add(info);

            ArrayList<String> arrayList = new RetriveData().execute("http://testdbforpbl.000webhostapp.com/PerformQuery.php?query=select * from Attendance where Department='" + Department + "' and Class='" + Class + "' and User='" + user + "' and  Subject='" + Subject + "' and Practical='" + Practical + "' and Batch='" + Batch + "' order by Date").get();
            float dateArray[] = null;
            if (arrayList.size() > 9) {
                dateArray = new float[10];
            } else {
                dateArray = new float[arrayList.size() + 1];
            }
            Arrays.fill(dateArray, 0, dateArray.length, 50F);
            int noTables = new Double(Math.ceil((float) (arrayList.size() + 1) / 9)).intValue();
            Table table[] = new Table[noTables];
            for (int i = 0; i < noTables; i++) {
                table[i] = new Table(dateArray);
                table[i].setMarginLeft(convertDpToPx(7));
            }

            for (int i = Integer.parseInt(eligible[0]) - 1; i <= Integer.parseInt(eligible[1]); i++) {
                if (i == Integer.parseInt(eligible[0]) - 1) {
                    for (int j = 0; j < noTables; j++) {
                        table[j].addCell(new Cell().add("Roll No").setBackgroundColor(com.itextpdf.kernel.color.Color.ORANGE).setFontColor(com.itextpdf.kernel.color.Color.BLACK));
                    }
                } else {
                    for (int j = 0; j < noTables; j++) {
                        table[j].addCell(new Cell().add(String.valueOf(i)).setBackgroundColor(com.itextpdf.kernel.color.Color.ORANGE).setFontColor(com.itextpdf.kernel.color.Color.BLACK));
                    }
                }
                int j = 0;
                for (String temp : arrayList) {
                    JSONObject jsonObject1 = new JSONObject(temp);
                    if (i == Integer.parseInt(eligible[0]) - 1) {
                        String date = jsonObject1.getString("Date");
                        date = date.substring(8, 10) + " " + new DateFormatSymbols().getMonths()[Integer.parseInt(date.substring(5, 7))].substring(0, 3).toUpperCase();
                        table[(int) Math.floor((float) j / 9)].addCell(new Cell().add(date).setBackgroundColor(com.itextpdf.kernel.color.Color.ORANGE).setFontColor(com.itextpdf.kernel.color.Color.BLACK));
                    } else {
                        if (jsonObject1.getString("Present").contains(i + ",")) {
                            table[(int) Math.floor((float) j / 9)].addCell(new Cell().add(new Image(convertDrawable(R.drawable.ic_check_black_10dp)).setWidthPercent(40).setHorizontalAlignment(HorizontalAlignment.CENTER)));
                        } else {
                            table[(int) Math.floor((float) j / 9)].addCell(new Cell().add(new Image(convertDrawable(R.drawable.ic_clear_red_10dp)).setWidthPercent(40).setHorizontalAlignment(HorizontalAlignment.CENTER)));
                        }
                    }
                    j++;
                }
                table[(int) Math.floor((float) (j-1) / 9)].startNewRow();
            }

            for (int i = 0; i < noTables; i++) {
                doc.add(table[i]);
                if (i != noTables - 1)
                    doc.add(new AreaBreak());
            }

            doc.setBorder(new SolidBorder(com.itextpdf.kernel.color.Color.BLACK, 2));
            doc.close();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    int convertDpToPx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    ImageData convertDrawable(int id) {
        Drawable drawable = ContextCompat.getDrawable(this, id);
        drawable = (DrawableCompat.wrap(drawable)).mutate();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        return ImageDataFactory.create(bitmapdata);
    }
}

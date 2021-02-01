package com.example.responsiveui;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

class RetriveData extends AsyncTask<String, Void, ArrayList<String>> {

    ArrayList<String> arrayList;

    @Override
    protected ArrayList<String> doInBackground(String... para) {
        try {
            String link = para[0];
            link = link.replace(" ", "%20");
            link = link.replace("'", "%27");
            URL url = new URL(link);
            Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
            arrayList = new ArrayList<>();
            while (scanner.hasNext()) {
                arrayList.add(scanner.nextLine());
            }
        } catch (Exception e) {
        }
        return arrayList;
    }
}
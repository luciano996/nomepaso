package com.example.nomepaso.data;

import android.content.Context;
import com.example.nomepaso.model.Stop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes4.dex */
public class JsonDataManager {
    private static final String FILE_NAME = "stops_data.json";
    private final Context context;
    private final Gson gson = new Gson();

    public JsonDataManager(Context context) {
        this.context = context;
    }

    public void saveStops(List<Stop> stops) {
        File file = new File(this.context.getFilesDir(), FILE_NAME);
        try {
            FileWriter writer = new FileWriter(file);
            try {
                this.gson.toJson(stops, writer);
                writer.close();
            } finally {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Stop> loadStops() {
        File file = new File(this.context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            return new ArrayList();
        }
        try {
            FileReader reader = new FileReader(file);
            try {
                Type listType = new TypeToken<ArrayList<Stop>>() { // from class: com.example.nomepaso.data.JsonDataManager.1
                }.getType();
                List<Stop> list = (List) this.gson.fromJson(reader, listType);
                reader.close();
                return list;
            } finally {
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }
}

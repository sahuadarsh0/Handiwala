package com.tecqza.handiwala;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryModel {

    String id, name, image, details, cdt;
    public CategoryModel(JSONObject category) throws JSONException {
        this.id=category.getString("id");
        this.name=category.getString("name");
        this.image=category.getString("image");
        this.details=category.getString("details");
        this.cdt=category.getString("cdt");
    }

    public static ArrayList<CategoryModel> fromJson(JSONArray jsonObjects) {
        ArrayList<CategoryModel> categories = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                categories.add(new CategoryModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }

}

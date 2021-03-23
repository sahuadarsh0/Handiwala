package com.tecqza.handiwala;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductModel {

    String id, name, image, category, product_description, flag, cdt, in_stock, popular, popularity, varieties;


    public ProductModel(JSONObject product) throws JSONException {
        this.id = product.getString("id");
        this.name = product.getString("name");
        this.image = product.getString("image");
        this.category = product.getString("category");
        this.in_stock = product.getString("in_stock");
        this.product_description = product.getString("product_description");
        this.flag = product.getString("flag");
        this.cdt = product.getString("cdt");
        this.varieties = product.getString("variety");
        this.popular = product.getString("popular");
        this.popularity = product.getString("popularity");


    }

    public static ArrayList<ProductModel> fromJson(JSONArray jsonObjects) {
        ArrayList<ProductModel> products = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                products.add(new ProductModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return products;
    }
}

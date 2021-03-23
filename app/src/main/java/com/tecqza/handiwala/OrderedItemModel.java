package com.tecqza.handiwala;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderedItemModel {

    String id, order_id, product, variety, qty, amount, total_amount, flag, cdt, product_name, variety_name;
    public OrderedItemModel(JSONObject item) throws JSONException {
        id=item.getString("id");
        order_id=item.getString("order_id");
        product=item.getString("product");
        variety=item.getString("variety");
        qty=item.getString("qty");
        amount=item.getString("amount");
        total_amount=item.getString("total_amount");
        flag=item.getString("flag");
        cdt=item.getString("cdt");
        product_name=item.getString("product_name");
        variety_name=item.getString("variety_name");
    }

    public static ArrayList<OrderedItemModel> fromJson(JSONArray jsonObjects) {
        ArrayList<OrderedItemModel> items = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                items.add(new OrderedItemModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
}

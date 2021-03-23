package com.tecqza.handiwala;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderModel {

    String id, customer, total, tax, grand_total, items, status, flag, cdt;
    public OrderModel(JSONObject order) throws JSONException {
        id=order.getString("id");
        customer=order.getString("customer");
        total=order.getString("total");
        tax=order.getString("tax");
        grand_total=order.getString("grand_total");
        items=order.getString("items");
        status=order.getString("status");
        flag=order.getString("flag");
        cdt=order.getString("cdt");
    }

    public static ArrayList<OrderModel> fromJson(JSONArray jsonObjects) {
        ArrayList<OrderModel> orders = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                orders.add(new OrderModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return orders;
    }
}

package com.mycompany.mavenproject3;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mycompany.mavenproject3.transaction.Transaction;
import com.mycompany.mavenproject3.transaction.TransactionDetail;
import com.mycompany.mavenproject3.transaction.TransactionDetailService;

public class TransactionDeserializer implements JsonDeserializer<Transaction> {
    @Override
    public Transaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        System.out.println(json);

        /*
         * {
         *      "id":1,
         *      "code":"SF001",
         *      "cashier":"donny",
         *      "customer":"Cash",
         *      "dateTime":"26::Jun::2025 09::34::33",
         *      "total":18000.0,
         *      "detailService": {
         *          "transactionDetailList":[
         *              {
         *                  "id":1,
         *                  "productId":1,
         *                  "qty":1,
         *                  "total":18000.0
         *              }
         *          ],
         *          "listeners":[],
         *          "currentId":1
         *      }
         * }
         */
        int id = jsonObject.get("id").getAsInt();
        String code = jsonObject.get("code").getAsString();
        String cashier = jsonObject.get("cashier").getAsString();
        String customer = jsonObject.get("customer").getAsString();
        LocalDateTime dateTime = LocalDateTime.parse(jsonObject.get("dateTime").getAsString(), DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss"));
        double total = jsonObject.get("total").getAsDouble();
     
        JsonObject detailServiceObject = jsonObject.getAsJsonObject("detailService");

        Type detailListType = new TypeToken<ArrayList<TransactionDetail>>() {}.getType();
        List<TransactionDetail> detailList = context.deserialize(detailServiceObject.get("transactionDetailList"), detailListType);

        int currentId = detailServiceObject.get("currentId").getAsInt();
        
        TransactionDetailService detailService = new TransactionDetailService(detailList, new ArrayList<>(), currentId);

        return new Transaction(id, code, cashier, customer, dateTime, total, detailService);
    }
}

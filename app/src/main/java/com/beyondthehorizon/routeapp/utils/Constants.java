package com.beyondthehorizon.routeapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;

public class Constants {
    public static String BASE_URL = "http://167.172.214.193/api/v1/";
    private static boolean ALLOW_REDIRECT = false;
    public static String REG_APP_PREFERENCES = "profilePref";

    public static final String LOGGED_IN = "LOGGED_IN";
    public static final String TRANSACTIONS_PIN = "TRANSACTIONS_PIN";

    public static final String FirstName = "FirstName";
    public static final String LastName = "LastName";
    public static final String SurName = "SurName";
    public static final String UserName = "UserName";

    public static final String ID_NUMBER = "ID_NUMBER";
    public static final String USER_EMAIL = "EMAIL";

    public static final String MyPhoneNumber = "MyPhoneNumber";
    public static final String USER_PASSWORD = "USER_PASSWORD";

    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE = "REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE";
    public static final String REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY = "REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY";
    public static final String REQUEST_MONEY = "REQUEST_MONEY";
    public static final String SEND_MONEY = "SEND_MONEY";
    public static final String SEND_MONEY_TO_ROUTE = "SEND_MONEY_TO_ROUTE";
    public static final String SEND_MONEY_TO_MOBILE_MONEY = "SEND_MONEY_TO_MOBILE_MONEY";
    public static final String SEND_MONEY_TO_BANK = "SEND_MONEY_TO_BANK";

    public static final String MOBILE_PROVIDERS = "MOBILE_PROVIDERS";
    public static final String BANK_PROVIDERS = "BANK_PROVIDERS";


    public static ResponseFuture<JsonObject> sendSignInRequest(Context context, String first_name, String last_name,
                                                               String surname, String username, String password, String id_number,
                                                               String phone_number, String email) {
        String SERVER_URL = BASE_URL + "users/signup";

        JsonObject json = new JsonObject();
        json.addProperty("first_name", first_name);
        json.addProperty("last_name", last_name);
        json.addProperty("surname", surname);
        json.addProperty("username", username);
        json.addProperty("password", password);
        json.addProperty("id_number", id_number);
        json.addProperty("phone_number", phone_number);
        json.addProperty("email", email);

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> sendLogInRequest(Context context, String password, String email) {
        String SERVER_URL = BASE_URL + "users/login";
        JsonObject json = new JsonObject();
        json.addProperty("password", password);
        json.addProperty("email", email);

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> getUserProfile(Context context, String token) {
        String SERVER_URL = BASE_URL + "users/profile";
        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> setUserPin(Context context, String token, String pin) {
        String SERVER_URL = BASE_URL + "users/pin";
        JsonObject json = new JsonObject();
        json.addProperty("pin", pin);
        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> verifyUserEntry(Context context, String entryType, String entryContent) {
        String SERVER_URL = BASE_URL + "users/signup/validation";
        JsonObject json = new JsonObject();
        json.addProperty(entryType, entryContent);
        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> loadUserContacts(Context context, String token) {
        String SERVER_URL = BASE_URL + "users/retrieve";
        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> requestFund(Context context, String recipient, String amount, String reason, String token) {
        String SERVER_URL = BASE_URL + "requests/";

        JsonObject json = new JsonObject();
        json.addProperty("recipient", recipient);
        json.addProperty("amount", amount);
        json.addProperty("reason", reason);

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> getFundRequests(Context context, String option, String token) {
        String SERVER_URL = BASE_URL + "requests/?request_option=" + option;

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> approveFundRequests(Context context, String request_id, String pin, String narration, String provider, String token) {
        String SERVER_URL = BASE_URL + "wallets/transactions/" + request_id;

        JsonObject json = new JsonObject();
        json.addProperty("pin", pin);
        json.addProperty("narration", narration);
        json.addProperty("provider", provider);

        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> rejectFundRequests(Context context, String request_id, String canceled_by, String status, String cancellation_reason, String token) {
        String SERVER_URL = BASE_URL + "requests/cancel/" + request_id;

        JsonObject json = new JsonObject();
        json.addProperty("canceled_by", canceled_by);
        json.addProperty("status", status);
        json.addProperty("cancellation_reason", cancellation_reason);

        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> verifyPin(Context context, String pin, String token) {
        String SERVER_URL = BASE_URL + "users/pin/verify";

        JsonObject json = new JsonObject();
        json.addProperty("pin", pin);

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> sendMoney(Context context, String beneficiary_account,
                                                       String amount, String pin, String token,
                                                       String provider, String narration) {
        String SERVER_URL = BASE_URL + "wallets/transactions";
        JsonObject json = new JsonObject();
        json.addProperty("beneficiary_account", beneficiary_account);
        json.addProperty("amount", amount);
        json.addProperty("pin", pin);
        json.addProperty("narration", narration);
        json.addProperty("provider", provider);

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    public static ResponseFuture<JsonObject> sendMoneyBeneficiary(Context context, String beneficiary_account,
                                                                  String amount, String pin, String token,
                                                                  String provider, String beneficiary_reference,
                                                                  String narration) {
        String SERVER_URL = BASE_URL + "wallets/transactions";
        JsonObject json = new JsonObject();
        json.addProperty("beneficiary_account", beneficiary_account);
        json.addProperty("amount", amount);
        json.addProperty("pin", pin);
        json.addProperty("narration", narration);
        json.addProperty("provider", provider);
        json.addProperty("beneficiary_reference", beneficiary_reference);

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //GET SERVICE PROVIDERS
    public static ResponseFuture<JsonObject> getServiceProviders(Context context, String token) {
        String SERVER_URL = BASE_URL + "wallets/providers";

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }

    //UPDATE FIREBASE TOKEN
    public static ResponseFuture<JsonObject> updateFirebaseToken(Context context, String token,
                                                                 String registration_token) {
        String SERVER_URL = BASE_URL + "users/profile";
        JsonObject json = new JsonObject();
        json.addProperty("registration_token", registration_token);
        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //UPDATE USER PROFILE
    public static ResponseFuture<JsonObject> updateUserProfile(Context context, String token,
                                                               String phone_number, String username) {
        String SERVER_URL = BASE_URL + "users/profile";
        JsonObject json = new JsonObject();
        json.addProperty("phone_number", phone_number);
        json.addProperty("username", username);
        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //GET STATEMENT
    public static ResponseFuture<JsonObject> getUserStatement(Context context, String token) {
        String SERVER_URL = BASE_URL + "wallets/statement";
        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }
}

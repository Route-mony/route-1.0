package com.beyondthehorizon.routeapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;

public class Constants {
    private static String BASE_URL = "http://167.172.214.193/api/v1/";
    private static boolean ALLOW_REDIRECT = false;
    public static String REG_APP_PREFERENCES = "profilePref";

    public static final String LOGGED_IN = "LOGGED_IN";

    public static final String FirstName = "FirstName";
    public static final String LastName = "LastName";
    public static final String SurName = "SurName";
    public static final String UserName = "UserName";

    public static final String ID_NUMBER = "ID_NUMBER";
    public static final String USER_EMAIL = "EMAIL";

    public static final String MyPhoneNumber = "MyPhoneNumber";
    public static final String USER_PASSWORD = "USER_PASSWORD";

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
                .load("http://167.172.214.193/api/v1/users/signup")
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonObject();
    }
}

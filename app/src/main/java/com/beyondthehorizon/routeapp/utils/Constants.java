package com.beyondthehorizon.routeapp.utils;

import android.content.Context;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;

public class Constants {
    public static String BASE_URL = "http://167.172.214.193/api/v1/";
    private static boolean ALLOW_REDIRECT = false;
    public static String REG_APP_PREFERENCES = "profilePref";
    public static String VISITING_HISTORY_PROFILE = "VISITING_HISTORY_PROFILE";
    public static String TRANS_TYPE = "TRANS_TYPE";
    public static String TRANSACTION_DETAILS = "TRANSACTION_DETAILS";
    public static String SHARE_RECEIPT_TO_ID = "SHARE_RECEIPT_TO_ID";
    public static String SHARE_RECEIPT_TITLE = "SHARE_RECEIPT_TITLE";

    public static final String LOGGED_IN = "LOGGED_IN";
    public static final String TRANSACTIONS_PIN = "TRANSACTIONS_PIN";

    public static final String FirstName = "FirstName";
    public static final String LastName = "LastName";
    public static final String SurName = "SurName";
    public static final String UserName = "UserName";

    public static final String ID_NUMBER = "ID_NUMBER";
    public static final String USER_EMAIL = "EMAIL";
    public static final String USER_ID = "USER_ID";

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

    public static final String LOAD_WALLET_FROM_CARD = "LOAD_WALLET_FROM_CARD";
    public static final String LOAD_WALLET_FROM_MPESA = "LOAD_WALLET_FROM_MPESA";
    public static final String CARD_NUMBER = "CARD_NUMBER";
    public static final String EXPIRY_DATE = "EXPIRY_DATE";
    public static final String CVV_NUMBER = "CVV_NUMBER";
    public static final String COUNTRY = "COUNTRY";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String BUY_AIRTIME = "BUY_AIRTIME";
    public static final String MOBILE_TRANSACTION = "MOBILE_TRANSACTION";
    public static final String CARDS = "CARD";
    public static final String CARD_STATUS = "CARD";
    public static final String NEW_CARD = "NEW_CARD";
    public static final String OLD_CARD = "OLD_CARD";
    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final String ADD_MONEY_ACTIVITY = "ADD_MONEY_ACTIVITY";
    public static final String RESET_PASSWORD_ACTIVITY = "RESET_PASSWORD_ACTIVITY";

    //firebase images
    public static final String RECEIPTS = "RECEIPTS";
    public static final String PROFILE_IMAGES = "PROFILE IMAGES";

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

    //UPDATE USER PROFILE IMAGE
    public static ResponseFuture<JsonObject> updateUserProfileImage(Context context, String token,
                                                                    String image) {
        String SERVER_URL = BASE_URL + "users/profile";
        JsonObject json = new JsonObject();
        json.addProperty("image", image);
        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //GET STATEMENT
    public static ResponseFuture<JsonObject> getUserStatement(Context context, String token, String transaction_type) {

        String SERVER_URL = BASE_URL + "wallets/statement?transaction_type=" + transaction_type;
        return Ion.with(context)
                .load("GET", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }

    //GET RECEIPT http://167.172.214.193/api/v1/receipts/?receipt_option=sent/received
    public static ResponseFuture<JsonObject> getUserReceipt(Context context, String token, String receipt_type) {

        String SERVER_URL = BASE_URL + "receipts/?receipt_option=" + receipt_type;
        return Ion.with(context)
                .load("GET", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }

    //GET RECEIPT http://167.172.214.193/api/v1/receipts/?receipt_option=sent/received
    public static ResponseFuture<JsonObject> postUserReceipt(Context context, String token,
                                                             String title,
                                                             String recipient,
                                                             String amount_spent,
                                                             String description,
                                                             String image,
                                                             String transaction_date) {
        JsonObject json = new JsonObject();
        json.addProperty("title", title);
        json.addProperty("recipient", recipient);
        json.addProperty("amount_spent", amount_spent);
        json.addProperty("description", description);
        json.addProperty("image", image);
        json.addProperty("transaction_date", transaction_date);

        String SERVER_URL = BASE_URL + "receipts/";
        return Ion.with(context)
                .load("POST", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    // approveUserReceipt
    public static ResponseFuture<JsonObject> approveUserReceipt(Context context, String token,
                                                                String receipt_id) {
        String SERVER_URL = BASE_URL + "receipts/" + receipt_id + "/approve";
        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asJsonObject();
    }

    // rejectUserReceipt
    public static ResponseFuture<JsonObject> rejectUserReceipt(Context context, String token,
                                                               String receipt_id,
                                                               String cancel_reason) {
        JsonObject json = new JsonObject();
        json.addProperty("cancellation_reason", cancel_reason);
        String SERVER_URL = BASE_URL + "receipts/" + receipt_id + "/cancel";
        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //ADD PAYMENT CARD
    public static ResponseFuture<JsonObject> addPaymentCard(Context context, String cardNumber,
                                                            String expiryDate, String cvv, String country, String token) {
        String SERVER_URL = BASE_URL + "payments/debitcard";
        JsonObject json = new JsonObject();
        json.addProperty("card_number", cardNumber);
        json.addProperty("expiry_date", expiryDate);
        json.addProperty("cvv", cvv);
        json.addProperty("country", country);

        return Ion.with(context)
                .load(SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //PASSWORD CHANGE
    public static ResponseFuture<JsonObject> changePassword(Context context, String new_password, String old_password, String token) {
        String SERVER_URL = BASE_URL + "users/password-change";
        JsonObject json = new JsonObject();
        json.addProperty("new_password", new_password);
        json.addProperty("previous_password", old_password);

        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //RESET PASSWORD
    public static ResponseFuture<JsonObject> resetPassword(Context context, String email) {
        String SERVER_URL = BASE_URL + "users/password-reset-otp";
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        return Ion.with(context)
                .load("POST", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //OTP VERIFY
    public static ResponseFuture<JsonObject> otpVerify(Context context, String email, String otp) {
        String SERVER_URL = BASE_URL + "users/password-otp-verify";
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("one_time_password", otp);
        return Ion.with(context)
                .load("POST", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonObject();
    }

    //UPDATE PASSWORD
    public static ResponseFuture<JsonObject> updatePassword(Context context, String password,
                                                                    String token) {
        String SERVER_URL = BASE_URL + "users/profile";
        JsonObject json = new JsonObject();
        json.addProperty("password", password);
        return Ion.with(context)
                .load("PATCH", SERVER_URL)
                .addHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .setJsonObjectBody(json)
                .asJsonObject();
    }
}

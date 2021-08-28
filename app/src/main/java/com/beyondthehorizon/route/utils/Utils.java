package com.beyondthehorizon.route.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.WALLET_BALANCE;
import static com.beyondthehorizon.route.utils.Constants.getWalletBalance;

public class Utils {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public Utils(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(REG_APP_PREFERENCES, 0);
        this.editor = pref.edit();
    }

    public static boolean isPhoneNumberValid(String phoneNumber, String countryCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        boolean result = false;
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            result = phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            Timber.d(e.getMessage());
        }

        return result;
    }

    public static String getFormattedPhoneNumber(String phoneNumber, String countryCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber number;
        try {
            number = phoneUtil.parse(phoneNumber, countryCode);
        } catch (NumberParseException e) {
            Timber.d(e);
            return "";
        }
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replaceAll(" ", "");
    }


    public static boolean passwordValidator(String password) {
        String regex = "^(?=.[0-9])" + "(?=.[a-z])(?=.[A-Z])" + "(?=.[*.!_@#$%^&+={}(|):;<>?/~-])" + "(?=\\S+$).{8,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static boolean emailValidator(String email) {
        final Pattern EMAIL_REGEX = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email.trim()).matches();
    }

    public static String invalidPasswordMessage() {
        return "Password must be between 8 and 20 characters; must contain at least one lowercase letter, one uppercase letter, one numeric digit, and one special character";
    }

    public void loadWalletBalance(String token) {
        getWalletBalance(context, token)
                .setCallback((e, result) -> {
                    try {
                        if (result.has("data")) {
                            String balance = result.get("data").getAsJsonObject().get("wallet").getAsJsonObject().get("available_balance").getAsString();
                            editor.putString(WALLET_BALANCE, balance);
                            editor.commit();
                        } else {
                            Timber.d(e);
                        }
                    } catch (Exception ex) {
                        Timber.d(ex);
                    }
                });
    }

    public String getCountrySymbol() {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Locale locale = new Locale("", tm.getNetworkCountryIso().toUpperCase());
            return locale.getCountry();
        } catch (Exception ex) {
            return Locale.getDefault().getDisplayCountry();
        }
    }
}

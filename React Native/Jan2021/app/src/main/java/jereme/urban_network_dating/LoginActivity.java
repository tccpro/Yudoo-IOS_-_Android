package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.messengerapp.RetrofitInstance;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;
import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.LanguageSpinnerAdapter;
import jereme.urban_network_dating.Chats.Singleton;
import jereme.urban_network_dating.List.UserModel;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    Dialog dialog;
    EditText etEmail, etPassword;
    String login_success = "0", myname = "", myid = "", myphoto = "";
    Dialog loadingDialog;
    private String this_username, this_password;
    public static String base_url = "https://urban.network/Apinew/";
    public static String terms_url = "https://urban.network/terms.php";

    // public static String nodebase_url = "https://urban.network/nodejs/";
    public static String nodebase_url = "http://192.168.0.109:3000/";
    public static String static_email;
    public static String deviceLanguage = "";
    public static String deviceCountry = "";
    private static final String ENGLISH_LOCALE = "English";
    private static final String AFRIKAANS_LOCALE = "Afrikaans";
    private static final String ALBANIAN_LOCALE = "Albanian";
    private static final String ARMENIAN_LOCALE = "Armenian";
    private static final String AZERBAIJANI_LOCALE = "Azerbaijani";
    private static final String BANGALA_LOCALE = "Bangala";
    private static final String BELARUSIAN_LOCALE = "Belarusian";
    private static final String BOSNIAN_LOCALE = "Bosinian";
    private static final String CATALAN_LOCALE = "Catalan";
    private static final String CHINESE_LOCALE = "Chinese";
    private static final String CZECH_LOCALE = "Czech";
    private static final String DANISH_LOCALE = "Danish";
    private static final String DUTCH_LOCALE = "Dutch";
    private static final String FRANCH_LOCALE = "Franch";
    private static final String GERMAN_LOCALE = "German";
    private static final String ITALIAN_LOCALE = "Italian";
    private static final String JAPANESE_LOCALE = "Japanese";
    private static final String KHMER_LOCALE = "Khmer";
    private static final String MALAY_LOCALE = "Malay";
    private static final String POLISH_LOCALE = "Polish";
    private static final String PORTUGUESE_LOCALE = "Portuguese";
    private static final String RUSSIAN_LOCALE = "Russian";
    private static final String SPANISH_LOCALE = "Spanish";
    private static final String ARABIC_LOCALE = "Arabic";
    private static final String PERSIAN_LOCALE = "Persian";

    public static String profile_id, profile_name, profile_searchName, profile_email, profile_password, profile_picture, profile_hometown, profile_currentCity;
    public static String profile_paypalEmail, profile_information, profile_status, profile_photoUrl = "", profile_points, profile_dating, profile_birthday;
    public static String profile_friendrequest = "", profile_membermsg, profile_membersearch, profile_userquote, deviceid = "";

    public static Locale locale = new Locale("en");
    public static String locale_name = "en";
    private Spinner spinnerLanguage;
    final String[] languageList = {
        ENGLISH_LOCALE, AFRIKAANS_LOCALE, ALBANIAN_LOCALE, ARMENIAN_LOCALE, AZERBAIJANI_LOCALE, BANGALA_LOCALE,
        BELARUSIAN_LOCALE, BOSNIAN_LOCALE, CATALAN_LOCALE, CHINESE_LOCALE, CZECH_LOCALE, DANISH_LOCALE,DUTCH_LOCALE,
        FRANCH_LOCALE, GERMAN_LOCALE, ITALIAN_LOCALE, JAPANESE_LOCALE, KHMER_LOCALE,
        MALAY_LOCALE, POLISH_LOCALE, PORTUGUESE_LOCALE, RUSSIAN_LOCALE, SPANISH_LOCALE,
    };

    final int[] flag={
        R.drawable.en, R.drawable.afrikaans, R.drawable.albania, R.drawable.armenian, R.drawable.azerbaijani, R.drawable.bangla,
        R.drawable.belarus, R.drawable.bosnia, R.drawable.catalonia, R.drawable.china, R.drawable.czech, R.drawable.danish,
        R.drawable.dutch, R.drawable.france, R.drawable.germany, R.drawable.italian, R.drawable.japan, R.drawable.khmer,
        R.drawable.malaysia, R.drawable.polish, R.drawable.portuguese, R.drawable.russia, R.drawable.spain
    };

    TextView tvSelectLanguage, btnCondition;
    CheckBox chkTermsOfUse;
    ImageView flagImag;
    int languageNum = 0;
    ProgressDialog progress;
    public static boolean flagOnStart;

    LinearLayout ll_message;
    TextView tv_message;
    String reason = "";
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences loadUser1 = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        String storedLanguage = loadUser1.getString("storedLanguageName", "0");
        String storedflag = loadUser1.getString("storedLanguage", "0");

        if (storedLanguage.equals("0")) {
            locale = new Locale("en");
            locale_name = "en";
            //  flagImag = (ImageView) findViewById(R.id.flag);
            //  flagImag.setImageResource(flag[0]);
        } else {
            locale = new Locale(storedLanguage);
            locale_name = storedLanguage;
            //  flagImag = (ImageView) findViewById(R.id.flag);
            //  flagImag.setImageResource(R.drawable.default_head_icon);
        }

        //  locale = new Locale("en");
        //  locale_name = "en";
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_login);
        //  flagImag = findViewById(R.id.flag);
        Button loginBtn = findViewById(R.id.login_btn);
        //  Button registerUserButton = findViewById(R.id.register_btn);
        //  Button registerGroupButton = findViewById(R.id.register_as_group_btn);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        //  tvSelectLanguage = findViewById(R.id.select_language_btn);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        //  registerUserButton.setOnClickListener(this);
        //  registerGroupButton.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        ll_message = findViewById(R.id.ll_message);
        tv_message = findViewById(R.id.tv_message);

        //  tvSelectLanguage.setOnClickListener(this);
        //  int flagnumber = Integer.parseInt(storedflag);

        //  if (0 < flagnumber) {
        //        flagnumber = flagnumber - 1;
        //  }

        //  flagImag.setImageResource(flag[flagnumber]);
        //  int newHeight = 40, newWidth = 45;
        //  flagImag.requestLayout();
        //  flagImag.getLayoutParams().height = newHeight;
        //  flagImag.getLayoutParams().width = newWidth;
        //  flagImag.setScaleType(ImageView.ScaleType.FIT_XY);
        //  flagImag.setOnClickListener(this);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences loadUser = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        String storedUsername = loadUser.getString("storedUsername", "0");
        String storedPassword = loadUser.getString("storedPassword", "0");

        if (storedUsername.equals("0")) {

        } else {
            etEmail.setText(storedUsername);
            etPassword.setText(storedPassword);
        }

        btnCondition = (TextView) findViewById(R.id.btnCondition);
        btnCondition.setOnClickListener(this);
        chkTermsOfUse = findViewById(R.id.chkTermsOfUse);
    }

    public void popup_selectLanguage() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_selectlanguage);

        final Resources resources = getResources();
        TextView cancelBtn, okBtn;
        okBtn = dialog.findViewById(R.id.btn_ok);
        cancelBtn = dialog.findViewById(R.id.btn_cancel);
        deviceLanguage = Locale.getDefault().getDisplayLanguage();
        deviceCountry = Locale.getDefault().getDisplayCountry();
        spinnerLanguage = dialog.findViewById(R.id.spinner_language);

        spinnerLanguage.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (languageList[position].equals(ENGLISH_LOCALE)) {
                    locale = new Locale("en");
                    locale_name = "en";
                    languageNum = 1;
                } else if (languageList[position].equals(AFRIKAANS_LOCALE)) {
                    locale = new Locale("af");
                    locale_name = "af";
                    languageNum = 2;
                } else if (languageList[position].equals(ALBANIAN_LOCALE)) {
                    locale = new Locale("sq");
                    locale_name = "sq";
                    languageNum = 3;
                } else if (languageList[position].equals(ARMENIAN_LOCALE)) {
                    locale = new Locale("hy");
                    locale_name = "hy";
                    languageNum = 4;
                } else if (languageList[position].equals(AZERBAIJANI_LOCALE)) {
                    locale = new Locale("az");
                    locale_name = "az";
                    languageNum = 5;
                } else if (languageList[position].equals(BANGALA_LOCALE)) {
                    locale = new Locale("bn");
                    locale_name = "bn";
                    languageNum = 6;
                } else if (languageList[position].equals(BELARUSIAN_LOCALE)) {
                    locale = new Locale("be");
                    locale_name = "be";
                    languageNum = 7;
                } else if (languageList[position].equals(BOSNIAN_LOCALE)) {
                    locale = new Locale("bs");
                    locale_name = "bs";
                    languageNum = 8;
                } else if (languageList[position].equals(CATALAN_LOCALE)) {
                    locale = new Locale("ca");
                    locale_name = "ca";
                    languageNum = 9;
                } else if (languageList[position].equals(CHINESE_LOCALE)) {
                    locale = new Locale("zh");
                    locale_name = "zh";
                    languageNum = 10;
                } else if (languageList[position].equals(CZECH_LOCALE)) {
                    locale = new Locale("cs");
                    locale_name = "cs";
                    languageNum = 11;
                } else if (languageList[position].equals(DANISH_LOCALE)) {
                    locale = new Locale("da");
                    locale_name = "da";
                    languageNum = 12;
                } else if (languageList[position].equals(DUTCH_LOCALE)) {
                    locale = new Locale("nl");
                    locale_name = "nl";
                    languageNum = 13;
                } else if (languageList[position].equals(FRANCH_LOCALE)) {
                    locale = new Locale("fr");
                    locale_name = "fr";
                    languageNum = 14;
                } else if (languageList[position].equals(GERMAN_LOCALE)) {
                    locale = new Locale("de");
                    locale_name = "de";
                    languageNum = 15;
                } else if (languageList[position].equals(ITALIAN_LOCALE)) {
                    locale = new Locale("it");
                    locale_name = "it";
                    languageNum = 16;
                } else if (languageList[position].equals(JAPANESE_LOCALE)) {
                    locale = new Locale("ja");
                    locale_name = "ja";
                    languageNum = 17;
                } else if (languageList[position].equals(KHMER_LOCALE)) {
                    locale = new Locale("km");
                    locale_name = "km";
                    languageNum = 18;
                } else if (languageList[position].equals(MALAY_LOCALE)) {
                    locale = new Locale("ms");
                    locale_name = "ms";
                    languageNum = 19;
                } else if (languageList[position].equals(POLISH_LOCALE)) {
                    locale = new Locale("pl");
                    locale_name = "pl";
                    languageNum = 20;
                } else if (languageList[position].equals(PORTUGUESE_LOCALE)) {
                    locale = new Locale("pt");
                    locale_name = "pt";
                    languageNum = 21;
                } else if (languageList[position].equals(RUSSIAN_LOCALE)) {
                    locale = new Locale("ru");
                    locale_name = "ru";
                    languageNum = 22;
                } else if (languageList[position].equals(SPANISH_LOCALE)) {
                    locale = new Locale("es");
                    locale_name = "es";
                    languageNum = 23;
                } else if (languageList[position].equals(ARABIC_LOCALE)) {
                    locale = new Locale("en");
                    locale_name = "en";
                    languageNum = 1;
                } else if (languageList[position].equals(PERSIAN_LOCALE)) {
                    locale = new Locale("en");
                    locale_name = "en";
                    languageNum = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        LanguageSpinnerAdapter customAdapter = new LanguageSpinnerAdapter(getApplicationContext(), languageList, flag);
        spinnerLanguage.setAdapter(customAdapter);
        SharedPreferences loadUser = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        String storedLanguage = loadUser.getString("storedLanguage", "0");

        int a = Integer.valueOf(storedLanguage);
        if (a != 0)
            spinnerLanguage.setSelection(a - 1);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                getBaseContext().getResources().updateConfiguration(configuration,
                        getBaseContext().getResources().getDisplayMetrics());

                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = saveUser.edit();
                editor.putString("storedLanguage", String.valueOf(languageNum));
                editor.putString("storedLanguageName", String.valueOf(locale_name));
                editor.commit();

                flagOnStart = true;
                recreate();
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_btn) {
            ll_message.setVisibility(View.GONE);

            if (chkTermsOfUse.isChecked()) {
                loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Authenticating");
                if (etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
                    new GlideToast.makeToast(LoginActivity.this, getResources().getString(R.string.input_username_password));
                } else {
                    progress = new ProgressDialog(this);
                    progress.setTitle(getResources().getString(R.string.loading));
                    progress.setMessage(getResources().getString(R.string.wait) + "...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    //  loadingDialog.show();
                    this_username = etEmail.getText().toString();
                    this_password = etPassword.getText().toString();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

                    deviceid = sharedPref.getString("FirbaseToken", "");
                    new GetLogin().execute();
                }
                //  Intent intent = new Intent(LoginActivity.this, NewHomeActivity.class);
                //  startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Please read Terms of service", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btnCondition) {
            String terms_link = terms_url + "?lang=" + locale_name;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(terms_link));
            startActivity(intent);
        }
        //  else if (v.getId() == R.id.register_btn) {
        //      Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        //      startActivity(intent);
        //  } else if (v.getId() == R.id.register_as_group_btn) {

        //  } else if (v.getId() == R.id.select_language_btn) {
        //      popup_selectLanguage();
        //  } else if (v.getId() == R.id.flag) {
        //      popup_selectLanguage();
        //  }
    }

    private class GetLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/loginUser.php?";
            String username = this_username;
            String password = this_password;
            String parameters = "email=" +  username + "&deviceid=" + deviceid;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            System.out.println(jsonStr);

            if (jsonStr != null) {
                try {
                    System.out.println("tag1");
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    System.out.println(jsonObj.length());

                    if (jsonObj.length() == 2) {
                        reason = jsonObj.getString("reason");

                        if (reason.equals("Reported")) {
                            message = jsonObj.getString("message");
                        } else {
                            message = "";
                        }
                    } else {
                        myname = jsonObj.getString("name");
                        myid = jsonObj.getString("id");
                        myphoto = jsonObj.getString("picture");
                        String pw = jsonObj.getString("password");
                        System.out.println(jsonObj);
                        if (password.equals(pw)) {
                            login_success = "1";
                            // Singleton.Companion.getInstance().currentUser = new UserModel(jsonObj);
                        } else {
                            login_success = "0";
                        }
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server while loading login", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            //  WeiboDialogUtils.closeDialog(loadingDialog);

            if (login_success.equals("1")) {
                static_email = etEmail.getText().toString();
                Intent intent = new Intent(LoginActivity.this, NewHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = saveUser.edit();
                editor.putString("userid", myid);
                editor.commit();
                //  try {
                //      loginFunction(myname, myid, myphoto);
                //  } catch (JSONException e) {
                //      e.printStackTrace();
                //  }
                startActivity(intent);
            } else {
                if (!message.isEmpty()) {
                    ll_message.setVisibility(View.VISIBLE);
                    tv_message.setText(message);
                } else {
                    new GlideToast.makeToast(LoginActivity.this, getResources().getString(R.string.username_password_incorrect));
                }
            }
        }
    }

    private void loginFunction(String name,String id,String photo) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("id", id);
        jsonObject.put("count", Integer.parseInt(id));
        jsonObject.put("photo", photo);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());
        RetrofitInstance.Companion.getRetrofit().login(jsonBody).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.code() == 200 ) {
                    Singleton.Companion.getInstance().currentUser = response.body();
                    Intent intent = new Intent(LoginActivity.this, NewHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.i("LoginActivity", t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}

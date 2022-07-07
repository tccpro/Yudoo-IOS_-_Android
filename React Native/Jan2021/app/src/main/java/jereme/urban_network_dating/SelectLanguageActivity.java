package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

import jereme.urban_network_dating.Adapters.LanguageSpinnerAdapter;

public class SelectLanguageActivity extends AppCompatActivity {
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
    private static final String FRANCH_LOCALE = "French";
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

    public static Locale locale = new Locale("en");
    public static String locale_name = "en";
    private Spinner spinnerLanguage;

    final String[] languageList = {
        ENGLISH_LOCALE, AFRIKAANS_LOCALE, ALBANIAN_LOCALE,
        ARMENIAN_LOCALE,AZERBAIJANI_LOCALE,BANGALA_LOCALE,BELARUSIAN_LOCALE,BOSNIAN_LOCALE,
        CATALAN_LOCALE,CHINESE_LOCALE,CZECH_LOCALE,DANISH_LOCALE,DUTCH_LOCALE,
        FRANCH_LOCALE,GERMAN_LOCALE,ITALIAN_LOCALE,JAPANESE_LOCALE,KHMER_LOCALE,
        MALAY_LOCALE,POLISH_LOCALE,PORTUGUESE_LOCALE,RUSSIAN_LOCALE,SPANISH_LOCALE,
    };

    final int[] flag = { R.drawable.en, R.drawable.afrikaans, R.drawable.albania,
        R.drawable.armenian,R.drawable.azerbaijani,R.drawable.bangla,R.drawable.belarus,R.drawable.bosnia,
        R.drawable.catalonia,R.drawable.china,R.drawable.czech,R.drawable.danish,R.drawable.dutch,
        R.drawable.france,R.drawable.germany,R.drawable.italian,R.drawable.japan,R.drawable.khmer,
        R.drawable.malaysia,R.drawable.polish,R.drawable.portuguese,R.drawable.russia,R.drawable.spain,
    };

    TextView tvSelectLanguage;
    ImageView flagImag;
    int languageNum = 0;
    ListView listLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isSettedLang = false;
        SharedPreferences loadUser1 = PreferenceManager.getDefaultSharedPreferences(SelectLanguageActivity.this);
        String storedLanguage = loadUser1.getString("storedLanguageName","0");
        String storedflag = loadUser1.getString("storedLanguage","0");

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

        final Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());

        Intent myIntent = getIntent();
        isSettedLang = myIntent.getBooleanExtra("set_lang", true);

        if (storedLanguage.equals("0") || !isSettedLang) {
            setContentView(R.layout.activity_select_language);
            listLanguage = findViewById(R.id.select_language);
            LanguageSpinnerAdapter customAdapter = new LanguageSpinnerAdapter(getApplicationContext(),languageList,flag);
            listLanguage.setAdapter(customAdapter);

            listLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
                        // locale = new Locale("ar");
                        locale = new Locale("en");
                        locale_name = "en";
                        languageNum = 1;
                    } else if (languageList[position].equals(PERSIAN_LOCALE)) {
                        locale = new Locale("en");
                        locale_name = "en";
                        languageNum = 1;
                    }

                    Configuration configuration = resources.getConfiguration();
                    configuration.setLocale(locale);
                    getBaseContext().getResources().updateConfiguration(configuration,
                            getBaseContext().getResources().getDisplayMetrics());

                    SharedPreferences pref = getSharedPreferences("lang_pref", MODE_PRIVATE);
                    SharedPreferences.Editor shared_editor = pref.edit();
                    shared_editor.putString("storedLanguage", String.valueOf(languageNum));
                    shared_editor.putString("storedLanguageName", String.valueOf(locale_name));
                    shared_editor.commit();

                    SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(SelectLanguageActivity.this);
                    SharedPreferences.Editor editor = saveUser.edit();
                    editor.putString("storedLanguage", String.valueOf(languageNum));
                    editor.putString("storedLanguageName", String.valueOf(locale_name));
                    editor.commit();

                    Intent intent = new Intent(SelectLanguageActivity.this, MeetNewPeopleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(SelectLanguageActivity.this, MeetNewPeopleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
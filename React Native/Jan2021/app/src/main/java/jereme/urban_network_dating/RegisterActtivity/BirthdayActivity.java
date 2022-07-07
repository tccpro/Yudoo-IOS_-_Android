package jereme.urban_network_dating.RegisterActtivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.Calendar;

import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.GlideToast;

public class BirthdayActivity extends AppCompatActivity {
    Button continues;
    EditText mnth, day, year;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);
        mnth = (EditText) findViewById(R.id.et_month);
        day = (EditText) findViewById(R.id.et_day);
        year = (EditText) findViewById(R.id.et_year);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.et_month , "^[0-9]+$", R.string.montherror);
        awesomeValidation.addValidation(this, R.id.et_day , "^[0-9]+$", R.string.dayerror);
        awesomeValidation.addValidation(this, R.id.et_year , "^[0-9]+$", R.string.yearerror);
        continues = (Button) findViewById(R.id.continues);

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate() && validate()) {
                    String birthday = day.getText().toString() + "/" + mnth.getText().toString() + "/" + year.getText().toString();
                    SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(BirthdayActivity.this);
                    SharedPreferences.Editor editor = saveUser.edit();
                    editor.putString("storedBirthday", birthday);
                    editor.commit();
                    Intent intent = new Intent(BirthdayActivity.this, PasswordActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean validate() {
        int month = Integer.parseInt(mnth.getText().toString());
        int bday = Integer.parseInt(day.getText().toString());
        int byear = Integer.parseInt(year.getText().toString());
        final Calendar cldr = Calendar.getInstance();
        final int age = byear - cldr.get(Calendar.YEAR);
        //cldr.add(Calendar.YEAR,-18);
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int mmonth = cldr.get(Calendar.MONTH);

        if (month > 12 || bday > 31 || byear >= cldr.get(Calendar.YEAR)) {
            new GlideToast.makeToast(BirthdayActivity.this, "" + (getResources().getString(R.string.invalid_date)));
            return false;
        } else if(age < 18) {
            new GlideToast.makeToast(BirthdayActivity.this, "" + (getResources().getString(R.string.only_show_profiles_with_picture)));
            return true;
        }
        return true;
    }
}

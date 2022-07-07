package jereme.urban_network_dating.RegisterActtivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import jereme.urban_network_dating.R;
public class EmailActivity extends AppCompatActivity {
    EditText email;
    Button continues;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        email = (EditText) findViewById(R.id.et_email);
        awesomeValidation.addValidation(this, R.id.et_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        continues = (Button) findViewById(R.id.continues);

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (awesomeValidation.validate()) {
                    SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(EmailActivity.this);
                    SharedPreferences.Editor editor = saveUser.edit();
                    editor.putString("storedEmail", email.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(EmailActivity.this, GenderActivity.class);
                    startActivity(intent);

                }

            }
        });
    }
}

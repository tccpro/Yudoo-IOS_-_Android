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

import jereme.urban_network_dating.R;

public class NameActivity extends AppCompatActivity {
    Button continues;
    EditText username;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        username = (EditText) findViewById(R.id.et_name);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        continues = (Button) findViewById(R.id.continues);
        awesomeValidation.addValidation(this, R.id.et_name , "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(NameActivity.this);
                    SharedPreferences.Editor editor = saveUser.edit();
                    editor.putString("storedUsername", username.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(NameActivity.this, BirthdayActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}

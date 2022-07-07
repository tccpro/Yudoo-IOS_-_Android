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

public class PasswordActivity extends AppCompatActivity {
    Button continues;
    EditText password;
    private AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        password = (EditText) findViewById(R.id.et_password);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.et_password , "^(?=.*[0-9])(?=.*[a-z]).{8,15}", R.string.passworderror);
        continues = (Button) findViewById(R.id.continues);

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(PasswordActivity.this);
                    SharedPreferences.Editor editor = saveUser.edit();
                    editor.putString("storedPassword", password.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(PasswordActivity.this, ChoosepictureActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}

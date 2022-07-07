package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import jereme.urban_network_dating.Utils.GlideToast;

public class VerifyCodeActivity extends AppCompatActivity {

    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        final EditText verifycode=findViewById(R.id.verify_code);

        Button verifyCodeBtn = findViewById(R.id.verify_code_btn);





        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras =getIntent().getExtras();
                   int getverifycode=extras.getInt("verifycode");
                   String gettextcode=verifycode.getText().toString();
                if(verifycode.getText().toString().equals("") ) {
                    new GlideToast.makeToast(VerifyCodeActivity.this,getResources().getString(R.string.verify_code_require));
                } else {
                    if (2 > count) {
                        if (getverifycode == Integer.parseInt(gettextcode)) {
                            Intent intent = new Intent(VerifyCodeActivity.this, NewPasswordActivity.class);
                            intent.putExtra("email", extras.getString("email"));
                            startActivity(intent);
                        } else {
                            new GlideToast.makeToast(VerifyCodeActivity.this,getResources().getString(R.string.invalid_code));
                        }
                    } else {
                        new GlideToast.makeToast(VerifyCodeActivity.this,getResources().getString(R.string.maximum_limit)+"!!");

                        onBackPressed();
                    }

                    count++;
                }
            }
        });
    }
}

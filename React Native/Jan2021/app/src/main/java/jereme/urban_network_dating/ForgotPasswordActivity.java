package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etEmail;
    String login_success = "0";
    Dialog loadingDialog;
    private String this_username, this_lang;
   int resetcode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Button resetPassword =findViewById(R.id.reset_password);

        etEmail = findViewById(R.id.et_email);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog = WeiboDialogUtils.createLoadingDialog(ForgotPasswordActivity.this, "Authenticating");
                if(etEmail.getText().toString().equals("") ) {
                    new GlideToast.makeToast(ForgotPasswordActivity.this,getResources().getString(R.string.email_require));
                } else {
                    loadingDialog.show();
                    this_username = etEmail.getText().toString();
                    SharedPreferences loadUser1 =  PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this);


                    this_lang = loadUser1.getString("storedLanguageName","0");
                    new GetForgot().execute();
                }

            }
        });


    }


    private class GetForgot extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/sendEmail.php?";
            String username = this_username;
            String tolang = this_lang;

            String parameters = "email=" +  username +
                    "&tolang=" + tolang;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    resetcode =jsonObj.getInt("resetcode");
                    System.out.println(jsonObj);
                    login_success = "1";

                } catch (final JSONException e) {
                }
            } else {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(login_success.equals("1")){
                new GlideToast.makeToast(ForgotPasswordActivity.this, getResources().getString(R.string.reset_password_toast));

                Intent intent = new Intent(ForgotPasswordActivity.this,VerifyCodeActivity.class);
                intent.putExtra("verifycode",resetcode);
                intent.putExtra("email",this_username);
                startActivity(intent);

            } else {
                new GlideToast.makeToast(ForgotPasswordActivity.this, getResources().getString(R.string.email_exist));
            }
        }
    }
}

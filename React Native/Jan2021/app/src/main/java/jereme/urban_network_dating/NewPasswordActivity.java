package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

public class NewPasswordActivity extends AppCompatActivity {
    EditText etEmail;
    String change_success = "0";
    Dialog loadingDialog;
    private String this_username,this_password;
    String changepassword="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        Button changePassword = findViewById(R.id.change_password);
        final EditText password = findViewById(R.id.password);
        final EditText confirmPassword = findViewById(R.id.confirm_password);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog = WeiboDialogUtils.createLoadingDialog(NewPasswordActivity.this, "Authenticating");
                if(password.getText().toString().equals("") ) {
                   // new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.input_password));
                    new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.password_require));

                } else  if(confirmPassword.getText().toString().equals("") ) {
                    // new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.input_password));
                    new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.confirm_password));

                } else  if(!(confirmPassword.getText().toString().equals(password.getText().toString()) )) {
                    // new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.input_password));
                    new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.mismatch));

                }else {
                    Bundle extras=getIntent().getExtras();
                    loadingDialog.show();
                    this_password = password.getText().toString();
                    this_username = extras.getString("email");
                    new GetChangePassword().execute();
                }


            }
        });
    }



    private class GetChangePassword extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/updateUserpassword.php?";
            String username = this_username;
            String password = this_password;

            String parameters = "email=" +  username +
                               "&password=" + password ;

            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    changepassword =jsonObj.getString("message");
                    System.out.println(jsonObj);
                    change_success = "1";

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
            if(change_success.equals("1")){
                new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.change_password_toast));

                Intent intent = new Intent(NewPasswordActivity.this,LoginActivity.class);
                startActivity(intent);

            } else {
                new GlideToast.makeToast(NewPasswordActivity.this, getResources().getString(R.string.password_not_update));
            }
        }
    }
}

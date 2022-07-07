package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;


public class FeedBackActivity extends AppCompatActivity {
  ImageView back_btn;
    Dialog loadingDialog;
    public static String profile_id, title, description;
    private int success_flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        back_btn = findViewById(R.id.btn_back);
        final EditText titletxt=findViewById(R.id.title);
        final EditText desc = findViewById(R.id.description);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Button submit = findViewById(R.id.submit);

      submit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              title=titletxt.getText().toString();
              description=desc.getText().toString();
              loadingDialog = WeiboDialogUtils.createLoadingDialog(FeedBackActivity.this, "Sending");
              loadingDialog.show();
              new sendFeedback().execute();

          }
      });



    }


    private class sendFeedback extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "feedback/feedback.php?";


            String parameters = "&userid=" + profile_id +
                    "&title=" + title +
                    "&description=" + description;

            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    success_flag = 1;

                } catch (final JSONException e) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error in update: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(success_flag == 1){
                Toast.makeText(getApplicationContext(),"Thanks for your feedback ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

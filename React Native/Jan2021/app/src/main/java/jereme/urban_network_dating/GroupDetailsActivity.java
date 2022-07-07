
package jereme.urban_network_dating;

 import androidx.appcompat.app.AppCompatActivity;

 import android.app.AlertDialog;
 import android.app.Dialog;
 import android.content.DialogInterface;
 import android.os.AsyncTask;
 import android.os.Bundle;
import android.view.View;
 import android.widget.Button;
 import android.widget.ImageView;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.squareup.picasso.Picasso;

 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

 import jereme.urban_network_dating.API.HttpHandler;
 import jereme.urban_network_dating.Utils.ImageTrans_CircleTransform;
 import jereme.urban_network_dating.Utils.WeiboDialogUtils;

 import static jereme.urban_network_dating.HomeActivity.profile_id;
 import static jereme.urban_network_dating.LoginActivity.base_url;

public class GroupDetailsActivity extends AppCompatActivity {
    String addGroupMessage = "", addMessageResoponse = "";
    String selectedGroupID="";
    Dialog loadingDialog;
    TextView name,description,type;
    ImageView photo;
    Button join_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_groupdetail);
        ImageView ivBack = findViewById(R.id.btn_back);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        type = findViewById(R.id.type);
        join_group = findViewById(R.id.join_btn);
         photo = findViewById(R.id.user_photo);


             Bundle extras = getIntent().getExtras();
             selectedGroupID =extras.getString("id");
             name.setText(extras.getString("name"));
             description.setText(extras.getString("description"));
             type.setText(extras.getString("description"));
        if(extras.getString("photo").equals("") || extras.getString("photo").equals("null")){
            photo.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + extras.getString("photo");
            Picasso.with(GroupDetailsActivity.this).load(photourl)
                    .transform(new ImageTrans_CircleTransform()).into(photo);
        }
       join_group.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               addGroupMessage = "";
               loadingDialog = WeiboDialogUtils.createLoadingDialog(GroupDetailsActivity.this, "Join Group");
               loadingDialog.show();
               new JoinGroup().execute();
           }
       });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }







    private class JoinGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/joingroup.php?";
            String parameters = "user=" +  profile_id +
                    "&group=" + selectedGroupID;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        addGroupMessage = c.getString("message");
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server for join group",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(addGroupMessage.equals("success")) {
                new AlertDialog.Builder(GroupDetailsActivity.this)
                        .setTitle("Join Group")
                        .setMessage("joined successfully")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                HomeActivity homeActivity = new HomeActivity();
            } else {
                new AlertDialog.Builder(GroupDetailsActivity.this)
                        .setTitle("Join Group")
                        .setMessage("You have already joined into this group!")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
}
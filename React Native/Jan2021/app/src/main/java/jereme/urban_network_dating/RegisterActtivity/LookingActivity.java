package jereme.urban_network_dating.RegisterActtivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.LoginActivity;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.GlideToast;

import static com.google.common.collect.Iterables.size;
import static jereme.urban_network_dating.LoginActivity.base_url;

public class LookingActivity extends AppCompatActivity implements View.OnClickListener {
    Button continues;
    final String onbkcolor = "#FF5776";
    final String offbkcolor = "#FFF1F3";
    final String onfcolor = "#ffffff";
    final String offcolor ="#FF5776";
    String register_success = "0";
    ArrayList<String> lookarr = new ArrayList<>();
    Dialog loadingDialog;
    ArrayList<String> lookinglist = new ArrayList<String>();
    ProgressDialog createprogress;
    boolean isbtnfriendSelected = false, isbtndatingSelected = false, isbtnrelationSelected = false, isbtntravelSelected = false;
    boolean isbtnworkoutSelected = false, isbtnactivitySelected = false, isbtnstudySelected = false, isbtnroommateSelected = false;
    boolean isbtncoffeSelected = false, isbtnpetSelected = false;
    Button btnfriend, btndating, btnrelation, btntravel, btnworkout, btnactivity, btnstudy, btnroommate, btncoffe, btnpet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looking);
        btnfriend = (Button) findViewById(R.id.btnfriend);
        btndating = (Button) findViewById(R.id.btndating);
        btnrelation = (Button) findViewById(R.id.btnrelation);
//        btntravel = (Button) findViewById(R.id.btntravel);
//        btnworkout = (Button) findViewById(R.id.btnworkout);
//        btnactivity = (Button) findViewById(R.id.btnactivity);
//        btnstudy = (Button) findViewById(R.id.btnstudy);
//        btnroommate = (Button) findViewById(R.id.btnroommate);
//        btncoffe = (Button) findViewById(R.id.btncoffe);
//        btnpet = (Button) findViewById(R.id.btnpet);

        btnfriend.setOnClickListener(this);
        btndating.setOnClickListener(this);
//        btnactivity.setOnClickListener(this);
//        btncoffe.setOnClickListener(this);
//        btnpet.setOnClickListener(this);
        btnrelation.setOnClickListener(this);
//        btnroommate.setOnClickListener(this);
//        btnstudy.setOnClickListener(this);
//        btntravel.setOnClickListener(this);
//        btnworkout.setOnClickListener(this);

        continues = (Button) findViewById(R.id.continues);

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isbtnactivitySelected) lookinglist.add("activity");
//                if (isbtncoffeSelected) lookinglist.add("Coffee");
                if (isbtndatingSelected) lookinglist.add("Dating");
                if (isbtnfriendSelected) lookinglist.add("Friends");
//                if (isbtnpetSelected) lookinglist.add("pet");
                if (isbtnrelationSelected) lookinglist.add("Marriage");
//                if (isbtnroommateSelected) lookinglist.add("roommate");
//                if (isbtnstudySelected) lookinglist.add("study");
//                if (isbtntravelSelected) lookinglist.add("travel");
//                if (isbtnworkoutSelected) lookinglist.add("workout");
                if (size(lookinglist) > 0) {
                    Toast.makeText(getApplicationContext(),"Registration Successfull", Toast.LENGTH_LONG);
                    createprogress = new ProgressDialog(LookingActivity.this);
                    createprogress.setTitle("Loading");
                    createprogress.setMessage("Create account...");
                    createprogress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    createprogress.show();
//                    loadingDialog = WeiboDialogUtils.createLoadingDialog(LookingActivity.this, "Authenticating");
//                    loadingDialog.show();
                    new GetRegister().execute();
                } else {
                    new GlideToast.makeToast( LookingActivity.this, "" + (getResources().getString(R.string.select_Atleast)));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnfriend) {
            if (isbtnfriendSelected) {
                btnfriend.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
                btnfriend.setTextColor(Color.parseColor(offcolor));
                lookarr.remove("Friends");
                isbtnfriendSelected = false;
            } else {
                btnfriend.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
                btnfriend.setTextColor(Color.parseColor(onfcolor));
                isbtnfriendSelected = true;
            }

            lookarr.add("Friends");
        } else if (v.getId() == R.id.btndating) {
            if (isbtndatingSelected) {
                btndating.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
                btndating.setTextColor(Color.parseColor(offcolor));
                isbtndatingSelected = false;
                lookarr.remove("Dating");
            } else {
                btndating.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
                btndating.setTextColor(Color.parseColor(onfcolor));
                lookarr.add("Dating");
                isbtndatingSelected = true;
            }
        } else if (v.getId() == R.id.btnrelation) {
            if (isbtnrelationSelected) {
                btnrelation.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
                btnrelation.setTextColor(Color.parseColor(offcolor));
                isbtnrelationSelected = false;
                lookarr.remove("Marriage");
            } else {
                btnrelation.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
                btnrelation.setTextColor(Color.parseColor(onfcolor));
                lookarr.add("Marriage");
                isbtnrelationSelected = true;
            }
        }
//        else if (v.getId() == R.id.btntravel) {
//            if (isbtntravelSelected) {
//                btntravel.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
//                btntravel.setTextColor(Color.parseColor(offcolor));
//                isbtntravelSelected = false;
//                lookarr.remove("travel");
//            } else {
//                btntravel.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
//                btntravel.setTextColor(Color.parseColor(onfcolor));
//                lookarr.add("travel");
//                isbtntravelSelected = true;
//            }
//        } else if (v.getId() == R.id.btnworkout) {
//            if (isbtnworkoutSelected) {
//                btnworkout.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
//                btnworkout.setTextColor(Color.parseColor(offcolor));
//                isbtnworkoutSelected = false;
//                lookarr.remove("workout");
//            } else {
//                btnworkout.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
//                btnworkout.setTextColor(Color.parseColor(onfcolor));
//                lookarr.add("workout");
//                isbtnworkoutSelected = true;
//            }
//        } else if (v.getId() == R.id.btnactivity) {
//            if (isbtnactivitySelected) {
//                btnactivity.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
//                btnactivity.setTextColor(Color.parseColor(offcolor));
//                isbtnactivitySelected = false;
//                lookarr.remove("activity");
//            } else {
//                btnactivity.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
//                btnactivity.setTextColor(Color.parseColor(onfcolor));
//                lookarr.add("activity");
//                isbtnactivitySelected = true;
//            }
//        } else if (v.getId() == R.id.btnstudy) {
//            if (isbtnstudySelected) {
//                btnstudy.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
//                btnstudy.setTextColor(Color.parseColor(offcolor));
//                isbtnstudySelected = false;
//                lookarr.remove("study");
//            } else {
//                btnstudy.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
//                btnstudy.setTextColor(Color.parseColor(onfcolor));
//                lookarr.add("study");
//                isbtnstudySelected = true;
//            }
//        } else if (v.getId() == R.id.btnroommate) {
//            if (isbtnroommateSelected) {
//                btnroommate.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
//                btnroommate.setTextColor(Color.parseColor(offcolor));
//                isbtnroommateSelected = false;
//                lookarr.remove("roommate");
//            } else {
//                btnroommate.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
//                btnroommate.setTextColor(Color.parseColor(onfcolor));
//                lookarr.add("roommate");
//                isbtnroommateSelected = true;
//            }
//        } else if (v.getId() == R.id.btncoffe) {
//            if (isbtncoffeSelected) {
//                btncoffe.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
//                btncoffe.setTextColor(Color.parseColor(offcolor));
//                lookarr.remove("coffee");
//                isbtncoffeSelected = false;
//            } else {
//                btncoffe.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
//                btncoffe.setTextColor(Color.parseColor(onfcolor));
//                lookarr.add("coffee");
//                isbtncoffeSelected = true;
//            }
//        } else if (v.getId() == R.id.btnpet) {
//            if (isbtnpetSelected) {
//                btnpet.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
//                btnpet.setTextColor(Color.parseColor(offcolor));
//                lookarr.remove("pet");
//                isbtnpetSelected = false;
//            } else {
//                btnpet.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
//                btnpet.setTextColor(Color.parseColor(onfcolor));
//                lookarr.add("pet");
//                isbtnpetSelected = true;
//            }
//        }
    }

    private class GetRegister extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/registerUser.php?";
            SharedPreferences loadUser1 =  PreferenceManager.getDefaultSharedPreferences(LookingActivity.this);
            String email = loadUser1.getString("storedEmail","0");
            String gender = loadUser1.getString("storedgender","0");
            String password = loadUser1.getString("storedPassword","0");
            String username = loadUser1.getString("storedUsername","0");
            String newPicture = loadUser1.getString("profilepic","0");
            String newBirthday = loadUser1.getString("storedBirthday","0");
            String morePicture = loadUser1.getString("morephoto","0");
            String yourInterest = loadUser1.getString("yourinterest","0");
            String lookingfor = new Gson().toJson(lookinglist);

            System.out.println(morePicture);
            System.out.println(yourInterest);
            System.out.println(lookingfor);

            String newGender = gender;
            String parameters = "name=" +  username +
                    "&email=" + email +
                    "&password=" + password +
                    "&picture=" + newPicture +
                    "&birthday=" + newBirthday +
                    "&gender=" + newGender+
                    "&morepic=" + morePicture +
                    "&looking=" + lookingfor +
                    "&interest=" + yourInterest;

            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String message = jsonObj.getString("message");

                    if (message.equals("New User was created.")){
                        register_success = "1";
                    } else {
                        register_success = "0";
                    }
                } catch (final JSONException e) {
                    register_success = "0";
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createprogress.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            createprogress.dismiss();
            //  WeiboDialogUtils.closeDialog(loadingDialog);
            if (register_success.equals("1")) {
                //  static_email = etEmail.getText().toString();
                Intent intent = new Intent(LookingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(LookingActivity.this);
                SharedPreferences.Editor editor = saveUser.edit();
                editor.putString("storedUsername", "");  //etEmail.getText().toString());
                editor.putString("storedPassword", ""); //etPassword.getText().toString());
                editor.commit();
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(),"register is fault", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

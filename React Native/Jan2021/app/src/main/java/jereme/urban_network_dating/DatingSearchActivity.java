package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.SearchUserListAdapter;
import jereme.urban_network_dating.Adapters.SpinnerAdapter;
import jereme.urban_network_dating.List.SearchUserList;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static jereme.urban_network_dating.HomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;

public class DatingSearchActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinnerDisplayBy, spinnerGenderBy, spinnerAgesBy;

    Dialog loadingDialog;
    SearchUserListAdapter userlistapater;
    ListView userList;
    TextView tvSelectedUsername, tvSelectedUserSearchName,tvSelectedUserAge, tvSelectedUserGender, tvSelectedUser, tvSelectedUserHometown;
    ImageView ivSelectedUserPhoto;
    EditText etSearchName, etLocationBy;
    EditText etMessageTitle, etMessageDescription;
    ImageView sendMessageBtn;
    CheckBox cbSearchByPicture;
    ArrayList<SearchUserList> members = new ArrayList<>();
    LinearLayout llDatingMode;
    LinearLayout llDatingModeGroup;
    RelativeLayout rlMessageGroup;
    ImageView ivShowMessage,ivHideMessage;
    String selectedUsername="", selectedUserPhotoUrl="", selectedUserID="", selectedUserSearchName, selectedUserGender, selectedUserBirthday, selectedUserAge,SelectedEmail;
    String selectedUserHometown, selectedUserCurrent_city, selectedMessageName, selectedMessageDescription;
    String addMessageResoponse;
    String addFriendMessage = "" ;
    String searchName, searchDisplayBy, searchGenderBy, searchLocationBy, searchAgesBy, searchPictureBy;
    Button details;
    public  int albumlength=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating_search);
        Button searchBtn;
        userList= findViewById(R.id.member_list);
        ImageView ivBack = findViewById(R.id.btn_back);
        final Button  addFriendBtn;
//        spinnerDisplayBy = (Spinner) findViewById(R.id.spinner1);
        spinnerGenderBy = (Spinner) findViewById(R.id.spinner2);
        etLocationBy = findViewById(R.id.et_location);
        spinnerAgesBy = (Spinner) findViewById(R.id.spinner4);
        llDatingMode = findViewById(R.id.ll_dating_mode);
        tvSelectedUsername = findViewById(R.id.selected_memeber_name);
        ivSelectedUserPhoto = findViewById(R.id.selected_member_photo);
        tvSelectedUserSearchName = findViewById(R.id.selected_member_searchname);
        tvSelectedUserAge = findViewById(R.id.selected_member_age);
        tvSelectedUserGender = findViewById(R.id.selected_member_gender);
        tvSelectedUserHometown = findViewById(R.id.selected_member_hometown);
        etMessageTitle = findViewById(R.id.et_message_title);
        etMessageDescription = findViewById(R.id.et_message_description);
        sendMessageBtn = findViewById(R.id.send_message_btn);
     //   etSearchName = findViewById(R.id.et_searchname);
       searchBtn = findViewById(R.id.search_member_btn);
        cbSearchByPicture = findViewById(R.id.cb_search_picture_by);
        llDatingModeGroup = findViewById(R.id.ll_dating_mode_group);
        ivShowMessage = findViewById(R.id.show_message_btn);
        ivHideMessage = findViewById(R.id.hide_message_btn);
        rlMessageGroup = findViewById(R.id.rl_message_group);
        details = findViewById(R.id.view_details);
        Button addAlbumBtn = findViewById(R.id.album_add_btn);
        addFriendBtn = findViewById(R.id.add_friend_btn);

        details.setOnClickListener(this);
        ivShowMessage.setOnClickListener(this);
        ivHideMessage.setOnClickListener(this);
        llDatingMode.setOnClickListener(this);
        sendMessageBtn.setOnClickListener(this);
        addFriendBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        addAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog = WeiboDialogUtils.createLoadingDialog(DatingSearchActivity.this, "Searching Members");
                loadingDialog.show();


                new GetAlbum().execute();
            }
        });

        final String[] searchNameType={getResources().getString(R.string.display_by_any),getResources().getString(R.string.name),
                getResources().getString(R.string.search_display_name)};
        final String[] searchGenderType={getResources().getString(R.string.gender_any),getResources().getString(R.string.male),
                getResources().getString(R.string.female)};
        final String[] searchAgesType={getResources().getString(R.string.ages_any),
                getResources().getString(R.string.ages18_27),
                getResources().getString(R.string.ages28_37),
                getResources().getString(R.string.ages38_47),
                getResources().getString(R.string.ages48_57),
                getResources().getString(R.string.ages_over57)};
//        spinnerDisplayBy.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position==0) {
//                    searchDisplayBy = "Display by(any)";
//                } else if(position == 1) {
//                    searchDisplayBy = "Name";
//                } else if(position == 2) {
//                    searchDisplayBy = "Search display name";
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
//        SpinnerAdapter customAdapter1 = new SpinnerAdapter(getApplicationContext(),searchNameType);
//        spinnerDisplayBy.setAdapter(customAdapter1);
        ivSelectedUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photourl ="";
                if(selectedUserPhotoUrl.equals("null")|| selectedUserPhotoUrl.equals("")) {

                    photourl ="default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;

                }

                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);

                intent.putExtra("Image", photourl);
                startActivity(intent);
            }
        });

        spinnerGenderBy.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    searchGenderBy = "Gender any";
                } else if(position == 1) {
                    searchGenderBy = "Male";
                } else if(position == 2) {
                    searchGenderBy = "Female";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        SpinnerAdapter customAdapter2 = new SpinnerAdapter(getApplicationContext(),searchGenderType);
        spinnerGenderBy.setAdapter(customAdapter2);

        spinnerAgesBy.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    searchAgesBy = "Gender any";
                }  else if(position == 1) {
                    searchAgesBy = "18 - 27";
                } else if(position == 2) {
                    searchAgesBy = "28 - 37";
                } else if(position == 3) {
                    searchAgesBy = "38 - 47";
                } else if(position == 4) {
                    searchAgesBy = "48 - 57";
                } else if(position == 5) {
                    searchAgesBy = "57+";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        SpinnerAdapter customAdapter4 = new SpinnerAdapter(getApplicationContext(),searchAgesType);
        spinnerAgesBy.setAdapter(customAdapter4);


        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                SearchUserList listitem = members.get(arg2);
                selectedUsername = listitem.getName();
                selectedUserPhotoUrl = listitem.getPhoto();
                selectedUserID = listitem.getID();
                selectedUserSearchName = listitem.getSearchName();
                selectedUserAge = listitem.getAge();
                selectedUserGender = listitem.getGender();
                selectedUserBirthday = listitem.getBirthday();
                selectedUserHometown = listitem.getHometown();
                selectedUserCurrent_city = listitem.getCurrentyCity();

                tvSelectedUserSearchName.setTextColor(getColor(R.color.whiteColor));
                tvSelectedUserAge.setTextColor(getColor(R.color.whiteColor));
                tvSelectedUserGender.setTextColor(getColor(R.color.whiteColor));
                tvSelectedUserHometown.setTextColor(getColor(R.color.whiteColor));

                tvSelectedUserSearchName.setText(selectedUserSearchName);
                tvSelectedUserAge.setText(getResources().getString(R.string.age)+": " +selectedUserAge);
                tvSelectedUserGender.setText(selectedUserGender);
                tvSelectedUserHometown.setText(selectedUserCurrent_city + " " +selectedUserHometown);
                if(selectedUserCurrent_city.equals("") && selectedUserHometown.equals("")) {
                    tvSelectedUserHometown.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserHometown.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserCurrent_city == null && selectedUserHometown== null) {
                    tvSelectedUserHometown.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserHometown.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserSearchName.equals("")) {
                    tvSelectedUserSearchName.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserSearchName.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserAge.equals("")) {
                    tvSelectedUserAge.setText(getResources().getString(R.string.age)+": "+getResources().getString(R.string.no_data));
                    tvSelectedUserAge.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserGender.equals("")) {
                    tvSelectedUserGender.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserGender.setTextColor(getColor(R.color.redColor));
                }


                if(selectedUserPhotoUrl.equals("null")) {
                    ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                    tvSelectedUsername.setText(selectedUsername);
                } else {
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(DatingSearchActivity.this).load(photourl).into(ivSelectedUserPhoto);
                    tvSelectedUsername.setText(selectedUsername);
                }
            }
        });




        searchLocationBy = etLocationBy.getText().toString();
        if(cbSearchByPicture.isChecked()) {
            searchPictureBy = "1";
        } else {
            searchPictureBy = "0";
        }
        members.clear();
        userList.setAdapter(null);
        loadingDialog = WeiboDialogUtils.createLoadingDialog(DatingSearchActivity.this, "Searching Members");
        loadingDialog.show();
        if(llDatingModeGroup.getVisibility()==View.GONE){
            searchGenderBy= "Gender any";
            searchLocationBy = "";
            searchAgesBy ="Ages, any";
        }
        new SearchMember().execute();

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.send_message_btn) {

            if(!etMessageTitle.getText().toString().equals("") && !etMessageDescription.getText().toString().equals("") && !selectedUserID.equals("")) {
                loadingDialog = WeiboDialogUtils.createLoadingDialog(DatingSearchActivity.this, "Sending Message");
                loadingDialog.show();
                selectedMessageName = etMessageTitle.getText().toString();
                selectedMessageDescription = etMessageDescription.getText().toString();
                new SendMessage().execute();
            } else {
                new AlertDialog.Builder(DatingSearchActivity.this)
                        .setTitle("Send Message")
                        .setMessage("Select user and input title and message")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } else if(view.getId()==R.id.btn_back) {
            onBackPressed();
        } else if(view.getId()==R.id.search_member_btn) {
           // searchName = etSearchName.getText().toString();
            searchLocationBy = etLocationBy.getText().toString();
            if(cbSearchByPicture.isChecked()) {
                searchPictureBy = "1";
            } else {
                searchPictureBy = "0";
            }
            members.clear();
            userList.setAdapter(null);
            loadingDialog = WeiboDialogUtils.createLoadingDialog(DatingSearchActivity.this, "Searching Members");
            loadingDialog.show();
            if(llDatingModeGroup.getVisibility()==View.GONE){
                searchGenderBy= getResources().getString(R.string.gender_any);
                searchLocationBy = "";
                searchAgesBy = getResources().getString(R.string.ages_any);
            }
            new SearchMember().execute();
        } else if(view.getId()==R.id.ll_dating_mode) {
            if(llDatingModeGroup.getVisibility()==View.GONE) {
                llDatingModeGroup.setVisibility(View.VISIBLE);
            } else {
                llDatingModeGroup.setVisibility(View.GONE);
            }
        } else if(view.getId()==R.id.show_message_btn) {
            rlMessageGroup.setVisibility(View.VISIBLE);
            ivHideMessage.setVisibility(View.VISIBLE);
            ivShowMessage.setVisibility(View.GONE);

        } else if(view.getId()==R.id.hide_message_btn) {
            rlMessageGroup.setVisibility(View.GONE);
            ivHideMessage.setVisibility(View.GONE);
            ivShowMessage.setVisibility(View.VISIBLE);

        } else if(view.getId()==R.id.view_details) {

            Intent intent = new Intent(getApplicationContext(),DatingDetailsActivity.class);
            intent.putExtra("name",selectedUsername);
            intent.putExtra("searchname",selectedUserSearchName);
            intent.putExtra("age",selectedUserAge);
            intent.putExtra("gender",selectedUserGender);
            intent.putExtra("birthday",selectedUserBirthday);
            intent.putExtra("hometown",selectedUserHometown);
            intent.putExtra("currentcity",selectedUserCurrent_city);
            intent.putExtra("email",SelectedEmail);
            intent.putExtra("photo",selectedUserPhotoUrl);
            startActivity(intent);

        }  else if(view.getId()==R.id.add_friend_btn) {
            addFriendMessage = "";
            loadingDialog = WeiboDialogUtils.createLoadingDialog(DatingSearchActivity.this, "Searching Members");
            loadingDialog.show();
            new AddMemberToFriend().execute();
        }
    }

    private class SearchMember extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "member/searchmemberBy.php?";

            String parameters = "id=" +  profile_id +
                    "&searchname=" + "" +
                    "&displayby=" + "" +
                    "&genderby=" + searchGenderBy +
                    "&locationby=" + searchLocationBy +
                    "&agesby=" + searchAgesBy +
                    "&pictureby=" + searchPictureBy;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String searchname1 = c.getString("searchname");
                        String age = String.valueOf(c.getInt("age"));
                        String gender = c.getString("gender");
                        String birthday = c.getString("birthday");
                        String hometown = c.getString("hometown");
                        String currenty_city = c.getString("current_city");
                        String looking = c.getString("looking");
                        String interest = c.getString("interest");
                        String morepic = c.getString("morepic");

                        String email = c.getString("email");
                        String photo = c.getString("photo");
                        if(!photo.equals("null") && !photo.equals("")) {
                            selectedUserID = id;
                            selectedUserPhotoUrl = photo;
                            selectedUserSearchName = searchname1;
                            selectedUserAge = age;
                            selectedUserGender = gender;
                            selectedUserBirthday = birthday;
                            selectedUserHometown = hometown;
                            selectedUserCurrent_city = currenty_city;
                            SelectedEmail = email;
                            if(name.equals("")) selectedUsername = "<NULL>";
                            else selectedUsername = name;
                            members.add(new SearchUserList(selectedUsername,id, photo,searchname1,age,gender,birthday, hometown, currenty_city,looking,interest,morepic,""));
                        }

                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DatingSearchActivity.this,
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(members.size()>0) {
                userlistapater = new SearchUserListAdapter(DatingSearchActivity.this, members);
                userList.setAdapter(userlistapater);
                tvSelectedUsername.setText(selectedUsername);

                tvSelectedUserSearchName.setTextColor(getColor(R.color.whiteColor));
                tvSelectedUserAge.setTextColor(getColor(R.color.whiteColor));
                tvSelectedUserGender.setTextColor(getColor(R.color.whiteColor));
                tvSelectedUserHometown.setTextColor(getColor(R.color.whiteColor));

                tvSelectedUserSearchName.setText(selectedUserSearchName);
                tvSelectedUserAge.setText(getResources().getString(R.string.age)+": " +selectedUserAge);
                tvSelectedUserGender.setText(selectedUserGender);
                tvSelectedUserHometown.setText(selectedUserCurrent_city + " " +selectedUserHometown);
                if(selectedUserCurrent_city.equals("") && selectedUserHometown.equals("")) {
                    tvSelectedUserHometown.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserHometown.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserCurrent_city == null && selectedUserHometown== null) {
                    tvSelectedUserHometown.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserHometown.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserSearchName.equals("")) {
                    tvSelectedUserSearchName.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserSearchName.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserAge.equals("")) {
                    tvSelectedUserAge.setText(getResources().getString(R.string.age)+": " +getResources().getString(R.string.no_data));
                    tvSelectedUserAge.setTextColor(getColor(R.color.redColor));
                }
                if(selectedUserGender.equals("")) {
                    tvSelectedUserGender.setText(getResources().getString(R.string.no_data));
                    tvSelectedUserGender.setTextColor(getColor(R.color.redColor));
                }
                if(!selectedUserPhotoUrl.equals("")) {
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(DatingSearchActivity.this).load(photourl).into(ivSelectedUserPhoto);
                } else {
                    ivSelectedUserPhoto.setImageDrawable(getResources().getDrawable(R.drawable.default_head_icon));
                }
            }
        }
    }

    private class AddMemberToFriend extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "friend/addfriend.php?";
            String parameters = "user=" +  profile_id +
                    "&user4=" + selectedUserID;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        addFriendMessage = c.getString("message");
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server while add friend",
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
            if(addFriendMessage.equals("success")) {
                new AlertDialog.Builder(DatingSearchActivity.this)
                        .setTitle("Add Friend")
                        .setMessage("This member is added as your friend successfully")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(DatingSearchActivity.this)
                        .setTitle("Add Friend")
                        .setMessage(addFriendMessage)

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

    private class SendMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/messageadd.php?";
            String parameters = "user=" +  profile_id +
                    "&user4=" + selectedUserID +
                    "&name=" + selectedMessageName +
                    "&message=" + selectedMessageDescription;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String message_response = jsonObj.getString("m");
                    addMessageResoponse = message_response;
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DatingSearchActivity.this,"Json parsing error while send message: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DatingSearchActivity.this,
                                "Couldn't get json from server while sending message",
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
            if(addMessageResoponse.equals("success")) {
                etMessageDescription.setText("");
                etMessageTitle.setText("");
                new GlideToast.makeToast(DatingSearchActivity.this, "sent message successfully");
            } else {
                new AlertDialog.Builder(DatingSearchActivity.this)
                        .setTitle("Send Message")
                        .setMessage("can't send message")
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



    private class GetAlbum extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/album.php?";
            String parameters = "user=" +  selectedUserID;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {



                try {

                    JSONArray contacts = new JSONArray(jsonStr);
                     if(contacts.length()>0){
                         albumlength=1;
                     } else {
                         albumlength=0;
                     }


                } catch (final JSONException e) {

                    albumlength=0;
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GlideToast.makeToast(DatingSearchActivity.this,getResources().getString(R.string.server_connect_error));
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(albumlength==1) {
                String photourl ="";
                if(selectedUserPhotoUrl.equals("null")|| selectedUserPhotoUrl.equals("")) {

                    photourl ="default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;

                }

                Intent intent = new Intent(DatingSearchActivity.this, AddPhotoActivity.class);
                intent.putExtra("id",selectedUserID);
                intent.putExtra("Image", photourl);
                startActivity(intent);

                 } else {

                String photourl ="";
                if(selectedUserPhotoUrl.equals("null")|| selectedUserPhotoUrl.equals("")) {

                    photourl ="default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;

                }
                Intent intent = new Intent(DatingSearchActivity.this, FullscreenActivity.class);

                intent.putExtra("Image", photourl);
                startActivity(intent);
            }

        }
    }
}

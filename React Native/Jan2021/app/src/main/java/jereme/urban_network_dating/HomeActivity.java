package jereme.urban_network_dating;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.FriendListAdapter;
import jereme.urban_network_dating.Adapters.MessageListAdapter;
import jereme.urban_network_dating.Adapters.SearchUserListAdapter;
import jereme.urban_network_dating.Adapters.TasklistListAdapter;
import jereme.urban_network_dating.List.FriendList;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.List.TasklistList;
import jereme.urban_network_dating.List.UserList;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.ImageTrans_CircleTransform;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;


import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.static_email;
import static jereme.urban_network_dating.RegisterActivity.PICK_IMAGE;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvInfoTab, tvFollowingTab, tvBlockedTab, tvSubscriptionsTab;
    private RelativeLayout nv;
    private DrawerLayout drawer;
    Dialog loadingDialog;
    int success_flag = 0;
    public static String profile_id, profile_name, profile_searchName, profile_email, profile_password, profile_picture, profile_hometown, profile_currentCity;
    public static String profile_paypalEmail, profile_information, profile_status, profile_photoUrl="", profile_points, profile_dating,profile_birthday;
    public static String profile_friendrequest = "", profile_membermsg, profile_membersearch, profile_userquote;
    ImageView ivProfilePhoto;
    ImageView  ivStatusUpdate, ivStatusPrivacy,ivHideMessage, ivShowMessage;
    TextView tvProfileName, tvProfileStatus, tvProfilePoint, tvProfileAlbum, tvProfileFriend, tvProfileGroup,ivStatusDating, tvProfileUser,ivStatusCheckList,ivStatusMessage,feedbackbtn,editUser;

    TextView navTitle,ivStatusRequest;
    LinearLayout llNavHelp, llNavMessage, llNavRequest, llNavAlbum, llNavFriend, llNavGroup, llNavChecklist, llNavPrivacy, llNavSearchByDating;

    ArrayList<MessageList> messages, albums, groups, receiveRequests, sentRequests;
    ArrayList<FriendList> friends;
    ArrayList<TasklistList> tasklists;
    ArrayList<UserList> userDatingOns, userDatingOffs;
    ListView messageList, albumList, friendList, groupList, tasklistList, receiverequestList, sentrequestList, userByDatingList;
    RadioButton rbNavDatingOn, rbNavDatingOff;
    EditText etNavNewTasklist;
    CheckBox cbNavChecklistPrivate;
    CheckBox cbNavPrivateFriendRequest, cbNavPrivateMemberMsg, cbNavPrivateMemberSearch, cbNavPrivateUserQuote;
    MessageListAdapter messagelistapater;
    TasklistListAdapter tasklistListAdapter;
    SearchUserListAdapter.UserListAdapter userByDaintListApater;
    FriendListAdapter friendListAdapter;
    String addNewTaskListResult = "";
    String changePrivacyOptionsResult = "";
    public static String albumname = "";
    Boolean openfrinedphotoFrame = false;
    Handler handler;
    View infoTabSelected, followingTabSelected, blockedTabSelected, subscriptionTabSelected;
    Button changePrivacyOptions;
    Dialog dialog;
    String this_newTasklistName;
    String this_checklistPrivateState;
    String this_tempPrivacyFriendRequest = "0";
    String this_tempPrivacyMemberMsg = "0";
    String this_tempPrivacyMemberSearch = "0";
    String this_tempPrivacyUserQuote = "0";
    String flag_acceptDecline = "0";
    String selectedReceiveRequestUser;
    String selectedSentRequestUser;
    String requestAcceptDeclineResult = "";
    String requestWaitCancelResult = "";





    EditText etProfileStatus, etProfileName, etProfileSearchName, etProfilePaypaEmail, etProfileHometown, etProfileCurrentCity;
    EditText etProfilePersonalInformatioin;
    RadioButton rbProfileDatingOn, rbProfileDatingOff;
    ImageView updatePhotoImageView;
    private String imagepath="";
    private int success_flag1 = 0;
    private int serverResponseCode = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    String profile_temp_dating = "";
    String uploadPhotoName = "";
    String uploadPhotoNamewithoutExtention = "";
    int updated_photo = 0;
    private String upLoadServerUri = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        ImageView menuBtn = findViewById(R.id.btn_menu);
        menuBtn.setOnClickListener(this);
        Button logoutBtn = findViewById(R.id.logout_btn);
        TextView addAlbumBtn = findViewById(R.id.album_add_btn);
        Button addFriendBtn = findViewById(R.id.friend_add_btn);
        Button addGroupBtn = findViewById(R.id.group_add_btn);
        Button addTaskListBtn = findViewById(R.id.tasklist_add_btn);
        logoutBtn.setOnClickListener(this);
        addAlbumBtn.setOnClickListener(this);
        addFriendBtn.setOnClickListener(this);
        addGroupBtn.setOnClickListener(this);
        addTaskListBtn.setOnClickListener(this);


        changePrivacyOptions = findViewById(R.id.btn_nav_change_privacy);
        cbNavChecklistPrivate = findViewById(R.id.nav_check_privateProject);
        //ivProfilePhoto = findViewById(R.id.profile_photo);
        tvProfileName = findViewById(R.id.user_name);
       //tvProfileStatus = findViewById(R.id.profile_status);
      //  tvProfilePoint = findViewById(R.id.profile_point);
        etNavNewTasklist = findViewById(R.id.et_nav_new_tasklist);
      //  tvProfileAlbum = findViewById(R.id.profile_album);
        tvProfileFriend = findViewById(R.id.tv_profile_friend);
        tvProfileGroup = findViewById(R.id.tv_profile_group);
//        tvProfileUser = findViewById(R.id.tv_profile_user);
     //   tvInfoTab = findViewById(R.id.tv_tab_info);
        //tvFollowingTab = findViewById(R.id.tv_tab_following);
     //   tvBlockedTab = findViewById(R.id.tv_tab_blocked);
      //  tvSubscriptionsTab = findViewById(R.id.tv_tab_subscription);
     //   infoTabSelected = findViewById(R.id.tab_info_selected);
     //   followingTabSelected = findViewById(R.id.tab_following_selected);
      //  blockedTabSelected = findViewById(R.id.tab_blocked_selected);
       // subscriptionTabSelected = findViewById(R.id.tab_subscription_selected);
//        ivStatusUpdate = findViewById(R.id.iv_status_update);
       ivStatusMessage = findViewById(R.id.iv_status_message);
        ivStatusRequest = findViewById(R.id.iv_status_request);
        ivStatusCheckList = findViewById(R.id.iv_status_checklist);
//        ivStatusPrivacy = findViewById(R.id.iv_status_privacy);
        ivStatusDating = findViewById(R.id.iv_status_dating);
//        rbNavDatingOn = findViewById(R.id.nav_search_userByDating_on);
//        rbNavDatingOff = findViewById(R.id.nav_search_userByDating_off);
        ivHideMessage = findViewById(R.id.hide_message_btn);
        feedbackbtn = findViewById(R.id.feedback);
        editUser=findViewById(R.id.user_edit);

        editUser.setOnClickListener(this);
        feedbackbtn.setOnClickListener(this);
        changePrivacyOptions.setOnClickListener(this);
        //tvProfileAlbum.setOnClickListener(this);
        tvProfileFriend.setOnClickListener(this);
        tvProfileGroup.setOnClickListener(this);
    //    tvProfileUser.setOnClickListener(this);
       // infoTabSelected.setVisibility(View.VISIBLE);
  //  followingTabSelected.setVisibility(View.GONE);
   //     blockedTabSelected.setVisibility(View.GONE);
     //   subscriptionTabSelected.setVisibility(View.GONE);
      //  tvInfoTab.setOnClickListener(this);
      //  tvFollowingTab.setOnClickListener(this);
       // tvBlockedTab.setOnClickListener(this);
       // tvSubscriptionsTab.setOnClickListener(this);
//        ivStatusUpdate.setOnClickListener(this);
      ivStatusMessage.setOnClickListener(this);
        ivStatusRequest.setOnClickListener(this);
       ivStatusCheckList.setOnClickListener(this);
//        ivStatusPrivacy.setOnClickListener(this);
        ivStatusDating.setOnClickListener(this);
//        rbNavDatingOn.setOnClickListener(this);
//        rbNavDatingOff.setOnClickListener(this);

        navTitle = findViewById(R.id.nav_title);
        llNavHelp = findViewById(R.id.ll_nav_help);
        llNavMessage = findViewById(R.id.ll_nav_messages);
        llNavAlbum = findViewById(R.id.ll_nav_album);
        llNavFriend = findViewById(R.id.ll_nav_friend);
        llNavGroup = findViewById(R.id.ll_nav_group);
        llNavChecklist = findViewById(R.id.ll_nav_checklist);
        llNavPrivacy = findViewById(R.id.ll_nav_privacy);
        llNavRequest = findViewById(R.id.ll_nav_request);
        llNavSearchByDating = findViewById(R.id.ll_nav_searchByDating);

        llNavHelp.setVisibility(View.VISIBLE);
        llNavMessage.setVisibility(View.GONE);
        llNavAlbum.setVisibility(View.GONE);
        llNavFriend.setVisibility(View.GONE);
        llNavGroup.setVisibility(View.GONE);
        llNavChecklist.setVisibility(View.GONE);
        llNavPrivacy.setVisibility(View.GONE);
        llNavRequest.setVisibility(View.GONE);
        llNavSearchByDating.setVisibility(View.GONE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nv = (RelativeLayout) findViewById(R.id.nav_view);

        messages = new ArrayList<>();
        albums = new ArrayList<>();
        friends = new ArrayList<>();
        groups = new ArrayList<>();
        tasklists = new ArrayList<>();
        receiveRequests = new ArrayList<>();
        sentRequests = new ArrayList<>();
        userDatingOns = new ArrayList<>();
        userDatingOffs = new ArrayList<>();

        messageList = findViewById(R.id.message_list);
        albumList = findViewById(R.id.album_list);
        friendList = findViewById(R.id.friend_list);
        groupList = findViewById(R.id.group_list);
        tasklistList = findViewById(R.id.tasklist_list);
        receiverequestList = findViewById(R.id.receive_request_list);
        sentrequestList = findViewById(R.id.sent_request_list);
        userByDatingList = findViewById(R.id.user_list_byDating);

        cbNavPrivateFriendRequest = findViewById(R.id.cb_nav_privacy_friendrequest);
        cbNavPrivateMemberMsg = findViewById(R.id.cb_nav_privacy_membermsg);
        cbNavPrivateMemberSearch = findViewById(R.id.cb_nav_privacy_membersearch);
        cbNavPrivateUserQuote = findViewById(R.id.cb_nav_privacy_userquote);
        cbNavPrivateFriendRequest.setOnClickListener(this);
        cbNavPrivateMemberMsg.setOnClickListener(this);
        cbNavPrivateMemberSearch.setOnClickListener(this);
        cbNavPrivateUserQuote.setOnClickListener(this);

        TextView aboutus=findViewById(R.id.about_us);
        TextView advertising=findViewById(R.id.advertising);
        TextView privacy=findViewById(R.id.private_policy);
        TextView termService=findViewById(R.id.term_service);
        TextView contactus=findViewById(R.id.contact_us);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.urban.network/"));
                startActivity(browserIntent);
            }
        });
        advertising.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.urban.network/"));
                startActivity(browserIntent);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.urban.network/"));
                startActivity(browserIntent);
            }
        });
        termService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.urban.network/"));
                startActivity(browserIntent);
            }
        });

        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.urban.network/"));
                startActivity(browserIntent);
            }
        });
        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                drawer.closeDrawer(nv);
                Intent intent = new Intent(HomeActivity.this, AddPhotoActivity.class);
                startActivity(intent);
            }
        });
        tasklistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawer.closeDrawer(nv);
                Intent intent = new Intent(HomeActivity.this, ManageTaskActivity.class);
                startActivity(intent);
            }
        });

        receiverequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageList listitem = receiveRequests.get(i);
                String name = listitem.getZoneTitle();
                String photo = listitem.getPhoto();
                String id = listitem.getAddress();
                selectedReceiveRequestUser = id;
                popup_receiveRequest(name);
            }
        });

        sentrequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageList listitem = sentRequests.get(i);
                String name = listitem.getZoneTitle();
                String photo = listitem.getPhoto();
                String id = listitem.getAddress();
                selectedSentRequestUser = id;
                popup_sentRequest(name);
            }
        });

        loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Data");
        loadingDialog.show();
        handler = new Handler();
        new GetProfile().execute();
        messages.clear();
        albums.clear();
        new GetAlbum().execute();
        friends.clear();
        new GetFriend().execute();
        groups.clear();
        new GetGroup().execute();
        tasklists.clear();
        new GetTasklist().execute();
    }

    private void loadProfile() {

//        Button updateInformationBtn = findViewById(R.id.update_information_btn);
//        etProfileStatus = findViewById(R.id.et_profile_status);
//        etProfileName = findViewById(R.id.et_profile_name);
//        etProfileSearchName = findViewById(R.id.et_profile_searchName);
//        etProfilePaypaEmail = findViewById(R.id.et_profile_paypalEmail);
//        etProfileHometown = findViewById(R.id.et_profile_hometown);
//        etProfileCurrentCity = findViewById(R.id.et_profile_currentCity);
//        etProfilePersonalInformatioin = findViewById(R.id.et_profile_personalInformation);
//        rbProfileDatingOn = findViewById(R.id.rb_profile_dating_on);
//        rbProfileDatingOff = findViewById(R.id.rb_profile_dating_off);
//        etProfileStatus.setText(profile_status);
//        etProfileName.setText(profile_name);
//        etProfileSearchName.setText(profile_searchName);
//        etProfilePaypaEmail.setText(profile_paypalEmail);
//        etProfileHometown.setText(profile_hometown);
//        etProfileCurrentCity.setText(profile_currentCity);
//        etProfilePersonalInformatioin.setText(profile_information);
//        if(profile_dating.equals("0")) {
//            rbProfileDatingOff.setChecked(true);
//        } else {
//            rbProfileDatingOn.setChecked(true);
//        }

        updatePhotoImageView = findViewById(R.id.update_photo_imageview);
        if(profile_photoUrl.equals("")){
            updatePhotoImageView.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + profile_photoUrl;
            Picasso.with(HomeActivity.this).load(photourl)
                    .transform(new ImageTrans_CircleTransform()).into(updatePhotoImageView);
        }
//        updatePhotoImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//                int newHeight = 200, newWidth = 200;
//                updatePhotoImageView.requestLayout();
//                updatePhotoImageView.getLayoutParams().height = newHeight;
//                updatePhotoImageView.getLayoutParams().width = newWidth;
//                updatePhotoImageView.setScaleType(ImageView.ScaleType.FIT_XY);

              //  getImageFromAlbum();
//            }
//        });
//        updateInformationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                profile_status = etProfileStatus.getText().toString();
//                profile_name = etProfileName.getText().toString();
//                profile_searchName = etProfileSearchName.getText().toString();
//                profile_paypalEmail = etProfilePaypaEmail.getText().toString();
//                profile_hometown = etProfileHometown.getText().toString();
//                profile_currentCity = etProfileCurrentCity.getText().toString();
//                profile_information = etProfilePersonalInformatioin.getText().toString();
//                if(rbProfileDatingOn.isChecked()) {
//                    profile_temp_dating = "1";
//                } else {
//                    profile_temp_dating = "0";
//                }
//                loadingDialog = WeiboDialogUtils.createLoadingDialog(HomeActivity.this, "Updating");
//                loadingDialog.show();
//                upLoadServerUri = "https://urban.network/Api/upload/upload.php";
//                if(updated_photo==1) new GetLatestPhotoID().execute();
//                new updateInformation().execute();
//
//            }
//        });

    }

    public void popup_receiveRequest(String fromUserName) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_selectlanguage);
        TextView tvPopupMessage, cancelBtn, okBtn, quitBtn;
        tvPopupMessage = dialog.findViewById(R.id.tv_popup_message);
        okBtn = dialog.findViewById(R.id.btn_ok);
        cancelBtn = dialog.findViewById(R.id.btn_cancel);
        quitBtn = dialog.findViewById(R.id.btn_quit);
        quitBtn.setVisibility(View.VISIBLE);
        okBtn.setText(getResources().getString(R.string.accept));
        cancelBtn.setText(getResources().getString(R.string.decline));
        quitBtn.setText(getResources().getString(R.string.later));
        tvPopupMessage.setText(getResources().getString(R.string.you_have_a_request_from) + " " + fromUserName);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_acceptDecline = "1";
                new AcceptDeclineReciveRequest().execute();
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_acceptDecline = "0";
                new AcceptDeclineReciveRequest().execute();
                dialog.dismiss();
            }
        });
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void popup_sentRequest(String toUserName) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_selectlanguage);
        TextView tvPopupMessage, cancelBtn, okBtn;
        tvPopupMessage = dialog.findViewById(R.id.tv_popup_message);
        okBtn = dialog.findViewById(R.id.btn_ok);
        cancelBtn = dialog.findViewById(R.id.btn_cancel);
        okBtn.setText(getResources().getString(R.string.wait));
        cancelBtn.setText(getResources().getString(R.string.cancel));
        tvPopupMessage.setText(toUserName + " " + getResources().getString(R.string.didnt_accept_yet));
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new WaitCancelSentRequest().execute();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.logout_btn) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if(v.getId() == R.id.btn_menu) {
            navTitle.setText(getResources().getString(R.string.help));
            initNavUI();
            llNavHelp.setVisibility(View.VISIBLE);
            drawer.openDrawer(nv);
        }
//        else if(v.getId() == R.id.iv_status_update) {
//
//        }
        else if(v.getId() == R.id.iv_status_message) {
            messages.clear();
            messageList.setAdapter(null);
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Friends");
            loadingDialog.show();
            new GetMessage().execute();
            navTitle.setText(getResources().getString(R.string.messages));
            initNavUI();
          //  llNavMessage.setVisibility(View.VISIBLE);
//            drawer.openDrawer(nv);
            Intent intent = new Intent(HomeActivity.this, FriendMessageActivity.class);
            intent.putExtra("type","new_message");
            startActivity(intent);
        }
        else if(v.getId() == R.id.iv_status_request) {
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Requests");
//            loadingDialog.show();
//            receiveRequests.clear();
//            sentRequests.clear();
//            new GetReceiveRequest().execute();
//            navTitle.setText(getResources().getString(R.string.requests));
//            initNavUI();
//            llNavRequest.setVisibility(View.VISIBLE);
//            drawer.openDrawer(nv);

            Intent intent =new Intent(HomeActivity.this,RequestActivity.class);
            startActivity(intent);
        }
//        else if(v.getId() == R.id.iv_status_dating) {
//            Intent intent = new Intent(HomeActivity.this, DatingSearchActivity.class);
//            startActivity(intent);
//        }
   else if(v.getId() == R.id.iv_status_dating) {
          Intent intent = new Intent(HomeActivity.this, DatingSearchActivity.class);
           startActivity(intent);
       }
        else if(v.getId() == R.id.iv_status_checklist) {
//            tasklists.clear();
//            tasklistList.setAdapter(null);
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Checklist");
//            loadingDialog.show();
//            new GetTasklist().execute();
//            navTitle.setText(getResources().getString(R.string.tasklist));
//            initNavUI();
//            llNavChecklist.setVisibility(View.VISIBLE);
//            drawer.openDrawer(nv);
            Intent intent = new Intent(HomeActivity.this, ManageTaskActivity.class);
            startActivity(intent);

        }
//        else if(v.getId() == R.id.iv_status_privacy) {
//            initNavUI();
//            changePrivacyOptions.setEnabled(false);
//            navTitle.setText(getResources().getString(R.string.privacy_options));
//            llNavPrivacy.setVisibility(View.VISIBLE);
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Privacy");
//            loadingDialog.show();
//            new GetPrivacy().execute();
//            drawer.openDrawer(nv);
//        }
//        else if(v.getId() == R.id.profile_album) {
//            navTitle.setText(getResources().getString(R.string.album));
//            initNavUI();
//            llNavAlbum.setVisibility(View.VISIBLE);
//            drawer.openDrawer(nv);
//        }
        else if(v.getId() == R.id.tv_profile_friend) {
//            friends.clear();
//            friendList.setAdapter(null);
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Friends");
//            loadingDialog.show();
//            new GetFriend().execute();
//            navTitle.setText(getResources().getString(R.string.friends));
//            initNavUI();
//            llNavFriend.setVisibility(View.VISIBLE);
//            drawer.openDrawer(nv);
            Intent intent = new Intent(HomeActivity.this, FriendMessageActivity.class);
            intent.putExtra("type","friend");
            startActivity(intent);
        } else if(v.getId()==R.id.tv_profile_group) {
//            groups.clear();
//            groupList.setAdapter(null);
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Groups");
//            loadingDialog.show();
//            new GetGroup().execute();
//            navTitle.setText(getResources().getString(R.string.groups));
//            initNavUI();
//            llNavGroup.setVisibility(View.VISIBLE);
//            drawer.openDrawer(nv);
            Intent intent = new Intent(HomeActivity.this, ManageUserActivity.class);
            intent.putExtra("type","group");
            startActivity(intent);
        }
//        else if(v.getId() == R.id.tv_profile_user)  {
//            Intent intent = new Intent(HomeActivity.this, ManageUserActivity.class);
//            intent.putExtra("type","friend");
//            startActivity(intent);
//        }

            else if(v.getId() == R.id.user_edit)  {
            Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);

            startActivity(intent);


        }
        else if(v.getId() == R.id.feedback)  {

           Intent intent = new Intent(HomeActivity.this, FeedBackActivity.class);
           startActivity(intent);
       }
        else if(v.getId() == R.id.album_add_btn) {
         //   drawer.closeDrawer(nv);
            Intent intent = new Intent(HomeActivity.this, AddPhotoActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.friend_add_btn) {
            drawer.closeDrawer(nv);
            Intent intent = new Intent(HomeActivity.this, ManageUserActivity.class);
            intent.putExtra("type","friend");
            startActivity(intent);
        } else if(v.getId() == R.id.group_add_btn) {
            drawer.closeDrawer(nv);
            Intent intent = new Intent(HomeActivity.this, ManageUserActivity.class);
            intent.putExtra("type","group");
            startActivity(intent);
        } else if(v.getId() == R.id.tasklist_add_btn) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Uploading new Tasklist");
            loadingDialog.show();
            this_newTasklistName = etNavNewTasklist.getText().toString();
            if(cbNavChecklistPrivate.isChecked()){
                this_checklistPrivateState = "1";
            } else {
                this_checklistPrivateState = "0";
            }
            new CreateTasklist().execute();
        } else if(v.getId() == R.id.btn_nav_change_privacy) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Changing Privacy Options");
            loadingDialog.show();
            if(cbNavPrivateFriendRequest.isChecked()) this_tempPrivacyFriendRequest = "1";
            if(cbNavPrivateMemberMsg.isChecked()) this_tempPrivacyMemberMsg = "1";
            if(cbNavPrivateMemberSearch.isChecked()) this_tempPrivacyMemberSearch = "1";
            if(cbNavPrivateUserQuote.isChecked()) this_tempPrivacyUserQuote = "1";
            new ChangePrivacy().execute();
        } else if(v.getId()== R.id.cb_nav_privacy_friendrequest || v.getId()== R.id.cb_nav_privacy_membermsg || v.getId()== R.id.cb_nav_privacy_membersearch || v.getId()== R.id.cb_nav_privacy_userquote) {
            checkPrivacySettings();
        } else if(v.getId() == R.id.nav_search_userByDating_on) {
            userByDaintListApater = new SearchUserListAdapter.UserListAdapter(HomeActivity.this, userDatingOns);
            userByDatingList.setAdapter(userByDaintListApater);
        } else  if(v.getId() == R.id.nav_search_userByDating_off) {
            userByDaintListApater = new SearchUserListAdapter.UserListAdapter(HomeActivity.this, userDatingOffs);
            userByDatingList.setAdapter(userByDaintListApater);
        }
    }

    private void initNavUI(){
        llNavHelp.setVisibility(View.GONE);
        llNavMessage.setVisibility(View.GONE);
        llNavAlbum.setVisibility(View.GONE);
        llNavFriend.setVisibility(View.GONE);
        llNavGroup.setVisibility(View.GONE);
        llNavChecklist.setVisibility(View.GONE);
        llNavPrivacy.setVisibility(View.GONE);
        llNavRequest.setVisibility(View.GONE);
        llNavSearchByDating.setVisibility(View.GONE);
    }
    private void checkPrivacySettings() {
        int a = 0;
        if(profile_friendrequest.equals("0")) {
            if(cbNavPrivateFriendRequest.isChecked()) a=1;
        } else {
            if(!cbNavPrivateFriendRequest.isChecked()) a=1;
        }
        if(profile_membermsg.equals("0")) {
            if(cbNavPrivateMemberMsg.isChecked()) a=1;
        } else {
            if(!cbNavPrivateMemberMsg.isChecked()) a=1;
        }
        if(profile_membersearch.equals("0")) {
            if(cbNavPrivateMemberSearch.isChecked()) a=1;
        } else {
            if(!cbNavPrivateMemberSearch.isChecked()) a=1;
        }
        if(profile_userquote.equals("0")) {
            if(cbNavPrivateUserQuote.isChecked()) a=1;
        } else {
            if(!cbNavPrivateUserQuote.isChecked()) a=1;
        }
        if(a==1) {
            changePrivacyOptions.setEnabled(true);
            changePrivacyOptions.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            changePrivacyOptions.setEnabled(false);
            changePrivacyOptions.setTextColor(getResources().getColor(R.color.whiteLightColor));
        }
    }
    private void getImageFromAlbum(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                updated_photo = 1;
                final Uri imageUri = data.getData();
                imagepath =getPath(imageUri);
                final InputStream imageStream = HomeActivity.this.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                updatePhotoImageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(HomeActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = HomeActivity.this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        cursor = HomeActivity.this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;
        String extention = fileName.substring(fileName.length()-5,fileName.length());
        String[] separated = extention.split("\\.");
        fileName = uploadPhotoName + "." + separated[1];
        uploadPhotoName = fileName;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            WeiboDialogUtils.closeDialog(loadingDialog);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(HomeActivity.this, "Source File not exist :"+ imagepath, Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        }
        else{
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if(serverResponseCode == 200){
//                    getActivity().runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(getActivity(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                ex.printStackTrace();
                HomeActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(HomeActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getActivity(), "Upload fault", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
            return serverResponseCode;
        }
    }

    private class GetLatestPhotoID extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "photos/profilePicture.php";
            String parameters = "";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String id = jsonObj.getString("id");
                    int a = Integer.parseInt(id);
                    a = a+1;
                    uploadPhotoName = String.valueOf(a);
                    uploadPhotoNamewithoutExtention = uploadPhotoName;
                } catch (final JSONException e) {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this,"Json parsing error photoID: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this,
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
            if(imagepath.length()>0) {
                new Thread(new Runnable() {
                    public void run() {
                        uploadFile(imagepath);
                    }
                }).start();
            } else {
                Toast.makeText(HomeActivity.this, "Select Photo", Toast.LENGTH_SHORT).show();
                WeiboDialogUtils.closeDialog(loadingDialog);
            }
        }
    }

    private class updateInformation extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/updateProfile.php?";
            String s = null;
            if(updated_photo==1) {
                s = uploadPhotoNamewithoutExtention;
            } else {
                s= profile_picture;
            }
            String parameters = "email=" +  static_email +
                    "&status=" + profile_status +
                    "&id=" + profile_id +
                    "&name=" + profile_name +
                    "&search_name=" + profile_searchName +
                    "&picture=" + s +
                    "&paypalemail=" + profile_paypalEmail +
                    "&hometown=" + profile_hometown +
                    "&current_city=" + profile_currentCity +
                    "&dating=" + profile_temp_dating
                    ;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    success_flag1 = 1;

                } catch (final JSONException e) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this,"Json parsing error 222: " + e.getMessage(),
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

            if(success_flag1 == 1){
                new update_show_picture_user().execute();
            } else {
                Toast.makeText(HomeActivity.this,"Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetProfile extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/loadProfile.php?";
            String parameters = "email=" +  static_email;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    profile_id = jsonObj.getString("id");
                    profile_name = jsonObj.getString("name");
                    profile_searchName = jsonObj.getString("search_name");
                    profile_email = jsonObj.getString("email");
                    profile_picture = jsonObj.getString("picture");
                    profile_password = jsonObj.getString("password");
                    profile_hometown = jsonObj.getString("hometown");
                    profile_currentCity = jsonObj.getString("current_city");
                    profile_paypalEmail = jsonObj.getString("paypalemail");
                    profile_information = jsonObj.getString("information");
                    profile_status = jsonObj.getString("status");
                    profile_points = jsonObj.getString("points");
                    profile_dating = jsonObj.getString("dating");
                    profile_birthday=jsonObj.getString("birthday");
                    success_flag = 1;

                } catch (final JSONException e) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error getprofile: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(success_flag == 1){
               tvProfileName.setText(profile_name);
               // tvProfileStatus.setText(profile_status);
               // tvProfilePoint.setText(profile_points + " points");
                success_flag = 0;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new GetPhoto().execute();
                    }
                }, 1002);
            } else {

                Toast.makeText(getApplicationContext(),"Connect Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetPhoto extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/getPhoto.php?";
            String parameters = "user=" +  profile_picture;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    profile_photoUrl = jsonObj.getString("show");
                    success_flag = 1;
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error getphoto: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            if(success_flag == 1){
                String photourl = "https://urban.network/" + profile_photoUrl;
            //    Picasso.with(HomeActivity.this).load(photourl).into(ivProfilePhoto);
//                Toast.makeText(getApplicationContext(),"Called",Toast.LENGTH_LONG).show();
                loadProfile();
            } else {

                Toast.makeText(getApplicationContext(),"Connect Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/message.php?";
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String name = c.getString("name");
                        String message = c.getString("message");
                        String photo = c.getString("photo");
                        messages.add(new MessageList(name,message, photo) );
                    }
                } catch (final JSONException e) {
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(messages.size()>0) {
                messagelistapater = new MessageListAdapter(getApplicationContext(), messages);
                messageList.setAdapter(messagelistapater);
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
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String name = c.getString("name");
                        String show = c.getString("show");
                        String photo = c.getString("photo");
                        String temp = photo.substring(9,photo.length()-9);
                        String[] separeted = temp.split("/");
                        albumname = separeted[0];

                        albums.add(new MessageList(name,show, photo) );
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server with album",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(albums.size()>0) {
                messagelistapater = new MessageListAdapter(getApplicationContext(), albums);
                albumList.setAdapter(messagelistapater);


            }
        }
    }

    private class GetFriend extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "friend/getfriend.php?";
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String photo = c.getString("photo");
                        String status = c.getString("status");
                        friends.add(new FriendList(name,id, photo, status) );
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(friends.size()>0) {
                friendListAdapter = new FriendListAdapter(getApplicationContext(), friends);
                friendList.setAdapter(friendListAdapter);

//                LinearLayout llFlowFriendsPhoto = findViewById(R.id.ll_flow_friends_photo);
//                llFlowFriendsPhoto.setOrientation(LinearLayout.HORIZONTAL);
//                llFlowFriendsPhoto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                int i;

                for (i=0;i<friends.size();i++) {
                    FriendList s = friends.get(i);
                    String photourl1 = s.getPhoto();
                    if(photourl1.equals("") || photourl1.equals("null")) continue;
                    String photourl = "https://urban.network/" + photourl1;
                    ImageView imageView = new ImageView(getApplicationContext());

                    RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
                    relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
                    ImageView imageView1 = new ImageView(getApplicationContext());
                    imageView1.setImageResource(R.drawable.mask);
                    imageView1.setLayoutParams(new RelativeLayout.LayoutParams(200,200));


                    Picasso.with(HomeActivity.this).load(photourl).into(imageView);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(lp);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                    if(!openfrinedphotoFrame) {
                        relativeLayout.addView(imageView);
                        relativeLayout.addView(imageView1);
                    }
               //     llFlowFriendsPhoto.addView(relativeLayout);
                }
                openfrinedphotoFrame = true;
            }
        }
    }

    private class GetGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/getgroup.php?";
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String type = c.getString("type");
                        String name = c.getString("name");
                        String photo = c.getString("photo");
                        groups.add(new MessageList(name,type, photo) );
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server while get groups",
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
            if(groups.size()>0) {
                messagelistapater = new MessageListAdapter(getApplicationContext(), groups);
                groupList.setAdapter(messagelistapater);
            }
        }
    }

    private class GetTasklist extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/gettasklist.php?";
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String private1 = c.getString("private");
                        tasklists.add(new TasklistList(name, private1,id) );
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(tasklists.size()>0) {
                tasklistListAdapter = new TasklistListAdapter(getApplicationContext(), tasklists);
                tasklistList.setAdapter(tasklistListAdapter);
            }
        }
    }

    private class CreateTasklist extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/createtasklist.php?";
//            newTasklistName.replace(" ","__");

            String parameters = "user=" +  profile_id +
                    "&name=" + this_newTasklistName +
                    "&private=" + this_checklistPrivateState;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    addNewTaskListResult = jsonObj.getString("message");
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(addNewTaskListResult.equals("success")) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Add new Tasklist")
                        .setMessage("New Tasklist is created successfully")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                tasklists.clear();
                new GetTasklist().execute();

            } else {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Add new Tasklist")
                        .setMessage("Error in creating new tasklist")
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

    private class GetPrivacy extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "privacy/getprivacy.php?";
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        profile_friendrequest = c.getString("friendrequest");
                        profile_membermsg = c.getString("membermsg");
                        profile_membersearch = c.getString("membersearch");
                        profile_userquote = c.getString("userquote");
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(profile_friendrequest.length()>0) {
                if(profile_friendrequest.equals("1")) {
                    cbNavPrivateFriendRequest.setChecked(true);
                } else {
                    cbNavPrivateFriendRequest.setChecked(false);
                }
                if(profile_membermsg.equals("1")) {
                    cbNavPrivateMemberMsg.setChecked(true);
                } else {
                    cbNavPrivateMemberMsg.setChecked(false);
                }
                if(profile_membersearch.equals("1")) {
                    cbNavPrivateMemberSearch.setChecked(true);
                } else {
                    cbNavPrivateMemberSearch.setChecked(false);
                }
                if(profile_userquote.equals("1")) {
                    cbNavPrivateUserQuote.setChecked(true);
                } else {
                    cbNavPrivateUserQuote.setChecked(false);
                }

            }
        }
    }

    private class ChangePrivacy extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "privacy/changeprivacy.php?";
            String parameters = "user=" +  profile_id +
                    "&friendrequest=" + this_tempPrivacyFriendRequest +
                    "&membermsg=" + this_tempPrivacyMemberMsg +
                    "&membersearch=" + this_tempPrivacyMemberSearch +
                    "&userquote=" + this_tempPrivacyUserQuote;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    changePrivacyOptionsResult = jsonObj.getString("message");
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(changePrivacyOptionsResult.equals("success")) {
                new GlideToast.makeToast(HomeActivity.this,"Privacy Settings are updated");
                new GetPrivacy().execute();
            } else {
                new GlideToast.makeToast(HomeActivity.this,"Unable to update privacy settings");
            }
        }
    }

    private class GetReceiveRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "friend/getreceiverequest.php?";
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String photo = c.getString("photo");
                        receiveRequests.add(new MessageList(name,id, photo) );
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            if(receiveRequests.size()>0) {
                messagelistapater = new MessageListAdapter(getApplicationContext(), receiveRequests);
                receiverequestList.setAdapter(messagelistapater);
            }
            sentRequests.clear();
            new GetSentRequest().execute();
        }
    }

    private class GetSentRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "friend/getsentrequest.php?";
            String parameters = "user=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String photo = c.getString("photo");
                        sentRequests.add(new MessageList(name,id, photo) );
                    }
                } catch (final JSONException e) {

                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(sentRequests.size()>0) {
                messagelistapater = new MessageListAdapter(getApplicationContext(), sentRequests);
                sentrequestList.setAdapter(messagelistapater);
            }
        }
    }

    private class AcceptDeclineReciveRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "friend/acceptdeclinereceiverequest.php?";
            String parameters = "user=" +  profile_id +
                    "&user4=" + selectedReceiveRequestUser +
                    "&flag=" + flag_acceptDecline;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        requestAcceptDeclineResult = c.getString("message");

                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            receiveRequests.clear();
            new GetReceiveRequest().execute();
            if(requestAcceptDeclineResult.equals("success")) {
                new GlideToast.makeToast(HomeActivity.this,"Accepted");
            } else if (requestAcceptDeclineResult.equals("success_decline")){
                new GlideToast.makeToast(HomeActivity.this,"Declined");
            }
        }
    }

    private class WaitCancelSentRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "friend/calcelsentrequest.php?";
            String parameters = "user=" +  profile_id +
                    "&user4=" + selectedSentRequestUser;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        requestWaitCancelResult = c.getString("message");

                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);
            sentRequests.clear();
            new GetSentRequest().execute();
            if(requestWaitCancelResult.equals("success")) {
                new GlideToast.makeToast(HomeActivity.this,"Accepted");
            } else{
                new GlideToast.makeToast(HomeActivity.this,"Fault");
            }
        }
    }

    private class update_show_picture_user extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "upload/updatePictureUser.php?";

            String parameters = "id=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    success_flag = 1;

                } catch (final JSONException e) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
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
                new GetProfile().execute();
            } else {
                Toast.makeText(HomeActivity.this,"Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

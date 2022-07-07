package jereme.urban_network_dating.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.SearchUserListAdapter;
import jereme.urban_network_dating.FullscreenActivity;
import jereme.urban_network_dating.List.UserList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static jereme.urban_network_dating.HomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;

public class UserFragment extends Fragment implements View.OnClickListener{
    EditText searchName;
    ArrayList<UserList> members = new ArrayList<>();
    Dialog loadingDialog;
    SearchUserListAdapter.UserListAdapter userlistapater;
    ListView userList;
    ImageView ivSelectedUserPhoto, ivShowMessage, ivHideMessage;
    TextView tvSelectedUserName;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedUserID = "", selectedMessageName, selectedMessageDescription;
    String addFriendMessage = "", addMessageResoponse = "";
    EditText etMessageTitle, etMessageDescription;
    RelativeLayout rlMessageGroup;
    private String this_searchName;

//    @BindView(R.id.btn_bottom_sheet)
    Button btnBottomSheet;

//    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        final Button memberTabBtn, addFriendBtn;
        ImageView sendMessageBtn;
        memberTabBtn = view.findViewById(R.id.search_member_btn);
        addFriendBtn = view.findViewById(R.id.add_friend_btn);
        sendMessageBtn = view.findViewById(R.id.send_message_btn);
        ivSelectedUserPhoto = view.findViewById(R.id.selected_member_photo);
        tvSelectedUserName = view.findViewById(R.id.selected_memeber_name);
        etMessageTitle = view.findViewById(R.id.et_message_title);
        etMessageDescription = view.findViewById(R.id.et_message_description);
        searchName = view.findViewById(R.id.et_searchname);
        userList= view.findViewById(R.id.member_list);
        ivShowMessage= view.findViewById(R.id.show_message_btn);
        ivHideMessage= view.findViewById(R.id.hide_message_btn);
        rlMessageGroup= view.findViewById(R.id.massageopen);

        ivHideMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlMessageGroup.setVisibility(View.GONE);
                ivHideMessage.setVisibility(View.GONE);
                ivShowMessage.setVisibility(View.VISIBLE);
            }
        });

        ivShowMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlMessageGroup.setVisibility(View.VISIBLE);
                ivHideMessage.setVisibility(View.VISIBLE);
                ivShowMessage.setVisibility(View.GONE);
            }
        });

        memberTabBtn.setOnClickListener(this);
        addFriendBtn.setOnClickListener(this);
        sendMessageBtn.setOnClickListener(this);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                UserList listitem = members.get(arg2);
                selectedUsername = listitem.getZoneTitle();
                selectedUserPhotoUrl = listitem.getPhoto();
                selectedUserID = listitem.getAddress();

                if (selectedUserPhotoUrl.equals("null")) {
                    ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                    tvSelectedUserName.setText(selectedUsername);
                } else {
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                    tvSelectedUserName.setText(selectedUsername);
                }
            }
        });
        https://www.ziprecruiter.com/k/t/AAIF49yXsjy7DKTSbtW9LbpZWkpjAWuxTThninPs56KZXZVI6jfllxki5-s-9uIt6GtKYhB198W8F5rz33cTQ4pjGl3yrgshKxAM_rCM3RyYAbL6gwnaMX-6z96C6B1cGm1jc-eEkgRHe8HBRaVCt1NW_hVFo9CVxCDArFkU7_02ZW-PggCGUsj4Pw6BEa6yf7s42PZqDbjbPGQsdpGdojS-mBPOUjmGCSIUjTpw_HVfJ1M3pKUy02Bv8ySXovLxtDvlfajnD6OhvPovgSDY7Qi3xnOeMFDa13WALtJc9LmrUUmF4KPWfVO3JH5pDbRtc-u6uAd6LMCCMCEKxYiuBgc-Ga9PpAQurkFGfGBjjYwVMuIokabdo4w1TF4kuR0gVwdUuVgyWjevgHeSWAYJPaTL9k7Gj94fJpPstokgVgEmYh7lWDcgASdImi4wEGrSVso6juDZb74rm1zi7NlJOtzJU1R1sQ

        ivSelectedUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photourl = "";
                if (selectedUserPhotoUrl.equals("null")) {
                    photourl ="default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;
                }

                Intent intent = new Intent(getActivity().getApplication(), FullscreenActivity.class);
                intent.putExtra("Image", photourl);
                startActivity(intent);
            }
        });

        if (members.size() == 0) {
            members.clear();
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Members");
            loadingDialog.show();
            this_searchName = searchName.getText().toString().trim();
            new SearchMember().execute();
        } else {
            userlistapater = new SearchUserListAdapter.UserListAdapter(getActivity(), members);
            userList.setAdapter(userlistapater);

            if (selectedUserPhotoUrl.equals("null")) {
                ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                tvSelectedUserName.setText(selectedUsername);
            } else {
                String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                tvSelectedUserName.setText(selectedUsername);
            }
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_member_btn) {
            members.clear();
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Members");
            loadingDialog.show();
            this_searchName = searchName.getText().toString().trim();
            new SearchMember().execute();
        } else if (v.getId() == R.id.add_friend_btn) {
            addFriendMessage = "";
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Members");
            loadingDialog.show();
            new AddMemberToFriend().execute();
        } else if (v.getId() == R.id.send_message_btn) {
            if (selectedUserID.equals("")) {
                new AlertDialog.Builder(getActivity())
                    .setTitle("Send Message")
                    .setMessage("Select user")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            } else {
                if (!etMessageTitle.getText().toString().equals("") && !etMessageDescription.getText().toString().equals("")) {
                    loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Sending Message");
                    loadingDialog.show();
                    selectedMessageName = etMessageTitle.getText().toString();
                    selectedMessageDescription = etMessageDescription.getText().toString();
                    new SendMessage().execute();
                } else {
                    new AlertDialog.Builder(getActivity())
                        .setTitle("Send Message")
                        .setMessage("Input title and message")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                }
            }
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
            String sub_url = "member/searchmember.php?";
            String parameters = "id=" + profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String photo = c.getString("photo");
                        if (!photo.equals("null") && !photo.equals("")) {
                            selectedUserID = id;
                            selectedUserPhotoUrl = photo;
                            if (name.equals("")) selectedUsername = "<NULL>";
                            else selectedUsername = name;
                            members.add(new UserList(selectedUsername, id, photo));
                        }
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server for search member",
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
            if (members.size() > 0) {
                userlistapater = new SearchUserListAdapter.UserListAdapter(getActivity(), members);
                userList.setAdapter(userlistapater);
                if (!selectedUserPhotoUrl.equals("")) {
                    tvSelectedUserName.setText(selectedUsername);
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
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
            String parameters = "user=" + profile_id +
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
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
                new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.add_friend))
                    .setMessage(getResources().getString(R.string.add_friend_success))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            } else {
                new AlertDialog.Builder(getActivity())
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
//            selectedMessageName.replace(" ", "__");
//            selectedMessageDescription.replace(" ", "__");
            String parameters = "user=" + profile_id +
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
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
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.send_message))
                        .setMessage(getResources().getString(R.string.message_send_success))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.send_message))
                        .setMessage(getResources().getString(R.string.not_send_massage))
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

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.FriendListAdapter;
import jereme.urban_network_dating.AddPhotoActivity;
import jereme.urban_network_dating.FullscreenActivity;
import jereme.urban_network_dating.List.FriendList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static jereme.urban_network_dating.HomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;

public class FriendFragment extends Fragment implements View.OnClickListener{
    EditText searchName;
    Dialog loadingDialog;
    ImageView ivSelectedUserPhoto, ivShowMessage, ivHideMessage;
    TextView tvSelectedUserName;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedUserID = "", selectedMessageName, selectedMessageDescription;
    String addMessageResoponse = "";
    EditText etMessageTitle, etMessageDescription;
    ArrayList<FriendList> friends = new ArrayList<>();
    ListView friendList;
    RelativeLayout rlMessageGroup;
    FriendListAdapter friendListAdapter;
    public  int albumlength = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        ImageView sendMessageBtn;
        sendMessageBtn = view.findViewById(R.id.send_message_btn);
        ivSelectedUserPhoto = view.findViewById(R.id.selected_member_photo);
        tvSelectedUserName = view.findViewById(R.id.selected_memeber_name);
        etMessageTitle = view.findViewById(R.id.et_message_title);
        etMessageDescription = view.findViewById(R.id.et_message_description);
        searchName = view.findViewById(R.id.et_searchname);
        friendList= view.findViewById(R.id.friend_list);
        sendMessageBtn.setOnClickListener(this);
        ivShowMessage= view.findViewById(R.id.show_message_btn);
        ivHideMessage= view.findViewById(R.id.hide_message_btn);
        rlMessageGroup= view.findViewById(R.id.massageopen);
        Button addAlbumBtn = view.findViewById(R.id.album_add_btn);

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

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                FriendList listitem = friends.get(arg2);
                selectedUsername = listitem.getName();
                selectedUserPhotoUrl = listitem.getPhoto();
                selectedUserID = listitem.getID();

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

        addAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Members");
                loadingDialog.show();
                new GetAlbum().execute();
            }
        });

        if (friends.size() == 0) {
            friends.clear();
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Loading friends");
            loadingDialog.show();
            new GetFriend().execute();
        } else {
            friendListAdapter = new FriendListAdapter(getActivity(), friends);
            friendList.setAdapter(friendListAdapter);

            if (selectedUserPhotoUrl.equals("null")) {
                ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                tvSelectedUserName.setText(selectedUsername);
            } else {
                String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                tvSelectedUserName.setText(selectedUsername);
            }
        }

        ivSelectedUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photourl = "";

                if (selectedUserPhotoUrl.equals("null")|| selectedUserPhotoUrl.equals("")) {
                    photourl ="default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;
                }

                Intent intent = new Intent(getActivity().getApplication(), FullscreenActivity.class);
                intent.putExtra("Image", photourl);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_message_btn) {
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

    private class SendMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/messageadd.php?";
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
            if (addMessageResoponse.equals("success")) {
                etMessageDescription.setText("");
                etMessageTitle.setText("");
                new AlertDialog.Builder(getActivity())
                    .setTitle("Send Message")
                    .setMessage("Sent new message successfully")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            } else {
                new AlertDialog.Builder(getActivity())
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

                    if (contacts.length() > 0) {
                        albumlength = 1;
                    } else {
                        albumlength = 0;
                    }
                } catch (final JSONException e) {
                    albumlength = 0;
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GlideToast.makeToast(getActivity(),getResources().getString(R.string.server_connect_error));
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);

            if (albumlength == 1) {
                String photourl = "";

                if (selectedUserPhotoUrl.equals("null") || selectedUserPhotoUrl.equals("")) {
                    photourl = "default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;
                }

                Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                intent.putExtra("id",selectedUserID);
                intent.putExtra("Image", photourl);
                startActivity(intent);
            } else {
                String photourl = "";

                if (selectedUserPhotoUrl.equals("null") || selectedUserPhotoUrl.equals("")) {
                    photourl = "default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;
                }

                Intent intent = new Intent(getActivity(), FullscreenActivity.class);
                intent.putExtra("Image", photourl);
                startActivity(intent);
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
            String parameters = "user=" + profile_id;
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
                        selectedUsername = name;
                        selectedUserPhotoUrl = photo;
                        selectedUserID = id;
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server while loading friends",
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

            if (friends.size() > 0) {
                friendListAdapter = new FriendListAdapter(getActivity(), friends);
                friendList.setAdapter(friendListAdapter);
                String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                tvSelectedUserName.setText(selectedUsername);
            }
        }
    }
}

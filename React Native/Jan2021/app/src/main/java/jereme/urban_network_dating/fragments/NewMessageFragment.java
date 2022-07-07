package jereme.urban_network_dating.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.MessageFriendListAdapter;
import jereme.urban_network_dating.Chats.ChatRoom;
import jereme.urban_network_dating.Chats.ChatTapFragment;
import jereme.urban_network_dating.List.MessageFriendList;
import jereme.urban_network_dating.R;

import static jereme.urban_network_dating.Chats.ChatTapFragment.listnerObj;
import static jereme.urban_network_dating.NewHomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.locale_name;

public class NewMessageFragment extends Fragment implements View.OnClickListener, ChatTapFragment.OnSearchViewText {
    Dialog loadingDialog;
    ArrayList<MessageFriendList> messages, allmessages;
    ListView messageList;
    MessageFriendListAdapter messageFriendListAdapter;

    ImageView ivSelectedUserPhoto, ivShowMessage, ivHideMessage;
    TextView tvSelectedUserName, tvSelectedMessageDate;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedUserID = "", selectedMessageName, selectedMessageDescription;
    String addMessageResoponse = "";
    EditText etMessageTitle, etMessageDescription;
    private int page = 0;
    private int flag = 1;
    private int page_count = 0;
    RelativeLayout rlMessageGroup;
    SwipeRefreshLayout pullToRefresh;
    ProgressDialog progress;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_message, container, false);
        ImageView sendMessageBtn;
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        messages = new ArrayList<>();
        allmessages = new ArrayList<>();
        messageList = view.findViewById(R.id.message_list);
        messages.clear();
        messageList.setAdapter(null);
        progress = new ProgressDialog(getActivity());
        progress.setTitle(getResources().getString(R.string.loading));
        progress.setMessage(getResources().getString(R.string.inbox) + "...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialo
        page = 0;
        progress.show();
        new GetMessage().execute();

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                messages.get(i).getMessageDescription();
                System.out.println(messages.get(i).getMessageDescription());
                System.out.println(messages.get(i).getMessageTitle());
                System.out.println(messages.get(i).getUser4());

                String id = (messages.get(i).getUser4().equals(profile_id)) ? messages.get(i).getUser() : messages.get(i).getUser4();
                System.out.println(messages.get(i).getPartner());
                Intent intent = new Intent(getActivity(), ChatRoom.class);
                intent.putExtra(ChatRoom.EXTRA_ID, id);
                intent.putExtra(ChatRoom.EXTRA_NAME, messages.get(i).getPartner());
                //  intent.putExtra(ChatRoom.EXTRA_COUNT, user.count);
                intent.putExtra(ChatRoom.EXTRA_COUNT, Integer.parseInt(messages.get(i).getUser4()));
                startActivity(intent);
            }
        });
        Bundle bundle = this.getArguments();
//        System.out.println(bundle.getString("photo"));
        listnerObj = (NewMessageFragment) this;
        return view;
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.send_message_btn) {
//            if (selectedUserID.equals("")) {
//                new AlertDialog.Builder(getActivity())
//                        .setTitle(getResources().getString(R.string.send_message))
//                        .setMessage("Select user")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//            } else {
//                if (!etMessageTitle.getText().toString().equals("") && !etMessageDescription.getText().toString().equals("")) {
//                    loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Sending Message");
//                    loadingDialog.show();
//                    selectedMessageName = etMessageTitle.getText().toString();
//                    selectedMessageDescription = etMessageDescription.getText().toString();
//                    new SendMessage().execute();
//                } else {
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle("Send Message")
//                            .setMessage("Input title and message")
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//                }
//            }
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void searchMessage(String val) {
        System.out.println(val);
        //  Toast.makeText(getActivity(), val, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void searchViewText(String searchtext) {
        messages = (ArrayList<MessageFriendList>) allmessages.stream().filter(message -> message.getMessageDescription().toLowerCase().contains(searchtext) || message.getMessageTitle().toLowerCase().contains(searchtext)).collect(Collectors.toList());
//        if (messages.size() > 0) {
            messageFriendListAdapter = new MessageFriendListAdapter(getActivity(), messages);
            messageList.setAdapter(messageFriendListAdapter);
//        }
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
            String parameters = "user=" + profile_id +
                               "&lang=" + locale_name+
                               "&page=" + page +
                               "&flag=" + flag;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);
                    page_count = contacts.length();

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String name = c.getString("name");
                        String message = c.getString("message");
                        String photo = c.getString("photo");
                        String messagedirection = c.getString("messagedirection");
                        String user = c.getString("user");
                        String user4 = c.getString("user4");
                        String partner = c.getString("partner");
                        String date = c.getString("date");
                        int page1 = c.getInt("id");
                        page = page1;
                        messages.add(new MessageFriendList(name, message, photo, messagedirection, user, user4, partner, date) );
                        allmessages.add(new MessageFriendList(name, message, photo, messagedirection, user, user4, partner, date) );
                    }
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(getActivity(),
                                    "You have no Inbox messages  ",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                //  WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server while getting messages",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            //  WeiboDialogUtils.closeDialog(loadingDialog);

            if (messages.size() > 0) {
                messageFriendListAdapter = new MessageFriendListAdapter(getActivity(), messages);
                messageList.setAdapter(messageFriendListAdapter);
//              messageList.setSelection(messages.size() - page_count - 1);
            }
        }
    }
}

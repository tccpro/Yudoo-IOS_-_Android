package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.MessageListAdapter;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

public class RequestActivity extends AppCompatActivity {




    private RelativeLayout nv;
    private DrawerLayout drawer;
    Dialog loadingDialog;
    int success_flag = 0;
    public static String  profile_picture;

    ArrayList<MessageList> receiveRequests, sentRequests;

    TextView no_data_receive,no_data_send;
    ListView  receiverequestList, sentrequestList;

    MessageListAdapter messagelistapater;

    Dialog dialog;

    String selectedReceiveRequestUser;
    String selectedSentRequestUser;
    String flag_acceptDecline = "0";
    String requestAcceptDeclineResult = "";
    String requestWaitCancelResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        receiverequestList = findViewById(R.id.receive_request_list);
        sentrequestList = findViewById(R.id.sent_request_list);
        ImageView ivBack= findViewById(R.id.btn_back);

        receiveRequests = new ArrayList<>();
        sentRequests = new ArrayList<>();

        no_data_receive =findViewById(R.id.receive);
        no_data_send =findViewById(R.id.send);

        receiverequestList = findViewById(R.id.receive_request_list);
        sentrequestList = findViewById(R.id.sent_request_list);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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


        loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Requests");
        loadingDialog.show();
        receiveRequests.clear();
        sentRequests.clear();
         new GetReceiveRequest().execute();

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
            String parameters = "user=" +  HomeActivity.profile_id;
            String url = LoginActivity.base_url + sub_url + parameters;
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
                no_data_receive.setVisibility(View.GONE);
                receiverequestList.setVisibility(View.VISIBLE);
                messagelistapater = new MessageListAdapter(getApplicationContext(), receiveRequests);
                receiverequestList.setAdapter(messagelistapater);
            } else {
                no_data_receive.setVisibility(View.VISIBLE);
                receiverequestList.setVisibility(View.GONE);
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
            String parameters = "user=" +  HomeActivity.profile_id;
            String url = LoginActivity.base_url + sub_url + parameters;
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
                no_data_send.setVisibility(View.GONE);
                sentrequestList.setVisibility(View.VISIBLE);
                messagelistapater = new MessageListAdapter(getApplicationContext(), sentRequests);
                sentrequestList.setAdapter(messagelistapater);
            }else {
                no_data_send.setVisibility(View.VISIBLE);
                sentrequestList.setVisibility(View.GONE);
            }
        }
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




    private class AcceptDeclineReciveRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "friend/acceptdeclinereceiverequest.php?";
            String parameters = "user=" +  HomeActivity.profile_id +
                    "&user4=" + selectedReceiveRequestUser +
                    "&flag=" + flag_acceptDecline;
            String url = LoginActivity.base_url + sub_url + parameters;
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
                new GlideToast.makeToast(RequestActivity.this,"Accepted");
            } else if (requestAcceptDeclineResult.equals("success_decline")){
                new GlideToast.makeToast(RequestActivity.this,"Declined");
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
            String parameters = "user=" +  HomeActivity.profile_id +
                    "&user4=" + selectedSentRequestUser;
            String url = LoginActivity.base_url + sub_url + parameters;
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
                new GlideToast.makeToast(RequestActivity.this,"Accepted");
            } else{
                new GlideToast.makeToast(RequestActivity.this,"Fault");
            }
        }
    }

}

package jereme.urban_network_dating.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.NotificationsAdapter;
import jereme.urban_network_dating.List.Notifications;
import jereme.urban_network_dating.R;

import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.locale_name;
import static jereme.urban_network_dating.NewHomeActivity.profile_id;

public class PeopleNotificationFragment  extends Fragment {

    private List<Notifications> notificationList = new ArrayList<>();
    private NotificationsAdapter mAdapter;
    private int page = 0;
    private int flag = 1;
    private int page_count = 0;
    RecyclerView recyclerView;
    ProgressDialog progress, progressprofile;
    int success_flag = 0;
    String userid = "", user_name = "", user_email = "", user_photoUrl = "", user_hometown = "", user_birthday = "", user_moreimage = "", user_looking = "", user_interest = "", user_about = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        View view = inflater.inflate(R.layout.fragment_people_notification, null);
        TextView backBtn = view.findViewById(R.id.btn_backs);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeopleFragment peopleFragment =new PeopleFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, peopleFragment);
                ft.commit();
            }
        });

        backBtn.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recyclerView_notification);

        progress = new ProgressDialog(getActivity());
        progress.setTitle(getResources().getString(R.string.loading));
        progress.setMessage(getResources().getString(R.string.title_notifications) + "...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialo
        progress.show();
        new GetMessage().execute();
        return view;
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
                        String type = c.getString("type");
                        int page1 = c.getInt("id");
                        page = page1;

                        if (messagedirection.equals("income")) {
                            if (type.equals("wave") || type.equals("like"))
                                notificationList.add(new Notifications(partner, date, photo, type, user));
                        }
                    }
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(getActivity(), "You have no Inbox messages ", Toast.LENGTH_LONG).show();
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
            if (notificationList.size() > 0) {
                mAdapter = new NotificationsAdapter(notificationList);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(onItemClickListener);
//              messageList.setSelection(messages.size() - page_count - 1);
            }
        }
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO: Step 4 of 4: Finally call getTag() on the view.
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            // viewHolder.getItemId();
            // viewHolder.getItemViewType();
            // viewHolder.itemView;
            Notifications thisItem = notificationList.get(position);
            Bundle bundle = new Bundle();
            Toast.makeText(getActivity(), thisItem.getMessage(), Toast.LENGTH_LONG).show();
            progressprofile = new ProgressDialog(getActivity());
            progressprofile.setTitle(getResources().getString(R.string.loading));
            progressprofile.setMessage(getResources().getString(R.string.profile) + "...");
            progressprofile.setCancelable(false); // disable dismiss by tapping outside of the dialo
            progressprofile.show();
            new GetProfile(thisItem.getUser()).execute();
        }
    };

    private class GetProfile extends AsyncTask<Void, Void, Void> {
        String user;
        public GetProfile(String user) {
            this.user=user;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/loadProfile.php?";
            String parameters = "user=" + user;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    userid = jsonObj.getString("id");
                    user_name = jsonObj.getString("name");
                    //  user_searchName = jsonObj.getString("search_name");
                    user_email = jsonObj.getString("email");
                    String pic = jsonObj.getString("picture");
                    String a[] = pic.split("\\.");

                    if (a.length > 1)
                        user_photoUrl ="pictures/"+ pic;
                    else
                        user_photoUrl ="pictures/"+ pic + ".jpg";

                    //  profile_picture = jsonObj.getString("picture");
                    //  user_password = jsonObj.getString("password");
                    user_hometown = jsonObj.getString("hometown");
//                    user_currentCity = jsonObj.getString("current_city");
//                    profile_paypalEmail = jsonObj.getString("paypalemail");
//                    profile_information = jsonObj.getString("information");
//                    profile_status = jsonObj.getString("status");
//                    profile_points = jsonObj.getString("points");
//                    profile_dating = jsonObj.getString("dating");
                    user_birthday = jsonObj.getString("birthday");
                    user_moreimage = jsonObj.getString("morepic");
                    user_interest = jsonObj.getString("interest");
                    user_looking = jsonObj.getString("looking");
                    user_about = jsonObj.getString("aboutme") != null ? jsonObj.getString("aboutme") : "";
                    success_flag = 1;
                } catch (final JSONException e) {
                    //  WeiboDialogUtils.closeDialog(loadingDialog);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressprofile.dismiss();
                            Toast.makeText(getActivity(), "Json parsing error getprofile: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                // WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressprofile.dismiss();
                        Toast.makeText(getActivity(),
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
            //   WeiboDialogUtils.closeDialog(loadingDialog);
            progressprofile.dismiss();

            if (success_flag == 1) {
                Bundle sbundle=new Bundle();
                sbundle.putString("profileId", userid);
                sbundle.putString("profilepic", user_photoUrl);
                sbundle.putString("name", user_name);
                sbundle.putString("age", "24");
                sbundle.putString("interest", user_interest);
                sbundle.putString("looking", user_looking);
                sbundle.putString("morepic", user_moreimage);
                sbundle.putString("aboutme", user_about);
                sbundle.putString("hometown", user_hometown != null ? user_hometown : "");
                PeopleDetailsFragment peopleDetailsFragment = new PeopleDetailsFragment();
                peopleDetailsFragment.setArguments(sbundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, peopleDetailsFragment);
                ft.commit();
                //  tvProfileName.setText(profile_name);
                //  tvProfileStatus.setText(profile_status);
                //  tvProfilePoint.setText(profile_points + " points");
                success_flag = 0;
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        new GetPhoto().execute();
//                    }
//                }, 1002);
            } else {
                Toast.makeText(getActivity(), "Connect Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
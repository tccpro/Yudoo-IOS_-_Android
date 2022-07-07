package jereme.urban_network_dating.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.makeramen.roundedimageview.RoundedImageView;
import com.robert.autoplayvideo.CustomRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.ArticleListAdapter;
import jereme.urban_network_dating.Adapters.GroupPeopleAdapter;
import jereme.urban_network_dating.List.ArticleList;
import jereme.urban_network_dating.NewHomeActivity;
import jereme.urban_network_dating.R;

import static jereme.urban_network_dating.NewHomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.locale_name;

public class GroupDetailFragment extends DialogFragment implements CreatePostBottomSheet.OnCreatePostClickListener , NewHomeActivity.OnBackPressed {
    private int page = 0;
    Dialog loadingDialog;
    private GroupPeopleAdapter mAdapter;
    ArrayList<ArticleList> articles;
    CustomRecyclerView articleList;
    ArticleListAdapter articleListAdapter;
    String addGroupMessage = "", addMessageResoponse = "", exitGroupMessage = "", joinedgroup = "0";
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedGroupID = "", selectedGroupType = "";
    LinearLayout createpost;
    Picasso p;
    Button joingroup;
    ProgressDialog joinprogress, exitprogress;
    private ProgressBar articlespinner;
    CreatePostBottomSheet.OnCreatePostClickListener calback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        calback = this;

        View view = inflater.inflate(R.layout.fragment_group_detail, null);
        RoundedImageView groupImge = (RoundedImageView) view.findViewById(R.id.group_image);
        TextView groupname = (TextView) view.findViewById(R.id.groupname);
        TextView members = (TextView) view.findViewById(R.id.desc);
        articleList = view.findViewById(R.id.article_list_details);
        TextView desc = (TextView) view.findViewById(R.id.description);
        articlespinner = (ProgressBar)view.findViewById(R.id.articleprogressBar);
        articlespinner.setVisibility(View.GONE);
        joingroup = (Button) view.findViewById(R.id.join_group_btn);
        createpost = view.findViewById(R.id.create_post);
        Bundle bundle = this.getArguments();
//        System.out.println(bundle.getString("photo"));
        selectedGroupID = bundle.getString("groupId");
//        name.setText(extras.getString("name"));
//        description.setText(extras.getString("description"));
//        type.setText(extras.getString("description"));

        ButterKnife.bind(getActivity());
        members.setText("20k " + R.string.members);
        p = Picasso.with(getActivity());
        groupname.setText(bundle.getString("title"));
        desc.setText(bundle.getString("desc"));

        createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle sbundle = new Bundle();
                sbundle.putString("groupId",selectedGroupID);
                CreatePostBottomSheet bottomSheet = new CreatePostBottomSheet(calback);
                bottomSheet.setArguments(sbundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        joinedgroup=bundle.getString("status").equals("joined") ? "1" : "0";

        if (joinedgroup.equals("1")) {
            createpost.setVisibility(View.VISIBLE);
            joingroup.setText(getResources().getString(R.string.exitgroup));

            joingroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exitGroupMessage = "";
                    exitprogress = new ProgressDialog(getActivity());
                    exitprogress.setTitle(getResources().getString(R.string.loading));
                    exitprogress.setMessage(getResources().getString(R.string.exitgroup)+"...");
                    exitprogress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    exitprogress.show();
//                loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Join Group");
//                loadingDialog.show();
                    new ExitGroup().execute();
                }
            });
        } else {
            createpost.setVisibility(View.GONE);
            joingroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addGroupMessage = "";
                    joinprogress = new ProgressDialog(getActivity());
                    joinprogress.setTitle(getResources().getString(R.string.loading));
                    joinprogress.setMessage(getResources().getString(R.string.updating) + "...");
                    joinprogress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    joinprogress.show();
//                loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Join Group");
//                loadingDialog.show();
                    new JoinGroup().execute();
                }
            });
        }

        if (bundle.getString("photo").equals("") || bundle.getString("photo").equals("null")) {
            groupImge.setImageResource(R.drawable.default_head_icon);
        } else {
//            String photourl = "https://urban.network/pictures/202010/" + bundle.getString("photo");
            String photourl = "https://urban.network/" + bundle.getString("photo");
            Picasso.with(getActivity()).load(photourl).into(groupImge);
//            Picasso.with(getActivity()).load(photourl).transform(new ImageTrans_CircleTransform()).into(groupImge);
        }

//        mAdapter = new GroupAdapter(getActivity(),groupList);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        grouplistrecyclerView.setLayoutManager(mLayoutManager);
//        grouplistrecyclerView.setItemAnimator(new DefaultItemAnimator());
//        grouplistrecyclerView.setAdapter(mAdapter);
        articles = new ArrayList<>();
//        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Get articles");
//        loadingDialog.show();
        articlespinner.setVisibility(View.VISIBLE);
        new GetArticle().execute();

        return  view;
    }

    @Override
    public void onCreatePostClick() {
        articles = new ArrayList<>();
        articlespinner.setVisibility(View.VISIBLE);
//        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Get articles");
//        loadingDialog.show();
        new GetArticle().execute();
    }

//    @Override
//    public boolean onKey(View view, int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_UP) {
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                onBackButtonPressed();
//                return true;
//            }
//        }
//
//        return false;
//    }

    public void onBackButtonPressed() {
        Toast.makeText(getActivity(), "Tostt", Toast.LENGTH_LONG).show();
        //  getActivity().onBackPressed();
    }

    @Override
    public void onBackPressed() {
        GroupsFragment groupsFragment = new GroupsFragment();
        FragmentManager mFragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.addToBackStack(groupsFragment.getTag());
        ft.add(R.id.fragment_container, groupsFragment);
        ft.commit();
        //  getActivity().finish();
//        FragmentManager fragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
//        fragmentManager.popBackStack();
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        CreatePostBottomSheet bottomSheet = new CreatePostBottomSheet();
//        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(bottomSheet.getId());
//        // ProfileFragment mFragment = new ProfileFragment();
//        if (currentFragment instanceof CreatePostBottomSheet) {
//            currentFragment.onActivityResult(requestCode, resultCode, data);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private class GetArticle extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/article.php?";
            String parameters = "user=" +  selectedGroupID +
                    "&lang=" + locale_name +
                    "&page=" + page;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    Object json = new JSONTokener(jsonStr).nextValue();

                    if (json instanceof JSONObject) {
                        JSONObject contacts = new JSONObject(jsonStr);
                        //articlespinner.setVisibility(View.GONE);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                articlespinner.setVisibility(View.GONE);
                                try {
                                    Toast.makeText(getActivity(),
                                            contacts.getString("article"),
                                            Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (json instanceof JSONArray) {
                        JSONArray contacts = new JSONArray(jsonStr);

                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            String name = c.getString("name");
                            String show = c.getString("show");
                            String date = c.getString("date");
                            String user4 = c.getString("user4");
                            String file = c.getString("file");
                            String username = c.getString("username");
                            String userimage = c.getString("userimage");
                            String commentscount = c.getString("commentscount");
                            String likecount = c.getString("likecount");
                            int like = c.getInt("likes");
                            int page1 = c.getInt("id");
                            // page = page1;
                            articles.add(new ArticleList(name, show, date, username, "https://urban.network/pictures/" + file, userimage, String.valueOf(page1), likecount, commentscount, like));
                        }
                    }
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            articlespinner.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),
                                        e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articlespinner.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server while loading article",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            articlespinner.setVisibility(View.GONE);

            if (articles.size() > 0) {
                articleListAdapter = new ArticleListAdapter(getActivity(), articles, "details", p, selectedGroupID);
//                articleList.setAdapter(articleListAdapter);
//                articleList.setSelection(articles.size());
//                Helper.getListViewSize(articleList);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

                articleList.setLayoutManager(mLayoutManager);
                articleList.setItemAnimator(new DefaultItemAnimator());

                //todo before setAdapter
                articleList.setActivity(getActivity());

                //optional - to play only first visible video
                articleList.setPlayOnlyFirstVideo(true); // false by default

                //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
                articleList.setCheckForMp4(false); //true by default

                //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
                articleList.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

                articleList.setDownloadVideos(true); // false by default

                //extra - start downloading all videos in background before loading RecyclerView
//                List<String> urls = new ArrayList<>();
//                for (ArticleList object : articles) {
//                    if (object.getFile() != null && object.getFile().contains("http"))
//                        urls.add(object.getFile());
//                }
//                articleList.preDownload(urls);

                articleList.setAdapter(articleListAdapter);
            }
        }
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
            String parameters = "user=" + profile_id +
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            joinprogress.dismiss();
                        }
                    });
                }
            } else {
                // WeiboDialogUtils.closeDialog(loadingDialog);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinprogress.dismiss();
                        Toast.makeText(getActivity(),
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
            joinprogress.dismiss();
            // WeiboDialogUtils.closeDialog(loadingDialog);

            if(addGroupMessage.equals("success")) {
                joinedgroup = "0";
                createpost.setVisibility(View.VISIBLE);
                joingroup.setText(getResources().getString(R.string.exitgroup));
                joingroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exitGroupMessage = "";
                        exitprogress = new ProgressDialog(getActivity());
                        exitprogress.setTitle(getResources().getString(R.string.loading));
                        exitprogress.setMessage(getResources().getString(R.string.exitgroup) + "...");
                        exitprogress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        exitprogress.show();
//                        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Join Group");
//                        loadingDialog.show();
                                new ExitGroup().execute();
                    }
                });

                new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.join_group))
                    .setMessage(getResources().getString(R.string.join_group) + " " + getResources().getString(R.string.success))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
                    //  HomeActivity homeActivity = new HomeActivity();
            } else {
                new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.join_group))
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

    private class ExitGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/exitgroup.php?";
            String parameters = "user=" + profile_id + "&group=" + selectedGroupID;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject contacts = new JSONObject(jsonStr);
                    exitGroupMessage = contacts.getString("message");
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exitprogress.dismiss();
                        }
                    });
                }
            } else {
                // WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exitprogress.dismiss();
                        Toast.makeText(getActivity(),
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
            exitprogress.dismiss();
            // WeiboDialogUtils.closeDialog(loadingDialog);

            if (exitGroupMessage.equals("success")) {
                joinedgroup = "0";
                createpost.setVisibility(View.GONE);
                joingroup.setText(getResources().getString(R.string.join));
                joingroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addGroupMessage = "";
                        joinprogress = new ProgressDialog(getActivity());
                        joinprogress.setTitle(getResources().getString(R.string.loading));
                        joinprogress.setMessage(getResources().getString(R.string.updating)+"...");
                        joinprogress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        joinprogress.show();
//                        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Join Group");
//                        loadingDialog.show();
                        new JoinGroup().execute();
                    }
                });

                new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.exitgroup))
                    .setMessage(getResources().getString(R.string.exitgroup) + " " + getResources().getString(R.string.success))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
                    //  HomeActivity homeActivity = new HomeActivity();
            } else {
                new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.exitgroup))
                    .setMessage("You have already exit into this group!")
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

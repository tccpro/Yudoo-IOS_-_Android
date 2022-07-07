package jereme.urban_network_dating.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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
import jereme.urban_network_dating.Adapters.ArticleFileListAdapter;
import jereme.urban_network_dating.Adapters.ArticleListAdapter;
import jereme.urban_network_dating.Adapters.GroupListAdapter;
import jereme.urban_network_dating.FullscreenActivity;
import jereme.urban_network_dating.GroupDetailsActivity;
import jereme.urban_network_dating.List.ArticleList;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;
import static jereme.urban_network_dating.HomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.locale_name;

public class GroupFragment extends Fragment implements View.OnClickListener{
    ArrayList<GroupList> groups = new ArrayList<>();
    ArrayList<ArticleList> articles;
    ArrayList<ArticleList> articleFiles;

    Dialog loadingDialog;
    GroupListAdapter groupListAdapter;
    ArticleListAdapter articleListAdapter;
    ArticleFileListAdapter articleFileListAdapter;

    ListView groupList, articleList;
    ImageView ivSelectedUserPhoto, ivShowMessage, ivHideMessage;
    TextView tvSelectedUserName;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedGroupID = "", selectedGroupType = "";
    String uploadArticleMessage = "";
    Button searchGroupBtn, uploadArticleBtn, viewArticlesBtn;
    EditText etNewArticleTitle, etNewArticleMessage;
    String newArticleTitle, newAricleMessage;
    CheckBox cbpostArticleView;
    RelativeLayout rlPostArticleView, rlMessageGroup;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        searchGroupBtn = view.findViewById(R.id.search_group_btn);
        ivSelectedUserPhoto = view.findViewById(R.id.selected_member_photo);
        tvSelectedUserName = view.findViewById(R.id.selected_memeber_name);
        viewArticlesBtn = view.findViewById(R.id.view_articles_btn);
        groupList = view.findViewById(R.id.group_list);
        articleList = view.findViewById(R.id.article_list);
        uploadArticleBtn = view.findViewById(R.id.upload_new_article_btn);
        etNewArticleTitle = view.findViewById(R.id.et_new_article_title);
        etNewArticleMessage = view.findViewById(R.id.et_new_article_message);
        cbpostArticleView = view.findViewById(R.id.post_article_btn);
        rlPostArticleView = view.findViewById(R.id.rl_post_artitle_view);
        cbpostArticleView.setOnClickListener(this);
        ivShowMessage = view.findViewById(R.id.show_message_btn);
        ivHideMessage = view.findViewById(R.id.hide_message_btn);
        rlMessageGroup = view.findViewById(R.id.massageopen);

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

        articles = new ArrayList<>();
        articleFiles = new ArrayList<>();
        searchGroupBtn.setOnClickListener(this);
        uploadArticleBtn.setOnClickListener(this);
        Button viewArticle = view.findViewById(R.id.view_articles_btn);
        viewArticle.setOnClickListener(this);

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                GroupList listitem = groups.get(arg2);
                selectedUsername = listitem.getZoneTitle();
                selectedUserPhotoUrl = listitem.getPhoto();
                selectedGroupType = listitem.getAddress();
                selectedGroupID = listitem.getID();

                if (selectedUserPhotoUrl.equals("null")) {
                    ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                } else {
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                }
                tvSelectedUserName.setText(selectedUsername);

                if (cbpostArticleView.isChecked()) {
                    articles.clear();
                    articleList.setAdapter(null);
                    loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "loading Articles");
                    loadingDialog.show();
                    page = 0;
                    new GetArticle().execute();
                }
            }
        });

        articleList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (articleList.getCount() > 10) {
                        if (articleList.getLastVisiblePosition() == articleList.getAdapter().getCount() - 1 &&
                                articleList.getChildAt(articleList.getChildCount() - 1).getBottom() <= articleList.getHeight()) {
                            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Updating messages");
                            loadingDialog.show();
                            new GetArticle().execute();
                        }
                    }
                }

                return false;
            }
        });

        ivSelectedUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photourl = "";
                if (selectedUserPhotoUrl.equals("null")) {
                    photourl = "default";
                }  else {
                    photourl = "https://urban.network/" + selectedUserPhotoUrl;
                }

                Intent intent = new Intent(getActivity().getApplication(), FullscreenActivity.class);
                intent.putExtra("Image", photourl);
                startActivity(intent);
            }
        });

        if (groups.size() == 0) {
            groups.clear();
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
            loadingDialog.show();
            new GetGroup().execute();
        } else {
            groupListAdapter = new GroupListAdapter(getActivity(), groups);
            groupList.setAdapter(groupListAdapter);

            if (selectedUserPhotoUrl.equals("null")) {
                ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                tvSelectedUserName.setText("");
            } else {
                String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                tvSelectedUserName.setText(selectedUsername);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cbpostArticleView.isChecked()) {
            rlPostArticleView.setVisibility(View.VISIBLE);
        } else {
            rlPostArticleView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_group_btn) {
            groups.clear();
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
            loadingDialog.show();
            new GetGroup().execute();
        } else if(v.getId() == R.id.upload_new_article_btn) {
            uploadArticleMessage = "";
            if (!etNewArticleTitle.getText().toString().equals("") && !etNewArticleMessage.getText().toString().equals("")) {
                loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "uploading new Article");
                loadingDialog.show();
                newArticleTitle = etNewArticleTitle.getText().toString().replace(" ", " ");
                newAricleMessage = etNewArticleMessage.getText().toString().replace(" ", "");
                new UploadArticle().execute();
                articles.clear();
                new GetArticle().execute();
            } else {
                new AlertDialog.Builder(getActivity())
                    .setTitle("Upload Article")
                    .setMessage("Input title and message")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        } else if(v.getId() == R.id.view_articles_btn) {
            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("id",  selectedGroupID);
            intent.putExtra("name", selectedUsername);
            intent.putExtra("description", selectedGroupType);
            intent.putExtra("photo", selectedUserPhotoUrl);
            startActivity(intent);

//            articleFiles.clear();
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//            loadingDialog.show();
//            new GetFileArticle().execute();
        } else if(v.getId() == R.id.post_article_btn) {
            if (cbpostArticleView.isChecked()) {
                rlPostArticleView.setVisibility(View.VISIBLE);
            } else {
                rlPostArticleView.setVisibility(View.GONE);
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
                        String status = c.getString("status");

                        if (!photo.equals("null")) {
                            selectedGroupID = id;
                            selectedUsername = name;
                            selectedGroupType = type;
                            selectedUserPhotoUrl = photo;
                        }

                        groups.add(new GroupList(name,type, photo, id,status));
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server while loading group",
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

            if (groups.size() > 0) {
                groupListAdapter = new GroupListAdapter(getActivity(), groups);
                groupList.setAdapter(groupListAdapter);

                if (!selectedUserPhotoUrl.equals("")) {
                    tvSelectedUserName.setText(selectedUsername);
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                    new GetArticle().execute();
                }
            }
        }
    }

    private class GetArticle extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/article.php?";
            String parameters = "user=" + selectedGroupID +
                    "&lang=" + locale_name +
                    "&page=" + page;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
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
                        int page1 = c.getInt("id");
                        page = page1;
                        articles.add(new ArticleList(name,show, date, username, file,userimage,String.valueOf(page1),"1","2",1) );
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Couldn't get json from server while loading article", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if (articles.size() > 0) {
//                articleListAdapter = new ArticleListAdapter(getActivity(), articles, "");
//                articleList.setAdapter(articleListAdapter);
//                articleList.setSelection(articles.size());
            }
        }
    }

    private class UploadArticle extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/articleadd.php?";
            String parameters = "title=" + newArticleTitle +
                    "&message=" + newAricleMessage +
                    "&user=" + selectedGroupID +
                    "&user4=" + profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    uploadArticleMessage = jsonObj.getString("message");
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server while uploading article",
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

            if (uploadArticleMessage.equals("success")) {
                etNewArticleTitle.setText("");
                etNewArticleMessage.setText("");
                new AlertDialog.Builder(getActivity())
                    .setTitle("Upload Article")
                    .setMessage("New Article is added successfully")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            } else {
                new AlertDialog.Builder(getActivity())
                    .setTitle("Upload Article")
                    .setMessage("upload article fault!")
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

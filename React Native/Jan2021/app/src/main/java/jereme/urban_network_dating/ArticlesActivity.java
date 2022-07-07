package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.ArticleFileListAdapter;
import jereme.urban_network_dating.Adapters.GroupListAdapter;
import jereme.urban_network_dating.List.ArticleList;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

public class ArticlesActivity extends AppCompatActivity implements View.OnClickListener{
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedGroupID = "", selectedGroupType = "",selectedStatus = "";
    String selectedArticleTitle, selectedArticleMessage, selectedArticleFile, selectedArticleUserName, selectedArticleDate;
    ArrayList<GroupList> groups;
    ArrayList<ArticleList> articles;

    Dialog loadingDialog;
    GroupListAdapter groupListAdapter;
    ArticleFileListAdapter articleListAdapter;

    ListView groupList, articleList;
    ImageView ivSelectedUserPhoto, ivSelectedArticlePhoto;
    TextView tvSelectedUserName;
    String initialGroupID;
    Button startVideo, stopVideo, startAudio, stopAudio;

    private ProgressDialog bar;
    private String path = "";
    private MediaController ctlr;
    private VideoView videoview;
    MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        Bundle extras = getIntent().getExtras();
        initialGroupID = extras.getString("groupID");
        ImageView ivBack= findViewById(R.id.btn_back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivSelectedUserPhoto = findViewById(R.id.selected_member_photo);
        tvSelectedUserName = findViewById(R.id.selected_memeber_name);
        ivSelectedArticlePhoto = findViewById(R.id.selected_article_photo);

        videoview = findViewById(R.id.selected_article_video);
        videoview.setVisibility(View.GONE);

        startVideo = findViewById(R.id.start_video_btn);
        stopVideo = findViewById(R.id.stop_video_btn);
        startAudio = findViewById(R.id.start_audio_btn);
        stopAudio = findViewById(R.id.stop_audio_btn);
        startVideo.setOnClickListener(this);
        stopVideo.setOnClickListener(this);
        startAudio.setOnClickListener(this);
        stopAudio.setOnClickListener(this);
        startVideo.setVisibility(View.GONE);
        stopVideo.setVisibility(View.GONE);
        startAudio.setVisibility(View.GONE);
        stopAudio.setVisibility(View.GONE);

        groupList= findViewById(R.id.group_list);
        articleList = findViewById(R.id.article_list);
        groups = new ArrayList<>();
        articles = new ArrayList<>();

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
                    tvSelectedUserName.setText("");
                } else {
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(getApplicationContext()).load(photourl).into(ivSelectedUserPhoto);
                    tvSelectedUserName.setText(selectedUsername);
                }

                articles.clear();
                articleList.setAdapter(null);

                loadingDialog = WeiboDialogUtils.createLoadingDialog(ArticlesActivity.this, "loading Articles");
                loadingDialog.show();
                new GetFileArticle().execute();
            }
        });

        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ArticleList listitem = articles.get(arg2);

                selectedArticleTitle = listitem.getTitle();
                selectedArticleMessage = listitem.getMessage();
                selectedArticleFile = listitem.getFile();
                selectedArticleUserName = listitem.getUser();
                selectedArticleDate = listitem.getDate();

                String fileExtention = "image";

                if (selectedArticleFile.substring(selectedArticleFile.length() - 3, selectedArticleFile.length()).toLowerCase().equals("jpg")) {
                    fileExtention = "image";
                } else if (selectedArticleFile.substring(selectedArticleFile.length() - 4, selectedArticleFile.length()).toLowerCase().equals("jpeg")) {
                    fileExtention = "image";
                } else if (selectedArticleFile.substring(selectedArticleFile.length() - 3, selectedArticleFile.length()).toLowerCase().equals("png")) {
                    fileExtention = "image";
                } else if (selectedArticleFile.substring(selectedArticleFile.length() - 3, selectedArticleFile.length()).toLowerCase().equals("avi")) {
                    fileExtention = "video";
                } else if (selectedArticleFile.substring(selectedArticleFile.length() - 3, selectedArticleFile.length()).toLowerCase().equals("mp4")) {
                    fileExtention = "video";
                } else if (selectedArticleFile.substring(selectedArticleFile.length() - 3, selectedArticleFile.length()).toLowerCase().equals("mp3")) {
                    fileExtention = "Music(mp3)";
                    fileExtention = "audio";
                } else {
                    fileExtention = "";
                }

                if (fileExtention.equals("image")) {
                    String photourl = "https://urban.network/" + selectedArticleFile;
                    Picasso.with(ArticlesActivity.this).load(photourl).into(ivSelectedArticlePhoto);
                    ivSelectedArticlePhoto.setVisibility(View.VISIBLE);
                    videoview.setVisibility(View.GONE);
                    startVideo.setVisibility(View.GONE);
                    stopVideo.setVisibility(View.GONE);
                    startAudio.setVisibility(View.GONE);
                    stopAudio.setVisibility(View.GONE);
                } else if (fileExtention.equals("video")) {
                    ivSelectedArticlePhoto.setVisibility(View.GONE);
                    videoview.setVisibility(View.VISIBLE);
                    startVideo.setVisibility(View.VISIBLE);
                    stopVideo.setVisibility(View.VISIBLE);
                    startAudio.setVisibility(View.GONE);
                    stopAudio.setVisibility(View.GONE);
                } else if (fileExtention.equals("audio")) {
                    ivSelectedArticlePhoto.setVisibility(View.GONE);
                    videoview.setVisibility(View.GONE);
                    startVideo.setVisibility(View.GONE);
                    stopVideo.setVisibility(View.GONE);
                    startAudio.setVisibility(View.VISIBLE);
                    stopAudio.setVisibility(View.VISIBLE);
                }
            }
        });

        loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "loading Groups");
        loadingDialog.show();
        new GetGroup().execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_video_btn) {
            bar = new ProgressDialog(getApplicationContext());
            bar.setTitle("Connecting server");
            bar.setMessage("Please Wait... ");
            bar.show();

            if (bar.isShowing()) {
                path = "https://urban.network/" + selectedArticleFile;
                Uri uri = Uri.parse(path);
                videoview.setVideoURI(uri);
                videoview.start();
                ctlr = new MediaController(this);
                ctlr.setMediaPlayer(videoview);
                videoview.setMediaController(ctlr);
                videoview.requestFocus();
            }
            bar.dismiss();
        } else if (v.getId() == R.id.stop_video_btn) {

        } else if (v.getId() == R.id.start_audio_btn) {
            String AudioURL = "https://urban.network/" + selectedArticleFile;
            mediaplayer = new MediaPlayer();
            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaplayer.setDataSource(AudioURL);
                mediaplayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaplayer.start();
        } else if (v.getId() == R.id.stop_audio_btn) {
            mediaplayer.stop();
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
            String parameters = "user=" + HomeActivity.profile_id;
            String url = LoginActivity.base_url + sub_url + parameters;
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

                        if (initialGroupID.equals(id)) {
                            selectedGroupID = id;
                            selectedUsername = name;
                            selectedGroupType = type;
                            selectedUserPhotoUrl = photo;
                            selectedStatus = status;
                        }

                        groups.add(new GroupList(name, type, photo, id, status));
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
            if (groups.size() > 0) {
                groupListAdapter = new GroupListAdapter(getApplicationContext(), groups);
                groupList.setAdapter(groupListAdapter);

                if (!selectedUserPhotoUrl.equals("")) {
                    tvSelectedUserName.setText(selectedUsername);
                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                    Picasso.with(getApplicationContext()).load(photourl).into(ivSelectedUserPhoto);
                    articles.clear();
                    new GetFileArticle().execute();
                }
            }
        }
    }

    private class GetFileArticle extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/article.php?";
            String parameters = "user=" + selectedGroupID +
                    "&lang=" + LoginActivity.locale_name;
            String url = LoginActivity.base_url + sub_url + parameters;
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

                        if (!file.equals("null") && !file.equals("")) {
                            articles.add(new ArticleList(name, show, date, username, file, username, user4, "1", "2", 1));
                        }
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
            if (articles.size() > 0) {
                articleListAdapter = new ArticleFileListAdapter(getApplicationContext(), articles);
                articleList.setAdapter(articleListAdapter);
            } else {
                new GlideToast.makeToast(ArticlesActivity.this,"There is no movie or audio article");
            }
        }
    }
}

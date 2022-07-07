package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.MessageListAdapter;
import jereme.urban_network_dating.Adapters.PictureListAdapter;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static jereme.urban_network_dating.HomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;

public class AlbumPhotosActivity extends AppCompatActivity {

    ImageView ivPhoto, ivBack;
    public static final int PICK_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String imagepath = "";
    Dialog loadingDialog;
    private String upLoadServerUri = null;
    private int serverResponseCode = 0;

    ArrayList<MessageList> albums, pictures;
    ListView albumList, pictureList;
    MessageListAdapter messagelistapater;
    PictureListAdapter pictureListAdapter;

    EditText etNewAlbumName, etNewAlbumDescription;
    Button createAlbumBtn;
    String newalbumname, newalbumdescription;
    private String selectedAlbumName, selectedAlbumDescription, selectedAlbumPhoto;
    private String selectedPictureName, selectedPictureDescription, selectedPicturePhoto;
    TextView tvPictureCount;
    ImageView ivSelectedPicture;
    TextView tvSelectedPictureName, tvSelectedPictureDescription;
    Dialog dialog;
    String createNewAlbumeResponseMessage, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photos);

        ivBack = findViewById(R.id.btn_back);

        etNewAlbumName = findViewById(R.id.et_new_album_name);
        etNewAlbumDescription = findViewById(R.id.et_new_album_description);
        albumList = findViewById(R.id.album_list);
        pictureList = findViewById(R.id.picture_list);
        tvPictureCount = findViewById(R.id.tv_pictures_count);
        ivSelectedPicture = findViewById(R.id.iv_selected_picture);
        tvSelectedPictureName = findViewById(R.id.tv_selected_picture_name);
        tvSelectedPictureDescription = findViewById(R.id.tv_selected_picture_description);
        albums = new ArrayList<>();
        pictures = new ArrayList<>();

        ImageView ivBack= findViewById(R.id.btn_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pictureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MessageList listitem = pictures.get(arg2);
                selectedPictureName = listitem.getZoneTitle();
                selectedPictureDescription = listitem.getAddress();
                selectedPicturePhoto = listitem.getPhoto();

                String photourl = "https://urban.network/" + selectedPicturePhoto;
                Picasso.with(AlbumPhotosActivity.this).load(photourl).into(ivSelectedPicture);
                tvSelectedPictureName.setText(selectedPictureName);
                tvSelectedPictureDescription.setText(selectedPictureDescription);
            }
        });

        Bundle extras =getIntent().getExtras();
        user_id = profile_id;

        if (extras != null) {
            if (extras.getString("id") != null) {
                user_id = extras.getString("id");
            }
        }

        MessageList listitem = (MessageList) extras.getSerializable("model");
        selectedAlbumName = listitem.getZoneTitle();
        selectedAlbumDescription = listitem.getAddress();
        selectedAlbumPhoto = listitem.getPhoto();

        String temp = selectedAlbumPhoto.substring(9, selectedAlbumPhoto.length() - 9);
        String[] separeted = temp.split("/");
        selectedAlbumPhoto = separeted[0];
        ivSelectedPicture.setImageDrawable(null);
        tvSelectedPictureName.setText(selectedAlbumName);
        tvSelectedPictureDescription.setText(selectedAlbumDescription);

        //  tvPictureCount.setText(selectedAlbumName);
        loadingDialog = WeiboDialogUtils.createLoadingDialog(AlbumPhotosActivity.this, "Loading pictures");
        loadingDialog.show();
        pictures.clear();
        pictureList.setAdapter(null);
        new GetPicture().execute();

        pictureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MessageList listitem = pictures.get(arg2);
                selectedPictureName = listitem.getZoneTitle();
                selectedPictureDescription = listitem.getAddress();
                selectedPicturePhoto = listitem.getPhoto();

                String photourl = "https://urban.network/" + selectedPicturePhoto;
                Picasso.with(AlbumPhotosActivity.this).load(photourl).into(ivSelectedPicture);
                tvSelectedPictureName.setText(selectedPictureName);
                tvSelectedPictureDescription.setText(selectedPictureDescription);
            }
        });
    }

    private class GetPicture extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/albumphoto.php?";
            String parameters = "user=" + user_id +
                    "&photo=" + selectedAlbumPhoto;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);
                    pictures.clear();

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String name = c.getString("name");
                        String show = c.getString("show");
                        String photo = c.getString("photo");
                        pictures.add(new MessageList(name, show, photo) );
                        selectedPictureName = name;
                        selectedPicturePhoto = photo;
                        selectedPictureDescription = show;
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GlideToast.makeToast(AlbumPhotosActivity.this, getResources().getString(R.string.server_connect_error));
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);

            if (pictures.size() > 0) {
                pictureList.setAdapter(null);
                pictureListAdapter = new PictureListAdapter(getApplicationContext(), pictures);
                pictureList.setAdapter(pictureListAdapter);
                String photourl = "https://urban.network/" + selectedPicturePhoto;
                Picasso.with(AlbumPhotosActivity.this).load(photourl).into(ivSelectedPicture);
                tvSelectedPictureName.setText(selectedPictureName);
                tvSelectedPictureDescription.setText(selectedPictureDescription);
            } else {

            }
        }
    }
}

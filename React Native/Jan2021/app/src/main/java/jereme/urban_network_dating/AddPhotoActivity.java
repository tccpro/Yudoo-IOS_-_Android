package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import jereme.urban_network_dating.Adapters.MessageListAdapter;
import jereme.urban_network_dating.Adapters.PictureListAdapter;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

public class AddPhotoActivity extends AppCompatActivity implements View.OnClickListener{
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
    private String selectedPictureName, selectedPictureDescription, selectedPicturePhoto, user_id;
    TextView tvPictureCount;
    ImageView ivSelectedPicture;
    TextView tvSelectedPictureName, tvSelectedPictureDescription;
    Dialog dialog;
    Bundle extras;

    String createNewAlbumeResponseMessage, albumlength = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        Button uploadBtn, refreshBtn, createAlbumBtn;
        ivPhoto = findViewById(R.id.iv_photo);
        ivBack = findViewById(R.id.btn_back);
        uploadBtn = findViewById(R.id.upload_btn);
        refreshBtn = findViewById(R.id.refresh_btn);
        createAlbumBtn = findViewById(R.id.create_album_btn);
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

        ivPhoto.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        createAlbumBtn.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        extras = getIntent().getExtras();
        user_id = HomeActivity.profile_id;

        if (extras != null) {
            if (extras.getString("id") != null) {
                LinearLayout linearLayout = findViewById(R.id.upload_photo);
                linearLayout.setVisibility(View.GONE);
                user_id = extras.getString("id");
            }
        }

        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MessageList listitem = albums.get(arg2);
                Log.e("Rawed", String.valueOf(listitem));
                Intent intent = new Intent(getApplicationContext(), AlbumPhotosActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("model", listitem);
                intent.putExtras(b);
                intent.putExtra("id", user_id);
                //  intent.putParcelableArrayListExtra("listitem", listitem);
                startActivity(intent);

//                selectedAlbumName = listitem.getZoneTitle();
//                selectedAlbumDescription = listitem.getAddress();
//                selectedAlbumPhoto = listitem.getPhoto();
//
//                String temp = selectedAlbumPhoto.substring(9, selectedAlbumPhoto.length() - 9);
//                String[] separeted = temp.split("/");
//                selectedAlbumPhoto = separeted[0];
//                ivSelectedPicture.setImageDrawable(null);
//                tvSelectedPictureName.setText(selectedAlbumName);
//                tvSelectedPictureDescription.setText(selectedAlbumDescription);
//
//                tvPictureCount.setText(selectedAlbumName);
//                loadingDialog = WeiboDialogUtils.createLoadingDialog(AddPhotoActivity.this, "Loading pictures");
//                loadingDialog.show();
//                pictures.clear();
//                pictureList.setAdapter(null);
//                new GetPicture().execute();
            }
        });

//        pictureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                MessageList listitem = pictures.get(arg2);
//                selectedPictureName = listitem.getZoneTitle();
//                selectedPictureDescription = listitem.getAddress();
//                selectedPicturePhoto = listitem.getPhoto();
//
//                String photourl = "https://urban.network/" + selectedPicturePhoto;
//                Picasso.with(AddPhotoActivity.this).load(photourl).into(ivSelectedPicture);
//                tvSelectedPictureName.setText(selectedPictureName);
//                tvSelectedPictureDescription.setText(selectedPictureDescription);
//            }
//        });

        loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Albums");
        loadingDialog.show();
        albums.clear();
        selectedAlbumName = "";
        new GetAlbum().execute();
    }

    private void getImageFromAlbum() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            startActivityForResult(chooserIntent, PICK_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                imagepath = getPath(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivPhoto.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri.substring(sourceFileUri.lastIndexOf("/") + 1);
        fileName = selectedAlbumPhoto + "__" + fileName;
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
                    new GlideToast.makeToast(AddPhotoActivity.this, getResources().getString(R.string.select_image)).show();
                }
            });

            return 0;
        } else {
            try {
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
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
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
                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //new GetPicture().execute();
                        }
                    });
                }

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                ex.printStackTrace();
            } catch (Exception e) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
            }

            return serverResponseCode;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_photo) {
            int newHeight = 200, newWidth = 200;
//          ivPhoto.requestLayout();
//          ivPhoto.getLayoutParams().height = newHeight;
//          ivPhoto.getLayoutParams().width = newWidth;
//          ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
            getImageFromAlbum();
        } else if (view.getId() == R.id.refresh_btn) {
            albums.clear();
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    loadingDialog.show();
                    new GetPicture().execute();
                }
            };
            handler.postDelayed(r, 1000);
        } else if (view.getId() == R.id.create_album_btn) {
            popup_create_album();
        } else if (view.getId() == R.id.upload_btn) {
            upLoadServerUri = "https://urban.network/Api/upload/photoupload.php";

            if (selectedAlbumName.equals("")) {
                new AlertDialog.Builder(AddPhotoActivity.this)
                    .setTitle(getResources().getString(R.string.upload_photo))
                    .setMessage(getResources().getString(R.string.not_create_album))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            } else {
                loadingDialog.show();

                new Thread(new Runnable() {
                    public void run() {
                        uploadFile(imagepath);
                    }
                }).start();
            }
        } else if (view.getId() == R.id.btn_back) {
            onBackPressed();
        }
    }

    public void popup_create_album() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_album);
        TextView cancelBtn, okBtn;
        final EditText newAlbumName, newAlbumDescription;
        newAlbumName = dialog.findViewById(R.id.et_new_album_name);
        newAlbumDescription = dialog.findViewById(R.id.et_new_album_description);
        okBtn = dialog.findViewById(R.id.btn_ok);
        cancelBtn = dialog.findViewById(R.id.btn_cancel);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newalbumname = newAlbumName.getText().toString();
                newalbumdescription = newAlbumDescription.getText().toString();

                if (!newAlbumName.getText().toString().equals("") && newAlbumName.getText().toString() != null) {
                    loadingDialog = WeiboDialogUtils.createLoadingDialog(AddPhotoActivity.this, "Creating new album");
                    loadingDialog.show();
                    new CreateAlbum().execute();
                    dialog.dismiss();
                } else {
                    new GlideToast.makeToast(AddPhotoActivity.this, getResources().getString(R.string.name_require));
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
            String parameters = "user=" + user_id;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String name = c.getString("name");
                        String show = c.getString("show");
                        String photo = c.getString("photo");
                        albums.add(new MessageList(name, show, photo) );
                        selectedAlbumName = name;
                        selectedAlbumDescription = show;
                        selectedAlbumPhoto = photo;
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GlideToast.makeToast(AddPhotoActivity.this, getResources().getString(R.string.server_connect_error));
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);

            if (albums.size() > 0) {
                messagelistapater = new MessageListAdapter(getApplicationContext(), albums);
                albumList.setAdapter(messagelistapater);
                tvPictureCount.setText(selectedAlbumName);
                String temp = selectedAlbumPhoto.substring(9,selectedAlbumPhoto.length() - 9);
                String[] separeted = temp.split("/");
                selectedAlbumPhoto = separeted[0];

//                loadingDialog = WeiboDialogUtils.createLoadingDialog(AddPhotoActivity.this, "Loading pictures");
//                loadingDialog.show();
//                pictures.clear();
//                pictureList.setAdapter(null);
//                new GetPicture().execute();
            }
        }
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
            String url = LoginActivity.base_url + sub_url + parameters;
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
                        new GlideToast.makeToast(AddPhotoActivity.this, getResources().getString(R.string.server_connect_error));
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
                Picasso.with(AddPhotoActivity.this).load(photourl).into(ivSelectedPicture);
                tvSelectedPictureName.setText(selectedPictureName);
                tvSelectedPictureDescription.setText(selectedPictureDescription);
            } else {

            }
        }
    }

    private class CreateAlbum extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/albumCreate.php?";
            String parameters = "user=" + user_id + "&name=" + newalbumname + "&show=" + newalbumdescription;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        createNewAlbumeResponseMessage = c.getString("message");
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GlideToast.makeToast(AddPhotoActivity.this, getResources().getString(R.string.server_connect_error));
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);

            if (createNewAlbumeResponseMessage.equals("success")) {
                new GlideToast.makeToast(AddPhotoActivity.this, getResources().getString(R.string.success));
                albums.clear();
                new GetAlbum().execute();
            } else {
                new AlertDialog.Builder(AddPhotoActivity.this)
                    .setTitle("Create Album")
                    .setMessage("Can't create new Album")
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

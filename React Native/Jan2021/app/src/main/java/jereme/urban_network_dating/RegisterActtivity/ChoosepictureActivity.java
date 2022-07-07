package jereme.urban_network_dating.RegisterActtivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;
import jereme.urban_network_dating.fragments.BottomsheetFragment;

import static jereme.urban_network_dating.LoginActivity.base_url;

public class ChoosepictureActivity extends AppCompatActivity {
    Button continues;
    private String imagepath = "";
    public static int CAMERA_REQUEST = 100;
    Dialog loadingDialog;
    String uploadPhotoName = "";
    private String upLoadServerUri = null;
    private int serverResponseCode = 0;
    String uploadPhotoNamewithoutExtention = "", profileimage;
    CircleImageView profileimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosepicture);
        continues = (Button) findViewById(R.id.continues);
        profileimg = (CircleImageView) findViewById(R.id.profile_image);
        loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Authenticating");

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomsheetFragment bottomSheet = new BottomsheetFragment();
                bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadServerUri = "https://urban.network/Apinew/upload/upload.php";
                new ChoosepictureActivity.GetLatestPhotoID().execute();

                if (imagepath != "") {
                    Intent intent = new Intent(ChoosepictureActivity.this, AddmorePictureActivity.class);
                    intent.putExtra("imagepath", imagepath);
                    startActivity(intent);
                } else {
                    new GlideToast.makeToast(ChoosepictureActivity.this, ""+(getResources().getString(R.string.select_photo)));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (reqCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profileimg.setImageBitmap(photo);
            //imgUser.setImageBitmap(decodeFile(selectedImagePath));
            try {
                File file = createImageFile();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (Exception e ) {
                Log.e("error","Failed to convert images");
            }
        } else if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                imagepath = getPath(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileimg.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(reqCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg", /* suffix */
                storageDir   /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagepath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        String path = "";

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private class GetLatestPhotoID extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "photos/profilePicture.php";
            String parameters = "";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String id = jsonObj.getString("id");
                    int a = Integer.parseInt(id);
                    a = a + 1;
                    uploadPhotoName = String.valueOf(a);
                    profileimage = String.valueOf(a);
                    uploadPhotoNamewithoutExtention = uploadPhotoName;
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error photoID: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
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
            if (imagepath.length() > 0) {
                new Thread(new Runnable() {
                    public void run() {
                        SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(ChoosepictureActivity.this);
                        SharedPreferences.Editor editor = saveUser.edit();
                        editor.putString("storedphoto", imagepath);
                        editor.putString("profilepic", profileimage);
                        editor.commit();
                        uploadFile(imagepath);
                    }
                }).start();
            } else {
                Toast.makeText(ChoosepictureActivity.this, "Select Photo", Toast.LENGTH_SHORT).show();
                WeiboDialogUtils.closeDialog(loadingDialog);
            }
        }
    }

    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        String extention = fileName.substring(fileName.length() - 5, fileName.length());
        String[] separated = extention.split("\\.");
        fileName = uploadPhotoName + "." + separated[1];
        uploadPhotoName = fileName;

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
                    Toast.makeText(ChoosepictureActivity.this, "Source File not exist :" + imagepath, Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
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
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
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

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ChoosepictureActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    /* newUsername = etUsername.getText().toString();
                    newEmail = etEmail.getText().toString();
                    newPassword = etPassword.getText().toString();
                    newPicture = uploadPhotoNamewithoutExtention;
                    newPaypal = etPaypal.getText().toString();
                    newBirthday = birthday.getText().toString();
                    newHomeTown = etHomeTown.getText().toString();
                    new RegisterActivity.GetRegister().execute(); */
                }

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ChoosepictureActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ChoosepictureActivity.this, "Upload fault", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return serverResponseCode;
        }
    }
}

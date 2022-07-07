package jereme.urban_network_dating.RegisterActtivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;

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
import java.util.HashMap;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static jereme.urban_network_dating.LoginActivity.base_url;

public class AddmorePictureActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
//    private String imagepath = "";
    Button continues;
    int pic = 0;
    Dialog loadingDialog;
    String uploadPhotoName = "";
    private String upLoadServerUri = null;
    private int serverResponseCode = 0;
    String uploadPhotoNamewithoutExtention = "";
    HashMap<Integer, String> imagepath;
    RoundedImageView pic1, pic2, pic3, pic4, pic5, pic6;
    String moreImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmore_picture);
        TextView skipbtn = (TextView) findViewById(R.id.btnskip);
        File imgFile = new File(getIntent().getStringExtra("imagepath"));

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.image1);
            ImageView myImage1 = (ImageView) findViewById(R.id.image2);
            myImage.setImageBitmap(myBitmap);
            myImage1.setImageBitmap(myBitmap);
        }

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic = 1;
                //getImageFromAlbum();
                Intent intent = new Intent(AddmorePictureActivity.this, YourInterestActivity.class);
                startActivity(intent);
            }
        });

        continues =(Button) findViewById(R.id.continues);
        pic1 = (RoundedImageView) findViewById(R.id.image1);
        pic2 = (RoundedImageView) findViewById(R.id.image2);
        pic3 = (RoundedImageView) findViewById(R.id.pic3);
        pic4 = (RoundedImageView) findViewById(R.id.pic4);
        pic5 = (RoundedImageView) findViewById(R.id.pic5);
        pic6 = (RoundedImageView) findViewById(R.id.pic6);
//        pic1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pic = 1;
//                getImageFromAlbum();
//            }
//        });

        imagepath = new HashMap<Integer, String>();

        // Adding object in HashMap
        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              pic = 2;
              getImageFromAlbum();
            }
        });

        pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic = 3;
                getImageFromAlbum();
            }
        });
        pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic = 4;
                getImageFromAlbum();
            }
        });
        pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic = 5;
                getImageFromAlbum();
            }
        });
        pic6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic = 6;
                getImageFromAlbum();
            }
        });

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadServerUri = "https://urban.network/Apinew/upload/upload.php";
                new GetLatestPhotoID().execute();
//                Intent intent = new Intent(AddmorePictureActivity.this,YourInterestActivity.class);
//                startActivity(intent);
            }
        });
    }

    private void getImageFromAlbum() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                String image =getPath(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(AddmorePictureActivity.this);
//                if (pic == 1) {
//                    SharedPreferences.Editor editor = saveUser.edit();
//                    editor.putString("storedphoto1", imagepath);
//                    editor.commit();
//                    pic1.setImageBitmap(selectedImage);
//                }
//                else
                if (pic == 2) {
                     imagepath.put(0, image);
                    pic2.setImageBitmap(selectedImage);
                } else if (pic == 3) {
                    imagepath.put(1, image);
                    pic3.setImageBitmap(selectedImage);
                } else if (pic == 4) {
                    imagepath.put(2, image);
                    pic4.setImageBitmap(selectedImage);
                } else if (pic == 5) {
                    imagepath.put(3, image);
                    pic5.setImageBitmap(selectedImage);
                } else if (pic == 6) {
                    imagepath.put(4, image);
                    pic6.setImageBitmap(selectedImage);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
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

    ProgressDialog dialog;
    private class GetLatestPhotoID extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddmorePictureActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.show();
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

                    //uploadPhotoNamewithoutExtention = String.valueOf(a);
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error photoID: " + e.getMessage(),
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (imagepath.size() > 0) {
                new Thread(new Runnable() {
                    public void run() {
                        int prograss = 100 / imagepath.size();

                        for (int i = 0; i < 5; i++) {
                            if (imagepath.get(i) != null) {
                                publishProgress(prograss);
                                int a = Integer.parseInt(uploadPhotoName);
                                a = a + 1;
                                uploadPhotoName = String.valueOf(a);
                                moreImage = uploadPhotoName + "," + moreImage;
                                uploadFile(imagepath.get(i), uploadPhotoName, i);
                            }

                            if (i==4) {
                                dialog.dismiss();

                                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(AddmorePictureActivity.this);
                                SharedPreferences.Editor editor = saveUser.edit();
                                editor.putString("morephoto", moreImage);
                                editor.commit();
                                Intent intent =new Intent(AddmorePictureActivity.this,YourInterestActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                }).start();
            } else {
                Toast.makeText(AddmorePictureActivity.this, "Select Photo", Toast.LENGTH_SHORT).show();
                WeiboDialogUtils.closeDialog(loadingDialog);
            }
        }
    }

    public int uploadFile(String sourceFileUri, String upload_PhotoName, final int ind) {
        String fileName = sourceFileUri;
        String extention = fileName.substring(fileName.length() - 5, fileName.length());
        String[] separated = extention.split("\\.");
        fileName = upload_PhotoName + "." + separated[1];
        //  upload_PhotoName = fileName;

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
                    Toast.makeText(AddmorePictureActivity.this, "Source File not exist :"+ imagepath, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddmorePictureActivity.this, "File Upload Complete."+ind+1, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddmorePictureActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AddmorePictureActivity.this, "Upload fault", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;
        }
    }
}

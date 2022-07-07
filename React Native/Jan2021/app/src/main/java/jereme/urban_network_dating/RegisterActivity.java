package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

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
import java.util.Calendar;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView ivPhoto;
    Context context;
    Dialog loadingDialog;
    public static final int PICK_IMAGE = 1;
    private String upLoadServerUri = null;
    private String imagepath = "";
    private int serverResponseCode = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    String register_success = "0";
    private EditText etUsername, etEmail, etPassword, etPaypal, etHomeTown;
    RadioButton rbMale, rbFemale;
    String gender = "Male";
    String uploadPhotoName = "";
    String uploadPhotoNamewithoutExtention = "";
    String newUsername;
    String newEmail;
    String newPassword;
    String newPicture;
    String newBirthday;
    String newPaypal;
    String newHomeTown;
    //DatePickerDialog picker;
    EditText birthday;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        Button registerBtn = findViewById(R.id.btn_register);
        etUsername = findViewById(R.id.et_new_username);
        etEmail = findViewById(R.id.et_new_email);
        etPassword = findViewById(R.id.et_new_password);
        etPaypal = findViewById(R.id.et_new_paypal);
        etHomeTown = findViewById(R.id.et_new_hometown);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        ivPhoto = findViewById(R.id.iv_photo);

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.et_new_username, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.et_new_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.et_new_password, "^(?=.*[0-9])(?=.*[a-z]).{8,15}", R.string.passworderror);
        //  awesomeValidation.addValidation(this, R.id.editText1, "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", R.string.nameerror);
        birthday = (EditText) findViewById(R.id.editText1);
        birthday.setInputType(InputType.TYPE_NULL);

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Calendar cldr = Calendar.getInstance();
                cldr.add(Calendar.YEAR, -18);
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                final int year = cldr.get(Calendar.YEAR);
                View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.datepicker_dialog, null);
                final DatePicker picker = (DatePicker) view.findViewById(R.id.datePicker1);
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setView(view)
                       .setCancelable(false)
                       .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (year > picker.getYear()) {
                                     birthday.setText(picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear());
                                     newBirthday = birthday.getText().toString();
                                     dialog.cancel();
                                } else {
                                    new GlideToast.makeToast(RegisterActivity.this, "18+ " + (getResources().getString(R.string.only_show_profiles_with_picture)).substring(0, 5));
                                }
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.birthday));
                alert.show();
            }
        });

        registerBtn.setOnClickListener(this);
        ivPhoto.setOnClickListener(this);
        rbMale.setOnClickListener(this);
        rbFemale.setOnClickListener(this);
    }

    private void getImageFromAlbum() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                getIntentAlbum();
            }
        } else {
            getIntentAlbum();
        }
    }

    private void getIntentAlbum() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                imagepath =getPath(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivPhoto.setImageBitmap(selectedImage);
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
        String path = "";

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_photo) {
            int newHeight = 200, newWidth = 200;
            ivPhoto.requestLayout();
            ivPhoto.getLayoutParams().height = newHeight;
            ivPhoto.getLayoutParams().width = newWidth;
            ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
            getImageFromAlbum();
        } else if (v.getId() == R.id.btn_register) {
            if (awesomeValidation.validate()) {
                if (newBirthday != null && newBirthday !="") {
                    if (imagepath != null && imagepath !="") {
                            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Authenticating");
                            loadingDialog.show();
                            upLoadServerUri = "https://urban.network/Api/upload/upload.php";
                            new GetLatestPhotoID().execute();
                    } else {
                        new GlideToast.makeToast(RegisterActivity.this, getResources().getString(R.string.choose_photo));
                    }
                } else {
                    new GlideToast.makeToast(RegisterActivity.this, getResources().getString(R.string.birthday) + " " + (getResources().getString(R.string.email_require)).substring(5, 14));
                }
            }
        } else if (v.getId() == R.id.rb_male) {
            gender = "Male";
        } else if (v.getId() == R.id.rb_female) {
            gender = "Female";
        }
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
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String id = jsonObj.getString("id");
                    int a = Integer.parseInt(id);
                    a = a + 1;
                    uploadPhotoName = String.valueOf(a);
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
                        uploadFile(imagepath);
                    }
                }).start();
            } else {
                Toast.makeText(RegisterActivity.this, "Select Photo", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegisterActivity.this, "Source File not exist :" + imagepath, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    newUsername = etUsername.getText().toString();
                    newEmail = etEmail.getText().toString();
                    newPassword = etPassword.getText().toString();
                    newPicture = uploadPhotoNamewithoutExtention;
                    newPaypal = etPaypal.getText().toString();
                    newBirthday = birthday.getText().toString();
                    newHomeTown = etHomeTown.getText().toString();
                    new GetRegister().execute();
                }

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "Upload fault", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return serverResponseCode;
        }
    }

    private class GetRegister extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/registerUser.php?";
            String newGender = gender;
            String parameters = "name=" +  newUsername +
                    "&email=" + newEmail +
                    "&password=" + newPassword +
                    "&picture=" + newPicture +
                    "&paypalemail=" + newPaypal +
                    "&hometown=" + newHomeTown +
                    "&birthday=" + newBirthday +
                    "&gender=" + newGender;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String message = jsonObj.getString("message");

                    if (message.equals("New User was created.")) {
                        register_success = "1";
                    } else {
                        register_success = "0";
                    }
                } catch (final JSONException e) {
                    register_success = "0";
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
            WeiboDialogUtils.closeDialog(loadingDialog);

            if (register_success.equals("1")) {
                LoginActivity.static_email = etEmail.getText().toString();
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                SharedPreferences.Editor editor = saveUser.edit();
                editor.putString("storedUsername", etEmail.getText().toString());
                editor.putString("storedPassword", etPassword.getText().toString());
                editor.commit();
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "register is fault", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

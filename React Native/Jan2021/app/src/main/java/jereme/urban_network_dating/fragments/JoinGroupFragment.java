package jereme.urban_network_dating.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import java.util.Calendar;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.GroupListAdapter;
import jereme.urban_network_dating.FullscreenActivity;
import jereme.urban_network_dating.GroupDetailsActivity;
import jereme.urban_network_dating.HomeActivity;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static android.app.Activity.RESULT_OK;
import static jereme.urban_network_dating.HomeActivity.profile_birthday;
import static jereme.urban_network_dating.HomeActivity.profile_currentCity;
import static jereme.urban_network_dating.HomeActivity.profile_dating;
import static jereme.urban_network_dating.HomeActivity.profile_hometown;
import static jereme.urban_network_dating.HomeActivity.profile_id;
import static jereme.urban_network_dating.HomeActivity.profile_information;
import static jereme.urban_network_dating.HomeActivity.profile_name;
import static jereme.urban_network_dating.HomeActivity.profile_paypalEmail;
import static jereme.urban_network_dating.HomeActivity.profile_photoUrl;
import static jereme.urban_network_dating.HomeActivity.profile_picture;
import static jereme.urban_network_dating.HomeActivity.profile_searchName;
import static jereme.urban_network_dating.HomeActivity.profile_status;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.static_email;
import static jereme.urban_network_dating.RegisterActivity.PICK_IMAGE;

public class JoinGroupFragment extends Fragment implements View.OnClickListener{
    EditText searchName;
    ArrayList<GroupList> groups = new ArrayList<>();
    Dialog loadingDialog;
    GroupListAdapter grouplistapater;
    ListView groupList;
    ImageView ivSelectedUserPhoto;
    TextView tvSelectedGroupName, tvSelectedGroupDescription;
    String selectedGroupName = "", selectedGroupPhotoUrl = "", selectedGroupID = "", selectedGroupDescription = "";
    String addGroupMessage = "", addMessageResoponse = "";
    EditText etMessageTitle, etMessageDescription;
    Button searchGroupBtn;
    String searc_name = "";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group, container, false);

        searchGroupBtn = view.findViewById(R.id.search_group_btn);
        ivSelectedUserPhoto = view.findViewById(R.id.selected_group_photo);
        tvSelectedGroupName = view.findViewById(R.id.selected_group_name);
        tvSelectedGroupDescription = view.findViewById(R.id.selected_group_description);
        searchName = view.findViewById(R.id.et_searchname);
        groupList = view.findViewById(R.id.group_list);
        searchGroupBtn.setOnClickListener(this);
        Button joinGroupBtn = view.findViewById(R.id.join_group_btn);
        joinGroupBtn.setOnClickListener(this);

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                GroupList listitem = groups.get(arg2);
                selectedGroupName = listitem.getZoneTitle();
                selectedGroupPhotoUrl = listitem.getPhoto();
                selectedGroupID = listitem.getID();
                tvSelectedGroupName.setText(selectedGroupName);
                tvSelectedGroupDescription.setText(selectedGroupDescription);

                if (selectedGroupPhotoUrl.equals("null") || selectedGroupPhotoUrl.equals("")) {
                    ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                } else {
                    String photourl = "https://urban.network/" + selectedGroupPhotoUrl;
                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                }
            }
        });

        if (groups.size() == 0) {
            groups.clear();
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
            loadingDialog.show();
            new SearchGroup().execute();
        } else {
            grouplistapater = new GroupListAdapter(getActivity(), groups);
            groupList.setAdapter(grouplistapater);

            if (selectedGroupPhotoUrl.equals("null") || selectedGroupPhotoUrl.equals("")) {
                ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);
                tvSelectedGroupName.setText("");
            } else {
                String photourl = "https://urban.network/" + selectedGroupPhotoUrl;
                Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                tvSelectedGroupName.setText(selectedGroupName);
                tvSelectedGroupDescription.setText(selectedGroupDescription);
            }
        }

        ivSelectedUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photourl ="";
                if (selectedGroupPhotoUrl.equals("null") || selectedGroupPhotoUrl.equals("")) {
                    photourl ="default";
                }  else {
                    photourl = "https://urban.network/" + selectedGroupPhotoUrl;
                }

                Intent intent = new Intent(getActivity().getApplication(), FullscreenActivity.class);
                intent.putExtra("Image", photourl);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_group_btn) {
            groups.clear();
            searc_name = searchName.getText().toString();
            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
            loadingDialog.show();
            new FilterGroup().execute();
        } else if (v.getId() == R.id.join_group_btn) {
            Intent intent =new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("id",selectedGroupID);
            intent.putExtra("name",selectedGroupName);
            intent.putExtra("description",selectedGroupDescription);
            intent.putExtra("photo",selectedGroupPhotoUrl);
            startActivity(intent);
//            addGroupMessage = "";
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Join Group");
//            loadingDialog.show();
//            new JoinGroup().execute();
        }
    }

    private class SearchGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/searchgroup.php?";
            String parameters = "";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String type = c.getString("type");
                        String type4 = c.getString("type4");
                        String photo = c.getString("photo");
                        String status = c.getString("status");

                        if (!photo.equals("null")) {
                            selectedGroupID = id;
                            selectedGroupName = name;
                            selectedGroupDescription = type;
                            selectedGroupPhotoUrl = photo;
                        }

                        groups.add(new GroupList(name,type, photo,id,status) );
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server for search group",
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
                grouplistapater = new GroupListAdapter(getActivity(), groups);
                groupList.setAdapter(grouplistapater);

                if (!selectedGroupPhotoUrl.equals("")) {
                    tvSelectedGroupName.setText(selectedGroupName);
                    tvSelectedGroupDescription.setText(selectedGroupDescription);
                    String photourl = "https://urban.network/" + selectedGroupPhotoUrl;
                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                }
            }
        }
    }

    private class FilterGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/groupbyName.php?";
            String parameters = "&name=" + searc_name;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String type = c.getString("type");
                        String type4 = c.getString("type4");
                        String photo = c.getString("photo");
                        String status = c.getString("status");

                        if (!photo.equals("null")) {
                            selectedGroupID = id;
                            selectedGroupName = name;
                            selectedGroupDescription = type;
                            selectedGroupPhotoUrl = photo;
                        }
                        groups.add(new GroupList(name,type, photo,id,status) );
                    }
                } catch (final JSONException e) {

                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server for search group",
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
                grouplistapater = new GroupListAdapter(getActivity(), groups);
                groupList.setAdapter(grouplistapater);

                if (!selectedGroupPhotoUrl.equals("")) {
                    tvSelectedGroupName.setText(selectedGroupName);
                    tvSelectedGroupDescription.setText(selectedGroupDescription);
                    String photourl = "https://urban.network/" + selectedGroupPhotoUrl;
                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                }
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
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            WeiboDialogUtils.closeDialog(loadingDialog);

            if (addGroupMessage.equals("success")) {
                new AlertDialog.Builder(getActivity())
                    .setTitle("Join Group")
                    .setMessage("joined successfully")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
                HomeActivity homeActivity = new HomeActivity();
            } else {
                new AlertDialog.Builder(getActivity())
                    .setTitle("Join Group")
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

    public static class InfoFragment extends Fragment {
        EditText etProfileStatus, etProfileName, etProfileSearchName, etProfilePaypaEmail, etProfileHometown, etProfileCurrentCity;
        EditText etProfilePersonalInformatioin;
        RadioButton rbProfileDatingOn, rbProfileDatingOff;
        ImageView updatePhotoImageView;
        DatePickerDialog picker;
        EditText birthday;
        private String imagepath = "";
        private int success_flag = 0;
        private int serverResponseCode = 0;
        private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
        Dialog loadingDialog;
        int profile_temp_dating = 0;
        String uploadPhotoName = "";
        String upadateBirthday = "";
        String uploadPhotoNamewithoutExtention = "";
        int updated_photo = 0;
        private String upLoadServerUri = null;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_info, container, false);
//            Button updatePhoto = view.findViewById(R.id.profile_update_photo_btn);
            Button updateInformationBtn = view.findViewById(R.id.update_information_btn);
            etProfileStatus = view.findViewById(R.id.et_profile_status);
            etProfileName = view.findViewById(R.id.et_profile_name);
            etProfileSearchName = view.findViewById(R.id.et_profile_searchName);
            etProfilePaypaEmail = view.findViewById(R.id.et_profile_paypalEmail);
            etProfileHometown = view.findViewById(R.id.et_profile_hometown);
            etProfileCurrentCity = view.findViewById(R.id.et_profile_currentCity);
            etProfilePersonalInformatioin = view.findViewById(R.id.et_profile_personalInformation);
            rbProfileDatingOn = view.findViewById(R.id.rb_profile_dating_on);
            rbProfileDatingOff = view.findViewById(R.id.rb_profile_dating_off);
            etProfileStatus.setText(profile_status);
            etProfileName.setText(profile_name);
            etProfileSearchName.setText(profile_searchName);
            etProfilePaypaEmail.setText(profile_paypalEmail);
            etProfileHometown.setText(profile_hometown);
            etProfileCurrentCity.setText(profile_currentCity);
            etProfilePersonalInformatioin.setText(profile_information);
            birthday = (EditText) view.findViewById(R.id.birthday_date);
            birthday.setInputType(InputType.TYPE_NULL);

            birthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cldr = Calendar.getInstance();
                    cldr.add(Calendar.YEAR, -18);
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    final int year = cldr.get(Calendar.YEAR);
                    // date picker dialog

                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.datepicker_dialog, null);
                    final DatePicker picker=(DatePicker) view.findViewById(R.id.datePicker1);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(view)
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (year > picker.getYear()) {
                                    birthday.setText(picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear());
                                    upadateBirthday = birthday.getText().toString();
                                } else {
                                    new GlideToast.makeToast(getActivity(), "18+ " + (getResources().getString(R.string.only_show_profiles_with_picture)).substring(0, 5));
                                }
                            }
                        });

                    AlertDialog alert = builder.create();
                    alert.setTitle(getResources().getString(R.string.birthday));
                    alert.show();
    //                picker = new DatePickerDialog(getActivity(),
    //                        new DatePickerDialog.OnDateSetListener() {
    //                            @Override
    //                            public void onDateSet(DatePicker view, int dyear, int monthOfYear, int dayOfMonth) {
    //                                if (year > dyear) {
    //                                    birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + dyear);
    //                                    upadateBirthday = birthday.getText().toString();
    //                                } else {
    //                                    new GlideToast.makeToast(getActivity(), "18+ " + (getResources().getString(R.string.only_show_profiles_with_picture)).substring(0, 5));
    //                                }
    //                            }
    //                        }, year, month, day);
    //                picker.show();
                }
            });

            if (profile_birthday != null && profile_birthday != "") {
                birthday.setText(profile_birthday);
                upadateBirthday = birthday.getText().toString();
            }
            if (profile_dating != null && profile_dating.equals("0")) {
                rbProfileDatingOn.setChecked(true);
            } else {
                rbProfileDatingOn.setChecked(true);
            }

            updatePhotoImageView = view.findViewById(R.id.update_photo_imageview);

            if (profile_photoUrl != null && profile_photoUrl != "") {
                String photourl = "https://urban.network/" + profile_photoUrl;
                Picasso.with(getActivity()).load(photourl).into(updatePhotoImageView);
            }

            updatePhotoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newHeight = 200, newWidth = 200;
                    updatePhotoImageView.requestLayout();
                    updatePhotoImageView.getLayoutParams().height = newHeight;
                    updatePhotoImageView.getLayoutParams().width = newWidth;
                    updatePhotoImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    getImageFromAlbum();
                }
            });

            updateInformationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profile_status = etProfileStatus.getText().toString();
//                    profile_temp_status = profile_status.replace(" ", "__");
                    profile_name = etProfileName.getText().toString();
                    profile_searchName = etProfileSearchName.getText().toString();
                    profile_paypalEmail = etProfilePaypaEmail.getText().toString();
                    profile_hometown = etProfileHometown.getText().toString();
                    profile_currentCity = etProfileCurrentCity.getText().toString();
                    profile_information = etProfilePersonalInformatioin.getText().toString();

                    if (rbProfileDatingOn.isChecked()) {
                        profile_temp_dating = 1;
                    } else {
                        profile_temp_dating = 0;
                    }

                    loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Updating");
                    loadingDialog.show();
                    upLoadServerUri = "https://urban.network/Api/upload/upload.php";

                    if (updated_photo == 1) new GetLatestPhotoID().execute();

                    Handler handler;
                    handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new updateInformation().execute();
                        }
                    }, 1002);

                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }, 4002);
                }
            });

            return view;
        }

        private void getImageFromAlbum() {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
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
        public void onActivityResult(int reqCode, int resultCode, Intent data) {
            super.onActivityResult(reqCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                try {
                    updated_photo = 1;
                    final Uri imageUri = data.getData();
                    imagepath =getPath(imageUri);
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    updatePhotoImageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }

        public String getPath(Uri uri) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
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
                        uploadPhotoNamewithoutExtention = uploadPhotoName;
                    } catch (final JSONException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"Json parsing error photoID: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Couldn't get json from server get latest id",
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
                    Toast.makeText(getActivity(), "Select Photo", Toast.LENGTH_SHORT).show();
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

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "Source File not exist :"+ imagepath, Toast.LENGTH_SHORT).show();
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
                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    if (serverResponseCode == 200) {

                    }

                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    ex.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    e.printStackTrace();
    //                getActivity().runOnUiThread(new Runnable() {
    //                    public void run() {
    //                        Toast.makeText(getActivity(), "Upload fault", Toast.LENGTH_SHORT).show();
    //                    }
    //                });
                }
                return serverResponseCode;
            }
        }

        private class updateInformation extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();
                String sub_url = "users/updateProfile.php?";
                String s = null;

                if (updated_photo == 1) {
                    s = uploadPhotoNamewithoutExtention;
                } else {
                    s= profile_picture;
                }

                String parameters = "email=" +  static_email +
                        "&status=" + profile_status +
                        "&id=" + profile_id +
                        "&name=" + profile_name +
                        "&search_name=" + profile_searchName +
                        "&picture=" + s +
                        "&paypalemail=" + profile_paypalEmail +
                        "&hometown=" + profile_hometown +
                        "&birthday=" + upadateBirthday +
                        "&current_city=" + profile_currentCity +
                        "&dating=" + profile_temp_dating;
                String url = base_url + sub_url + parameters;
                String jsonStr = sh.makeServiceCall(url);

                if (jsonStr != null) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        success_flag = 1;
                    } catch (final JSONException e) {
                        WeiboDialogUtils.closeDialog(loadingDialog);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"Json parsing error in update: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                WeiboDialogUtils.closeDialog(loadingDialog);
                if (success_flag == 1) {
                    new update_show_picture_user().execute();
                } else {
                    Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private class update_show_picture_user extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();
                String sub_url = "upload/updatePictureUser.php?";
                String parameters = "id=" + profile_id;
                String url = base_url + sub_url + parameters;
                String jsonStr = sh.makeServiceCall(url);

                if (jsonStr != null) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        success_flag = 1;
                    } catch (final JSONException e) {
                        WeiboDialogUtils.closeDialog(loadingDialog);
                    }
                } else {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                WeiboDialogUtils.closeDialog(loadingDialog);
                if (success_flag == 1) {
                    new update_show_picture_user().execute();
                } else {
                    Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

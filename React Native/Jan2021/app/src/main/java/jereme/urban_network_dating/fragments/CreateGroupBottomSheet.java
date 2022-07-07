package jereme.urban_network_dating.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.makeramen.roundedimageview.RoundedImageView;

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

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.RegisterActtivity.ChoosepictureActivity;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.NewHomeActivity.profile_id;

public class CreateGroupBottomSheet extends BottomSheetDialogFragment {

    private String upLoadServerUri = null, selectedAlbumPhoto = "1", uploadArticleMessage;
    private int serverResponseCode = 0;
    private String imagepath = "", filename, categoriname, name, descrip;
    Dialog loadingDialog;
    EditText groupname, description;
    Spinner categari;
    RoundedImageView groupimage;
    Button creategroup;
    ProgressDialog createprogress;
    private AwesomeValidation awesomeValidation;
    OnCreateGroupClickListener onCreateGroupClickListener;

    public CreateGroupBottomSheet(OnCreateGroupClickListener groupCallback) {
        this.onCreateGroupClickListener = groupCallback;
    }

    public CreateGroupBottomSheet() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_create_group, container, false);
        categari = v.findViewById(R.id.categoris);
        groupname = v.findViewById(R.id.group_names);
        description = v.findViewById(R.id.descriptions);
        groupimage = v.findViewById(R.id.group_image);
        creategroup = v.findViewById(R.id.creategroup);
        //  String birthday = categari.getText().toString() + "/" + categari.getText().toString() + "/" + description.getText().toString();
        awesomeValidation = new AwesomeValidation(BASIC);
        //  awesomeValidation.addValidation(categari, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", getResources().getString(R.string.categorirequired));
        awesomeValidation.addValidation(groupname, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", getResources().getString(R.string.groupname));
        awesomeValidation.addValidation(description, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", getResources().getString(R.string.description));
        String[] items = new String[]{ "Movie & Tv", "Games", "Animals", "News", "Educations"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        categari.setAdapter(adapter);

        groupimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomsheetFragment bottomSheet = new BottomsheetFragment();
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        //  awesomeValidation.addValidation(getActivity(),R.id.categori, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);

        creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    createprogress = new ProgressDialog(getActivity());
                    createprogress.setTitle(getResources().getString(R.string.loading));
                    createprogress.setMessage(getResources().getString(R.string.create_group) + "...");
                    createprogress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    createprogress.show();
//                    loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//                    loadingDialog.show();
                    uploadArticleMessage = "";
                    name = groupname.getText().toString();
                    descrip = description.getText().toString();
                    categoriname = categari.getSelectedItem().toString();
                    upLoadServerUri = "https://urban.network/Apinew/upload/photoupload.php";
                    //  loadingDialog.show();

                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(imagepath);
                        }
                    }).start();
                }
            }
        });

        ImageView close = v.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ChoosepictureActivity.CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            final Bitmap photo = (Bitmap) data.getExtras().get("data");
//            upLoadServerUri = "https://urban.network/Api/upload/photoupload.php";
//            loadingDialog.show();
//            new Thread(new Runnable() {
//                public void run() {
//                    uploadFile(photo);
//                }
//            }).start();
            groupimage.setImageBitmap(photo);
            //  imgUser.setImageBitmap(decodeFile(selectedImagePath));

            try {
                File file = createImageFile();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (Exception e) {
                Log.e("error","Failed to convert images");
            }
        } else if (resultCode == getActivity().RESULT_OK) {
            final Uri imageUri = data.getData();
            imagepath = getPath(imageUri);
            InputStream imageStream = null;

            try {
                imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            groupimage.setImageBitmap(selectedImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        String path = "";
        cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        } else {
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getActivity().getContentResolver().query(uri,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            path = c.getString(columnIndex);
            c.close();
        }

        cursor.close();
        return path;
    }

    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri.substring(sourceFileUri.lastIndexOf("/") + 1);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = timeStamp + "__" + fileName;
        filename = fileName;
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
            createprogress.dismiss();
            //  WeiboDialogUtils.closeDialog(loadingDialog);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    new GlideToast.makeToast(getActivity(), getResources().getString(R.string.select_image)).show();
                }
            });

            return 0;
        } else{
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
                    //WeiboDialogUtils.closeDialog(loadingDialog);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "file uploaded", Toast.LENGTH_LONG).show();
                            new CreateGroup().execute();
                        }
                    });
                }

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                createprogress.dismiss();
                //  WeiboDialogUtils.closeDialog(loadingDialog);
                ex.printStackTrace();
            } catch (Exception e) {
                createprogress.dismiss();
                //  WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
            }
            return serverResponseCode;
        }
    }

    private class CreateGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/creategroup.php?";
            String parameters = "category=" + categoriname +
                               "&name=" + name +
                               "&description=" + descrip +
                               "&image=" + filename +
                               "&user=" + profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    uploadArticleMessage = jsonObj.getString("message");
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createprogress.dismiss();
                            Toast.makeText(getActivity(),
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                //  WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createprogress.dismiss();
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
            createprogress.dismiss();
            //  WeiboDialogUtils.closeDialog(loadingDialog);

            if (uploadArticleMessage.equals("success")) {
                groupname.setText("");
                description.setText("");
                dismiss();
                Toast.makeText(getActivity(), getResources().getString(R.string.newgroupsuccess), Toast.LENGTH_LONG).show();
                onCreateGroupClickListener.onCreateGroupClick();
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Create group")
//                        .setMessage("New Group is added successfully")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
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

    public interface OnCreateGroupClickListener {
        void onCreateGroupClick();
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}

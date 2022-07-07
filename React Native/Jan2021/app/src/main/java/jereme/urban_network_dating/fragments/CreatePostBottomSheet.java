package jereme.urban_network_dating.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

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
import java.util.concurrent.TimeUnit;

import jereme.urban_network_dating.API.HttpHandler;

import jereme.urban_network_dating.R;

import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.NewHomeActivity.profile_id;


public class CreatePostBottomSheet extends BottomSheetDialogFragment implements RecordigAudioFragment.OnCreatePhotoClickListener {
    EditText title, description;
    Button createpost;
    Dialog loadingDialog;
    String newArticleTitle, newAricleMessage, uploadArticleMessage, selectedGroupID, type, filename;
    private AwesomeValidation awesomeValidation;
    private String upLoadServerUri = null, selectedAlbumPhoto = "1";
    private int serverResponseCode = 0;
    private String imagepath = "";
    RoundedImageView postimage;
    VideoView mVideoView;
    MediaRecorder recorder;
    File audiofile = null;
    static final String TAG = "MediaRecording";
    Button startButton, stopButton;
    RecordigAudioFragment.OnCreatePhotoClickListener audiocallback;
    OnCreatePostClickListener createPostClickListener;

    RelativeLayout audiostart;
    private ImageButton forwardbtn, backwardbtn, pausebtn, playbtn;
    private MediaPlayer mPlayer;
    private TextView songName, startTime, songTime;
    private SeekBar songPrgs;
    private static int oTime = 0, sTime = 0, eTime = 0, fTime = 5000, bTime = 5000;
    private Handler hdlr = new Handler();
    String audiopath = "";
    ProgressDialog createprogress;

    public CreatePostBottomSheet(OnCreatePostClickListener callback) {
        this.createPostClickListener = callback;
    }

    public CreatePostBottomSheet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_post_create, container, false);
        mVideoView = (VideoView) v.findViewById(R.id.video_view);
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        postimage = (RoundedImageView) v.findViewById(R.id.post_image);
        CardView uploadfile = (CardView) v.findViewById(R.id.upload_file);
        audiostart = (RelativeLayout) v.findViewById(R.id.audiostart);
        createpost = v.findViewById(R.id.createpost);
        backwardbtn = (ImageButton) v.findViewById(R.id.btnBackward);
        forwardbtn = (ImageButton) v.findViewById(R.id.btnForward);
        playbtn = (ImageButton) v.findViewById(R.id.btnPlay);
        pausebtn = (ImageButton) v.findViewById(R.id.btnPause);
        startTime = (TextView) v.findViewById(R.id.txtStartTime);
        songTime = (TextView) v.findViewById(R.id.txtSongTime);
        songPrgs = (SeekBar) v.findViewById(R.id.sBar);
        songPrgs.setClickable(false);
        pausebtn.setEnabled(false);
        audiocallback = this;
        Bundle bundle = this.getArguments();
        // audioOpen();

        if (bundle != null)
            selectedGroupID = bundle.getString("groupId");

        //  String birthday = categari.getText().toString() + "/" + categari.getText().toString() + "/" + description.getText().toString();
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(title, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", getResources().getString(R.string.categorirequired));
        awesomeValidation.addValidation(description, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", getResources().getString(R.string.groupname));
        // awesomeValidation.addValidation(description, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$",  getResources().getString(R.string.description));
        // awesomeValidation.addValidation(getActivity(),R.id.categori, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);

        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer = new MediaPlayer();

                try {
                    mPlayer.setDataSource(audiopath);
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mPlayer.start();
                Toast.makeText(getActivity(), "Playing Audio", Toast.LENGTH_SHORT).show();

                eTime = mPlayer.getDuration();
                sTime = mPlayer.getCurrentPosition();

                if (oTime == 0) {
                    songPrgs.setMax(eTime);
                    oTime = 1;
                }

                songTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(eTime),
                        TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));
                startTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(sTime),
                        TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
                songPrgs.setProgress(sTime);
                hdlr.postDelayed(UpdateSongTime, 100);
                pausebtn.setEnabled(true);
                playbtn.setEnabled(false);
            }
        });

        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
                pausebtn.setEnabled(false);
                playbtn.setEnabled(true);
                Toast.makeText(getActivity(), "Pausing Audio", Toast.LENGTH_SHORT).show();
            }
        });

        forwardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((sTime + fTime) <= eTime) {
                    sTime = sTime + fTime;
                    mPlayer.seekTo(sTime);
                } else {
                    Toast.makeText(getActivity(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                }

                if (!playbtn.isEnabled()) {
                    playbtn.setEnabled(true);
                }
            }
        });

        backwardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((sTime - bTime) > 0) {
                    sTime = sTime - bTime;
                    mPlayer.seekTo(sTime);
                } else {
                    Toast.makeText(getActivity(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                }

                if (!playbtn.isEnabled()) {
                    playbtn.setEnabled(true);
                }
            }
        });

        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  takePhoto();
                CameraPostBottomsheetFragment bottomSheet = new CameraPostBottomsheetFragment(audiocallback);
                bottomSheet.show( getActivity().getSupportFragmentManager(), "ModalBottomSheet" );
            }
        });

        createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadArticleMessage = "";
                upLoadServerUri = "https://urban.network/Apinew/upload/photoupload.php";
                //  upLoadServerUri = "https://urban.network/Apinew/upload/upload.php";

                if (awesomeValidation.validate()) {
                    createprogress = new ProgressDialog(getActivity());
                    createprogress.setTitle(getResources().getString(R.string.loading));
                    createprogress.setMessage(getResources().getString(R.string.create_post) + "...");
                    createprogress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    createprogress.show();
//                    loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "uploading new Article");
//                    loadingDialog.show();

                    newArticleTitle = title.getText().toString().replace(" ", "");
                    newAricleMessage = description.getText().toString().replace(" ", "");

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

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            sTime = mPlayer.getCurrentPosition();
            startTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime),
                    TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))) );
            songPrgs.setProgress(sTime);
            hdlr.postDelayed(this, 100);
        }
    };

    public void audioOpen() {
        BottomsheetFragment bottomSheet = new BottomsheetFragment();
        bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet" );
//        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
//        View layout = layoutInflater.inflate(R.layout.audio_popup, null);
//        ListView messagerecyclerView = layout.findViewById(R.id.messages_view);
////      messageListAdapter = new CommentsAdapder(mContext,commentlist);
////      messagerecyclerView.setAdapter(messageListAdapter);
//        startButton = (Button) layout.findViewById(R.id.start);
//        stopButton = (Button) layout.findViewById(R.id.start_audio_btn);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(layout);
//
//        AlertDialog alert = builder.create();
//        alert.getWindow().setBackgroundDrawableResource(R.drawable.circleimg);
//        // alert.setTitle(getResources().getString(R.string.birthday));
//        alert.show();
    }

    public void takePhoto() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //  new Intent("android.media.action.IMAGE_CAPTURE");
//        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//        Uri.fromFile(photo));

        //  imageUri = Uri.fromFile(photo);
        CreatePostBottomSheet.this.startActivityForResult(intent, 101);
    }

    @Override
    public void onCreatePhotoClick(String path) {
        audiostart.setVisibility(View.VISIBLE);
        type = "audio";
        postimage.setVisibility(View.GONE);
        mVideoView.setVisibility(View.GONE);
        imagepath = path;
        Toast.makeText(getActivity(), path, Toast.LENGTH_LONG).show();
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
                               "&ftype=" + type +
                               "&filename=" + filename +
                               "&user4=" + profile_id;
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
                        }
                    });
                    //  WeiboDialogUtils.closeDialog(loadingDialog);
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
            // WeiboDialogUtils.closeDialog(loadingDialog);

            if (uploadArticleMessage.equals("success")) {
                Toast.makeText(getActivity(), "New Article is added successfully", Toast.LENGTH_LONG).show();
                dismiss();
                title.setText("");
                description.setText("");
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Upload Article")
//                        .setMessage("New Article is added successfully")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
                createPostClickListener.onCreatePostClick();
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == getActivity().RESULT_OK) {
            type = "image";
            postimage.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.GONE);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            postimage.setImageBitmap(photo);
            //imgUser.setImageBitmap(decodeFile(selectedImagePath));

            try {
                File file = createImageFile();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (Exception e) {
                Log.e("error","Failed to convert images");
            }
        } else if (requestCode == 102 && resultCode == getActivity().RESULT_OK) {
            type = "video";
            postimage.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
            final Uri videoUri = data.getData();
            mVideoView.setVideoURI(videoUri);
            mVideoView.setMediaController(new MediaController(getActivity()));
            mVideoView.requestFocus();
            // mVideoView.start();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    mVideoView.start();
                }
            });
//            imagepath = videoUri.getPath();
            imagepath = getRealPathFromURI(videoUri);
            //  imgUser.setImageBitmap(decodeFile(selectedImagePath));
//            try {
//                File file = createImageFile();
//                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
//                os.close();
//            } catch (Exception e ) {
//                Log.e("error","Failed to convert images");
//            }
        } else if (requestCode == 103 && resultCode == getActivity().RESULT_OK) {
            type = "video";
            postimage.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
            final Uri videoUri = data.getData();
            imagepath = getRealPathFromURI(videoUri);
        } else if (resultCode == getActivity().RESULT_OK) {
            try {
                final Uri uri = data.getData();
                ContentResolver cr = getActivity().getContentResolver();
                String mime = cr.getType(uri);
                System.out.println(mime);

                if (mime.split("/")[0].equals("image")) {
                    type = "image";
                    postimage.setVisibility(View.VISIBLE);
                    mVideoView.setVisibility(View.GONE);
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    postimage.setImageBitmap(selectedImage);
                    imagepath = getPath(uri);
                } else {
                    type = "video";
                    postimage.setVisibility(View.GONE);
                    mVideoView.setVisibility(View.VISIBLE);
//                    final Uri videoUri = data.getData();
                    mVideoView.setVideoURI(uri);
                    mVideoView.setMediaController(new MediaController(getActivity()));
                    mVideoView.requestFocus();
//                    mVideoView.start();

                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                            mVideoView.start();
                        }
                    });

                    // OI FILE Manager
                    String filemanagerstring = uri.getPath().toString();
                    imagepath = filemanagerstring;

                    // MEDIA GALLERY
                    String selectedVideoPath = getVideoPath(uri);
                    if (selectedVideoPath != null) {
                        imagepath = selectedVideoPath;
                    }
                   // imagepath = getVideoPath(uri);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
                storageDir   /* directory */);

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

    public String getVideoPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
                    new GlideToast.makeToast(getActivity(),getResources().getString(R.string.select_image)).show();
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
                    //  WeiboDialogUtils.closeDialog(loadingDialog);
//                    getActivity().runOnUiThread(new Runnable() {
//                        public void run() {
                            //new GetPicture().execute();
                            new UploadArticle().execute();
//                        }
//                    });
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

    public interface OnCreatePostClickListener {
        void onCreatePostClick();
    }

    @Override
    public int getTheme()  {
        return R.style.AppBottomSheetDialogTheme;
    }
}

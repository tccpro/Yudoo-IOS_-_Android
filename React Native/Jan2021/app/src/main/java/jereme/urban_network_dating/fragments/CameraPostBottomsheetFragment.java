package jereme.urban_network_dating.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.io.File;
import java.util.Date;
import jereme.urban_network_dating.R;


public class CameraPostBottomsheetFragment extends BottomSheetDialogFragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 104;
    private Uri fileUri;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 102;
    private static final int VIDEO_CAPTURE = 102;
    private static Context context;
    // private static final int CAMERA_REQUEST = 188;
    String imgPath = "";
    RecordigAudioFragment.OnCreatePhotoClickListener calbk;
    Uri videoUri;

    CameraPostBottomsheetFragment(RecordigAudioFragment.OnCreatePhotoClickListener calbk) {
        this.calbk=calbk;
    }

    public CameraPostBottomsheetFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_post_takecamera, container, false);
        context = getActivity();
        LinearLayout algo_button = v.findViewById(R.id.algo_button);
        LinearLayout video_button = v.findViewById(R.id.video_btn);
        //  LinearLayout audio_button = v.findViewById(R.id.audio_btn);
        LinearLayout course_button = v.findViewById(R.id.course_button);

        algo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Toast.makeText(getActivity(),
                        "Algorithm Shared", Toast.LENGTH_SHORT)
                        .show();*/
                if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(cameraIntent, 101);
                    //  ProfileFragment.class.startActivityForResult(cameraIntent, 100);
                }
                dismiss();
            }
        });

//        audio_button.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//
//                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    getActivity().startActivityForResult(intent, 103);
//                } else {
//                    Toast.makeText(getActivity(), "No sound record application found", Toast.LENGTH_SHORT).show();
//                }
//                  dismiss();
//                RecordigAudioFragment bottomSheetDialogFragment = new RecordigAudioFragment(calbk);
//                Bundle bun = new Bundle();
//                bun.putString("data", "contItems");
//                bottomSheetDialogFragment.audioOpen();
//                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//                dismiss();

//                if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
//                } else {
//                    //  Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//                    Intent soundRecorderIntent = new Intent(); // create intent
//                    soundRecorderIntent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION); // set action
//                    getActivity().startActivityForResult(soundRecorderIntent, 103);
//                    //  getActivity().startActivityForResult(intent, 103); // intent and requestCode of 1
//                }
//            }
//        });

        video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            getActivity().startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                        }
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        getActivity().startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                    }
                }

                dismiss();
            }
        });

        course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(getActivity(),
                        "Course Shared", Toast.LENGTH_SHORT)
                        .show();*/
                getImageFromAlbum();
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "audio permission granted", Toast.LENGTH_LONG).show();
                Intent soundRecorderIntent = new Intent();  // create intent
                soundRecorderIntent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);  // set action
                getActivity().startActivityForResult(soundRecorderIntent, 103);
            } else {
                Toast.makeText(getActivity(), "audio permission denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode ==  MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "audio permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                getActivity().startActivityForResult(cameraIntent, 101);
            } else {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isRecordAudioPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                // put your code for Version>=Marshmallow
                return true;
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(getActivity(), "App required access to audio", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},MY_PERMISSIONS_RECORD_AUDIO);
                return false;
            }
        } else {
            // put your code for Version < Marshmallow
            return true;
        }
    }

    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }

    public String getImagePath() {
        return imgPath;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    private void getImageFromAlbum() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getIntentAlbum();
            }
        } else {
            getIntentAlbum();
        }
    }

    private void getIntentAlbum() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("video/* image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("video/* image/*");
        //  pickIntent.setType("video/* image/* audio/*");
        //  Intent chooserIntent = Intent.createChooser(getIntent, "Select Image or Video");
        //  chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        getActivity().startActivityForResult(pickIntent, 2);
    }
}
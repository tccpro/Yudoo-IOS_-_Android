package jereme.urban_network_dating.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import jereme.urban_network_dating.RegisterActtivity.ChoosepictureActivity;

public class BottomsheetFragment  extends BottomSheetDialogFragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private int GALLERY_REQUEST = 1;
    String imgPath = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_take_photos, container, false);
        LinearLayout algo_button = v.findViewById(R.id.algo_button);
        LinearLayout course_button = v.findViewById(R.id.course_button);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            ChoosepictureActivity.CAMERA_REQUEST = 150;
            GALLERY_REQUEST = 5;
        } else {
            ChoosepictureActivity.CAMERA_REQUEST = 100;
            GALLERY_REQUEST = 1;
        }

//        System.out.println(bundle.getString("photo"));
//        selectedUserID = bundle.getString("profileId");

        algo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Toast.makeText(getActivity(),
                        "Algorithm Shared", Toast.LENGTH_SHORT)
                        .show();*/
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(cameraIntent, ChoosepictureActivity.CAMERA_REQUEST);
                    // ProfileFragment.class.startActivityForResult(cameraIntent, 100);
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

        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                getActivity().startActivityForResult(cameraIntent, ChoosepictureActivity.CAMERA_REQUEST);
            } else {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        getActivity().startActivityForResult(chooserIntent, GALLERY_REQUEST);
    }
}
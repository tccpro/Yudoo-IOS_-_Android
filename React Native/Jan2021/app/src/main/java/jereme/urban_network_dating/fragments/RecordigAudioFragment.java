package jereme.urban_network_dating.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jereme.urban_network_dating.R;

public class RecordigAudioFragment extends BottomSheetDialogFragment {
    MediaRecorder recorder;
    File audiofile = null;
    static final String TAG = "MediaRecording";
    Button startButton,stopButton,playButton;
    MediaRecorder mRecorder;
    MediaPlayer mediaPlayer;
    Button start, stop, play, stop_play;
    String FileName = "";
    File outfile;
    private boolean isRecording = false;
    public static final int request_code = 1000;
    OnCreatePhotoClickListener calback;

    RecordigAudioFragment(OnCreatePhotoClickListener calbk){
        calback = calbk;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.audio_popup, container, false);
        startButton = (Button) v.findViewById(R.id.start);
        stopButton = (Button) v.findViewById(R.id.stop);
        playButton = (Button) v.findViewById(R.id.play);

        if (checkPermissionFromDevice()) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopRecording();
                }
            });

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        playAudio();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            requestPermissionFromDevice();
        }
        return v;
    }

    public void startRecording() throws IOException {
        isRecording = true;
        startButton.setEnabled(false);
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
        //Creating file
        File dir = Environment.getExternalStorageDirectory();

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "Aud_" + timeStamp + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File audio = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".3gp", /* suffix */
                    storageDir   /* directory */
            );
            audiofile = audio;
            //  audiofile = File.createTempFile("sound", ".3gp", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }

        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    public void stopRecording() {
        if (recorder != null) {
            if (isRecording) {
                playButton.setEnabled(true);
                stopButton.setEnabled(false);
                //stopping recorder
                recorder.stop();
                recorder.release();
                //after stopping the recorder, create the sound file and add it to media library.
                addRecordingToMediaLibrary();
                isRecording = false;
            } else {
                mediaPlayer.release();
                mediaPlayer = null;
                startButton.setEnabled(true);
            }
        }
    }

    public void playAudio () throws IOException {
        playButton.setEnabled(false);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audiofile.getAbsolutePath());
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    protected void addRecordingToMediaLibrary() {
        //creating content values of size 4
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
        //creating content resolver and storing it in the external content uri
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        calback.onCreatePhotoClick(audiofile.getAbsolutePath());
//        getActivity().startActivityForResult(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri),103);

       dismiss();
        //sending broadcast message to scan the media file so that it can be available
//        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
//        Toast.makeText(getActivity(), "Added File " + newUri, Toast.LENGTH_LONG).show();
    }

    private void requestPermissionFromDevice() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO},
                request_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case request_code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    Toast.makeText(getActivity(),"permission granted...",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),"permission denied...",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int storage_permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recorder_permssion = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
        return storage_permission == PackageManager.PERMISSION_GRANTED && recorder_permssion == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int getTheme()  {
        return R.style.AppBottomSheetDialogTheme;
    }

    public interface OnCreatePhotoClickListener {
        void onCreatePhotoClick(String path);
    }
}

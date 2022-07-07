package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullscreenActivity extends AppCompatActivity {
    ImageView ivSelectedUserPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ivSelectedUserPhoto = findViewById(R.id.fullsize);
        String image = getIntent().getStringExtra("Image");
        if(image.equals("default")) {
            ivSelectedUserPhoto.setImageResource(R.drawable.default_head_icon);

        } else {
            String photourl = image;

            Picasso.with(getApplicationContext()).load(photourl).into(ivSelectedUserPhoto);

        }
        ImageView ivBack= findViewById(R.id.btn_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jereme.urban_network_dating.Utils.ImageTrans_CircleTransform;

public class DatingDetailsActivity extends AppCompatActivity {
    String name,searchname,age,gender,birthday,email,hometown,currentcity,profile_photoUrl;
    TextView uname,sname,uage,ugender,ubirthday,uemail,uhometown,ucurentcity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating_details);
        ImageView ivBack = findViewById(R.id.btn_back);
        uname = findViewById(R.id.username);
        sname = findViewById(R.id.searchname);
        uage = findViewById(R.id.age);
        ugender = findViewById(R.id.gender);
        ubirthday = findViewById(R.id.birthday);
      // uemail = findViewById(R.id.email);
        uhometown = findViewById(R.id.homedown);
        ucurentcity = findViewById(R.id.current_city);


        Bundle extras = getIntent().getExtras();
         name = extras.getString("name");
         searchname = extras.getString("searchname");
         age = extras.getString("age");
         gender = extras.getString("gender");
         birthday = extras.getString("birthday");
         email = extras.getString("email");
         hometown = extras.getString("hometown");
         currentcity =extras.getString("currentcity");
         profile_photoUrl = extras.getString("photo");
         uname.setText(name);
         sname.setText(searchname);
         uage.setText(age);
         ugender.setText(gender);
         ubirthday.setText(birthday);
      //   uemail.setText(email);
         uhometown.setText(hometown);
         ucurentcity.setText(currentcity);
         ImageView  photoImageView = findViewById(R.id.user_photo);
        if(profile_photoUrl.equals("") || extras.getString("photo").equals("null")){
            photoImageView.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + profile_photoUrl;
            Picasso.with(DatingDetailsActivity.this).load(photourl)
                    .transform(new ImageTrans_CircleTransform()).into(photoImageView);
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }




}

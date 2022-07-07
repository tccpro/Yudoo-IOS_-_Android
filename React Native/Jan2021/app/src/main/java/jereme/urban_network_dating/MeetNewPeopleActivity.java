package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import jereme.urban_network_dating.RegisterActtivity.EmailActivity;


public class MeetNewPeopleActivity extends AppCompatActivity {
  Button login,create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_new_people);
        login = (Button) findViewById(R.id.login_btn1);
        create = (Button) findViewById(R.id.creates);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(MeetNewPeopleActivity.this,LoginActivity.class);

                startActivity(intent);

            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Intent intent =new Intent(MeetNewPeopleActivity.this, EmailActivity.class);
                //Intent intent =new Intent(MeetNewPeopleActivity.this, YourInterestActivity.class);
                startActivity(intent);

            }

        });

    }
}

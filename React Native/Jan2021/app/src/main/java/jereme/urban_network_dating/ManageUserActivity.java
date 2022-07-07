package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import jereme.urban_network_dating.fragments.GroupFragment;
import jereme.urban_network_dating.fragments.JoinGroupFragment;
import jereme.urban_network_dating.fragments.UserFragment;

public class ManageUserActivity extends AppCompatActivity implements View.OnClickListener {

    private Fragment userFragment, joinGroupFragment, groupFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        ImageView ivBack= findViewById(R.id.btn_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RadioButton tabUserBtn, tabJoinGroupBtn, tabGroupBtn;
        userFragment = new UserFragment();
        joinGroupFragment = new JoinGroupFragment();
        groupFragment = new GroupFragment();
        tabUserBtn = findViewById(R.id.tab_user_btn);
        tabJoinGroupBtn = findViewById(R.id.tab_join_group_btn);


        tabGroupBtn = findViewById(R.id.tab_group_btn);
        tabUserBtn.setOnClickListener(this);
        tabJoinGroupBtn.setOnClickListener(this);
        tabGroupBtn.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String s = extras.getString("type");

        if(s.equals("friend")) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, userFragment);
            fragmentTransaction.commit();
            tabUserBtn.setChecked(true);

        } else if(s.equals("group")){

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, groupFragment);
            fragmentTransaction.commit();
            tabGroupBtn.setChecked(true);

        }


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tab_user_btn) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, userFragment);
            fragmentTransaction.commit();
        } else if(v.getId() ==R.id.tab_join_group_btn) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, joinGroupFragment);
            fragmentTransaction.commit();
        } else if(v.getId() ==R.id.tab_group_btn) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, groupFragment);
            fragmentTransaction.commit();
        }
    }


}

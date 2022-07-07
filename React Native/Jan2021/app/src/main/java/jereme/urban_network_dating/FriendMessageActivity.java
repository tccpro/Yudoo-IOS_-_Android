package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import jereme.urban_network_dating.Chats.ContactListActivity;
import jereme.urban_network_dating.fragments.NewMessageFragment;

public class FriendMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private Fragment newMessageFragment, memberFragment;
 //   TextView headername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_message);
      //  ImageView ivBack= findViewById(R.id.btn_back);
         //headername = findViewById(R.id.header_name);
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        RadioButton tabNewMessageBtn, tabFriendBtn;
        newMessageFragment = new NewMessageFragment();
        memberFragment = new ContactListActivity();
        tabNewMessageBtn = findViewById(R.id.tab_new_message);
        tabFriendBtn = findViewById(R.id.tab_friends);
        tabNewMessageBtn.setOnClickListener(this);
        tabFriendBtn.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String s = "new_message";
        if(s.equals("new_message")) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_containerchat, newMessageFragment);
            fragmentTransaction.commit();
            tabNewMessageBtn.setChecked(true);
        //    headername.setText(getResources().getString(R.string.message));
        } else if(s.equals("friend")){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_containerchat, memberFragment);
            fragmentTransaction.commit();
            tabFriendBtn.setChecked(true);
       //     headername.setText(getResources().getString(R.string.friends));
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tab_new_message) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, newMessageFragment);
            fragmentTransaction.commit();
           // headername.setText(getResources().getString(R.string.message));
        } else if(view.getId() ==R.id.tab_friends) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, memberFragment);
            fragmentTransaction.commit();
           // headername.setText(getResources().getString(R.string.friends));
        }
    }
}

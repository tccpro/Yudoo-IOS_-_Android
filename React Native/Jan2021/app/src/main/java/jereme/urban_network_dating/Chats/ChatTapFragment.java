package jereme.urban_network_dating.Chats;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.fragments.NewMessageFragment;

public class ChatTapFragment extends Fragment implements View.OnClickListener {
    private Fragment newMessageFragment, memberFragment;
    TextView headername;
    RadioButton tabNewMessageBtn, tabFriendBtn;
    SearchView searchView;
    ImageView search;
    OnSearchViewText onSearchViewText;
    String s = "new_message";
    public static OnSearchViewText listnerObj;

    public void setListnerObj(OnSearchViewText listnerObj) {
        this.listnerObj = listnerObj;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // just change the fragment_dashboard
        // with the fragment you want to inflate
        // like if the class is HomeFragment it should have R.layout.home_fragment
        // if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_chat, null);

        newMessageFragment = new NewMessageFragment();
        memberFragment = new ContactListActivity();
        searchView = (SearchView) view.findViewById(R.id.searchView);
        search = view.findViewById(R.id.search);
        headername = view.findViewById(R.id.btn_backs);
        tabNewMessageBtn = view.findViewById(R.id.tab_new_message);
        tabFriendBtn = view.findViewById(R.id.tab_friends);
        tabNewMessageBtn.setOnClickListener(this);
        tabFriendBtn.setOnClickListener(this);
        tabNewMessageBtn.setText(getResources().getString(R.string.inbox));
        tabFriendBtn.setText(getResources().getString(R.string.members));
        Bundle extras = getArguments();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setVisibility(View.VISIBLE);
                headername.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  onSearchViewText.searchViewText(query);
                listnerObj.searchViewText(query);
//                if (s.equals("new_message")) {
//                    listnerObj.searchViewText(query);
//                } else {
//                    listnerObj.searchViewText(query);
//                }

                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
//                ChatTapFragment parentFrag = ((ChatTapFragment)NewMessageFragment.this.getParentFragment());
//                parentFrag.onSearchViewText();
//                NewMessageFragment newMessage = (NewMessageFragment) getActivity().getSupportFragmentManager().getFragments();
//                ContactListActivity member = new ContactListActivity();
                Bundle bundle = new Bundle();
                listnerObj.searchViewText(newText);
//                if (s.equals("new_message")) {
//                    listnerObj.searchViewText(newText);
//                } else {
//                    listnerObj.searchViewText(newText);
//                }

                //  adapter.getFilter().filter(newText);
                //  onSearchViewText.searchViewText(newText);
                return false;
            }
        });

        if (s.equals("new_message")) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_containerchat, newMessageFragment);
            fragmentTransaction.commit();
            tabNewMessageBtn.setChecked(true);
            tabNewMessageBtn.setTextColor(getResources().getColor(R.color.whiteColor));
            tabFriendBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            s = "new_message";
            //  headername.setText(getResources().getString(R.string.message));
        } else if (s.equals("friend")) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_containerchat, memberFragment);
            fragmentTransaction.commit();
            //  onSearchViewText = (NewMessageFragment)(OnSearchViewText) this;
            tabFriendBtn.setChecked(true);
            tabFriendBtn.setTextColor(getResources().getColor(R.color.whiteColor));
            tabNewMessageBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            //  headername.setText(getResources().getString(R.string.friends));
            s = "friend";
        }

        return  view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tab_new_message) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_containerchat, newMessageFragment);
            fragmentTransaction.commit();
            tabNewMessageBtn.setTextColor(getResources().getColor(R.color.whiteColor));
            tabFriendBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            //  headername.setText("Inbox");
            s = "new_message";
        } else if (view.getId() == R.id.tab_friends) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_containerchat, memberFragment);
            fragmentTransaction.commit();
            tabFriendBtn.setTextColor(getResources().getColor(R.color.whiteColor));
            tabNewMessageBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            // headername.setText("Members");
            s = "friend";
        }
    }

    public  interface OnSearchViewText{
        void searchViewText(String searchtext);
    }
}

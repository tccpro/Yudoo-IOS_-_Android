package jereme.urban_network_dating.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import jereme.urban_network_dating.Adapters.CommentsAdapder;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.R;

public class ChatMessageFragment  extends Fragment {
    private ArrayList<MessageList> commentlist = new ArrayList<>();
    private CommentsAdapder messageListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_chat_message, null);

        ListView messagerecyclerView = view.findViewById(R.id.messages_view);
        ImageView back =view.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatsFragment chatsFragment = new ChatsFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, chatsFragment);
                ft.commit();
            }
        });
        messageListAdapter = new CommentsAdapder(getActivity(),commentlist);
        messagerecyclerView.setAdapter(messageListAdapter);

        messageData();
        return  view;
    }

    private void messageData() {
        MessageList group = new MessageList("NBA Fanbase", "Sports & Fitness", String.valueOf(R.drawable.group1));
        commentlist.add(group);
        group = new MessageList("Marvels Fans", "Movies & Tv shows", String.valueOf(R.drawable.group2));
        commentlist.add(group);
        group = new MessageList("Maths", "Action & Adventure", String.valueOf(R.drawable.group3));
        commentlist.add(group);
        group = new MessageList("It", "Action & Adventure", String.valueOf(R.drawable.group4));
        commentlist.add(group);
        //  messageListAdapter.notifyDataSetChanged();
    }
}

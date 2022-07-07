package jereme.urban_network_dating.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jereme.urban_network_dating.Adapters.ChatAdapter;
import jereme.urban_network_dating.ChatActivity;
import jereme.urban_network_dating.List.Chat;
import jereme.urban_network_dating.R;

public class ChatsFragment  extends Fragment {
    private List<Chat> chatList = new ArrayList<>();
    private ChatAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // just change the fragment_dashboard
        // with the fragment you want to inflate
        // like if the class is HomeFragment it should have R.layout.home_fragment
        // if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_chat, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_chat);
        mAdapter = new ChatAdapter(chatList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);
        prepareMovieData();
        return view;
    }

    private void prepareMovieData() {
        Chat movie = new Chat("Samual Waved at you!", "3mins ago", R.drawable.pic1);
        chatList.add(movie);
        movie = new Chat("Sara Liked at you!", "9mins ago", R.drawable.pic2);
        chatList.add(movie);
        movie = new Chat("Raja Waved at you!", "35mins ago", R.drawable.pic3);
        chatList.add(movie);
        movie = new Chat("Mani Waved at you!", "35mins ago", R.drawable.pic1);
        chatList.add(movie);

        mAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO: Step 4 of 4: Finally call getTag() on the view.
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            // viewHolder.getItemId();
            // viewHolder.getItemViewType();
            // viewHolder.itemView;
            Intent chatintent = new Intent(getActivity(), ChatActivity.class);
            startActivity(chatintent);
//            ChatMessageFragment chatMessageFragment = new ChatMessageFragment();
//            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.fragment_container, chatMessageFragment);
//            ft.commit();
            Chat thisItem = chatList.get(position);
            Toast.makeText(getActivity(), "You Clicked: " + thisItem.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}

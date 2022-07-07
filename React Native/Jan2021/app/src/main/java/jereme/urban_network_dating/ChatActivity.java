package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.ArrayList;

import jereme.urban_network_dating.Adapters.CommentsAdapder;
import jereme.urban_network_dating.List.MessageList;

public class ChatActivity extends AppCompatActivity {
    private ArrayList<MessageList> commentlist = new ArrayList<>();

    private CommentsAdapder messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ListView messagerecyclerView = findViewById(R.id.messages_view);
        ImageView back = findViewById(R.id.back);
        setupPusher();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                ChatsFragment chatsFragment = new ChatsFragment();
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.fragment_container, chatsFragment  );
//                ft.commit();
            }
        });
        messageListAdapter = new CommentsAdapder(ChatActivity.this,commentlist);
        messagerecyclerView.setAdapter(messageListAdapter);

        messageData();
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

        // messageListAdapter.notifyDataSetChanged();
    }

    private void setupPusher() {
        PusherOptions options = new PusherOptions();
        options.setCluster("mt1");

        Pusher pusher = new Pusher("4623ab31f34d74c8dc88", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i("Pusher", "There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe("my-channel");

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.i("Pusher", "Received event with data: " + event.toString());
            }
        });
    }
}

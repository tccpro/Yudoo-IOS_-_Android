package jereme.urban_network_dating.Adapters;

import  android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import jereme.urban_network_dating.List.Chat;
import jereme.urban_network_dating.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Chat> chatList;
    private View.OnClickListener mOnItemClickListener;
    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        CircleImageView image;
        MyViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.message);
            time = view.findViewById(R.id.time);
            image = view.findViewById(R.id.image);
            view.setTag(this);
            view.setOnClickListener(mOnItemClickListener);
        }
    }
    public ChatAdapter(List<Chat> chatList) {

        this.chatList=chatList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.message.setText(chat.getMessage());
        holder.time.setText(chat.getName());
        holder.image.setImageResource(chat.getImage());
    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

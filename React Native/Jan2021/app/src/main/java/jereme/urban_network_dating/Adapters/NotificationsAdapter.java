package jereme.urban_network_dating.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jereme.urban_network_dating.List.Notifications;
import jereme.urban_network_dating.R;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {

    private List<Notifications> notificationsList;
    private View.OnClickListener mOnItemClickListener;
    public NotificationsAdapter(List<Notifications> notificationList) {
        this.notificationsList=notificationList;
    }
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message, time,name,you;
        CircleImageView image;
        MyViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.message);
            name = view.findViewById(R.id.name);
            you = view.findViewById(R.id.you);
            time = view.findViewById(R.id.time);
            image = view.findViewById(R.id.image);
            view.setTag(this);
            view.setOnClickListener(mOnItemClickListener);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notifications notifications = notificationsList.get(position);
        if(notifications.getType().equals("wave")) {
            holder.name.setText(notifications.getMessage()+" ");
            holder.message.setText(holder.itemView.getContext().getResources().getString(R.string.waved));
            holder.you.setText(" "+holder.itemView.getContext().getResources().getString(R.string.atyou)+"!");
            holder.message.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            holder.name.setText(notifications.getMessage()+" ");
            holder.message.setText(holder.itemView.getContext().getResources().getString(R.string.liked));

            holder.message.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.rose));
            holder.you.setText(" "+holder.itemView.getContext().getResources().getString(R.string.you)+"!");
        }
        holder.time.setText(notifications.getTime());
      //  holder.image.setImageResource(notifications.getImage());
        if( notifications.getImage().equals("") ||  notifications.getImage().equals("null")){
            holder.image.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + notifications.getImage();
            Picasso.with(holder.itemView.getContext()).load(photourl).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}

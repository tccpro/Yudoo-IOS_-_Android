package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.R;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
    private Context mContext;
    private List<GroupList> groupList;
    private View.OnClickListener mOnItemClickListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, genre;
        RoundedImageView image;
        MyViewHolder(View view) {

            super(view);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.interest);
            image = view.findViewById(R.id.groupimag);
            view.setTag(this);
            view.setOnClickListener(mOnItemClickListener);

        }


    }
    public GroupAdapter(Context context, List<GroupList> groupList) {
        this.mContext = context;
        this.groupList=groupList;
    }
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GroupList movie = groupList.get(position);
        holder.title.setText(movie.getZoneTitle());
        holder.genre.setText(movie.getAddress());
       // holder.image.setImageURI(movie.getPhoto());

        if( groupList.get(position).getPhoto().equals("") ||  groupList.get(position).getPhoto().equals("null")){
            holder.image.setImageResource(R.drawable.default_head_icon);
        }else {
//            String photourl = "https://urban.network/pictures/202010/" + groupList.get(position).getPhoto();
            String pic[]=groupList.get(position).getPhoto().split(".");
            String photourl = "https://urban.network/" + groupList.get(position).getPhoto();
            Picasso.with(mContext).load(photourl).into(holder.image);
//            if(pic.length>0) {
//                String photourl = "https://urban.network/" + groupList.get(position).getPhoto();
//                Picasso.with(mContext).load(photourl).into(holder.image);
//            } else {
//                String photourl = "https://urban.network/pictures/" + groupList.get(position).getPhoto()+".jpg";
//                Picasso.with(mContext).load(photourl).into(holder.image);
//            }
        }

    }
    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

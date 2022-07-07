package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.List;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.fragments.GroupDetailFragment;

public class GroupPeopleAdapter extends RecyclerView.Adapter<GroupPeopleAdapter.MyViewHolder> {

    private List<GroupList> groupList;
    private Context mContext;
    String mygroup;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, genre;
        RoundedImageView image;
        Button visite,join;
        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.interest);
            image = view.findViewById(R.id.groupimag);
            visite = view.findViewById(R.id.visit);
            join = view.findViewById(R.id.join);

        }
    }
    public GroupPeopleAdapter(Context mContext, List<GroupList> groupList, String mygroup) {
        this.mContext=mContext;
        this.groupList=groupList;
        this.mygroup=mygroup;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_people_list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GroupList movie = groupList.get(position);
        holder.title.setText(movie.getZoneTitle());
        holder.genre.setText(movie.getAddress());
//        holder.image.setBackgroundResource(movie.getImage());
        if( groupList.get(position).getPhoto().equals("") ||  groupList.get(position).getPhoto().equals("null")){
            holder.image.setImageResource(R.drawable.default_head_icon);
        } else {
            String photourl = "https://urban.network/" + groupList.get(position).getPhoto();
         //   String photourl = "https://urban.network/pictures/202010/" + groupList.get(position).getPhoto();
           Picasso.with(mContext).load(photourl).into(holder.image);
        }

        if(movie.getStatus().equals("joined")){
            holder.join.setVisibility(View.GONE);
        } else {
            holder.join.setVisibility(View.VISIBLE);
        }
        holder.visite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupList thisItem = groupList.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("photo",thisItem.getPhoto());
                bundle.putString("title",thisItem.getZoneTitle());
                bundle.putString("desc",thisItem.getAddress());
                bundle.putString("groupId",thisItem.getID());
                bundle.putString("status",thisItem.getStatus());
                GroupDetailFragment groupDetailFragment = new GroupDetailFragment();
                groupDetailFragment.setArguments(bundle);
                FragmentTransaction ft = ((FragmentActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, groupDetailFragment);
                ft.addToBackStack(groupDetailFragment.getTag());
                ft.commit();
            }
        });
        if(mygroup.equals("mygroup")){
            holder.join.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

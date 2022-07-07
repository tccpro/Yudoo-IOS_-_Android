package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jereme.urban_network_dating.List.FriendList;
import jereme.urban_network_dating.R;

public class FriendListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<FriendList> zonelist;

    public FriendListAdapter(Context mContenxt, ArrayList<FriendList> zone_list) {
        this.mContext = mContenxt;
        this.zonelist = zone_list;
    }

    @Override
    public int getCount() {
        return zonelist.size();
    }

    @Override
    public Object getItem(int position) {
        return zonelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.messagelist_item, parent, false);
        }
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.item_image);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.item_name);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.item_message);

        if( zonelist.get(position).getPhoto().equals("") ||  zonelist.get(position).getPhoto().equals("null")){
            ivPhoto.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + zonelist.get(position).getPhoto();
            Picasso.with(mContext).load(photourl).into(ivPhoto);
        }

        tvTitle.setText(zonelist.get(position).getName());
        tvAddress.setText(zonelist.get(position).getStatus());
        if(zonelist.get(position).getStatus().equals("")) {
            tvAddress.setText(mContext.getResources().getString(R.string.status_no_data));
        }
        return convertView;
    }
}


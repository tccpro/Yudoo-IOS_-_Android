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
import jereme.urban_network_dating.List.SearchUserList;
import jereme.urban_network_dating.List.UserList;
import jereme.urban_network_dating.R;

public class SearchUserListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SearchUserList> userlist;

    public SearchUserListAdapter(Context mContenxt, ArrayList<SearchUserList> zone_list) {
        this.mContext = mContenxt;
        this.userlist = zone_list;
    }

    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return userlist.get(position);
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
        TextView tvName = (TextView) convertView.findViewById(R.id.item_name);
        TextView tvId = (TextView) convertView.findViewById(R.id.item_message);
        if( userlist.get(position).getPhoto().equals("") ||  userlist.get(position).getPhoto().equals("null")){
            ivPhoto.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + userlist.get(position).getPhoto();
            Picasso.with(mContext).load(photourl).into(ivPhoto);
        }

        tvName.setText(userlist.get(position).getName());
//        tvId.setText(userlist.get(position).getAddress());
        tvId.setText("");
        tvId.setVisibility(View.GONE);
        return convertView;
    }

    public static class UserListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<UserList> userlist;

        public UserListAdapter(Context mContenxt, ArrayList<UserList> zone_list) {
            this.mContext = mContenxt;
            this.userlist = zone_list;
        }

        @Override
        public int getCount() {
            return userlist.size();
        }

        @Override
        public Object getItem(int position) {
            return userlist.get(position);
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
            TextView tvName = (TextView) convertView.findViewById(R.id.item_name);
            TextView tvId = (TextView) convertView.findViewById(R.id.item_message);
            if( userlist.get(position).getPhoto().equals("") ||  userlist.get(position).getPhoto().equals("null")){
                ivPhoto.setImageResource(R.drawable.default_head_icon);
            }else {
                String photourl = "https://urban.network/" + userlist.get(position).getPhoto();
                Picasso.with(mContext).load(photourl).into(ivPhoto);
            }

            tvName.setText(userlist.get(position).getZoneTitle());
    //        tvId.setText(userlist.get(position).getAddress());

            tvId.setVisibility(View.GONE);

            return convertView;
        }
    }
}


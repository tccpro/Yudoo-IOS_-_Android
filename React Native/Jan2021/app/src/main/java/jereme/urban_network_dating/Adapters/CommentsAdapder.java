package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.R;

public class CommentsAdapder extends BaseAdapter {
    private Context mContext;
    private ArrayList<MessageList> zonelist;

    public CommentsAdapder(Context mContenxt, ArrayList<MessageList> zone_list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_commend, parent, false);
        }
        RoundedImageView ivPhoto = (RoundedImageView) convertView.findViewById(R.id.avatar);

        TextView tvAddress = (TextView) convertView.findViewById(R.id.message_body);

        if( zonelist.get(position).getPhoto().equals("") ||  zonelist.get(position).getPhoto().equals("null")){
            ivPhoto.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + zonelist.get(position).getPhoto();
            Picasso.with(mContext).load(photourl).into(ivPhoto);
        }


       // ivPhoto.setImageResource(Integer.parseInt(zonelist.get(position).getPhoto()));
        tvAddress.setText(zonelist.get(position).getAddress());
        return convertView;
    }
}

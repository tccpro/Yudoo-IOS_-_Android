package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jereme.urban_network_dating.List.MessageFriendList;
import jereme.urban_network_dating.R;

public class MessageFriendListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MessageFriendList> zonelist;

    public MessageFriendListAdapter(Context mContenxt, ArrayList<MessageFriendList> zone_list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_list, parent, false);
        }

        CircleImageView ivPhoto = (CircleImageView) convertView.findViewById(R.id.image);
//        ImageView ivIncome = convertView.findViewById(R.id.iv_income);
//        ImageView ivOutgoing = convertView.findViewById(R.id.iv_outgoing);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.message);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.time);

        if( zonelist.get(position).getPhoto().equals("") ||  zonelist.get(position).getPhoto().equals("null")){
            ivPhoto.setImageResource(R.drawable.default_head_icon);
        }else {
            String photourl = "https://urban.network/" + zonelist.get(position).getPhoto();
            Picasso.with(mContext).load(photourl).into(ivPhoto);
        }
   String title ="",message="";

           if(zonelist.get(position).getMessageTitle().length()>20)
               title=zonelist.get(position).getMessageTitle().substring(1,20)+"..";
           else
               title=zonelist.get(position).getMessageTitle();

            if(zonelist.get(position).getMessageDescription().length()>20)
                message=zonelist.get(position).getMessageDescription().substring(1,20)+"..";
            else
                message=zonelist.get(position).getMessageDescription();
        tvTitle.setText(title);
        tvAddress.setText(message);
        String messagedirection = zonelist.get(position).getMessagedirection();
//        if(messagedirection.equals("income")) {
//            ivIncome.setVisibility(View.VISIBLE);
//            ivOutgoing.setVisibility(View.GONE);
//        } else {
//            ivIncome.setVisibility(View.GONE);
//            ivOutgoing.setVisibility(View.VISIBLE);
//        }
        return convertView;
    }
}


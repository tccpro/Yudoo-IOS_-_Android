package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jereme.urban_network_dating.List.ArticleList;
import jereme.urban_network_dating.R;

public class ArticleFileListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ArticleList> zonelist;

    public ArticleFileListAdapter(Context mContenxt, ArrayList<ArticleList> zone_list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.articlelist_file_item, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.item_title);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.item_message);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.item_username);
        TextView tvDate = (TextView) convertView.findViewById(R.id.item_date);
        TextView tvFile = (TextView) convertView.findViewById(R.id.item_file);
        String filename = zonelist.get(position).getFile();
        String fileExtention = "";

        if (filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("jpg")) {
            fileExtention = "Image(jpg)";
        } else if (filename.substring(filename.length() - 4, filename.length()).toLowerCase().equals("jpeg")) {
            fileExtention = "Image(jpeg)";
        } else if (filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("png")) {
            fileExtention = "Image(png)";
        } else if (filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("avi")) {
            fileExtention = "Video(avi)";
        } else if (filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("mp4")) {
            fileExtention = "Video(mp4)";
        } else if (filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("mp3")) {
            fileExtention = "Music(mp3)";
        } else {
            fileExtention = "Unknown file";
        }

        tvTitle.setText("Title: " + zonelist.get(position).getTitle());
        tvMessage.setText("Message: " + zonelist.get(position).getMessage());
        tvUsername.setText("User: " + zonelist.get(position).getUser());
        tvDate.setText("Date: " + zonelist.get(position).getDate());
        tvFile.setText("File: " + fileExtention);

        return convertView;
    }
}

package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jereme.urban_network_dating.List.TasklistList;
import jereme.urban_network_dating.R;

public class TasklistListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TasklistList> zonelist;

    public TasklistListAdapter(Context mContenxt, ArrayList<TasklistList> zone_list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tasklist_item, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.item_name);
        TextView cbPrivate_State = (TextView) convertView.findViewById(R.id.item_private);

        tvName.setText(zonelist.get(position).getName());
        if(zonelist.get(position).getPrivate_state().equals("1")) {
            cbPrivate_State.setText("private");
        } else {
            cbPrivate_State.setText("public");
        }
//        cbPrivate_State.setClickable(false);
        return convertView;
    }
}


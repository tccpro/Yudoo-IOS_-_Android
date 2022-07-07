package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jereme.urban_network_dating.R;

public class GroupTitleAdapter extends RecyclerView.Adapter<GroupTitleAdapter.MyViewHolder> {
    private List<String> titlelist ;
     private Context context;
     int index=0;

  //  private View.OnClickListener mOnItemClickListener;

    private OnChangTypeClickListener onChangTypeClickListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
//            view.setTag(this);
//            view.setOnClickListener(mOnItemClickListener);
        }
    }
    public GroupTitleAdapter(Context context,List<String> titlelist,OnChangTypeClickListener onChangTypeClickListener) {
        this.context=context;
        this.titlelist=titlelist;
        this.onChangTypeClickListener=onChangTypeClickListener;
    }

//    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
//        mOnItemClickListener = itemClickListener;
//    }
//    public interface ItemClickListener {
//        void onItemClick(View view, int position);
//    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_title, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (titlelist.get(position).equals("All")) holder.title.setText(context.getResources().getString(R.string.all));
        if (titlelist.get(position).equals("Movie & Tv")) holder.title.setText(context.getResources().getString(R.string.movie));
        if (titlelist.get(position).equals("Games")) holder.title.setText(context.getResources().getString(R.string.games));
        if (titlelist.get(position).equals("Animals")) holder.title.setText(context.getResources().getString(R.string.animals));
        if (titlelist.get(position).equals("News")) holder.title.setText(context.getResources().getString(R.string.news));
        if (titlelist.get(position).equals("Education")) holder.title.setText(context.getResources().getString(R.string.Education));

        //holder.title.setText(titlelist.get(position));
        if (index == position)
          {
              holder.title.setTextColor(context.getResources().getColor(R.color.whiteColor));
              holder.title.setTextSize(context.getResources().getDimension(R.dimen.ten));
          }
        else
            {
                holder.title.setTextColor(context.getResources().getColor(R.color.subHeadColor));
                holder.title.setTextSize(context.getResources().getDimension(R.dimen.eight));
            }

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position;

                onChangTypeClickListener.onChangTypeClick(titlelist.get(position).equals("Movie & Tv")?"Movie":titlelist.get(position));
                notifyDataSetChanged();
            }
        });
    }
    @Override
    public int getItemCount() {
        return titlelist.size();
    }
    public interface OnChangTypeClickListener {
        void onChangTypeClick(String val);
    }
}

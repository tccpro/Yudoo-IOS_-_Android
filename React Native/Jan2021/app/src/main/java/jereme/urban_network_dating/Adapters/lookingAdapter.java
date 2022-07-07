package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;

public class lookingAdapter extends RecyclerView.Adapter<lookingAdapter.MyViewHolder> {
    private List<InterestList> titlelist ;
     private Context context;
     int index=0;
     int type;

    class MyViewHolder extends RecyclerView.ViewHolder {
        Button title;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);

        }
    }
    public lookingAdapter(Context context, List<InterestList> titlelist,int type) {
        this.context=context;
        this.titlelist=titlelist;
        this.type=type;
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
                .inflate(R.layout.item_looking_list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final InterestList model = titlelist.get(position);
        holder.title.setText("#"+model.getText());
//        index=0;
//        holder.title.setTextColor(model.isSelected()?context.getResources().getColor(R.color.whiteColor):context.getResources().getColor(R.color.colorPrimary));
//       holder.title.setBackground(model.isSelected()?context.getResources().getDrawable(R.drawable.background_blue):context.getResources().getDrawable(R.drawable.white_button_blue_border_small));

        if(type==1) {
            holder.title.setBackground(context.getResources().getDrawable(R.drawable.border_rose));
            holder.title.setTextColor(model.isSelected()?context.getResources().getColor(R.color.whiteColor):context.getResources().getColor(R.color.rose));
            holder.title.setBackgroundTintList(model.isSelected()?context.getResources().getColorStateList(R.color.rose):context.getResources().getColorStateList(R.color.transparentColor));
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // index = position;
                    model.setSelected(!model.isSelected());
                    //Toast.makeText(context,""+index,Toast.LENGTH_LONG).show();
                    notifyDataSetChanged();
                }
            });
        }

    }
    @Override
    public int getItemCount() {
        return titlelist.size();
    }
}

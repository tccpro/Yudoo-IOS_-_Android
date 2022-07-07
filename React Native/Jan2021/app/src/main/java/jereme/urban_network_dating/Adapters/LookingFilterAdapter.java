package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;

public class LookingFilterAdapter extends RecyclerView.Adapter<LookingFilterAdapter.MyViewHolder> {

    private List<InterestList> titlelist ;
    private Context context;
    int index=0;
    int type;
    private LookingFilterAdapter.OnAddLookingClickListener onAddLookingClickListener;
    public LookingFilterAdapter(Context context, ArrayList<InterestList> interestlist, int type, LookingFilterAdapter.OnAddLookingClickListener onAddLookingClickListener) {
        this.context=context;
        this.titlelist=interestlist;
        this.type=type;
        this.onAddLookingClickListener = onAddLookingClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_looking, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final InterestList model = titlelist.get(position);


        if(model.getText().equals("")){
            holder.titlelayout.setVisibility(View.GONE);
        } else {
            holder.title.setText("#"+model.getText());
            holder.titlelayout.setVisibility(View.VISIBLE);
        }
        if(position==titlelist.size()-1){
            holder.add.setVisibility(View.VISIBLE);
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAddLookingClickListener.onAddLookingClick("1");
                }
            });
        }


        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position!=titlelist.size()-1) {
                    titlelist.remove(position);
                } else {
                    titlelist.get(position).setText("");
                }
                notifyDataSetChanged();
            }
        });

//        index=0;
//        holder.title.setTextColor(model.isSelected()?context.getResources().getColor(R.color.whiteColor):context.getResources().getColor(R.color.colorPrimary));
//        holder.title.setBackground(model.isSelected()?context.getResources().getDrawable(R.drawable.background_blue):context.getResources().getDrawable(R.drawable.white_button_blue_border_small));

//        if(type==1){
//
//            holder.title.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // index = position;
//                    model.setSelected(!model.isSelected());
//                    //Toast.makeText(context,""+index,Toast.LENGTH_LONG).show();
//                    notifyDataSetChanged();
//                }
//            });
//
//        }
    }

    @Override
    public int getItemCount() {
        return titlelist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView close;
        LinearLayout add,titlelayout;
        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            add = view.findViewById(R.id.add_interest);
            titlelayout = view.findViewById(R.id.titlelayout);
            close = view.findViewById(R.id.close);
        }
    }
    public interface OnAddLookingClickListener {
        void onAddLookingClick(String add);
    }
}

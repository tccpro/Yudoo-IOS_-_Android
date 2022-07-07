package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jereme.urban_network_dating.List.ImageList;
import jereme.urban_network_dating.R;

public class HorizontalImageAdapter extends RecyclerView.Adapter<HorizontalImageAdapter.HorizontalViewHolder> {
    private List<ImageList> items=new ArrayList<>();
    Context mContext;
    int type;
    public HorizontalImageAdapter(Context context,List<ImageList> image,int type) {
        this.items = image;
        this.mContext = context;
        this.type=type;
    }




    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_hoeizontalimage, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HorizontalViewHolder holder, final int position) {
       // holder.image.setImageResource(items[position]);
        final ImageList model = items.get(position);

        if( items.get(position).getText().equals("") ||  items.get(position).getText().equals("null")){

            holder.image.setImageResource(R.drawable.default_head_icon);

        }else {

            String photourl = "https://urban.network/pictures/" + items.get(position).getText()+".jpg";
            Picasso.with(mContext).load(photourl).into(holder.image);

        }


       if(type==1) {
           if (model.isSelected()) {
               holder.deletelayout.setVisibility(View.VISIBLE);
               holder.deletelayout.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       items.remove(position);
                       notifyDataSetChanged();
                       Toast.makeText(mContext, "Item image has been removed from list",
                               Toast.LENGTH_SHORT).show();

                   }
               });
           } else {
               holder.deletelayout.setVisibility(View.INVISIBLE);
           }

           holder.image.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // index = position;
                   model.setSelected(!model.isSelected());
                   //Toast.makeText(context,""+index,Toast.LENGTH_LONG).show();
                   notifyDataSetChanged();
               }
           });

//     holder.image.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                model.setSelected(!model.isSelected());
//                //Toast.makeText(context,""+index,Toast.LENGTH_LONG).show();
//                notifyDataSetChanged();
//                return false;
//            }
//        });
       }
    }

    @Override
    public int getItemCount() {

        return items.size();
    }


    public void notifyData(List<ImageList> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.items = myList;
        notifyDataSetChanged();
    }
    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        LinearLayout deletelayout;
        public HorizontalViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_list);
            deletelayout = itemView.findViewById(R.id.delete);
        }
    }

}

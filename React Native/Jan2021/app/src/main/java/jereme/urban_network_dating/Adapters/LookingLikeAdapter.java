package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;

public class LookingLikeAdapter extends RecyclerView.Adapter<LookingLikeAdapter.MyViewHolder> {

    private List<InterestList> titlelist;
    private Context context;
    int index=0;
    int type;
    public LookingLikeAdapter(Context context, ArrayList<InterestList> selectlooking, int i) {
        this.context=context;
        this.titlelist=selectlooking;
        this.type=i;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        Button title;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title_view);

        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like_looking, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final InterestList model = titlelist.get(position);


//        if(model.getText().equals("")){
//            holder.titlelayout.setVisibility(View.GONE);
//        } else {
            holder.title.setText(model.getText());
//            holder.titlelayout.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {
        return titlelist.size();
    }
}

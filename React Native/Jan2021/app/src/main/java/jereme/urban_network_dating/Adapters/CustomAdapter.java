package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.basicviews.CircleView;

import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;

public class CustomAdapter  extends BaseAdapter {
    Context context;
    String countryList[];
    int flags[];
    LayoutInflater inflter;

    InterestAdapter interestAdapter;
    ArrayList<ArrayList<InterestList>> interestlist;

    public CustomAdapter(Context applicationContext, String[] countryList, int[] flags, ArrayList<ArrayList<InterestList>> interestlist) {
        this.context = applicationContext;
        this.countryList = countryList;
        this.flags = flags;
        this.interestlist = interestlist;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.interest_listview, null);
        TextView country = (TextView) view.findViewById(R.id.textView);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.title_recyclerView);
        final ImageView icon = (ImageView) view.findViewById(R.id.icon);
        final CircleView updown = (CircleView) view.findViewById(R.id.uparray);
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.iterestlist);
        country.setText(countryList[i]);
        icon.setImageResource(flags[i]);
        updown.setBackgroundResource(R.drawable.downarrow);
        final Boolean[] val = {false};

        updown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                val[0] =! val[0];
                if (val[0]) {
                    updown.setBackgroundResource(R.drawable.uparrow);
                    recyclerView.setVisibility(View.VISIBLE);
                    interestAdapter = new InterestAdapter(context,interestlist.get(i),1);
//                    LinearLayoutManager groupmLayoutManager = new LinearLayoutManager(context);
//                    groupmLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,2,LinearLayoutManager.HORIZONTAL,false);
                    recyclerView.setLayoutManager(mLayoutManager);
//                    recyclerView.setLayoutManager(groupmLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(interestAdapter);

                    linearLayout.setBackgroundResource(R.color.backroundlist);
                } else {
                    updown.setBackgroundResource(R.drawable.downarrow);
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setBackgroundResource(R.color.whiteColor);
                }
            }
        });

        return view;
    }
}
package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jereme.urban_network_dating.R;


public class LanguageSpinnerAdapter extends BaseAdapter {
    Context context;
    String[] countryNames;
    int[] flag;
    LayoutInflater inflter;

    public LanguageSpinnerAdapter(Context applicationContext, String[] countryNames,int[] flag) {
        this.context = applicationContext;
        this.countryNames = countryNames;
        this.flag=flag;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.spinner_itmes, null);
        LinearLayout relativeLayout = (LinearLayout) view.findViewById(R.id.spinner);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        TextView space = (TextView) view.findViewById(R.id.bottomtxt);
        icon.setImageResource(flag[i]);
        int newHeight = 70, newWidth = 50;
        icon.requestLayout();
        icon.getLayoutParams().height = newHeight;
        icon.getLayoutParams().width = newWidth;
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        icon.setVisibility(View.VISIBLE);
        names.setText(countryNames[i]);
        names.setTextSize(20);
        space.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        relativeLayout.setLayoutParams(params);
        relativeLayout.setBackgroundResource(R.drawable.circleimg);
        return view;
    }
}

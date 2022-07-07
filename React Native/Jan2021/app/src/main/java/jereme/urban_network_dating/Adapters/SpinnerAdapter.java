package jereme.urban_network_dating.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import jereme.urban_network_dating.R;


public class SpinnerAdapter extends BaseAdapter {
    Context context;
    String[] countryNames;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext, String[] countryNames) {
        this.context = applicationContext;
        this.countryNames = countryNames;
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
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(countryNames[i]);

        return view;
    }
}

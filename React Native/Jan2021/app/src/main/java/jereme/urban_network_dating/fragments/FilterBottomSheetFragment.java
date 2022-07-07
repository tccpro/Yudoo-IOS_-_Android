package jereme.urban_network_dating.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jereme.urban_network_dating.Adapters.InterestAdapter;
import jereme.urban_network_dating.Adapters.InterestFilterAdapter;
import jereme.urban_network_dating.Adapters.LookingFilterAdapter;
import jereme.urban_network_dating.Adapters.lookingAdapter;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;

public class FilterBottomSheetFragment  extends BottomSheetDialogFragment implements View.OnClickListener, InterestFilterAdapter.OnAddInterestClickListener,
        LookingFilterAdapter.OnAddLookingClickListener {
    private LinearLayout male, female, both, addinterest, applyfilter;
    //  private FlexboxLayout mLayout;
    private RangeSeekBar ageSeekbar2;
    private TextView maletext, femaletext, bothtext, addtext, addlookingtext;
    RecyclerView interestRecycleView, lookingRecycleView;
    ArrayList<InterestList> interestLists, lookingLists;
    ArrayList<InterestList> selectinterest, selectlooking;
    InterestAdapter interestAdapter;
    InterestFilterAdapter interestFilterAdapter;
    LookingFilterAdapter lookingFilterAdapter;
    lookingAdapter lookingAdapterobj;
    Context context;
    int distance = 50;
    TextView kmtext;
    String gender = "Gender any";
    OnFilterClickListener onFilterClickListener;

    public FilterBottomSheetFragment(OnFilterClickListener callbak) {
        this.onFilterClickListener=callbak;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_filter_people, container, false);
        context = getActivity();
        //  onFilterClickListener = (OnFilterClickListener) getParentFragment();
        male = v.findViewById(R.id.male);
        female = v.findViewById(R.id.female);
        both = v.findViewById(R.id.both);
        maletext = v.findViewById(R.id.maletext);
        femaletext = v.findViewById(R.id.femaletext);
        bothtext = v.findViewById(R.id.bothtext);
        applyfilter = v.findViewById(R.id.apply_filter);
        ageSeekbar2 = (RangeSeekBar) v.findViewById(R.id.pricebar_with_label);
        ageSeekbar2.setRangeValues(18, 100);
        ageSeekbar2.setSelectedMaxValue(30);
        ageSeekbar2.getSelectedMaxValue();

//           ageSeekbar2.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
//               @Override
//               public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
//                   //  //Now you have the minValue and maxValue of your RangeSeekbar
//                   // Toast.makeText(getActivity(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
//                   textAge.setText( minValue + "-" + maxValue);
//               }
//           });

// get seekbar from view
//           CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) v.findViewById(R.id.rangeSeekbar8);
//           final TextView tvMin = (TextView) v.findViewById(R.id.minValue);
//           final TextView tvMax = (TextView) v.findViewById(R.id.maxValue);
//
//// set listener
//           rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
//               @Override
//               public void valueChanged(Number minValue, Number maxValue) {
//                   tvMin.setText(String.valueOf(minValue));
//                   tvMax.setText(String.valueOf(maxValue));
//               }
//           });
//
//// set final value listener
//           rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
//               @Override
//               public void finalValue(Number minValue, Number maxValue) {
//                   Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
//               }
//           });
//           rangeSeekbar.setMinStartValue(0).setMaxStartValue(100).apply();
// change position left to right
        //  rangeSeekbar.setPosition(CrystalSeekbar.Position.RIGHT).apply();
        //  addinterest = v.findViewById(R.id.add_interest);
        lookingRecycleView = v.findViewById(R.id.looking_recycle);
        interestRecycleView = v.findViewById(R.id.interest_recycle);
        addtext = v.findViewById(R.id.saveinterest);
        addlookingtext = v.findViewById(R.id.savelooking);
        //  kmtext = v.findViewById(R.id.textViewScore);
        ImageView close = v.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  dismiss();
              }
        });

        male.setOnClickListener(this);
        female.setOnClickListener(this);
        both.setOnClickListener(this);
        // SeekBar seekBar = (SeekBar) v.findViewById(R.id.seekBar);

        //get interest
        getInterestList();

        //get looking
        getLookingList();

        addtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectinterest = new ArrayList<InterestList>();

                for (int i = 0; i < interestLists.size(); i++) {
                    if (interestLists.get(i).isSelected()) {
                        InterestList data = new InterestList(interestLists.get(i).getText(), false);
                        selectinterest.add(data);
                        //  System.out.println(interestlist.get(i).get(j).getText());}
                    }
                }

                if (selectinterest.size() > 0) {
                    setFilterInterestAdapters(selectinterest, 1);
                    addtext.setVisibility(View.GONE);
               }
            }
        });

        addlookingtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectlooking = new ArrayList<InterestList>();

                for (int i = 0; i < lookingLists.size(); i++) {
                    if (lookingLists.get(i).isSelected()) {
                        InterestList data = new InterestList(lookingLists.get(i).getText(), false);
                        selectlooking.add(data);
                        //  System.out.println(interestlist.get(i).get(j).getText());}
                    }
                }

                if (selectlooking.size() > 0) {
                    setFilterLookingAdapters(selectlooking,1);
                    addlookingtext.setVisibility(View.GONE);
                }
            }
        });

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,
//                                          boolean fromUser) {
//                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
//                kmtext.setText(progress + " Km");
//                distance = progress;
//                kmtext.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2 - 30);
//
//                //  Toast.makeText(getActivity(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//             //   Toast.makeText(getActivity(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//             //   Toast.makeText(getActivity(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
//            }
//        });

// apply filter
        applyfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 System.out.println(gender + ", " + ageSeekbar2.getSelectedMinValue() + "-" + ageSeekbar2.getSelectedMaxValue() + "," + distance);
//                 System.out.println(selectlooking);
//                 System.out.println(selectinterest);
                onFilterClickListener.onFilterClick(gender, ageSeekbar2.getSelectedMinValue() + "-" + ageSeekbar2.getSelectedMaxValue(), distance, selectlooking, selectinterest);
                dismiss();
                //  textAge.setText( ageSeekbar2.getSelectedMinValue() + "-" + ageSeekbar2.getSelectedMaxValue());
            }
        });

        return v;
    }

    private void getLookingList() {
        selectlooking = new ArrayList<InterestList>();
        InterestList data = new InterestList("", false);
        selectlooking.add(data);
        setFilterLookingAdapters(selectlooking, 1);
    }

    //get interest
    private void getInterestList() {
        selectinterest = new ArrayList<InterestList>();
        InterestList data = new InterestList("", false);
        selectinterest.add(data);
        setFilterInterestAdapters(selectinterest, 1);
    }

    //set interest adapter
    public void setInterestAdapters(ArrayList<InterestList> interestlist, int type) {
        interestAdapter = new InterestAdapter(context, interestlist, type);
        //  FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        //  layoutManager.setFlexWrap(FlexWrap.WRAP);
        RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        interestRecycleView.setLayoutManager(mintLayoutManager);
        interestRecycleView.setItemAnimator(new DefaultItemAnimator());
        interestRecycleView.setAdapter(interestAdapter);
    }

    private void setLookingAdapters(ArrayList<InterestList> lookingLists, int i) {
        lookingAdapterobj = new lookingAdapter(context, lookingLists, i);
        //  FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        //  layoutManager.setFlexWrap(FlexWrap.WRAP);
        RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        lookingRecycleView.setLayoutManager(mintLayoutManager);
        lookingRecycleView.setItemAnimator(new DefaultItemAnimator());
        lookingRecycleView.setAdapter(lookingAdapterobj);
    }

    private void setFilterLookingAdapters(ArrayList<InterestList> selectlooking, int i) {
        lookingFilterAdapter = new LookingFilterAdapter(context, selectlooking, i, this);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        //  RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        lookingRecycleView.setLayoutManager(layoutManager);
        lookingRecycleView.setItemAnimator(new DefaultItemAnimator());
        lookingRecycleView.setAdapter(lookingFilterAdapter);
    }

    public void setFilterInterestAdapters(ArrayList<InterestList> interestlist, int type) {
        interestFilterAdapter = new InterestFilterAdapter(context, interestlist, type, this);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        //  RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        interestRecycleView.setLayoutManager(layoutManager);
        interestRecycleView.setItemAnimator(new DefaultItemAnimator());
        interestRecycleView.setAdapter(interestFilterAdapter);
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.male) {
            male.setBackground(getResources().getDrawable(R.drawable.bacground_rose));
            maletext.setTextColor(getResources().getColorStateList(R.color.whiteColor));
            female.setBackground(getResources().getDrawable(R.drawable.border_rose));
            femaletext.setTextColor(getResources().getColorStateList(R.color.rose));
            both.setBackground(getResources().getDrawable(R.drawable.border_rose));
            bothtext.setTextColor(getResources().getColorStateList(R.color.rose));
            gender = "Male";
        } else if (view.getId() == R.id.female) {
            male.setBackground(getResources().getDrawable(R.drawable.border_rose));
            maletext.setTextColor(getResources().getColorStateList(R.color.rose));
            female.setBackground(getResources().getDrawable(R.drawable.bacground_rose));
            femaletext.setTextColor(getResources().getColorStateList(R.color.whiteColor));
            both.setBackground(getResources().getDrawable(R.drawable.border_rose));
            bothtext.setTextColor(getResources().getColorStateList(R.color.rose));
            gender = "Female";
        } else if(view.getId() == R.id.both) {
            male.setBackground(getResources().getDrawable(R.drawable.border_rose));
            maletext.setTextColor(getResources().getColorStateList(R.color.rose));
            female.setBackground(getResources().getDrawable(R.drawable.border_rose));
            femaletext.setTextColor(getResources().getColorStateList(R.color.rose));
            both.setBackground(getResources().getDrawable(R.drawable.bacground_rose));
            bothtext.setTextColor(getResources().getColorStateList(R.color.whiteColor));
            gender = "Both";
        }
    }

    @Override
    public void onAddIterestClick(String add) {
        addtext.setVisibility(View.VISIBLE);
        interestLists = new ArrayList<InterestList>();
        String all_interest[] = {"Action", "Adventure", "Animation", "Comedy", "Horror", "Thriller", "Romance", "Documentary", "Scary",
            "Dog", "Cat", "Snake", "Horse", "Rabbit", "Goat", "Monkey", "Loin", "Tiger",
            "Soccer", "Football", "Basketball", "Hockey", "Bowling", "Pool", "Golf", "Tennis",
            "Running", "Weightlifting", "Swimming", "Yoga", "Martial Arts", "Ballet", "Aerobics", "Zumba", "Dancing",
            "Music", "Painting", "Sculpture", "Sewing", "Spray Paint", "Singing", "Rapping", "Spoken Word", "Graphic Design", "Photoshop", "Acting",
            "Latest Trend", "Pop Culture", "Hip Hop", "Business Attire", "Casual", "Fancy", "Hipster", "Original", "Sporty",
            "Road Trip", "Picnics", "Camping", "Beaches", "Hiking", "Bar Hopper"
        };

        for (int i = 0; i < all_interest.length; i++) {
            InterestList data =  new InterestList(all_interest[i], false);
            interestLists.add(data);
        }

        if (interestLists.size() > 0) {
            setInterestAdapters(interestLists,1);
        }
    }

    @Override
    public void onAddLookingClick(String add) {
        addlookingtext.setVisibility(View.VISIBLE);
        lookingLists = new ArrayList<InterestList>();
        String all_looking[] = {"Friends", "Dating", "Marriage"};

        for (int i = 0; i < all_looking.length; i++) {
            InterestList data =  new InterestList(all_looking[i], false);
            lookingLists.add(data);
        }

        if (lookingLists.size() > 0) {
            setLookingAdapters(lookingLists, 1);
        }
    }

    public interface OnFilterClickListener {
        //  void onFilterClick(String gender,String age,);
        void onFilterClick(String gender, String age, int distance, ArrayList<InterestList> selectlooking, ArrayList<InterestList> selectinterest);
    }
}

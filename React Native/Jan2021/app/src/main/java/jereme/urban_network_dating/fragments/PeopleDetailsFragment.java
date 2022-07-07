package jereme.urban_network_dating.fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.GroupPeopleAdapter;
import jereme.urban_network_dating.Adapters.HorizontalImageAdapter;
import jereme.urban_network_dating.Adapters.InterestAdapter;
import jereme.urban_network_dating.Adapters.lookingAdapter;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.List.ImageList;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;


import static jereme.urban_network_dating.LoginActivity.base_url;


public class PeopleDetailsFragment extends Fragment {
    ArrayList<InterestList> interestLists;
    ArrayList<InterestList> lookingLists;
    InterestAdapter interestAdapter;
    lookingAdapter lookingAdapterobj;
    Dialog loadingDialog;
    private List<GroupList> groupList = new ArrayList<>();
    private List<String> imaglist = new ArrayList<>();
    List<ImageList> imglist =new ArrayList<>();
    RoundedImageView profilepic;
    TextView name, moreaboutext, location;
    RecyclerView recyclerView, list, interest_recyclerView, look_recyclerView;
    private GroupPeopleAdapter mAdapter;
    private ProgressBar groupspinner;
    String selectedUsername = "", selectLocation = "", selectedGroupID = "", selectedGroupType = "", selectedUserPhotoUrl = "", selectedUserID = "", selectedUserInterest,
           selectedmoreabout, selectedUserBirthday, selectedUserAge, SelectedLooking, SelectedMorepic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_peopledetails, null);
        profilepic = (RoundedImageView) view.findViewById(R.id.profile_pic);
        interest_recyclerView = (RecyclerView) view.findViewById(R.id.interest_recycle);
        name = (TextView) view.findViewById(R.id.name);
        location = (TextView) view.findViewById(R.id.text1);
        look_recyclerView = (RecyclerView) view.findViewById(R.id.looking_recycle);
        recyclerView = view.findViewById(R.id.group_recyclerView);
        groupspinner = (ProgressBar)view.findViewById(R.id.groupprogressBar);
        Bundle bundle = this.getArguments();
//        System.out.println(bundle.getString("photo"));
        selectedUserID = bundle.getString("profileId");
        selectedUsername = bundle.getString("name");
        selectedUserPhotoUrl = bundle.getString("profilepic");
        selectedUserAge = bundle.getString("age");
        selectedUserInterest = bundle.getString("interest");
        SelectedLooking = bundle.getString("looking");
        SelectedMorepic = bundle.getString("morepic");
        selectedmoreabout = bundle.getString("aboutme");
        selectLocation = bundle.getString("hometown");
        moreaboutext = view.findViewById(R.id.more_about);
        location.setText(selectLocation);
        moreaboutext.setText("" + selectedmoreabout);
        name.setText(selectedUsername + ',' + selectedUserAge);
        //looking

        if (!SelectedLooking.equals("") && SelectedLooking != null) {
            String looking_json = SelectedLooking;
            JSONArray lookjsonArr = null;

            try {
                lookjsonArr = new JSONArray(looking_json);
                lookingLists = new ArrayList<InterestList>();

                for (int i = 0; i < lookjsonArr.length(); i++) {
                    InterestList data = new InterestList(lookjsonArr.get(i).toString(),false);
                    lookingLists.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (lookingLists != null && lookingLists.size() > 0) {
                lookingAdapterobj = new lookingAdapter(getActivity(), lookingLists, 0);
                LinearLayoutManager mlookLayoutManager = new LinearLayoutManager(getActivity());
                mlookLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                look_recyclerView.setLayoutManager(mlookLayoutManager);
                look_recyclerView.setItemAnimator(new DefaultItemAnimator());
                look_recyclerView.setAdapter(lookingAdapterobj);
            }
        }

        // interest  list
        if (!selectedUserInterest.equals("") && selectedUserInterest != null) {
            String json = selectedUserInterest;
            JSONArray jsonArr = null;

            try {
                jsonArr = new JSONArray(json);
                interestLists = new ArrayList<InterestList>();

                for (int i = 0; i < jsonArr.length(); i++) {
                    InterestList data = new InterestList(jsonArr.get(i).toString(), false);
                    interestLists.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (interestLists != null && interestLists.size() > 0 ) {
                interestAdapter = new InterestAdapter(getActivity(), interestLists, 0);
                RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.HORIZONTAL, false);
                interest_recyclerView.setLayoutManager(mintLayoutManager);
                interest_recyclerView.setItemAnimator(new DefaultItemAnimator());
                interest_recyclerView.setAdapter(interestAdapter);
            }
        }

        list = (RecyclerView) view.findViewById(R.id.recyclerView);

        if (selectedUserPhotoUrl != null && selectedUserPhotoUrl != "") {
            String photourl = "https://urban.network/" + selectedUserPhotoUrl;
            Picasso.with(getActivity()).load(photourl).into(profilepic);
        }

        if (SelectedMorepic != null && SelectedMorepic != "") {
            String photos[] = SelectedMorepic.split(",");

            for (int i = 0; i < photos.length; i++) {
                if (imglist != null) {
                    ImageList imageList=new ImageList(photos[i]);
                    imglist.add(imageList);
                }
            }
        }

        ImageView peopleBack = view.findViewById(R.id.peopleback);

        peopleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeopleFragment peopleFragment = new PeopleFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, peopleFragment);
                ft.commit();
            }
        });

        //  imaglist.add("");
        //  RecyclerView list = (RecyclerView) view.findViewById(R.id.recyclerView);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setAdapter(new HorizontalImageAdapter(getContext(), imglist,0));

        if (groupList.size() == 0) {
            groupList.clear();
            groupspinner.setVisibility(View.VISIBLE);
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//            loadingDialog.show();
            new GetGroup().execute();
        } else {
            mAdapter = new GroupPeopleAdapter(getActivity(), groupList, "");
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            // prepareMovieData();
        }

        return view;
    }

    private class GetGroup extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();
                String sub_url = "group/getgroup.php?";
                String parameters = "user=" + selectedUserID;
                String url = base_url + sub_url + parameters;
                String jsonStr = sh.makeServiceCall(url);

                if (jsonStr != null) {
                    try {
                        Object json = new JSONTokener(jsonStr).nextValue();

                        if (json instanceof JSONObject) {
                            JSONObject contacts = new JSONObject(jsonStr);
                            //  articlespinner.setVisibility(View.GONE);

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        groupspinner.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(),
                                                "Group does not exist",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else if (json instanceof JSONArray) {
                            JSONArray contacts = new JSONArray(jsonStr);

                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject c = contacts.getJSONObject(i);
                                String id = c.getString("id");
                                String type = c.getString("type");
                                String name = c.getString("name");
                                String photo = c.getString("photo");
                                String status = c.getString("status");

                                if (!photo.equals("null")) {
                                    selectedGroupID = id;
                                    selectedUsername = name;
                                    selectedGroupType = type;
                                    selectedUserPhotoUrl = photo;
                                }

                                groupList.add(new GroupList(name, type, photo, id, status));
                            }
                        }
                    } catch (final JSONException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                groupspinner.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),
                                        e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    //  WeiboDialogUtils.closeDialog(loadingDialog);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupspinner.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),
                                    "Couldn't get json from server while loading group",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                groupspinner.setVisibility(View.GONE);
                //  WeiboDialogUtils.closeDialog(loadingDialog);

                if (groupList.size() > 0) {
                    mAdapter = new GroupPeopleAdapter(getActivity(), groupList, "");
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);

//                if(!selectedUserPhotoUrl.equals("")) {
//                    tvSelectedUserName.setText(selectedUsername);
//                    String photourl = "https://urban.network/" + selectedUserPhotoUrl;
//                    Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
//                 //   new GroupFragment.GetArticle().execute();
//                }
                }
            }
        }
//    private void prepareMovieData() {
//        GroupList movie = new GroupList("Mad Max: Fury Road", "Action & Adventure", "", "1");
//        groupList.add(movie);
//        movie = new GroupList("MCA", "King & Adventure", "", "2");
//        groupList.add(movie);
//        movie = new GroupList("Maths", "Action & Adventure", "", "3");
//        groupList.add(movie);
//        movie = new GroupList("Mad Max: Fury Road", "Action & Adventure", "", "4");
//        groupList.add(movie);
//        mAdapter.notifyDataSetChanged();
//    }
}
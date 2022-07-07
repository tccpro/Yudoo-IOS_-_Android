package jereme.urban_network_dating.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jereme.urban_network_dating.API.HttpHandler;


import jereme.urban_network_dating.Adapters.PeopleAdapter;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.List.SearchUserList;
import jereme.urban_network_dating.R;

import static jereme.urban_network_dating.NewHomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;

public class PeopleFragment extends Fragment implements FilterBottomSheetFragment.OnFilterClickListener {
    ArrayList<SearchUserList> members = new ArrayList<>();
    ArrayList<InterestList> selectinterest = new ArrayList<>();
    ArrayList<InterestList> selectlooking = new ArrayList<>();
    Dialog loadingDialog;
    RecyclerView peoplelist;
    PeopleAdapter mAdapter;
    FilterBottomSheetFragment.OnFilterClickListener callbak;
    String searchName, searchDisplayBy, searchGenderBy, searchLocationBy, searchAgesBy, searchPictureBy;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedUserID = "", selectedUserSearchName = "", selectedUserGender = "", selectedUserBirthday = "", selectedUserAge = "", SelectedEmail = "";
    String selectedUserHometown, selectedUserCurrent_city, selectedMessageName, selectedMessageDescription, SelectedMoreAboutme = "";
    private ProgressBar peoplespinner;
    LinearLayout progresslayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_people, null);
        ImageView notification = view.findViewById(R.id.notification);
        ImageView setting = view.findViewById(R.id.settings);
        peoplelist = view.findViewById(R.id.people_recyle);

        peoplespinner = (ProgressBar)view.findViewById(R.id.pepleprogressBar);
        progresslayout =(LinearLayout)view.findViewById(R.id.progresslayout);

        peoplelist.setVisibility(View.GONE);
        callbak = this;
//        if(cbSearchByPicture.isChecked()) {
        searchPictureBy = "1";
//        } else {
//            searchPictureBy = "0";
//        }
        members.clear();
        //userList.setAdapter(null);
        peoplespinner.setVisibility(View.VISIBLE);
        progresslayout.setVisibility(View.VISIBLE);
//        if(llDatingModeGroup.getVisibility()==View.GONE){
        searchGenderBy = "Gender any";
        searchLocationBy = "";
        searchAgesBy = "Ages, any";
        //   }
        new SearchMember().execute();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle sbundle = new Bundle();
//                sbundle.put;
                FilterBottomSheetFragment bottomSheet = new FilterBottomSheetFragment(callbak);
                //  bottomSheet.setArguments(sbundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeopleNotificationFragment peopleNotificationFragment = new PeopleNotificationFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, peopleNotificationFragment);
                ft.commit();
            }
        });
//        profilepic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PeopleDetailsFragment peopleDetailsFragment =new PeopleDetailsFragment();
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.fragment_container, peopleDetailsFragment);
//                ft.commit();
//            }
//        });

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.transparentColor, getActivity().getTheme()));
        } else if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.transparentColor));
        }
        //  getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.transparentColor));
        return view;
    }

    @Override
    public void onFilterClick(String gender, String age, int distance, ArrayList<InterestList> selectlooking, ArrayList<InterestList> selectinterest) {
        this.selectinterest = selectinterest;
        this.selectlooking = selectlooking;
        members.clear();
        peoplespinner.setVisibility(View.VISIBLE);
        progresslayout.setVisibility(View.VISIBLE);
        peoplelist.setVisibility(View.GONE);
//        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Members");
//        loadingDialog.show();
//        if (llDatingModeGroup.getVisibility() == View.GONE) {
            searchGenderBy = gender;
            searchLocationBy = "";
            searchAgesBy = age;
//        }
        new SearchMember().execute();
    }

    private class SearchMember extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "member/searchmemberBy.php?";
            String parameters = "id=" + profile_id +
                    "&searchname=" + "" +
                    "&displayby=" + "" +
                    "&genderby=" + searchGenderBy +
                    "&locationby=" + searchLocationBy +
                    "&agesby=" + searchAgesBy +
                    "&pictureby=" + searchPictureBy;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String searchname1 = c.getString("searchname");
                        String age = String.valueOf(c.getInt("age"));
                        String gender = c.getString("gender");
                        String birthday = c.getString("birthday");
                        String hometown = c.getString("hometown");
                        String currenty_city = c.getString("current_city");
                        String looking = c.getString("looking");
                        String interest = c.getString("interest");
                        String morepic = c.getString("morepic");
                        String moreabout = c.getString("aboutme") != null ? c.getString("aboutme") : "";
                        String email = c.getString("email");
                        String photo = c.getString("photo");

                        if (!photo.equals("null") && !photo.equals("")) {
                            if (selectinterest.size() > 0 || selectlooking.size() > 0) {
                                if (!selectinterest.get(0).getText().equals("") || !selectlooking.get(0).getText().equals("")) {
                                    int checkint = 0, noval = 0;
                                    JSONArray interestjsonmy = null, lookingjsonmy = null;

                                    if (interest != null && !interest.equals("")) {
                                        try {
                                            interestjsonmy = new JSONArray(interest);

                                            if (!selectinterest.get(0).getText().equals("")) {
                                                for (int j = 0; j < selectinterest.size(); j++) {
                                                    int flag = 0;

                                                    for (int k = 0; k < interestjsonmy.length(); k++) {
                                                        if (interestjsonmy.get(k).equals(selectinterest.get(j).getText())) {
                                                            flag = 1;
                                                            break;
                                                        }
                                                    }
                                                    if (flag == 0) {
                                                        checkint = 1;
                                                        break;
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        noval = noval + 1;
                                    }

                                    if (looking != null && !looking.equals("")) {
                                        try {
                                            lookingjsonmy = new JSONArray(looking);

                                            if (!selectlooking.get(0).getText().equals("")) {
                                                for (int j = 0; j < selectlooking.size(); j++) {
                                                    int flag = 0;

                                                    for (int k = 0; k < lookingjsonmy.length(); k++) {
                                                        if (lookingjsonmy.get(k).equals(selectlooking.get(j).getText())) {
                                                            flag = 1;
                                                            break;
                                                        }
                                                    }
                                                    if (flag == 0) {
                                                        checkint = 1;
                                                        break;
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        noval = noval + 1;
                                    }

                                    if (noval == 2) {
                                        checkint = 1;
                                    }

                                    if (checkint == 0) {
                                        getMember(id, photo, searchname1, age, gender, birthday, hometown, currenty_city, email, name, looking, interest, morepic, moreabout);
                                    }
                                } else {
                                    getMember(id, photo, searchname1, age, gender, birthday, hometown, currenty_city, email, name, looking, interest, morepic, moreabout);
                                }
                            } else {
                                getMember(id, photo, searchname1, age, gender, birthday, hometown, currenty_city, email, name, looking, interest, morepic, moreabout);
                            }
                        }
                    }
                } catch (final JSONException e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                peoplelist.setVisibility(View.VISIBLE);
                                peoplespinner.setVisibility(View.GONE);
                                progresslayout.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),
                                        e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            peoplespinner.setVisibility(View.GONE);
                            progresslayout.setVisibility(View.GONE);
                            peoplelist.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            peoplelist.setVisibility(View.VISIBLE);
            peoplespinner.setVisibility(View.GONE);
            progresslayout.setVisibility(View.GONE);
            if (members.size() > 0) {
                mAdapter = new PeopleAdapter(getActivity(), members);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                peoplelist.setLayoutManager(mLayoutManager);
                peoplelist.setItemAnimator(new DefaultItemAnimator());
                peoplelist.setAdapter(mAdapter);
            } else {
                peoplelist.setAdapter(null);
            }
        }
    }

    private void getMember(String id, String photo, String searchname1, String age, String gender, String birthday, String hometown, String currenty_city, String email, String name, String looking, String interest, String morepic, String aboutme) {
        selectedUserID = id;
        selectedUserPhotoUrl = photo;
        selectedUserSearchName = searchname1;
        selectedUserAge = age;
        selectedUserGender = gender;
        selectedUserBirthday = birthday;
        selectedUserHometown = hometown;
        selectedUserCurrent_city = currenty_city;
        SelectedEmail = email;
        SelectedMoreAboutme = aboutme;

        if (name.equals(""))
            selectedUsername = "<NULL>";
        else
            selectedUsername = name;
        members.add(new SearchUserList(selectedUsername, id, photo, searchname1, age, gender, birthday, hometown, currenty_city, looking, interest, morepic, aboutme));
    }

    private static void setStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

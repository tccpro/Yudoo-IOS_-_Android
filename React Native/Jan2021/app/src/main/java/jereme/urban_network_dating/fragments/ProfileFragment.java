package jereme.urban_network_dating.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.GroupPeopleAdapter;
import jereme.urban_network_dating.Adapters.HorizontalImageAdapter;
import jereme.urban_network_dating.Adapters.InterestAdapter;
import jereme.urban_network_dating.Adapters.lookingAdapter;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.List.ImageList;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.MainActivity;
import jereme.urban_network_dating.MeetNewPeopleActivity;
import jereme.urban_network_dating.PaymentActivity;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.SelectLanguageActivity;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;
import jereme.urban_network_dating.RegisterActtivity.ChoosepictureActivity;

import static jereme.urban_network_dating.HomeActivity.profile_status;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.static_email;
import static jereme.urban_network_dating.NewHomeActivity.profile_about;
import static jereme.urban_network_dating.NewHomeActivity.profile_currentCity;
import static jereme.urban_network_dating.NewHomeActivity.profile_id;
import static jereme.urban_network_dating.NewHomeActivity.profile_interest;
import static jereme.urban_network_dating.NewHomeActivity.profile_looking;
import static jereme.urban_network_dating.NewHomeActivity.profile_moreimage;
import static jereme.urban_network_dating.NewHomeActivity.profile_name;
import static jereme.urban_network_dating.NewHomeActivity.profile_photoUrl;
import static jereme.urban_network_dating.NewHomeActivity.profile_location;
import static jereme.urban_network_dating.NewHomeActivity.profile_membership;

public class ProfileFragment extends Fragment {
    int takephotoType = 0;
    InterestAdapter interestAdapter;
    lookingAdapter lookingAdapterobj;
    private String upLoadServerUri = null, selectedAlbumPhoto = "1";
    HorizontalImageAdapter horizontalImageAdapter;
    private int serverResponseCode = 0;
    private List<GroupList> groupList = new ArrayList<>();
    List<ImageList> imglist = new ArrayList<>();
    private GroupPeopleAdapter mAdapter;
    private String imagepath = "";
    ArrayList<InterestList> interestLists, lookingLists, mysavelooking, mysaveinterest;
    ArrayList<String> mylooking, myinterest;
    RecyclerView groupRecyclerView, imagrecycle, interest_recyclerView, look_recyclerView;
    Dialog loadingDialog;
    private int page = 0;
    LinearLayout addpicture, updateprofile, takepicture;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedGroupID = "", selectedGroupType = "", currentlocation="";
    String looking_json = "", interest_json = "", profilepictur, morepicture, getmoreabout;
    Context context;
    RoundedImageView profilepic;
    String uploadPhotoName = "", uploadPhotoNamewithoutExtention = "";
    TextView name, lookingedit, interestedit, lookingsave, interestsave, moreAboutedit, moreAboutsave, moreaboutext, location;
    int success_flag = 0;
    EditText moreaboutinput, locationedit;
    private ProgressBar groupspinner;
    ProgressDialog progress;
    TextView lang, langEdit;
    ImageView iv_flag;
    TextView tv_membership, upgrade;

    //  ImageView ivSelectedUserPhoto, ivShowMessage, ivHideMessage;

    final int[] flag = { R.drawable.en, R.drawable.afrikaans, R.drawable.albania,
            R.drawable.armenian, R.drawable.azerbaijani, R.drawable.bangla, R.drawable.belarus, R.drawable.bosnia,
            R.drawable.catalonia, R.drawable.china, R.drawable.czech, R.drawable.danish, R.drawable.dutch,
            R.drawable.france, R.drawable.germany, R.drawable.italian, R.drawable.japan, R.drawable.khmer,
            R.drawable.malaysia, R.drawable.polish, R.drawable.portuguese, R.drawable.russia, R.drawable.spain,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // just change the fragment_dashboard
        // with the fragment you want to inflate
        // like if the class is HomeFragment it should have R.layout.home_fragment
        // if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_profile, null);

        name = view.findViewById(R.id.name);
        groupspinner = (ProgressBar)view.findViewById(R.id.groupprogressBar);
        lookingedit = view.findViewById(R.id.lookingedit);
        interestedit = view.findViewById(R.id.interest_edit);
        moreAboutedit = view.findViewById(R.id.more_about_edit);
        location = view.findViewById(R.id.text1);
        moreAboutsave = view.findViewById(R.id.more_about_save);
        lookingsave = view.findViewById(R.id.lookingsave);
        interestsave = view.findViewById(R.id.interestsave);
        moreaboutinput = view.findViewById(R.id.more_about_input);
        moreaboutext = view.findViewById(R.id.more_about);
        locationedit = view.findViewById(R.id.location);
        lookingsave.setVisibility(View.GONE);
        interestsave.setVisibility(View.GONE);
        moreAboutsave.setVisibility(View.GONE);
        locationedit.setVisibility(View.GONE);
        context = getActivity();
        profilepic = (RoundedImageView) view.findViewById(R.id.profile_pic);
        takepicture = (LinearLayout) view.findViewById(R.id.take_profile);
        interest_recyclerView = (RecyclerView) view.findViewById(R.id.interest_recycle);
        look_recyclerView = (RecyclerView) view.findViewById(R.id.looking_recycle);
        imagrecycle = (RecyclerView) view.findViewById(R.id.recyclerView);
        addpicture = view.findViewById(R.id.add_delete_picture);
        groupRecyclerView = view.findViewById(R.id.group_recyclerView);
        updateprofile = view.findViewById(R.id.updateprofile);
        currentlocation = profile_currentCity;

        lang = view.findViewById(R.id.lang);
        langEdit = view.findViewById(R.id.lang_edit);
        iv_flag = view.findViewById(R.id.iv_flag);

        tv_membership = view.findViewById(R.id.tv_membership);
        upgrade = view.findViewById(R.id.upgrade);

        if (profile_membership.equals("0")) {
            tv_membership.setText("Free");
        } else if (profile_membership.equals("1")) {
            tv_membership.setText("Platinum");
        }

        SharedPreferences pref = context.getSharedPreferences("lang_pref", MODE_PRIVATE);
        String storedLanguage = pref.getString("storedLanguageName","0");
        int languageNum = 0;
        String strLang = "English";

        if (storedLanguage.equals("en")) {
            strLang = "English";
            languageNum = 0;
        } else if (storedLanguage.equals("af")) {
            strLang = "Afrikaans";
            languageNum = 1;
        } else if (storedLanguage.equals("sq")) {
            strLang = "Albanian";
            languageNum = 2;
        } else if (storedLanguage.equals("hy")) {
            strLang = "Armenian";
            languageNum = 3;
        } else if (storedLanguage.equals("az")) {
            strLang = "Azerbaijani";
            languageNum = 4;
        } else if (storedLanguage.equals("bn")) {
            strLang = "Bangala";
            languageNum = 5;
        } else if (storedLanguage.equals("be")) {
            strLang = "Belarusian";
            languageNum = 6;
        } else if (storedLanguage.equals("bs")) {
            strLang = "Bosinian";
            languageNum = 7;
        } else if (storedLanguage.equals("ca")) {
            strLang = "Catalan";
            languageNum = 8;
        } else if (storedLanguage.equals("zh")) {
            strLang = "Chinese";
            languageNum = 9;
        } else if (storedLanguage.equals("cs")) {
            strLang = "Czech";
            languageNum = 10;
        } else if (storedLanguage.equals("da")) {
            strLang = "Danish";
            languageNum = 11;
        } else if (storedLanguage.equals("nl")) {
            strLang = "Dutch";
            languageNum = 12;
        } else if (storedLanguage.equals("fr")) {
            strLang = "French";
            languageNum = 13;
        } else if (storedLanguage.equals("de")) {
            strLang = "German";
            languageNum = 14;
        } else if (storedLanguage.equals("it")) {
            strLang = "Italian";
            languageNum = 15;
        } else if (storedLanguage.equals("ja")) {
            strLang = "Japanese";
            languageNum = 16;
        } else if (storedLanguage.equals("km")) {
            strLang = "Khmer";
            languageNum = 17;
        } else if (storedLanguage.equals("ms")) {
            strLang = "Malay";
            languageNum = 18;
        } else if (storedLanguage.equals("pl")) {
            strLang = "Polish";
            languageNum = 19;
        } else if (storedLanguage.equals("pt")) {
            strLang = "Portuguese";
            languageNum = 20;
        } else if (storedLanguage.equals("ru")) {
            strLang = "Russian";
            languageNum = 21;
        } else if (storedLanguage.equals("es")) {
            strLang = "Spanish";
            languageNum = 22;
        } else if (storedLanguage.equals("en")) {
            strLang = "Arabic";
            languageNum = 0;
        } else if (storedLanguage.equals("en")) {
            strLang = "Persian";
            languageNum = 0;
        }

        iv_flag.setImageResource(flag[languageNum]);
        lang.setText(strLang);

        if (!currentlocation.equals("")) {
            location.setText(currentlocation);
        } else {
            location.setText("Edit Location");
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    location.setVisibility(View.GONE);
                    locationedit.setVisibility(View.VISIBLE);
                }
            });
        }

        updateprofile.setVisibility(View.GONE);
        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(getActivity());
                progress.setTitle("Loading");
                progress.setMessage("Updating...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                // To dismiss the dialog
                // loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Updating");
                // loadingDialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        new updateInformation().execute();
                    }
                }).start();
                // updateprofile.setVisibility(View.GONE);
            }
        });

        takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle sbundle = new Bundle();
                sbundle.putString("profile", "done");
                BottomsheetFragment bottomSheet = new BottomsheetFragment();
                bottomSheet.setArguments(sbundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        //Set profiles
        //get name
        name.setText(profile_name);
        looking_json = profile_looking;
        interest_json = profile_interest;
        profilepictur = profile_photoUrl;
        morepicture = profile_moreimage;
        getmoreabout = profile_about;

        //get profile picture
        moreaboutext.setText(getmoreabout);

        if (profilepictur != null && profilepictur != "") {
            String photourl = "https://urban.network/pictures/" + profilepictur;
            Picasso.with(getActivity()).load(photourl).into(profilepic);
        }

        //get more picture
        if (morepicture != null && morepicture != "") {
            String photos[] = morepicture.split(",");
            for (int i = 0; i < photos.length; i++) {
                if (imglist != null) {
                    ImageList imageList = new ImageList(photos[i]);
                    imglist.add(imageList);
                }
            }
        }

        //get looking
        getProfileLooking();
        //get interest  list
        getInterestList();

        //add picture
        addpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // takePhoto();
                BottomsheetFragment bottomSheet = new BottomsheetFragment();
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        //edit looking
        lookingedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lookingLists = new ArrayList<InterestList>();
                String all_looking[] = {"Friends", "Dating", "Marriage"};
                JSONArray lookjsonArr = null;

                try {
                    lookjsonArr = new JSONArray(looking_json);

                    for (int i = 0; i < all_looking.length; i++) {
                        InterestList data = null;

                        if (lookjsonArr.length() > 0) {
                            for (int j = 0; j < lookjsonArr.length(); j++) {
                                if (all_looking[i].equals(lookjsonArr.get(j).toString())) {
                                    data = new InterestList(lookjsonArr.get(j).toString(), true);
                                    break;
                                }
                            }
                        } else {
                            data = new InterestList(all_looking[i], false);
                        }

                        if (data == null) {
                            data = new InterestList(all_looking[i], false);
                        }

                        lookingLists.add(data);
                    }

                    if (lookingLists.size() > 0) {
                        setLookingAdapters(lookingLists, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                lookingedit.setVisibility(View.GONE);
                lookingsave.setVisibility(View.VISIBLE);
            }
        });

        //save looking
        lookingsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mylooking = new ArrayList<>();

                for (int i = 0; i < lookingLists.size(); i++) {
                    if (lookingLists.get(i).isSelected()) {
                        mylooking.add(lookingLists.get(i).getText());
                    }
                }

                looking_json = new Gson().toJson(mylooking);
                lookingedit.setVisibility(View.VISIBLE);
                lookingsave.setVisibility(View.GONE);
                getProfileLooking();
            }
        });

        //edit interest
        interestedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestLists = new ArrayList<InterestList>();
                String all_interest[] = {"Action", "Adventure", "Animation", "Comedy", "Horror", "Thriller", "Romance", "Documentary", "Scary",
                        "Dog", "Cat", "Snake", "Horse", "Rabbit", "Goat", "Monkey", "Loin", "Tiger",
                        "Soccer", "Football", "Basketball", "Hockey", "Bowling", "Pool", "Golf", "Tennis",
                        "Running", "Weightlifting", "Swimming", "Yoga", "Martial Arts", "Ballet", "Aerobics", "Zumba", "Dancing",
                        "Music", "Painting", "Sculpture", "Sewing", "Spray Paint", "Singing", "Rapping", "Spoken Word", "Graphic Design", "Photoshop", "Acting",
                        "Latest Trend", "Pop Culture", "Hip Hop", "Business Attire", "Casual", "Fancy", "Hipster", "Original", "Sporty",
                        "Road Trip", "Picnics", "Camping", "Beaches", "Hiking", "Bar Hopper"
                };

                JSONArray interestjsonArr = null;

                try {
                    interestjsonArr = new JSONArray(interest_json);

                    for (int i = 0; i < all_interest.length; i++) {
                        InterestList data = null;

                        if (interestjsonArr.length() > 0) {
                            for (int j = 0; j < interestjsonArr.length(); j++) {
                                if (all_interest[i].equals(interestjsonArr.get(j).toString())) {
                                    data = new InterestList(interestjsonArr.get(j).toString(), true);
                                    break;
                                }
                            }
                        } else {
                            data = new InterestList(all_interest[i], false);
                        }

                        if (data == null) {
                            data = new InterestList(all_interest[i], false);
                        }

                        interestLists.add(data);
                    }

                    if (interestLists.size() > 0) {
                        setInterestAdapters(interestLists, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                interestedit.setVisibility(View.GONE);
                interestsave.setVisibility(View.VISIBLE);
            }
        });

        //save interesting
        interestsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myinterest = new ArrayList<>();

                for (int i = 0; i < interestLists.size(); i++) {
                    if (interestLists.get(i).isSelected()) {
                        myinterest.add(interestLists.get(i).getText());
                    }
                }

                interest_json = new Gson().toJson(myinterest);
                interestedit.setVisibility(View.VISIBLE);
                interestsave.setVisibility(View.GONE);
                getInterestList();
            }
        });

       //more about
        moreAboutedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreaboutinput.setVisibility(View.VISIBLE);
                moreaboutext.setVisibility(View.GONE);
                moreaboutinput.setText(getmoreabout);
                moreAboutedit.setVisibility(View.GONE);
                moreAboutsave.setVisibility(View.VISIBLE);
            }
        });

        //save more about
        moreAboutsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getmoreabout = moreaboutinput.getText().toString();

                if (!getmoreabout.equals(moreaboutext.getText().toString())) {
                    updateprofile.setVisibility(View.VISIBLE);
                    moreaboutext.setText(getmoreabout);
                }

                moreaboutinput.setVisibility(View.GONE);
                moreaboutext.setVisibility(View.VISIBLE);
                moreAboutedit.setVisibility(View.VISIBLE);
                moreAboutsave.setVisibility(View.GONE);
            }
        });

        langEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SelectLanguageActivity.class);
                intent.putExtra("set_lang", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PaymentActivity.class);
                startActivity(intent);
            }
        });

        //get groups
        if (groupList.size() == 0) {
            groupList.clear();
            groupspinner.setVisibility(View.VISIBLE);
            // loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
            // loadingDialog.show();
            new Thread(new Runnable() {
                public void run() {
            new GetGroup().execute();
                }
            }).start();
        } else {
            mAdapter = new GroupPeopleAdapter(getActivity(), groupList, "mygroup");
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            groupRecyclerView.setLayoutManager(mLayoutManager);
            groupRecyclerView.setItemAnimator(new DefaultItemAnimator());
            groupRecyclerView.setAdapter(mAdapter);
            groupspinner.setVisibility(View.GONE);
        }
        // loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Loading pictures");
        // loadingDialog.show();
        // imglist.clear();
        imagrecycle.setAdapter(null);

        if (imglist.size() > 0) {
            imagrecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            horizontalImageAdapter = new HorizontalImageAdapter(getActivity(), imglist, 1);
            imagrecycle.setAdapter(horizontalImageAdapter);
        }

        return view;
    }

    //get looking list
    public void getProfileLooking() {
        if (!looking_json.equals(profile_looking)) {
            updateprofile.setVisibility(View.VISIBLE);
        }
        // String looking_json = profile_looking;
        JSONArray lookjsonArr = null;

        if (looking_json != null && !looking_json.equals("")) {
            try {
                lookjsonArr = new JSONArray(looking_json);
                lookingLists = new ArrayList<InterestList>();

                for (int i = 0; i < lookjsonArr.length(); i++) {
                    InterestList data = new InterestList(lookjsonArr.get(i).toString(), false);
                    lookingLists.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // if (lookingLists.size() > 0) {
                setLookingAdapters(lookingLists, 0);
            // }
        }
    }

    // get interest adapter
    public void getInterestList() {
        //  String json = profile_interest;
        if (!interest_json.equals(profile_interest)) {
            updateprofile.setVisibility(View.VISIBLE);
        }

        JSONArray jsonArr = null;

        if (interest_json != null && !interest_json.equals("")) {
            if (interest_json != null) {
                try {
                    jsonArr = new JSONArray(interest_json);
                    interestLists = new ArrayList<InterestList>();

                    for (int i = 0; i < jsonArr.length(); i++) {
                        InterestList data = new InterestList(jsonArr.get(i).toString(), false);
                        interestLists.add(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // if (interestLists.size() > 0) {
                    setInterestAdapters(interestLists, 0);
                // }
            }
        }
    }

    //set looing adapter
    public void setLookingAdapters(ArrayList<InterestList> lookinglist, int type) {
        lookingAdapterobj = new lookingAdapter(context, lookinglist, type);
        LinearLayoutManager mlookLayoutManager = new LinearLayoutManager(getActivity());
        mlookLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        look_recyclerView.setLayoutManager(mlookLayoutManager);
        look_recyclerView.setItemAnimator(new DefaultItemAnimator());
        look_recyclerView.setAdapter(lookingAdapterobj);
    }

    //set interest adapter
    public void setInterestAdapters(ArrayList<InterestList> interestlist, int type) {
        interestAdapter = new InterestAdapter(context, interestlist, type);
        // FlexboxLayoutManager layoutManager = new FlexboxLayoutManager();
        // layoutManager.setFlexWrap(FlexWrap.WRAP);
        RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        interest_recyclerView.setLayoutManager(mintLayoutManager);
        interest_recyclerView.setItemAnimator(new DefaultItemAnimator());
        interest_recyclerView.setAdapter(interestAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ChoosepictureActivity.CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            final Bitmap photo = (Bitmap) data.getExtras().get("data");

            if (requestCode == 150) {
                profilepic.setImageBitmap(photo);
                takephotoType = 1;
            } else {
                takephotoType = 0;
            }

            //imgUser.setImageBitmap(decodeFile(selectedImagePath));
            try {
                File file = createImageFile();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
                upLoadServerUri = "https://urban.network/Apinew/upload/upload.php";
                // loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
                // loadingDialog.show();
                new GetLatestPhotoID().execute();
            } catch (Exception e) {
                Log.e("error", "Failed to convert images");
            }
        } else if (resultCode == getActivity().RESULT_OK) {
            final Uri imageUri = data.getData();

            if (requestCode == 5) {
                // profilepic.setImageBitmap(photo);
                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    profilepic.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                takephotoType = 1;
            } else {
                takephotoType = 0;
            }

            imagepath = getPath(imageUri);
            // final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            // final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            // profileimg.setImageBitmap(selectedImage);

            upLoadServerUri = "https://urban.network/Apinew/upload/upload.php";
            // loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
            // loadingDialog.show();
            new GetLatestPhotoID().execute();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg", /* suffix */
            storageDir   /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagepath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        String path = "";

        cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        } else {
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getActivity().getContentResolver().query(uri,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            path = c.getString(columnIndex);
            c.close();
        }

        cursor.close();
        return path;
    }

//    @SuppressLint("ObsoleteSdkInt")
//    public String getPath(Uri uri){
//        String realPath="";
//// SDK < API11
//        if (Build.VERSION.SDK_INT < 11) {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            @SuppressLint("Recycle") Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
//            int column_index = 0;
//            String result="";
//            if (cursor != null) {
//                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                realPath=cursor.getString(column_index);
//            }
//        }
//        // SDK >= 11 && SDK < 19
//        else if (Build.VERSION.SDK_INT < 19){
//            String[] proj = { MediaStore.Images.Media.DATA };
//            CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, proj, null, null, null);
//            Cursor cursor = cursorLoader.loadInBackground();
//            if(cursor != null){
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                realPath = cursor.getString(column_index);
//            }
//        }
//        // SDK > 19 (Android 4.4)
//        else{
//            String wholeID = DocumentsContract.getDocumentId(uri);
//            // Split at colon, use second item in the array
//            String id = wholeID.split(":")[1];
//            String[] column = { MediaStore.Images.Media.DATA };
//            // where id is equal to
//            String sel = MediaStore.Images.Media._ID + "=?";
//            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id }, null);
//            int columnIndex = 0;
//            if (cursor != null) {
//                columnIndex = cursor.getColumnIndex(column[0]);
//                if (cursor.moveToFirst()) {
//                    realPath = cursor.getString(columnIndex);
//                }
//                cursor.close();
//            }
//        }
//        return realPath;
//    }

    private class GetGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/getgroup.php?";
            String parameters = "user=" + profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    Object json = new JSONTokener(jsonStr).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject contacts = new JSONObject(jsonStr);
                        // articlespinner.setVisibility(View.GONE);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    groupspinner.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Group does not exist", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else if (json instanceof JSONArray) {
                        JSONArray contacts = new JSONArray(jsonStr);

                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            String id = c.getString("id");
                            String type = c.getString("categoryname");
                            String name = c.getString("name");
                            String photo = c.getString("image");
                            String status = c.getString("status");

                            if (!photo.equals("null")) {
                                selectedGroupID = id;
                                selectedUsername = name;
                                selectedGroupType = type;
                                selectedUserPhotoUrl = photo;
                            }
                            groupList.add(new GroupList(name, type, photo, id,status));
                        }
                    }
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupspinner.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                // WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        groupspinner.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Couldn't get json from server while loading group", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            groupspinner.setVisibility(View.GONE);
            // WeiboDialogUtils.closeDialog(loadingDialog);
            if (groupList.size() > 0) {
                mAdapter = new GroupPeopleAdapter(getActivity(), groupList,"mygroup");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                groupRecyclerView.setLayoutManager(mLayoutManager);
                groupRecyclerView.setItemAnimator(new DefaultItemAnimator());
                groupRecyclerView.setAdapter(mAdapter);

                // if(!selectedUserPhotoUrl.equals("")) {
                //      tvSelectedUserName.setText(selectedUsername);
                //      String photourl = "https://urban.network/" + selectedUserPhotoUrl;
                //      Picasso.with(getActivity()).load(photourl).into(ivSelectedUserPhoto);
                //      // new GroupFragment.GetArticle().execute();
                // }
            }
        }
    }

    ProgressDialog dialog;

    private class GetLatestPhotoID extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "photos/profilePicture.php";
            String parameters = "";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String id = jsonObj.getString("id");
                    int a = Integer.parseInt(id);
                    a = a + 1;
                    uploadPhotoName = String.valueOf(a);
                    uploadPhotoNamewithoutExtention = uploadPhotoName;
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Json parsing error photoID: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.incrementProgressBy(values[0]);
        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            publishProgress(50);

            new Thread(new Runnable() {
                public void run() {
                    if (imagepath.length() > 0) {
                        uploadFile(imagepath);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Select Photo", Toast.LENGTH_SHORT).show();
                        WeiboDialogUtils.closeDialog(loadingDialog);
                    }
                }
            }).start();
        }
    }

    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        String extention = fileName.substring(fileName.length() - 5, fileName.length());
        String[] separated = extention.split("\\.");
        fileName = uploadPhotoName + "." + separated[1];
        uploadPhotoName = fileName;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            WeiboDialogUtils.closeDialog(loadingDialog);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), "Source File not exist :" + imagepath, Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (takephotoType == 0) {
                                String morepic = "";
                                ImageList imageList = new ImageList(uploadPhotoNamewithoutExtention);

                                imglist.add(imageList);

                                for (int i = 0; i < imglist.size(); i++){
                                    morepic = morepic + imglist.get(i).getText() + ',';
                                }

                                morepicture = morepic;
                                horizontalImageAdapter.notifyDataSetChanged();
                                WeiboDialogUtils.closeDialog(loadingDialog);

                                if (!morepicture.equals(profile_moreimage)) {
                                    updateprofile.setVisibility(View.VISIBLE);
                                }

                                Toast.makeText(getActivity(), "File Upload Complete." + uploadPhotoNamewithoutExtention, Toast.LENGTH_SHORT).show();
                            } else {
                                WeiboDialogUtils.closeDialog(loadingDialog);
                                profilepictur = uploadPhotoName;

                                if (!profilepictur.equals(profile_photoUrl)) {
                                    updateprofile.setVisibility(View.VISIBLE);
                                }

                                //get profile picture
                                if (profilepictur != null && profilepictur != "") {
                                    String photourl = "https://urban.network/pictures/" + profilepictur;
                                    Picasso.with(getActivity()).load(photourl).into(profilepic);
                                }
                            }
                        }
                    });
                }

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                ex.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "Upload fault", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return serverResponseCode;
        }
    }

    // update profile
    private class updateInformation extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/updateProfile.php?";
            String s = profilepictur;
            String parameters = "email=" +  static_email +
                    "&id=" + profile_id +
                    "&name=" + profile_name +
                    "&picture=" + s +
                    "&status=" + profile_status +
                    "&morepic=" + morepicture +
                    "&interest=" + interest_json +
                    "&looking=" + looking_json+
                    "&aboutme=" + getmoreabout+
                    "&location=" + "";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    success_flag = 1;
                } catch (final JSONException e) {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(getActivity(),"Json parsing error in update: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();

            if (success_flag == 1) {
                profile_moreimage = morepicture;
                profile_interest = interest_json;
                profile_looking = looking_json;
                profile_about = getmoreabout;

                location.setVisibility(View.VISIBLE);
                locationedit.setVisibility(View.GONE);
                updateprofile.setVisibility(View.GONE);
                Toast.makeText(getActivity(),getResources().getString(R.string.update_sucess), Toast.LENGTH_SHORT).show();

                if (!profile_photoUrl.equals(profilepictur))
                    new update_show_picture_user().execute();
                else
                    WeiboDialogUtils.closeDialog(loadingDialog);
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class update_show_picture_user extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "upload/updatePictureUser.php?";
            String parameters = "id=" +  profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                progress.dismiss();
                // WeiboDialogUtils.closeDialog(loadingDialog);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    success_flag = 1;
                } catch (final JSONException e) {
                  getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          progress.dismiss();
                      }
                  });
                  // WeiboDialogUtils.closeDialog(loadingDialog);
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
                // WeiboDialogUtils.closeDialog(loadingDialog);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            //  getActivity().runOnUiThread(new Runnable() {
            //      @Override
            //      public void run() {
                    progress.dismiss();
            //      }
            //  });

            if (success_flag == 1) {
                profile_photoUrl=profilepictur;
                Toast.makeText(getActivity(), "Profile picture Sucessfully", Toast.LENGTH_SHORT).show();
                //  new update_show_picture_user().execute();
            } else {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
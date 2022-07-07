package jereme.urban_network_dating.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.InterestLikeAdapter;
import jereme.urban_network_dating.Adapters.LookingLikeAdapter;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

import static jereme.urban_network_dating.NewHomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.NewHomeActivity.profile_interest;
import static jereme.urban_network_dating.NewHomeActivity.profile_looking;
import static jereme.urban_network_dating.NewHomeActivity.profile_photoUrl;
import static jereme.urban_network_dating.NewHomeActivity.profile_membership;

public class BottomsheetWaveFragment  extends BottomSheetDialogFragment {
    CircleImageView otherimageperson, userimage;
    RecyclerView interestRecycleView, lookingRecycleView;
    LookingLikeAdapter lookingLikeAdapter;
    InterestLikeAdapter interestLikeAdapter;
    ArrayList<InterestList> interestLists;
    TextView interesttext, you, and, name, likeseach;
    Button close, sendmessage;
    Dialog loadingDialog;
    EditText etMessageTitle, etMessageDescription;
    String addMessageResoponse = "";
    ArrayList<InterestList> lookingLists;
    Context context;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedUserID = "", selectedUserInterest = "",  selectedUserAge = "", SelectedLooking = "", SelectedMorepic = "";
    String  selectedMessageName, selectedMessageDescription;
    EditText et_message;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_both_wave, container, false);
        context = getActivity();
        lookingRecycleView = v.findViewById(R.id.looking_recycle);
        interestRecycleView = v.findViewById(R.id.interest_recycle);
        interesttext = v.findViewById(R.id.interest_count);
//        you = v.findViewById(R.id.you);
        and = v.findViewById(R.id.and);
        name = v.findViewById(R.id.name);
        likeseach = v.findViewById(R.id.likeseach);
        close = v.findViewById(R.id.close);
        sendmessage = v.findViewById(R.id.send_message);
        et_message = v.findViewById(R.id.et_wave_message);

        if (!profile_membership.equals("0")) {
            et_message.setVisibility(View.VISIBLE);
        }

        Bundle bundle = this.getArguments();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

//        System.out.println(bundle.getString("photo"));
        selectedUserID =       bundle.getString("profileId");
        selectedUsername =     bundle.getString("name");
        selectedUserPhotoUrl = bundle.getString("profilepic");
        selectedUserAge =      bundle.getString("age");
        selectedUserInterest = bundle.getString("interest");
        SelectedLooking =      bundle.getString("looking");
        SelectedMorepic =      bundle.getString("morepic");
        otherimageperson = (CircleImageView) v.findViewById(R.id.otherimage);
        userimage = (CircleImageView) v.findViewById(R.id.myimage);

        name.setText(selectedUsername);
        interesttext.setText(getResources().getString(R.string.youhave) + " " + 0 + " " + getResources().getString(R.string.interestincommon));

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Intent intent = new Intent(getActivity(), ChatRoom.class);
                intent.putExtra(ChatRoom.EXTRA_ID, selectedUserID);
                intent.putExtra(ChatRoom.EXTRA_NAME, selectedUsername);
                // intent.putExtra(ChatRoom.EXTRA_COUNT, user.count)
                intent.putExtra(ChatRoom.EXTRA_COUNT, Integer.parseInt(selectedUserID));
                startActivity(intent); */
                selectedMessageName = "Wave message";
                selectedMessageDescription = et_message.getText().toString(); //"";
                new BottomsheetWaveFragment.SendMessage().execute();
            }
        });

        if (selectedUserPhotoUrl != null && selectedUserPhotoUrl != "") {
            String photourl = "https://urban.network/" + selectedUserPhotoUrl;
            Picasso.with(getActivity()).load(photourl).into(otherimageperson);
        }
        if (selectedUserPhotoUrl != null && selectedUserPhotoUrl != "") {
            String photourl = "https://urban.network/pictures/" + profile_photoUrl;
            Picasso.with(getActivity()).load(photourl).into(userimage);
        }

        getLikeLooking();
        getLikeIntrest();
        return v;
    }

    private class SendMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/messageadd.php?";
            String parameters = "user=" + profile_id +
                               "&user4=" + selectedUserID +
                               "&name=" + selectedMessageName +
                               "&message=" + selectedMessageDescription +
                               "&type=wave";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String message_response = jsonObj.getString("m");
                    addMessageResoponse = message_response;
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server while sending message",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);

            if (addMessageResoponse.equals("success")) {
                //  etMessageDescription.setText("");
                //  etMessageTitle.setText("");
                new AlertDialog.Builder(getActivity())
                    .setTitle("Send Message")
                    .setMessage("Sent new message successfully")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            } else {
                new AlertDialog.Builder(getActivity())
                    .setTitle("Send Message")
                    .setMessage("can't send message")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        }
    }

    // get looking list
    public void  getLikeLooking() {
        String looking_json = profile_looking;
        JSONArray lookjsonmy = null, lookingother = null;

        if (profile_looking != null && !profile_looking.equals("") && SelectedLooking != null && !SelectedLooking.equals("")) {
            try {
                lookjsonmy = new JSONArray(looking_json);
                lookingother = new JSONArray(SelectedLooking);
                lookingLists = new ArrayList<InterestList>();

                for (int i = 0; i < lookjsonmy.length(); i++) {
                    for (int j = 0; j < lookingother.length(); j++) {
                        if (lookjsonmy.get(i).equals(lookingother.get(j))) {
                            System.out.println(lookjsonmy.get(i));
                            InterestList data = new InterestList(lookjsonmy.get(i).toString(), false);
                            lookingLists.add(data);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (lookingLists.size() > 0) {
                setLikeLookingAdapters(lookingLists, 0);
            }
        }
    }

    // get looking list
    public void  getLikeIntrest() {
        String interest_json = profile_interest;
        JSONArray interestjsonmy = null, interestother = null;

        if (profile_interest != null && !profile_interest.equals("") && selectedUserInterest != null && !selectedUserInterest.equals("")) {
            try {
                interestjsonmy = new JSONArray(interest_json);
                interestother = new JSONArray(selectedUserInterest);
                interestLists = new ArrayList<InterestList>();

                for (int i = 0; i < interestjsonmy.length(); i++) {
                    for (int j = 0; j < interestother.length(); j++) {
                        if (interestjsonmy.get(i).equals(interestother.get(j))) {
                            System.out.println(interestjsonmy.get(i));
                            InterestList data = new InterestList(interestjsonmy.get(i).toString(), false);
                            interestLists.add(data);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (interestLists.size() > 0) {
                interesttext.setText(getResources().getString(R.string.youhave) + " " + interestLists.size() + " " + getResources().getString(R.string.interestincommon));
                setLikeInterestAdapters(interestLists, 0);
            }
        }
    }

    private void setLikeLookingAdapters(ArrayList<InterestList> selectlooking, int i) {
        lookingLikeAdapter = new LookingLikeAdapter(context, selectlooking, i);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        //  RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        lookingRecycleView.setLayoutManager(layoutManager);
        lookingRecycleView.setItemAnimator(new DefaultItemAnimator());
        lookingRecycleView.setAdapter(lookingLikeAdapter);
    }

    private void setLikeInterestAdapters(ArrayList<InterestList> selectlooking, int i) {
        interestLikeAdapter = new InterestLikeAdapter(context, selectlooking, i);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        //  RecyclerView.LayoutManager mintLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        interestRecycleView.setLayoutManager(layoutManager);
        interestRecycleView.setItemAnimator(new DefaultItemAnimator());
        interestRecycleView.setAdapter(interestLikeAdapter);
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}

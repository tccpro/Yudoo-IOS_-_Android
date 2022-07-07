package jereme.urban_network_dating.Adapters;

import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.NewHomeActivity.profile_id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.List.SearchUserList;
import jereme.urban_network_dating.LoginActivity;
import jereme.urban_network_dating.NewHomeActivity;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.RegisterActtivity.LookingActivity;
import jereme.urban_network_dating.fragments.BottomsheetBothLikeFragment;
import jereme.urban_network_dating.fragments.BottomsheetWaveFragment;
import jereme.urban_network_dating.fragments.PeopleDetailsFragment;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.MyViewHolder> {
    List<SearchUserList> searchUserLists = new ArrayList<>() ;
    Context mContext;

    String report_success = "0";
    //  ArrayList<String> lookinglist = new ArrayList<String>();
    //  ProgressDialog createprogress;

    public PeopleAdapter(Context context, List<SearchUserList> searchUserLists) {
        this.mContext = context;
        this.searchUserLists = searchUserLists;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance;
        RoundedImageView image;
        LinearLayout like, wave;
        ImageView iv_report;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            distance = view.findViewById(R.id.distance);
            image = view.findViewById(R.id.profilepic);
            like = view.findViewById(R.id.like);
            wave = view.findViewById(R.id.wave);
            iv_report = view.findViewById(R.id.iv_report);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final SearchUserList profile = searchUserLists.get(position);
        holder.name.setText(profile.getName() + ", " + profile.getAge());
        holder.distance.setText(profile.getHometown());
        // holder.image.setImageURI(profile.getPhoto());

        if (searchUserLists.get(position).getPhoto().equals("") || searchUserLists.get(position).getPhoto().equals("null")) {
            holder.image.setImageResource(R.drawable.default_head_icon);
        } else {
            String photourl = "https://urban.network/" + searchUserLists.get(position).getPhoto();
            Picasso.with(mContext).load(photourl).into(holder.image);
        }

        // RoundedImageView profilepic = view.findViewById(R.id.profilepic);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle sbundle = new Bundle();
                sbundle.putString("profileId", profile.getID());
                sbundle.putString("profilepic", profile.getPhoto());
                sbundle.putString("name", profile.getName());
                sbundle.putString("age", profile.getAge());
                sbundle.putString("interest", profile.getInterest());
                sbundle.putString("looking", profile.getLooking());
                sbundle.putString("morepic", profile.getMorepic());
                sbundle.putString("aboutme", profile.getAboutme());
                BottomsheetBothLikeFragment bottomSheet = new BottomsheetBothLikeFragment();
                bottomSheet.setArguments(sbundle);
                bottomSheet.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        holder.wave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle sbundle = new Bundle();
                sbundle.putString("profileId", profile.getID());
                sbundle.putString("profilepic", profile.getPhoto());
                sbundle.putString("name", profile.getName());
                sbundle.putString("age", profile.getAge());
                sbundle.putString("interest", profile.getInterest());
                sbundle.putString("looking", profile.getLooking());
                sbundle.putString("morepic", profile.getMorepic());
                sbundle.putString("aboutme", profile.getAboutme());

                BottomsheetWaveFragment bottomSheet = new BottomsheetWaveFragment();
                bottomSheet.setArguments(sbundle);
                bottomSheet.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle sbundle = new Bundle();
                sbundle.putString("profileId", profile.getID());
                sbundle.putString("profilepic", profile.getPhoto());
                sbundle.putString("name", profile.getName());
                sbundle.putString("age", profile.getAge());
                sbundle.putString("interest", profile.getInterest());
                sbundle.putString("looking", profile.getLooking());
                sbundle.putString("morepic", profile.getMorepic());
                sbundle.putString("aboutme", profile.getAboutme());
                sbundle.putString("hometown", profile.getHometown() != null ? profile.getHometown() : "");
                PeopleDetailsFragment peopleDetailsFragment =new PeopleDetailsFragment();
                peopleDetailsFragment.setArguments(sbundle);
                FragmentTransaction ft = ((NewHomeActivity)mContext).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, peopleDetailsFragment);
                ft.commit();
            }
        });

        holder.iv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext.getApplicationContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.report, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.item_report) {
                            //Toast.makeText(mContext, profile_id, Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_report, (LinearLayout) v.findViewById(R.id.ll_report));
                            Spinner sp_reason = view.findViewById(R.id.reason);
                            EditText et_note = view.findViewById(R.id.et_note);

                            builder.setView(view);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String strReason = sp_reason.getSelectedItem().toString();
                                    String strNote = et_note.getText().toString();
                                    new PeopleAdapter.ReportUser(profile_id, profile.getID(), strReason, strNote, position).execute();
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(mContext, profile.getID(), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog reportDlg = builder.create();
                            reportDlg.show();
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount()  {
        return searchUserLists.size();
    }

    private class ReportUser extends AsyncTask<Void, Void, Void> {
        String userid;
        String reportid;
        String reason;
        String note;
        int posistion;

        public ReportUser(String userid, String reportid, String reason, String note, int position) {
            this.userid = userid;
            this.reportid = reportid;
            this.reason = reason;
            this.note = note;
            this.posistion = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/reportUser.php?";
            String parameters = "user=" +  this.userid +
                                "&user4=" + this.reportid +
                                "&reason=" + this.reason +
                                "&message=" + this.note;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String message = jsonObj.getString("m");

                    if (message.equals("success")) {
                        report_success = "1";
                    } else {
                        report_success = "0";
                    }
                } catch (final JSONException e) {
                    report_success = "0";
                }
            } else {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // WeiboDialogUtils.closeDialog(loadingDialog);
            if (report_success.equals("1")) {
                searchUserLists.remove(this.posistion);
                notifyDataSetChanged();
                Toast.makeText(mContext, "Successfully reported!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Failed to report", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

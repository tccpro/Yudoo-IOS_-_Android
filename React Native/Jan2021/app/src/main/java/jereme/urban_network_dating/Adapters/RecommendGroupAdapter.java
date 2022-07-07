package jereme.urban_network_dating.Adapters;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;
import jereme.urban_network_dating.fragments.GroupDetailFragment;

import static jereme.urban_network_dating.HomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;

public class RecommendGroupAdapter  extends RecyclerView.Adapter<RecommendGroupAdapter.MyViewHolder> {

    private List<GroupList> groupList;
    private Context context;
    String addGroupMessage = "", addMessageResoponse = "";
    String selectedUsername="", selectedUserPhotoUrl="", selectedGroupID="", selectedGroupType="";
    Dialog loadingDialog;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, genre;
        RoundedImageView image;
        Button join_group ,visit;
        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.interest);
            image = view.findViewById(R.id.groupimag);
            join_group = view.findViewById(R.id.join_group_btn);
            visit = view.findViewById(R.id.visit);
        }
    }
    public RecommendGroupAdapter(Context context,List<GroupList> groupList) {
        this.context=context;
        this.groupList=groupList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_group_list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        GroupList movie = groupList.get(position);
        if(movie.getZoneTitle().length()>13){
            holder.title.setText(movie.getZoneTitle().substring(0,12));
      } else {
            holder.title.setText(movie.getZoneTitle());
        }
        if(movie.getAddress().length()>13){
            holder.genre.setText(movie.getAddress().substring(0,12));
        } else {
            holder.genre.setText(movie.getAddress());
        }
        //holder.image.setImageResource(movie.getPhoto());
        holder.visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupList thisItem = groupList.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("photo",thisItem.getPhoto());
                bundle.putString("title",thisItem.getZoneTitle());
                bundle.putString("desc",thisItem.getAddress());
                bundle.putString("groupId",thisItem.getID());
                bundle.putString("status",thisItem.getStatus());
                GroupDetailFragment groupDetailFragment = new GroupDetailFragment();
                groupDetailFragment.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) view.getContext()).getSupportFragmentManager(); // instantiate your view context
                FragmentTransaction ft = fragmentManager.beginTransaction();
//                FragmentManager fragmentManager = context.getSupportFragmentManager();
//                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.fragment_container, groupDetailFragment);
                ft.commit();
            }
        });


        if(movie.getStatus().equals("joined")){

            holder.join_group.setVisibility(View.GONE);
        } else {
            holder.join_group.setVisibility(View.VISIBLE);
            holder.join_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GroupList thisItem = groupList.get(position);
                    selectedGroupID = thisItem.getID();
                    addGroupMessage = "";
                    loadingDialog = WeiboDialogUtils.createLoadingDialog(context, "Join Group");
                    loadingDialog.show();
                    new JoinGroup(holder.join_group).execute();
                }
            });
        }
        if( groupList.get(position).getPhoto().equals("") ||  groupList.get(position).getPhoto().equals("null")){
            holder.image.setImageResource(R.drawable.default_head_icon);
        }else {
          //  String photourl = "https://urban.network/pictures/202010/" + groupList.get(position).getPhoto();
            String photourl = "https://urban.network/" + groupList.get(position).getPhoto();
            Picasso.with(context).load(photourl).into(holder.image);
        }

    }
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    private class JoinGroup extends AsyncTask<Void, Void, Void> {
        Button join_group;
        public JoinGroup(Button join_group) {
            this.join_group = join_group;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/joingroup.php?";
            String parameters = "user=" +  profile_id +
                    "&group=" + selectedGroupID;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        //addGroupMessage = c.getString("message");
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,
                                "Couldn't get json from server for join group",
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
            if(addGroupMessage.equals("success")) {
                join_group.setVisibility(View.GONE);
                new AlertDialog.Builder(context)
                        .setTitle("Join Group")
                        .setMessage("joined successfully")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                //      HomeActivity homeActivity = new HomeActivity();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Join Group")
                        .setMessage("You have already joined into this group!")

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
}

package jereme.urban_network_dating.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import com.robert.autoplayvideo.CustomViewHolder;
import com.robert.autoplayvideo.VideosAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.List.ArticleList;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.R;
import jereme.urban_network_dating.Utils.UnixToHuman;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.locale_name;
import static jereme.urban_network_dating.NewHomeActivity.profile_photoUrl;
import static jereme.urban_network_dating.NewHomeActivity.profile_id;

public class ArticleListAdapter extends VideosAdapter {
    Glide glide;
    private int pos = 0;
    private MediaController mMediaController;
    String uploadArticleMessage, uploadlikeMessage;
    private Context mContext;
    private String groupType = "";
    private ArrayList<ArticleList> zonelist;
    private List<GroupList> recommentgroupList;
    private CommentsAdapder messageListAdapter;
    String articleid = ""; 
    private RecommendGroupAdapter mrAdapter;
    String gropid = "";

    private static int _counter = 0;
    private ArrayList<MessageList> commentlist = new ArrayList<>();
    private final Picasso picasso;

    public ArticleListAdapter(Context mContenxt, ArrayList<ArticleList> zone_list, String groupType, Picasso picasso, String selectedGroupID) {
        this.groupType = groupType;
        this.mContext = mContenxt;
        this.zonelist = zone_list;
        this.picasso = picasso;
        //  this.glide = glide;
        this.gropid = selectedGroupID;
    }

    public ArticleListAdapter(Context mContenxt, ArrayList<ArticleList> zone_list, String groupType, ArrayList<GroupList> recommentList, Picasso picasso, String userid) {
        this.recommentgroupList = recommentList;
        this.groupType = groupType;
        this.mContext = mContenxt;
        this.zonelist = zone_list;
        this.picasso = picasso;
        this.gropid = userid;
    }

    private String TAG = "AutoPlayVideoAdapter";

//    private final List<VideoModel> list;
//    private final Picasso picasso;

    public class MyViewHolder extends CustomViewHolder {
        final TextView tv, userName, title, titlerecommens, likecount, commentcount;
        final ImageView img_vol, img_playback, likeimage;
        final RoundedImageView userIcon, tvFile;
        RecyclerView recommendrecyclerView;
        CardView videoview;
        LinearLayout viewcomment, like;
        //  to mute/un-mute video (optional)
        ProgressBar progressBar;
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x);
            tv = x.findViewById( R.id.tv);
            title = x.findViewById( R.id.item_title);
            img_vol = x.findViewById( R.id.img_vol);
            img_playback = x.findViewById(R.id.img_playback);
            userIcon = x.findViewById(R.id.fb_user_icon);
            tvFile = x.findViewById(R.id.item_file);
            userName = x.findViewById(R.id.fb_user_name);
            videoview = x.findViewById(R.id.video);
            recommendrecyclerView = x.findViewById(R.id.recommend_group_recyclerView);
            titlerecommens = x.findViewById(R.id.titlerecommens);
            like = x.findViewById(R.id.like);
            viewcomment = x.findViewById(R.id.commend);
            likecount = x.findViewById(R.id.likecount);
            commentcount = x.findViewById(R.id.commendtext);
            likeimage = x.findViewById(R.id.likeimage);
            progressBar = x.findViewById(R.id.articlelikeBar);
        }

        //override this method to get callback when video starts to play
        @Override
        public void videoStarted() {
            super.videoStarted();
            img_playback.setImageResource(R.drawable.ic_pause);

            if (isMuted) {
                muteVideo();
                img_vol.setImageResource(R.drawable.ic_mute);
            } else {
                unmuteVideo();
                img_vol.setImageResource(R.drawable.ic_unmute);
            }
        }
        @Override
        public void pauseVideo() {
            super.pauseVideo();
            img_playback.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        String filename = zonelist.get(position).getFile();
        ((MyViewHolder) holder).userName.setText(zonelist.get(position).getUser());
        ((MyViewHolder) holder).title.setText(zonelist.get(position).getTitle());
        Date currentdate = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

//        try {
//            long time = sdf.parse(zonelist.get(position).getDate()).getTime();
//            ((MyViewHolder) holder).tv.setText(UnixToHuman.getTimeAgo(time));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String timeStamp = "";
//        Date createdate = null;

//        try {
//            createdate = format.parse(zonelist.get(position).getDate());
//            System.out.println(createdate);
//
//            if (createdate.getDate() - currentdate.getDate() > 0) {
//                timeStamp = createdate + " ago";
//            } else if (createdate.getHours() - currentdate.getHours() > 0) {
//                timeStamp = createdate.getHours() + " hrs ago";
//            } else if (createdate.getMinutes() - currentdate.getMinutes() > 0) {
//                timeStamp = createdate.getHours() + " mins ago";
//            } else {
//                timeStamp = "now";
//            }
//
//        //    createdate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(zonelist.get(position).getDate());
//            //new SimpleDateFormat("yyyyMMdd_HHmmss").parse(zonelist.get(position).getDate());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        try {
            ((MyViewHolder) holder).tv.setText(getFormate(zonelist.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //((MyViewHolder) holder).tv.setText(zonelist.get(position).getDate() + " ago");
        ((MyViewHolder)holder).likecount.setText(zonelist.get(position).getLikecount() + " " + mContext.getResources().getString(R.string.likes));
        ((MyViewHolder)holder).commentcount.setText(zonelist.get(position).getCommendCount() + " " + mContext.getResources().getString(R.string.comments));
        ((MyViewHolder)holder).progressBar.setVisibility(View.GONE);

        if (zonelist.get(position).getLike() == 1) {
            ((MyViewHolder)holder).likeimage.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            ((MyViewHolder)holder).likeimage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        //todo
        if (zonelist.get(position).getUserimage().equals("null") || zonelist.get(position).getUserimage().equals("")) {
            ((MyViewHolder) holder).userIcon.setImageResource(R.drawable.default_head_icon);
            //  tvFile.setText(mContext.getResources().getString(R.string.file) + ": " + mContext.getResources().getString(R.string.nothing));
        } else {
            String pic = zonelist.get(position).getUserimage();
            String a[] = pic.split("\\.");

            if (a.length > 1)
                pic = pic;
            else
                pic = pic + ".jpg";

            String photourl = "https://urban.network/pictures/" + pic;
            //  String photourl = "https://urban.network/pictures/202010/" + zonelist.get(position).getFile();
            Picasso.with(mContext).load(photourl).into(((MyViewHolder) holder).userIcon);
            //  tvFile.setText(mContext.getResources().getString(R.string.file) + ": " + zonelist.get(position).getFile());
        }

//        boolean bResponse = exists(zonelist.get(position).getFile());

//        if (bResponse == true) {
            if (filename.substring(filename.length() - 4, filename.length()).toLowerCase().equals("jpeg") || filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("jpg")
                    || filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("png")) {
                holder.setImageUrl(zonelist.get(position).getFile());
                picasso.load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getImageView());
                ((MyViewHolder) holder).videoview.setVisibility(View.VISIBLE);
                //  ((MyViewHolder) holder).tvFile.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).img_vol.setVisibility(View.GONE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.GONE);
//                String photourl = "https://urban.network/pictures/202010/" + zonelist.get(position).getFile();
//                Picasso.with(mContext).load(photourl).into(((MyViewHolder) holder).tvFile);
                Log.e(TAG, "--->ImageUrl=" + holder.getImageUrl());
            } else if (filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("mp4")) {
                ((MyViewHolder) holder).videoview.setVisibility(View.VISIBLE);

//                String a[] = zonelist.get(position).getFile().split(" ");
//
//                if (a.length == 1) {
                    holder.setVideoUrl(zonelist.get(position).getFile());
                    holder.setImageUrl(zonelist.get(position).getFile());

                    new AsyncTask<Void, Void, Bitmap>() {

                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            Bitmap bitmap = null;
                            String videoPath = zonelist.get(position).getFile();
                            MediaMetadataRetriever mediaMetadataRetriever = null;

                            try {
                                mediaMetadataRetriever = new MediaMetadataRetriever();
                                if (Build.VERSION.SDK_INT >= 14)
                                    // no headers included
                                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                                else
                                    mediaMetadataRetriever.setDataSource(videoPath);
                                //   mediaMetadataRetriever.setDataSource(videoPath);
                                bitmap = mediaMetadataRetriever.getFrameAtTime();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (mediaMetadataRetriever != null)
                                    mediaMetadataRetriever.release();
                            }
                            return bitmap;
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            if (bitmap != null)
                                holder.getImageView().setImageBitmap(bitmap);
                        }
                    }.execute();

                    holder.setLooping(true);

                    ((MyViewHolder) holder).img_playback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.isPlaying()) {
                                holder.pauseVideo();
                                holder.setPaused(true);
                            } else {
                                holder.playVideo();
                                holder.setPaused(false);
                            }
                        }
                    });

                    //to mute/un-mute video (optional)
                    ((MyViewHolder) holder).img_vol.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (((MyViewHolder) holder).isMuted) {
                                holder.unmuteVideo();
                                ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                            } else {
                                holder.muteVideo();
                                ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                            }
                            ((MyViewHolder) holder).isMuted = !((MyViewHolder) holder).isMuted;
                        }
                    });

                    ((MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
               // }

            } else {
                ((MyViewHolder) holder).videoview.setVisibility(View.GONE);
            }
//        }
        if (groupType.equals("recomment")) {
            if (position == (zonelist.size() > 2 ? 1 : 0)) {
                ((MyViewHolder) holder).titlerecommens.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).recommendrecyclerView.setVisibility(View.VISIBLE);
                mrAdapter = new RecommendGroupAdapter(mContext, recommentgroupList);
                LinearLayoutManager mrLayoutManager = new LinearLayoutManager(mContext);
                mrLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                ((MyViewHolder) holder).recommendrecyclerView.setLayoutManager(mrLayoutManager);
                ((MyViewHolder) holder).recommendrecyclerView.setItemAnimator(new DefaultItemAnimator());
                ((MyViewHolder) holder).recommendrecyclerView.setAdapter(mrAdapter);
            }
        }

        ((MyViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadlikeMessage = "";
                ((MyViewHolder)holder).progressBar.setVisibility(View.VISIBLE);
                ((MyViewHolder)holder).likeimage.setVisibility(View.GONE);

                new Thread(new Runnable() {
                    public void run() {
                        int i = 0;

                        if (zonelist.get(position).getLike() == 1) i = 0;
                        else i = 1;

                        new Addlike(i, ((MyViewHolder)holder).likeimage, ((MyViewHolder)holder).progressBar, zonelist.get(position).getId(), position,((MyViewHolder)holder).likecount).execute();
                    }
                }).start();
            }
        });

        //  commendData();

        ((MyViewHolder) holder).viewcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController = new MediaController(view.getContext());
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = layoutInflater.inflate(R.layout.comment_popup, null);
                RoundedImageView profilepic = layout.findViewById(R.id.profilepic);
                ListView messagerecyclerView = layout.findViewById(R.id.messages_view);
                RoundedImageView postimage = layout.findViewById(R.id.postpic);
                ProgressBar commentspinner = layout.findViewById(R.id.commentprogressBar);
                VideoView  mVideoView = (VideoView) layout.findViewById(R.id.video_view);
                EditText getcommend = layout.findViewById(R.id.sendcommend);
                ImageButton sendcommend = layout.findViewById(R.id.sendButton);
//                ImageView likeimage = layout.findViewById(R.id.likeimage);
//                CustomVideoPlayer customVideoPlayer = layout.findViewById(R.id.customVideoPlayer);
                TextView title = layout.findViewById(R.id.title);
                TextView times = layout.findViewById(R.id.interest1);
                TextView username = layout.findViewById(R.id.username);
//                TextView commentscount = layout.findViewById(R.id.commentscount);
//                TextView likecounts = layout.findViewById(R.id.likecount);
                title.setText(zonelist.get(position).getTitle());
                username.setText(zonelist.get(position).getUser());

                if (zonelist.get(position).getUserimage().equals("null") || zonelist.get(position).getUserimage().equals("")) {
                    profilepic.setImageResource(R.drawable.default_head_icon);
                    //  tvFile.setText(mContext.getResources().getString(R.string.file) + ": " + mContext.getResources().getString(R.string.nothing));
                } else {
                    String pic = zonelist.get(position).getUserimage();
                    String a[] = pic.split("\\.");

                    if (a.length > 1)
                        pic = pic;
                    else
                        pic = pic + ".jpg";

                    String photourl = "https://urban.network/pictures/" + pic;
                    //  String photourl = "https://urban.network/pictures/202010/" + zonelist.get(position).getFile();
                    Picasso.with(mContext).load(photourl).into(profilepic);
                    //  tvFile.setText(mContext.getResources().getString(R.string.file) + ": " + zonelist.get(position).getFile());
                }

//                likecounts.setText(zonelist.get(position).getLikecount() + " " + mContext.getResources().getString(R.string.likes));
//                commentscount.setText(zonelist.get(position).getCommendCount() + " " + mContext.getResources().getString(R.string.comments));

                try {
                     times.setText(getFormate(zonelist.get(position).getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                articleid = zonelist.get(position).getId();
                commentlist = new ArrayList<>();
                commentspinner.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    public void run() {
                        new GetComments(messagerecyclerView,zonelist.get(position).getId(), commentspinner).execute();
                    }
                }).start();

                _counter = 0;
                //  commentscount.setText(commentlist.size() + " comments");
                sendcommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadArticleMessage = "";
                        if (!getcommend.getText().toString().equals("")) {
                            //  _counter = _counter + 1;
                            int count = Integer.parseInt(zonelist.get(position).getCommendCount()) + 1;
                            ((MyViewHolder)holder).commentcount.setText(count + " " + mContext.getResources().getString(R.string.comments));
                            zonelist.get(position).sertComment(count + "");
                            notifyDataSetChanged();

                            new Thread(new Runnable() {
                                public void run() {
                                    new AddComments(getcommend.getText().toString(), Integer.parseInt(zonelist.get(position).getCommendCount()), ((MyViewHolder)holder).commentcount).execute();
                                }
                            }).start();

                            if (commentlist.size() == 0) {
                                commentlist.add(new MessageList("name", getcommend.getText().toString(), "pictures/"+profile_photoUrl));
                                messageListAdapter = new CommentsAdapder(mContext, commentlist);
                                messagerecyclerView.setAdapter(messageListAdapter);
                            } else {
                                commentlist.add(new MessageList("name", getcommend.getText().toString(), "pictures/"+profile_photoUrl));
                                // next thing you have to do is check if your adapter has changed
                                messageListAdapter.notifyDataSetChanged();
                            }
                            getcommend.setText("");
                        }
                    }
                });

                if (filename.substring(filename.length() - 4, filename.length()).toLowerCase().equals("jpeg") || filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("jpg")
                 || filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("png")) {
                    mVideoView.setVisibility(View.GONE);
                    if (zonelist.get(position).getFile().equals("null") || zonelist.get(position).getFile().equals("")) {
                        postimage.setVisibility(View.GONE);
                        //  tvFile.setText(mContext.getResources().getString(R.string.file) + ": " + mContext.getResources().getString(R.string.nothing));
                    } else {
                        postimage.setVisibility(View.VISIBLE);
                        String photourl = zonelist.get(position).getFile();
                        //  String photourl = "https://urban.network/pictures/202010/" + zonelist.get(position).getFile();
                        Picasso.with(mContext).load(photourl).into(postimage);
                        //  tvFile.setText(mContext.getResources().getString(R.string.file) + ": " + zonelist.get(position).getFile());
                    }
                } else {
//                    customVideoPlayer.setVisibility(View.VISIBLE);
                    mVideoView.setVisibility(View.VISIBLE);
                    postimage.setVisibility(View.GONE);
                    mVideoView.setVideoPath(zonelist.get(position).getFile());
                    mVideoView.setMediaController(mMediaController);
                    mVideoView.setKeepScreenOn(true);
                    mVideoView.requestFocus();
                    //  mVideoView.start();

                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                             mVideoView.start();
                         }
                    });

                    mVideoView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (mVideoView.isPlaying()) {
                                mVideoView.pause();
                                mMediaController.show(0);
                                pos = mVideoView.getCurrentPosition();
                                return false;
                            } else {
                                mMediaController.hide();
                                mVideoView.seekTo(pos);
                                mVideoView.start();
                                return false;
                            }
                        }
                    });
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(layout);
                AlertDialog alert = builder.create();
                alert.getWindow().setBackgroundDrawableResource(R.drawable.circleimg);
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                // alert.setTitle(getResources().getString(R.string.birthday));
                alert.show();
            }
        });

//        if (filename.substring(filename.length() - 4, filename.length()).toLowerCase().equals("jpeg") || filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("jpg")
//                || filename.substring(filename.length() - 3, filename.length()).toLowerCase().equals("png")) {
//
//        } else {
////            ((MyViewHolder) holder).videoview.setVisibility(View.VISIBLE);
////            ((MyViewHolder) holder).tvFile.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return zonelist .size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String getFormate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //  d.setTimeZone(TimeZone.getTimeZone("GMT"));
        //  Log.d("Date", String.valueOf(d));
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = UnixToHuman.getTimeAgo(d.getTime(),mContext);
        return monthName;
    }

    private class Addlike extends AsyncTask<Void, Void, Void> {
        ImageView likeimage;
        ProgressBar progressBar;
        TextView likecount;
        String id;
        int like;
        int position;

        public Addlike(int like, ImageView likeimage, ProgressBar progressBar, String id, int position, TextView likecount) {
            this.like = like;
            this.likeimage = likeimage;
            this.progressBar = progressBar;
            this.id = id;
            this.position = position;
            this.likecount = likecount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //  String sub_url = "group/searchgroup.php??";
            String sub_url = "group/addlike.php?";
            String parameters = "user=" + profile_id +
                               "&articleid=" + id +
                               "&like=" + like;

            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    uploadlikeMessage = jsonObj.getString("message");
                    //  JSONArray contacts = new JSONArray(jsonStr);

//                    for (int i = 0; i < contacts.length(); i++) {
//                        JSONObject c = contacts.getJSONObject(i);
//                        String id = c.getString("id");
//                        String type = c.getString("comment");
//                        String name = c.getString("name");
//                        String photo = c.getString("image");
//                        commentlist.add(new MessageList(name,type,photo));
//                    }
                } catch (final JSONException e) {
                    System.out.println("ErrorEx---" + e);

                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            likeimage.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                //    WeiboDialogUtils.closeDialog(loadingDialog);
                if(mContext!=null) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            likeimage.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext,
                                    "Couldn't get json from server while loading group",
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
            progressBar.setVisibility(View.GONE);
            likeimage.setVisibility(View.VISIBLE);
            if(uploadlikeMessage.equals("success")) {
                if(like==1) {
                    likeimage.setImageResource(R.drawable.ic_favorite_black_24dp);
                    zonelist.get(position).setLike(1);
                    likecount.setText(Integer.parseInt(zonelist.get(position).getLikecount())+1 +" "+ mContext.getResources().getString(R.string.likes));
                }
                else
                   { likeimage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                       zonelist.get(position).setLike(0);
                       likecount.setText(Integer.parseInt(zonelist.get(position).getLikecount()) +" "+ mContext.getResources().getString(R.string.likes));
                   }

              //  Toast.makeText(mContext,"Like is added successfully",Toast.LENGTH_LONG).show();

//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Upload Article")
//                        .setMessage("New Article is added successfully")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
            } else {
                Toast.makeText(mContext,"failed",Toast.LENGTH_LONG).show();
            }

        }
    }


    private class AddComments extends AsyncTask<Void, Void, Void> {

        String comments;
        int count;
        TextView commentcount;
        public AddComments(String comments, int counts, TextView commentcount) {
            this.comments=comments;
            count=counts;
            this.commentcount =commentcount;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //String sub_url = "group/searchgroup.php??";
            String sub_url = "group/commentsadd.php?";

            String parameters = "user=" +  profile_id +
                    "&user4=" + articleid +
                    "&comments=" + comments+ "&lang=" + locale_name;

            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {


                        JSONObject jsonObj = new JSONObject(jsonStr);
                       uploadArticleMessage = jsonObj.getString("message");


                  //  JSONArray contacts = new JSONArray(jsonStr);

//                    for (int i = 0; i < contacts.length(); i++) {
//                        JSONObject c = contacts.getJSONObject(i);
//                        String id = c.getString("id");
//                        String type = c.getString("comment");
//                        String name = c.getString("name");
//                        String photo = c.getString("image");
//
//                        commentlist.add(new MessageList(name,type,photo));
//
//                    }
                } catch (final JSONException e) {
                    System.out.println("ErrorEx---"+e);
                }


            } else {
                //    WeiboDialogUtils.closeDialog(loadingDialog);
                if(mContext!=null) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,
                                    "Couldn't get json from server while loading group",
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
            if(uploadArticleMessage.equals("success")) {


            //  commentcount.setText((count +1) +" "+ mContext.getResources().getString(R.string.comments));
              //  Toast.makeText(mContext,"Comment is added successfully",Toast.LENGTH_LONG).show();

//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Upload Article")
//                        .setMessage("New Article is added successfully")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
            } else {
                Toast.makeText(mContext,"failed",Toast.LENGTH_LONG).show();
            }

        }
    }




    private class GetComments extends AsyncTask<Void, Void, Void> {
        ListView messagerecyclerView;
        String id;
        ProgressBar commentspinner;
        public GetComments(ListView messagerecyclerView, String id, ProgressBar commentspinner) {
            this.messagerecyclerView=messagerecyclerView;
            this.id = id;
            this.commentspinner=commentspinner;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //String sub_url = "group/searchgroup.php??";
            String sub_url = "group/comments.php?";

            String parameters = "userid=" +  gropid +
                        "&articleid=" + id+  "&lang=" + locale_name  ;

            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String type = c.getString("comments");
                        //String name = c.getString("name");
                        String photo = c.getString("image");

                        commentlist.add(new MessageList("name",type,photo));

                    }
                } catch (final JSONException e) {
                    if(mContext!=null) {
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commentspinner.setVisibility(View.GONE);
                                Toast.makeText(mContext,
                                        e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }


            } else {
            //    WeiboDialogUtils.closeDialog(loadingDialog);
                if(mContext!=null) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commentspinner.setVisibility(View.GONE);
                            Toast.makeText(mContext,
                                    "Couldn't get json from server while loading group",
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
            commentspinner.setVisibility(View.GONE);
            if(commentlist.size()>0) {
                Collections.reverse(commentlist); // ADD THIS LINE TO REVERSE ORDER!
                messageListAdapter = new CommentsAdapder(mContext,commentlist);

                messagerecyclerView.setAdapter(messageListAdapter);

              //  YourList.notifyDataSetChanged;
                messagerecyclerView.smoothScrollToPosition(commentlist.size() + 1);
            } else {
              //  WeiboDialogUtils.closeDialog(loadingDialog);
            }
        }
    }

    public static boolean exists(String URLName){


        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =  (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            System.out.println(con.getResponseCode());
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}


package jereme.urban_network_dating.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robert.autoplayvideo.CustomRecyclerView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.ButterKnife;
import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.ArticleListAdapter;
import jereme.urban_network_dating.Adapters.CommentsAdapder;
import jereme.urban_network_dating.Adapters.GroupAdapter;
import jereme.urban_network_dating.Adapters.GroupTitleAdapter;
import jereme.urban_network_dating.Adapters.RecommendGroupAdapter;
import jereme.urban_network_dating.List.ArticleList;
import jereme.urban_network_dating.List.GroupList;
import jereme.urban_network_dating.List.MessageList;
import jereme.urban_network_dating.List.VideoModel;
import jereme.urban_network_dating.R;

import static jereme.urban_network_dating.NewHomeActivity.profile_id;
import static jereme.urban_network_dating.LoginActivity.base_url;
import static jereme.urban_network_dating.LoginActivity.locale_name;


public class GroupsFragment extends Fragment implements GroupTitleAdapter.OnChangTypeClickListener, CreatePostBottomSheet.OnCreatePostClickListener,CreateGroupBottomSheet.OnCreateGroupClickListener {
    private List<GroupList> groupList = new ArrayList<>();
    private List<GroupList> recommentgroupList = new ArrayList<>();
    private final List<VideoModel> modelList = new ArrayList<>();
    ArrayList<ArticleList> articles = new ArrayList<>();
    private ArrayList<MessageList> commentlist = new ArrayList<>();
    private List<String> titlelist = new ArrayList<>();
    ArticleListAdapter articleListAdapter;
    private CommentsAdapder messageListAdapter;
    CustomRecyclerView articleList;
    private GroupAdapter mAdapter;
    String getarticle = "0", getgroup = "0";
    private RecommendGroupAdapter mrAdapter;
    String selectedUsername = "", selectedUserPhotoUrl = "", selectedGroupID = "", selectedGroupType = "";
    ImageView ivSelectedUserPhoto, ivShowMessage, ivHideMessage, search;
    TextView tvSelectedUserName;
    String addGroupMessage = "", addMessageResoponse = "";
    RecyclerView grouplistrecyclerView, recommendrecyclerView;
    private GroupTitleAdapter titleadapter;
    List<GroupList> filteredArticleList = new ArrayList<>();
    Dialog loadingDialog;
    LinearLayout createpost;
    CreatePostBottomSheet.OnCreatePostClickListener callback;
    CreateGroupBottomSheet.OnCreateGroupClickListener groupCallback;
    private int page = 0;
    private ProgressBar articlespinner, groupspinner;
    Picasso p;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_groups, null);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        search = view.findViewById(R.id.settings);
        callback = this;
        groupCallback = this;
        articlespinner = (ProgressBar)view.findViewById(R.id.articleprogressBar);
        articlespinner.setVisibility(View.GONE);
        groupspinner = (ProgressBar)view.findViewById(R.id.groupprogressBar);
        groupspinner.setVisibility(View.GONE);
        titlelist.add("All");
        titlelist.add("Movie & Tv");
        titlelist.add("Games");
        titlelist.add("Animals");
        titlelist.add("News");
        titlelist.add("Education");
        ButterKnife.bind(getActivity());
        p = Picasso.with(getActivity());

        ImageView addgroup = view.findViewById(R.id.add);
        articleList = view.findViewById(R.id.article_list);
        createpost = view.findViewById(R.id.create_post);

        addgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateGroupBottomSheet bottomSheet = new CreateGroupBottomSheet(groupCallback);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setVisibility(View.GONE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextSubmit(String query) {
               // if (filteredArticleList.contains(query)) {
                    filteredArticleList = groupList.stream().filter(article -> article.getAddress().toLowerCase().contains(query.toLowerCase()) || article.getZoneTitle().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
                    // System.out.println(filteredArticleList);
                    listGroupAdapter(filteredArticleList);
//                } else {
//                    Toast.makeText(getActivity(), "No Match found",Toast.LENGTH_LONG).show();
//                }

                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                //  adapter.getFilter().filter(newText);
                filteredArticleList= groupList.stream().filter(article -> article.getAddress().toLowerCase().contains(newText.toLowerCase()) || article.getZoneTitle().toLowerCase().contains(newText.toLowerCase())).collect(Collectors.toList());
                // System.out.println(filteredArticleList);
                listGroupAdapter(filteredArticleList);
                return false;
            }
        });

        createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle sbundle = new Bundle();
                sbundle.putString("groupId", profile_id);
                CreatePostBottomSheet bottomSheet = new CreatePostBottomSheet(callback);
                bottomSheet.setArguments(sbundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        RecyclerView grouprecyclerView = view.findViewById(R.id.title_recyclerView);
        titleadapter = new GroupTitleAdapter(getActivity(),titlelist,this);
        LinearLayoutManager groupmLayoutManager = new LinearLayoutManager(getActivity());
        groupmLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        grouprecyclerView.setLayoutManager(groupmLayoutManager);
        grouprecyclerView.setItemAnimator(new DefaultItemAnimator());
        grouprecyclerView.setAdapter(titleadapter);
        //  titleadapter.setOnItemClickListener(onTitleItemClickListener);

        grouplistrecyclerView = view.findViewById(R.id.group_recyclerView);
        recommendrecyclerView = view.findViewById(R.id.recommend_group_recyclerView);

        if (groupList.size() == 0) {
            groupList = new ArrayList<>();
            groupList.clear();
            groupspinner.setVisibility(View.VISIBLE);
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//            loadingDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    new GetGroup().execute();
                }
            }).start();
        } else {
            listGroupAdapter(groupList);
        }

        if (articles.size() == 0) {
            articles = new ArrayList<>();
            articles.clear();
//            loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//            loadingDialog.show();
//            new GetGroup().execute();
            articlespinner.setVisibility(View.VISIBLE);
          //  new Thread(new Runnable() {
              //  public void run() {
            new GetArticle().execute();
             //   }
          //  }).start();
        } else {
            articleAdapter(articles);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onChangTypeClick(final String val) {
        // Toast.makeText(getActivity(), "VAL---"+val, Toast.LENGTH_LONG).show();
        filteredArticleList= groupList.stream().filter(article -> article.getAddress().contains(val.equals("All") ? "" : val)).collect(Collectors.toList());
        // System.out.println(filteredArticleList);
        listGroupAdapter(filteredArticleList);
    }

    @Override
    public void onCreatePostClick() {
//        articles = new ArrayList<>();
//        groupList.clear();
//        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//        loadingDialog.show();
//        new GetGroup().execute();
        articlespinner.setVisibility(View.VISIBLE);
        articles = new ArrayList<>();
//        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//        loadingDialog.show();
        new GetArticle().execute();
    }

    @Override
    public void onCreateGroupClick() {
        //  articles = new ArrayList<>();
        groupList = new ArrayList<>();
        groupList.clear();
        groupspinner.setVisibility(View.VISIBLE);
//        loadingDialog = WeiboDialogUtils.createLoadingDialog(getActivity(), "Searching Groups");
//        loadingDialog.show();
        new GetGroup().execute();
    }

    private class GetGroup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //String sub_url = "group/searchgroup.php??";
            String sub_url = "group/listgroup.php?";
            String parameters = "user=" + profile_id;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    Object json = new JSONTokener(jsonStr).nextValue();

                    if (json instanceof JSONObject) {
                        JSONObject contacts = new JSONObject(jsonStr);
                        //  articlespinner.setVisibility(View.GONE);
                        getarticle= contacts.getString("article");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                groupspinner.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),
                                        "Group does not exist",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
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

                            groupList.add(new GroupList(name, type, photo, id, status));
                            filteredArticleList.add(new GroupList(name, type, photo, id, status));
                            recommentgroupList.add(new GroupList(name, type, photo, id, status));
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
                    //groupspinner.setVisibility(View.GONE);
                }
            } else {
                if (getActivity() != null) {
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
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            groupspinner.setVisibility(View.GONE);
            if (groupList.size() > 0) {
                //  mAdapter = new GroupListAdapter(getActivity(), groupList);
                listGroupAdapter(groupList);
                articleAdapter(articles);
            } else {

            }
        }
    }

    private class GetArticle extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "group/article.php?";
            String parameters = "user=" + profile_id +
                    "&lang=" + locale_name +
                    "&page=" + page;
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    Object json = new JSONTokener(jsonStr).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject contacts = new JSONObject(jsonStr);
                        //  articlespinner.setVisibility(View.GONE);
                        getarticle = contacts.getString("article");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                articlespinner.setVisibility(View.GONE);
                                try {
                                    Toast.makeText(getActivity(), contacts.getString("article"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (json instanceof JSONArray) {
                        getarticle = "0";
                        JSONArray contacts = new JSONArray(jsonStr);

                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            String name = c.getString("name");
                            String show = c.getString("show");
                            String date = c.getString("date");
                            String user4 = c.getString("user4");
                            String file = c.getString("file");
                            String username = c.getString("username");
                            String userimage = c.getString("userimage");
                            String commentscount = c.getString("commentscount");
                            String likecount = c.getString("likecount");
                            int like = c.getInt("likes");
                            int page1 = c.getInt("id");
                            //  page = page1;
                            articles.add(new ArticleList(name, show, date, username, "https://urban.network/pictures/" + file, userimage, String.valueOf(page1), likecount, commentscount, like));
                            //  modelList.add(new VideoModel("https://urban.network/pictures/202010/" + file, "https://urban.network/pictures/202010/" + file, "Video1"));
                        }
                    }
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            articlespinner.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articlespinner.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Couldn't get json from server while loading article", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            articlespinner.setVisibility(View.GONE);
            if (articles.size() > 0) {
                articleAdapter(articles);
            }
        }
    }

    private void articleAdapter(ArrayList<ArticleList> articles) {
        articleListAdapter = new ArticleListAdapter(getActivity(), articles, "recomment", (ArrayList<GroupList>) recommentgroupList, p, profile_id);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        articleList.setLayoutManager(mLayoutManager);
        articleList.setItemAnimator(new DefaultItemAnimator());
        //todo before setAdapter
        articleList.setActivity(getActivity());
        //optional - to play only first visible video
        articleList.setPlayOnlyFirstVideo(true); // false by default
        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        articleList.setCheckForMp4(false); //true by default
        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        articleList.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default
        articleList.setDownloadVideos(true); // false by default
        //extra - start downloading all videos in background before loading RecyclerView
        //  List<String> urls = new ArrayList<>();
//                for (VideoModel object : modelList) {
//                    if (object.getVideo_url() != null && object.getVideo_url().contains("http"))
//                        urls.add(object.getVideo_url());
//                }
//                articleList.preDownload(urls);
        articleList.setAdapter(articleListAdapter);
    }

    private void listGroupAdapter(List<GroupList> groupLists) {
        mAdapter = new GroupAdapter(getActivity(), groupLists);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        grouplistrecyclerView.setLayoutManager(mLayoutManager);
        grouplistrecyclerView.setItemAnimator(new DefaultItemAnimator());
        grouplistrecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO: Step 4 of 4: Finally call getTag() on the view.
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            //  viewHolder.getItemId();
            //  viewHolder.getItemViewType();
            //  viewHolder.itemView;
            //  GroupList thisItem = groupList.get(position);
            GroupList thisItem = filteredArticleList.get(position);
            Bundle bundle=new Bundle();
            bundle.putString("photo",thisItem.getPhoto());
            bundle.putString("title",thisItem.getZoneTitle());
            bundle.putString("desc",thisItem.getAddress());
            bundle.putString("groupId",thisItem.getID());
            bundle.putString("status",thisItem.getStatus());
            GroupDetailFragment groupDetailFragment = new GroupDetailFragment();
            groupDetailFragment.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, groupDetailFragment);
            ft.addToBackStack(groupDetailFragment.getTag());
            ft.commit();

//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            Fragment prev = getFragmentManager().findFragmentByTag("dialog");

//            if (prev != null) {
//                ft.remove(prev);
//            }

//            ft.addToBackStack(null);
//            DialogFragment dialogFragment = new GroupDetailFragment();
//            dialogFragment.setArguments(bundle);
//            dialogFragment.show(ft, "dialog");
//            Toast.makeText(getActivity(), "You Clicked: " + thisItem.getZoneTitle(), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener onTitleItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO: Step 4 of 4: Finally call getTag() on the view.
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            //  mAdapter.filter(newText);
            //  viewHolder.getItemId();
            //  viewHolder.getItemViewType();
            //  viewHolder.itemView;
            //  titlelist.get(position);

             Toast.makeText(getActivity(), "You Clicked: " + titlelist.get(position), Toast.LENGTH_SHORT).show();
        }
    };

















}

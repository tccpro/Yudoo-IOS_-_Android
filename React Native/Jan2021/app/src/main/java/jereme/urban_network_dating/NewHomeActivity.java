package jereme.urban_network_dating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import androidx.fragment.app.FragmentManager;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Chats.ChatTapFragment;
import jereme.urban_network_dating.fragments.CreateGroupBottomSheet;
import jereme.urban_network_dating.fragments.CreatePostBottomSheet;
import jereme.urban_network_dating.fragments.GroupsFragment;
import jereme.urban_network_dating.fragments.PeopleFragment;
import jereme.urban_network_dating.fragments.PeopleNotificationFragment;
import jereme.urban_network_dating.fragments.ProfileFragment;


public class NewHomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ChatTapFragment.OnSearchViewText {
    Dialog loadingDialog;
    int success_flag = 0;
    Handler handler;
    BottomNavigationView navigation;
    public static String profile_id, profile_name, profile_searchName, profile_email, profile_password, profile_picture,
            profile_hometown, profile_currentCity, profile_location, profile_membership;
    public static String profile_paypalEmail, profile_information, profile_status, profile_photoUrl="", profile_points,
            profile_dating, profile_birthday, profile_moreimage, profile_interest, profile_looking, profile_about;
    public static String profile_friendrequest = "", profile_membermsg, profile_membersearch, profile_userquote;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);
        // loading the default fragment
        loadFragment(new GroupsFragment());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparentColor, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparentColor));
        }

        // getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        //  loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Loading Data");
        //  loadingDialog.show();
        handler = new Handler();
        new GetProfile().execute();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = new GroupsFragment();
                break;
            case R.id.navigation_dashboard:
                fragment = new PeopleFragment();
                break;
            case R.id.navigation_chats:
                //  Intent intent = new Intent(NewHomeActivity.this, ContactListActivity.class);
                //  startActivity(intent);
                //  fragment = new ChatsFragment();
                fragment = new ChatTapFragment();
                break;
            case R.id.navigation_notifications:
                //  Intent intent = new Intent(NewHomeActivity.this, ContactListActivity.class);
                //  startActivity(intent);
                //  fragment = new ChatsFragment();
                fragment = new PeopleNotificationFragment();
                break;
            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        // switching fragment
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(backStateName)
                    .commit();

            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 || requestCode == 1 || requestCode == 150 || requestCode == 5) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            // ProfileFragment mFragment = new ProfileFragment();
            if (currentFragment instanceof ProfileFragment) {
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }  else {
                CreateGroupBottomSheet bottomSheet = new CreateGroupBottomSheet();
                Fragment current2Fragment = getSupportFragmentManager().findFragmentById(bottomSheet.getId());
                if (current2Fragment instanceof CreateGroupBottomSheet) {
                    current2Fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        } else if (requestCode == 101 || requestCode == 2 || requestCode == 102 || requestCode == 103) {
            CreatePostBottomSheet bottomSheet = new CreatePostBottomSheet();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(bottomSheet.getId());
            // ProfileFragment mFragment = new ProfileFragment();

            if (currentFragment instanceof CreatePostBottomSheet) {
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }

            // Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            // ProfileFragment mFragment = new ProfileFragment();
            // if (currentFragment instanceof GroupDetailFragment) {
            //       currentFragment.onActivityResult(requestCode, resultCode, data);
            // }
            // CreatePostBottomSheet createPostBottomSheet = new CreatePostBottomSheet();
            // if (createPostBottomSheet != null) {
            //       createPostBottomSheet.onActivityResult(requestCode, resultCode, data);
            // }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void searchViewText(String searchtext) {
        Toast.makeText(getApplicationContext(), searchtext, Toast.LENGTH_LONG).show();
    }

    private class GetProfile extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/loadProfile.php?";
            SharedPreferences loadUser = PreferenceManager.getDefaultSharedPreferences(NewHomeActivity.this);
            String userid = loadUser.getString("userid", "0");
            String parameters = "user=" +  userid;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    profile_id = jsonObj.getString("id");
                    profile_name = jsonObj.getString("name");
                    profile_searchName = jsonObj.getString("search_name");
                    profile_email = jsonObj.getString("email");
                    String pic = jsonObj.getString("picture");
                    String a[] = pic.split("\\.");

                    if (a.length > 1)
                         profile_photoUrl = pic;
                    else
                        profile_photoUrl = pic + ".jpg";

                    //  profile_picture = jsonObj.getString("picture");
                    profile_password = jsonObj.getString("password");
                    profile_hometown = jsonObj.getString("hometown");
                    profile_currentCity = jsonObj.getString("current_city");
                    profile_paypalEmail = jsonObj.getString("paypalemail");
                    profile_information = jsonObj.getString("information");
                    profile_status = jsonObj.getString("status");
                    profile_points = jsonObj.getString("points");
                    profile_dating = jsonObj.getString("dating");
                    profile_birthday=jsonObj.getString("birthday");
                    profile_moreimage = jsonObj.getString("morepic");
                    profile_interest = jsonObj.getString("interest");
                    profile_looking = jsonObj.getString("looking");
                    profile_about = jsonObj.getString("aboutme") != null ? jsonObj.getString("aboutme") : "";
                    profile_location = jsonObj.getString("location");
                    profile_membership = jsonObj.getString("membership");
                    success_flag = 1;
                } catch (final JSONException e) {
                    // WeiboDialogUtils.closeDialog(loadingDialog);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Json parsing error getprofile: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                // WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // WeiboDialogUtils.closeDialog(loadingDialog);
            progress.dismiss();
            if (success_flag == 1) {
                // tvProfileName.setText(profile_name);
                // tvProfileStatus.setText(profile_status);
                // tvProfilePoint.setText(profile_points + " points");
                success_flag = 0;
                //  handler.postDelayed(new Runnable() {
                //      @Override
                //      public void run() {
                //          new GetPhoto().execute();
                //      }
                //  }, 1002);
            } else {
                Toast.makeText(getApplicationContext(),"Connect Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        //  if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
        //      getSupportFragmentManager().popBackStackImmediate();
        //     // return;
        //  }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            int index = ((getSupportFragmentManager().getBackStackEntryCount()) - 1);
            if (index != 0)
               getSupportFragmentManager().popBackStack();

            // index == 0 ? index : index-1
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index == 0 ? index : index-1);
            int stackId = backEntry.getId();
            navigation.getMenu().getItem(stackId).setChecked(true);
        }
//        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        if (myFragment != null && myFragment instanceof GroupsFragment) {
//      //      nav_draw = (NavigationView) findViewById(R.id.nav_draw);
////            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
////                getSupportFragmentManager().popBackStack();
////            }
//            navigation.setSelectedItemId(R.id.navigation_home);
//     }   else if (myFragment != null && myFragment instanceof PeopleFragment) {
//            //      nav_draw = (NavigationView) findViewById(R.id.nav_draw);
//           // navigation.getMenu().findItem(R.id.navigation_dashboard).setEnabled(true);
////            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
////                getSupportFragmentManager().popBackStack();
////            }
//            navigation.setSelectedItemId(R.id.navigation_dashboard);
//        } else if (myFragment != null && myFragment instanceof ContactListActivity) {
//            //      nav_draw = (NavigationView) findViewById(R.id.nav_draw);
//          //  navigation.getMenu().findItem(R.id.navigation_notifications).setEnabled(true);
////            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
////                getSupportFragmentManager().popBackStack();
////            }
//            navigation.setSelectedItemId(R.id.navigation_notifications);
//        }  else if (myFragment != null && myFragment instanceof ProfileFragment) {
//            //      nav_draw = (NavigationView) findViewById(R.id.nav_draw);
//           // navigation.getMenu().findItem(R.id.navigation_profile).setEnabled(true);
////            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
////                getSupportFragmentManager().popBackStack();
////            }
//            navigation.setSelectedItemId(R.id.navigation_profile);
//        } else {
//            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                getSupportFragmentManager().popBackStack();
//            }
//        }

//        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        if (myFragment != null && myFragment instanceof GroupsFragment) {
//            finish();
//        } else {
//            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                getSupportFragmentManager().popBackStack();
//            } else {
//               // super.onBackPressed();
//            }
//        }
//        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
//           // finish();
//            super.onBackPressed();
//        } else {
//            finish();
//        }
//        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
//            getFragmentManager().popBackStack();
//        } else {
//            finish();
//          //  super.onBackPressed();
//        }
//        if (currentFragment instanceof OnBackPressed) {
//            ((OnBackPressed) currentFragment).onBackPressed();
//            return;
//        }
     //   super.onBackPressed();
        // Get the current fragment using the method from the second step above...
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//        // Determine whether or not this fragment implements Backable
//        // Do a null check just to be safe
//        if (currentFragment != null && currentFragment instanceof Backable) {
//
//            if (((Backable) currentFragment).onBackPressed()) {
//                getSupportFragmentManager().popBackStack();
//                // If the onBackPressed override in your fragment
//                // did absorb the back event (returned true), return
//                return;
//            } else {
//                // Otherwise, call the super method for the default behavior
//                super.onBackPressed();
//            }
//        }

        // Any other logic needed...
        // call super method to be sure the back button does its thing...
      //  super.onBackPressed();
    }

    public interface OnBackPressed {
        void onBackPressed();
    }

//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() > 0)
//            getFragmentManager().popBackStack();
//        else
//            super.onBackPressed();
//    }

    private class GetPhoto extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/getPhoto.php?";
            String parameters = "user=" + profile_picture;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //  profile_photoUrl = jsonObj.getString("show");
                    success_flag = 1;
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error getphoto: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
    }
}

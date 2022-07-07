package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jereme.urban_network_dating.API.HttpHandler;
import jereme.urban_network_dating.Adapters.TaskListAdapter;
import jereme.urban_network_dating.Adapters.TasklistListAdapter;
import jereme.urban_network_dating.List.TaskList;
import jereme.urban_network_dating.List.TasklistList;
import jereme.urban_network_dating.Utils.GlideToast;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;

public class ManageTaskActivity extends AppCompatActivity {

    TasklistListAdapter tasklistListAdapter;
    TaskListAdapter taskListAdapter;
    ArrayList<TasklistList> tasklists;
    ArrayList<TaskList> tasks;
    Dialog loadingDialog;
    ListView tasklistList, taskList;
    String selectedTasklistID, selectedTasklistName, selectedTasklistPrivacy;
    String selectedTaskID, selectedTaskName, selectedTaskCompletedState;
    TextView tvSelectedTaskListName, tvSelectedTaskListPrivacy;
    TextView tvSelectedTaskName;
    CheckBox cbSelectedTaskCompletedState;
    Button btnDeleteSelectedTaskList;
    Button btnChangeSelectedTaskCompletedState;
    Button btnCreateNewTask;
    String this_checklistPrivateState;
    EditText etNewTaskName;
    CheckBox cbNavChecklistPrivate;
    String addNewTaskListResult = "";
    String this_newTasklistName;
    String deleteTaskListFlagMessage = "0";
    String changeTaskCompletedStateMessage = "0";
    String createNewTaskMessage = "0";
    String this_taskname;
    EditText etNavNewTasklist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);
        cbNavChecklistPrivate = findViewById(R.id.nav_check_privateProject);
        etNavNewTasklist = findViewById(R.id.et_nav_new_tasklist);
        tasklistList = findViewById(R.id.tasklist_list);
        taskList = findViewById(R.id.task_list);
        tvSelectedTaskListName = findViewById(R.id.selected_tasklist_name);
        tvSelectedTaskListPrivacy = findViewById(R.id.selected_tasklist_privacy);
        tvSelectedTaskName = findViewById(R.id.selected_task_name);
        cbSelectedTaskCompletedState = findViewById(R.id.cb_selected_task_state);
        btnDeleteSelectedTaskList = findViewById(R.id.btn_delete_tasklist);
        btnChangeSelectedTaskCompletedState = findViewById(R.id.btn_change_task_state);
        btnChangeSelectedTaskCompletedState.setEnabled(false);
        btnCreateNewTask = findViewById(R.id.create_task_btn);
        etNewTaskName = findViewById(R.id.et_new_task_name);
        ImageView ivBack= findViewById(R.id.btn_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tasklists = new ArrayList<>();
        tasks = new ArrayList<>();
        tasklistList = findViewById(R.id.tasklist_list);
        Button addTaskListBtn = findViewById(R.id.tasklist_add_btn);
        addTaskListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                this_newTasklistName = etNavNewTasklist.getText().toString();

                if(!etNavNewTasklist.getText().toString().equals("")) {
                    loadingDialog = WeiboDialogUtils.createLoadingDialog(ManageTaskActivity.this, "Uploading new Tasklist");
                    loadingDialog.show();
                    if (cbNavChecklistPrivate.isChecked()) {
                        this_checklistPrivateState = "1";
                    } else {
                        this_checklistPrivateState = "0";
                    }
                    new CreateTasklist().execute();
                } else {
                    new GlideToast.makeToast(ManageTaskActivity.this,getResources().getString(R.string.tasklist_require));
                }
            }
        });
        tasklistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TasklistList listitem = tasklists.get(arg2);
                selectedTasklistName = listitem.getName();
                selectedTasklistID = listitem.getId();
                selectedTasklistPrivacy = listitem.getPrivate_state();
                tvSelectedTaskListName.setText(selectedTasklistName);
                if(selectedTasklistPrivacy.equals("1")) {
                    tvSelectedTaskListPrivacy.setText(getResources().getString(R.string.privates));
                } else {
                    tvSelectedTaskListPrivacy.setText(getResources().getString(R.string.publics));
                }
                tasks.clear();
                taskList.setAdapter(null);
                loadingDialog = WeiboDialogUtils.createLoadingDialog(ManageTaskActivity.this, "loading Tasks");
                loadingDialog.show();
                new GetTask().execute();
            }
        });
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TaskList listitem = tasks.get(arg2);
                selectedTaskName = listitem.getName();
                selectedTaskID = listitem.getId();
                selectedTaskCompletedState = listitem.getPrivate_state();
                tvSelectedTaskName.setText(selectedTaskName);
                btnChangeSelectedTaskCompletedState.setEnabled(false);
                if(selectedTaskCompletedState.equals("1")) {
                    cbSelectedTaskCompletedState.setChecked(true);
                } else {
                    cbSelectedTaskCompletedState.setChecked(false);
                }
            }
        });
        btnDeleteSelectedTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteTaskList().execute();
            }
        });
        btnChangeSelectedTaskCompletedState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeTaskState().execute();
            }
        });
        cbSelectedTaskCompletedState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTaskCompletedState.equals("1")) {
                    if(cbSelectedTaskCompletedState.isChecked()){
                        btnChangeSelectedTaskCompletedState.setEnabled(false);
                    } else {
                        btnChangeSelectedTaskCompletedState.setEnabled(true);
                    }
                } else {
                    if(cbSelectedTaskCompletedState.isChecked()){
                        btnChangeSelectedTaskCompletedState.setEnabled(true);
                    } else {
                        btnChangeSelectedTaskCompletedState.setEnabled(false);
                    }
                }
            }
        });
        btnCreateNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                this_taskname =  etNewTaskName.getText().toString().trim();
                if(!this_taskname.equals("")) {
                    new CreateNewTask().execute();
                } else {
                    new GlideToast.makeToast(ManageTaskActivity.this,getResources().getString(R.string.taskname_require));
                }
            }
        });
        new GetTasklist().execute();
    }

    private class GetTasklist extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/gettasklist.php?";
            String parameters = "user=" +  HomeActivity.profile_id;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String private1 = c.getString("private");
                        if(!id.equals("null")) {
                            selectedTasklistID = id;
                            selectedTasklistName = name;
                            selectedTasklistPrivacy = private1;
                        }
                        tasklists.add(new TasklistList(name, private1,id) );
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(tasklists.size()>0) {
                tasklistListAdapter = new TasklistListAdapter(getApplicationContext(), tasklists);
                tasklistList.setAdapter(tasklistListAdapter);
                tvSelectedTaskListName.setText(selectedTasklistName);
                if(selectedTasklistPrivacy.equals("1")) {
                    tvSelectedTaskListPrivacy.setText("private");
                } else {
                    tvSelectedTaskListPrivacy.setText("public");
                }

            }
        }
    }

    private class DeleteTaskList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/deletetasklist.php?";
            String parameters = "id=" +  selectedTasklistID;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    deleteTaskListFlagMessage = jsonObj.getString("message");
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(deleteTaskListFlagMessage.equals("success")) {
                new GlideToast.makeToast(ManageTaskActivity.this,getResources().getString(R.string.delete_success));
                tasklists.clear();
                new GetTasklist().execute();
            } else {
                new GlideToast.makeToast(ManageTaskActivity.this,getResources().getString(R.string.unable_remove_tasklist));
            }
        }
    }

    private class GetTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/gettask.php?";
            String parameters = "projectid=" +  selectedTasklistID;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("taskname");
                        String completed_state = c.getString("completed");
                        if(!id.equals("null")) {
                            selectedTaskID = id;
                            selectedTaskName = name;
                            selectedTaskCompletedState = completed_state;
                        }
                        tasks.add(new TaskList(name, completed_state,id) );
                    }
                } catch (final JSONException e) {
                }


            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(tasks.size()>0) {
                taskListAdapter = new TaskListAdapter(ManageTaskActivity.this, tasks);
                taskList.setAdapter(taskListAdapter);
                tvSelectedTaskName.setText(selectedTaskName);
                if(selectedTaskCompletedState.equals("1")) {
                    cbSelectedTaskCompletedState.setChecked(true);
                } else {
                    cbSelectedTaskCompletedState.setChecked(false);
                }
            } else {
                new GlideToast.makeToast(ManageTaskActivity.this,"No Task");
            }
        }
    }

    private class ChangeTaskState extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/changetaskstate.php?";
            String tempTaskState = "";
            if(selectedTaskCompletedState.equals("0")) {
                tempTaskState = "1";
            } else {
                tempTaskState = "0";
            }
            String parameters = "id=" +  selectedTaskID +
                    "&completed=" + tempTaskState;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    changeTaskCompletedStateMessage = jsonObj.getString("message");
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(changeTaskCompletedStateMessage.equals("success")) {
                new GlideToast.makeToast(ManageTaskActivity.this,"Changed Successfully");
                tasks.clear();
                new GetTask().execute();
            } else {
                new GlideToast.makeToast(ManageTaskActivity.this,"Unable to change the task state");
            }
        }
    }

    private class CreateNewTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/createtask.php?";
            String tempTaskState = "";

            String parameters = "projectid=" +  selectedTasklistID +
                    "&taskname=" + this_taskname+
                    "&completed=" + "0";
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    createNewTaskMessage = jsonObj.getString("message");
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(createNewTaskMessage.equals("success")) {
                new GlideToast.makeToast(ManageTaskActivity.this,"Created Successfully");
                tasks.clear();
                new GetTask().execute();
            } else {
                new GlideToast.makeToast(ManageTaskActivity.this,"Unable to create new task");
            }
        }
    }


    private class CreateTasklist extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "tasklist/createtasklist.php?";
//            newTasklistName.replace(" ","__");

            String parameters = "user=" +  HomeActivity.profile_id +
                    "&name=" + this_newTasklistName +
                    "&private=" + this_checklistPrivateState;
            String url = LoginActivity.base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    addNewTaskListResult = jsonObj.getString("message");
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error 222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                WeiboDialogUtils.closeDialog(loadingDialog);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(addNewTaskListResult.equals("success")) {
                new AlertDialog.Builder(ManageTaskActivity.this)
                        .setTitle("Add new Tasklist")
                        .setMessage("New Tasklist is created successfully")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                tasklists.clear();
                new GetTasklist().execute();

            } else {
                new AlertDialog.Builder(ManageTaskActivity.this)
                        .setTitle("Add new Tasklist")
                        .setMessage("Error in creating new tasklist")
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

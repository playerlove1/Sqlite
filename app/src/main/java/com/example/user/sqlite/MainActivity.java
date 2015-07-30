package com.example.user.sqlite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends Activity {


/*   asyncTask    的類別      */
    class MyTask extends AsyncTask<String, Integer, String > {
        private Context mContext;
        private ProgressDialog mDialog_loading;
        public MyTask(Context mContext) {
            this.mContext = mContext;
        }
        @Override
        protected void onPreExecute() { //執行前
            super.onPreExecute();
            mDialog_loading = new ProgressDialog(mContext);
            mDialog_loading.setMessage("Loading data....");
            mDialog_loading.setCancelable(false);
            mDialog_loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog_loading.setMax(100);
            mDialog_loading.show();
        }
        @Override
        protected String doInBackground(String... param) {

            try{
                String result = DBConnector.executeQuery("SELECT * FROM doctor"); //發送queryt取得資料
                JSONArray jsonArray = new JSONArray(result);  //接收回傳結果 (病人)
                int total=jsonArray.length();    //共有幾個病人

                for(int i = 0; i < total; i++) { //依序取出並設並各病人詳細的資料
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    Log.d("Asynctask_background","取得name:"+jsonData.getString("dname") );

                    DatabaseHandler db = new DatabaseHandler(mContext);
                    db.addContact(new Contact(jsonData.getString("did"),jsonData.getString("dname"),jsonData.getString("subject"),jsonData.getString("expertise")));
                    publishProgress((int) ((i / (float) (total)) * 100));  //更新進度條
                }

            } catch(Exception e) {
                Log.e("log_asynctask", e.toString());
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mDialog_loading.setProgress(progress[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            mDialog_loading.dismiss();
            super.onPostExecute(s);

            Log.d("task","結束 ");
        }
    }



    ListView userlist;
    ArrayAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userlist=(ListView)findViewById(R.id.userlist);

        /**
         * CRUD Operations
         * */
        // Inserting Contacts
   //     Log.d("Insert: ", "Inserting ..");


        // Reading all contacts


    }


    public void show(View view)
    {
        switch (view.getId())
        {
            case R.id.select:
                DatabaseHandler db = new DatabaseHandler(this);
                Log.d("Reading: ", "Reading all contacts..");
                List<Contact> contacts = db.getAllContacts();
                for (Contact cn : contacts) {
                    String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Subject: " + cn.getSubject()+",Expertise"+cn.getExpertise();
                    // Writing Contacts to log
                    Log.d("Name: ", log);

                }
                userlist.setAdapter(new listviewadapter(MainActivity.this,contacts));
                break;
            case R.id.look:
                new MyTask(this).execute("null");
                break;

        }

    }
}
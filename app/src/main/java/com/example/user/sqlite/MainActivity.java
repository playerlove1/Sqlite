package com.example.user.sqlite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.example.user.sqlite.app.MySingleton;
import com.example.user.sqlite.volley.utils.Const;
import com.example.user.sqlite.volley.utils.JsonArrayPostRequest;
import com.google.android.gcm.GCMRegistrar;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;


import static com.example.user.sqlite.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.user.sqlite.CommonUtilities.EXTRA_MESSAGE;
import static com.example.user.sqlite.CommonUtilities.SENDER_ID;
public class MainActivity extends Activity {
 //   private GCMIntentService.MyBinder myBinder;
    private static final String TAG =MainActivity.class.getSimpleName();

    ListView userlist;
    ArrayAdapter listAdapter;
    TextView lblMessage;
    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;
    public static String name="fong";
    public static String email="kos83611@gmail.com";

    private ProgressDialog pDialog;

    // These tags will be used to cancel the requests
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userlist=(ListView)findViewById(R.id.userlist);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;}
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);


        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);

        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        ServerUtilities.register(context, name, email, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }


    private void praseJsonarray(JSONArray jsonArray)
    {
        int total=jsonArray.length();    //共有幾個病人
        for(int i = 0; i < total; i++) { //依序取出並設並各病人詳細的資料
           try {
               JSONObject jsonData = jsonArray.getJSONObject(i);
               DatabaseHandler db = new DatabaseHandler(this);
               db.addContact(new Contact(jsonData.getString("did"), jsonData.getString("dname"), jsonData.getString("subject"), jsonData.getString("expertise")));

           }
         catch(Exception e) {
        Log.e("praseJson", e.toString());
    }
        }


    }


    private void makeJsonArryReq() {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("query_string", "select * from doctor where sync=0");
        JsonArrayPostRequest req = new JsonArrayPostRequest (Const.URL_JSON_ARRAY,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                      //  msgResponse.setText(response.toString());
                        praseJsonarray(response);
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        },params);

        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(req);



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
                makeJsonArryReq();
              //new MyTask(this).execute("null");
           //     Intent bindIntent = new Intent(this, GCMIntentService.class);
          //      bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;
        }
    }

    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            // Showing received message
           lblMessage.setText("訊息:"+newMessage );
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.d("UnRegister  Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
}
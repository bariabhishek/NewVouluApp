package com.wikav.voulu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.wikav.voulu.fragments.BottomSheetFragmentui;
import com.wikav.voulu.fragments.FullScreenDialogForNoInternet;

public class JobConfirm extends AppCompatActivity {

    String title,postId,comment_id,job_giver_mobile,job_seeker_mobile,job_giver_name,job_seeker_name,job_giver_profile,job_seeker_profile;
    SessionManger sessionManger;
    String Url="https://voulu.in/api/getJobSeekerDetail.php";
    String Url2="https://voulu.in/api/sendDataCompleteTask.php";
    String Url3="https://voulu.in/api/notification.php";
    TextView tvJobSeekr,tvJobGiver,yourChoosingLine,titile;
    CircleImageView jobGiverImage,jobSeekerImage;
    Button sendDatabtn;
    BroadcastReceiver broadcastReceiver;
    BottomSheetFragmentui bottomSheetFragmentui;
Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_job_confirm );
        postId=getIntent().getStringExtra("postId");
        title=getIntent().getStringExtra("title");
        comment_id=getIntent().getStringExtra("commnet_id");
        sendDatabtn=findViewById(R.id.confirm);
        snackbar=  Snackbar.make(this.findViewById(android.R.id.content), Html.fromHtml("<font color=\"#ffffff\">No Internet Connection</font>"), BaseTransientBottomBar.LENGTH_INDEFINITE);
        sessionManger=new SessionManger(this);
        HashMap<String,String> getUser=sessionManger.getUserDetail();
        job_giver_mobile=getUser.get(sessionManger.MOBILE);
        job_giver_profile=getUser.get(sessionManger.PROFILE_PIC);
        job_giver_name=getUser.get(sessionManger.NAME);
        getJobSeekerDetail(comment_id);
        checkInptenet();
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 7)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        sendDatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(postId,comment_id,job_giver_mobile,job_seeker_mobile);
            }


        });
    }

    private void initiliaze() {
        tvJobGiver=findViewById(R.id.jobGiverNameConfirm);
        tvJobSeekr=findViewById(R.id.jobSeekerNameConfirm);
        titile=findViewById(R.id.jobTitleConfirm);
        yourChoosingLine=findViewById(R.id.yourChoosingLine);
        jobGiverImage=findViewById(R.id.jobGiverConfirm);
        jobSeekerImage=findViewById(R.id.jobSeekerConfirm);
        yourChoosingLine.setText("You are choosing "+ job_seeker_name + " to be complete your task. Please confirm and contact with "+ job_seeker_name +".\n" +
                "Make sure that you share receive OTP with "+ job_seeker_name );
        tvJobSeekr.setText(job_seeker_name);
        tvJobGiver.setText(job_giver_name);
        Glide.with(this).load(job_giver_profile).into(jobGiverImage);
        Glide.with(this).load(job_seeker_profile).into(jobSeekerImage);
        titile.setText(title);

    }

    private void getJobSeekerDetail(final String comment_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("getmobile");
                            if (success.equals("1")){
                                Log.d("Respoo",response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    job_seeker_mobile = object.getString("mobile").trim();
                                    job_seeker_name = object.getString("username").trim();
                                    job_seeker_profile = object.getString("userPic").trim();
                                    //  final String profile = object.getString("profile").trim();
                                    initiliaze();
                                }

                            }
                            else
                            {
                                Toast.makeText(JobConfirm.this, "Something went wrong...", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JobConfirm.this, "Something went wrong..."+e, Toast.LENGTH_LONG).show();


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(JobConfirm.this, "Error2: " + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("postId", comment_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(JobConfirm.this);
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();

    }

    public void sendData(final String postId, final String comment_id, final String job_giver_mobile, final String job_seeker_mobile)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url2,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")){
                                Log.d("OtpRes",response);

                                String  OTP = jsonObject.getString("getOtp").trim();
                                bottomSheetFragmentui=new BottomSheetFragmentui();
                                Bundle bundle=new Bundle();
                                bundle.putString("otp",OTP);
                                bundle.putString("seekerName",job_seeker_name);
                                bundle.putString("seekerMobile",job_seeker_mobile);
                                bottomSheetFragmentui.setArguments(bundle);
                                bottomSheetFragmentui.show(getSupportFragmentManager(),"bottomSheet");


                                sendSms();

                            }
                            else
                            {Log.d("OtpRes",response);
                                Toast.makeText(JobConfirm.this, "Something went wrong...", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            Log.d("OtpRes",e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(JobConfirm.this, "Something went wrong..."+e, Toast.LENGTH_LONG).show();


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("OtpRes",error.toString());
                        Toast.makeText(JobConfirm.this, "Error2: " + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("comment_id",comment_id);
                params.put("postId",postId);
                params.put("jobseeker",job_seeker_mobile);
                params.put("jobgiver",job_giver_mobile);
                params.put("message",job_giver_name+" has accepted your Bid");
                params.put("title", "Task Accepted");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(JobConfirm.this);
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();
    }
    private void sendSms() {
        String msg="Hello "+job_seeker_name+", your comment on this '"+title +"' task has been accepted by "+job_giver_name +" to be completed. Please open Voulu app and click on task accepted notification";
        SendSms sendSms=new SendSms(job_seeker_mobile,msg);
        sendSms.send();
    }
    public void checkInptenet() {
        IntentFilter  intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};


                if (ConnectivityReceiver.isNetworkAvailable(getApplicationContext(), type)) {
                    if (snackbar.isShown())
                        snackbar.dismiss();
                } else {
                    //Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show();
                /*FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                FullScreenDialogForNoInternet full=new FullScreenDialogForNoInternet();
                full.show(ft,"show");*/

                    snackbar.show();


                }

            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bottomSheetFragmentui!=null)
        {
            bottomSheetFragmentui.dismiss();
            Intent view = new Intent(JobConfirm.this,Home. class);
            view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(view);
        }
        finish();
    }
}

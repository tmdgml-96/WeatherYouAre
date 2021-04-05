package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    static FirebaseAuth mAuth;
    static String userEmail,email;
    EditText etv_email;
    EditText etv_password;
    Button bt_register;
    Button bt_signin;
    ImageView iv_main;
    TextView tv_title;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        etv_email = (EditText)findViewById(R.id.etv_email);
        etv_password = (EditText)findViewById(R.id.etv_password);
        bt_register = (Button)findViewById(R.id.bt_register);
        bt_signin = (Button)findViewById(R.id.bt_signin);
        iv_main = (ImageView)findViewById(R.id.iv_mainImage);
        tv_title = (TextView)findViewById(R.id.tv_title);
        progressbar = (ProgressBar)findViewById(R.id.progressbar);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();

        bt_signin.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(final View view) {
                                             email = etv_email.getText().toString();
                                             String password = etv_password.getText().toString();

                                             progressbar.setVisibility(View.VISIBLE);

                                             if (email.isEmpty()) {
                                                 Toast.makeText(MainActivity.this, "이메일을 입력하세요.", Toast.LENGTH_LONG).show();
                                                 return;
                                             }
                                             if (password.isEmpty()) {
                                                 Toast.makeText(MainActivity.this, "패스워드를 입력하세요.", Toast.LENGTH_LONG).show();
                                                 return;
                                             }

                                             //로그인
                                             mAuth.signInWithEmailAndPassword(email, password)
                                                     .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<AuthResult> task) {
                                                             progressbar.setVisibility(view.GONE);
                                                             if (task.isSuccessful()) {
                                                                 // Sign in success, update UI with the signed-in user's information
                                                                 Log.d(TAG, "signInWithEmail:success");
                                                                 FirebaseUser user = mAuth.getCurrentUser();

                                                                 userEmail = user.getEmail();
                                                                 Log.d(TAG, "userEmail: " + userEmail);


                                                                 Intent intent = new Intent(MainActivity.this, TabActivity.class);
                                                                 intent.putExtra("email", email);

                                                                 startActivity(intent);

                                                             } else {
                                                                 // If sign in fails, display a message to the user.
                                                                 Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                                 Toast.makeText(MainActivity.this, "인증 실패",
                                                                         Toast.LENGTH_LONG).show();
//                                    updateUI(null);
                                                             }

                                                         }

                                                     });

                                             if (getIntent().getExtras() != null) {
                                                 for (String key : getIntent().getExtras().keySet()) {
                                                     Object value = getIntent().getExtras().get(key);
                                                     Log.d(TAG, "Key: " + key + " Value: " + value);
                                                 }
                                             }

                                             Log.d(TAG, "Subscribing to weather topic");
                                             // [START subscribe_topics]
                                             FirebaseMessaging.getInstance().subscribeToTopic("weather")
                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             String msg = getString(R.string.msg_subscribed);
                                                             if (!task.isSuccessful()) {
                                                                 msg = getString(R.string.msg_subscribe_failed);
                                                             }
                                                             Log.d(TAG, msg);
                                                             //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                         }
                                                     });
                                             FirebaseInstanceId.getInstance().getInstanceId()
                                                     .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                             if (!task.isSuccessful()) {
                                                                 Log.w(TAG, "getInstanceId failed", task.getException());
                                                                 return;
                                                             }

                                                             // Get new Instance ID token
                                                             String token = task.getResult().getToken();

                                                             // Log and toast
                                                             String msg = getString(R.string.msg_token_fmt, token);
                                                             Log.d(TAG, msg);

                                                             ref.child("userToken").setValue(token);
                                                         }
                                                     });


                                         }
                                     });


        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View,String>(iv_main,"startImage");
                pairs[1] = new Pair<View,String>(tv_title,"titleText");
                pairs[2] = new Pair<View,String>(etv_email,"etv_email");
                pairs[3] = new Pair<View,String>(etv_password,"etv_password");
                pairs[4] = new Pair<View,String>(bt_signin,"bt_go");
                pairs[5] = new Pair<View,String>(bt_register,"bt_register");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(intent,options.toBundle());


                String email = etv_email.getText().toString();
                String password = etv_password.getText().toString();

//                if (email.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Please insert Email", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if(password.isEmpty()){
//                    Toast.makeText(MainActivity.this, "Please insert Password", Toast.LENGTH_LONG).show();
//                    return;
//                }

//                mAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                progressbar.setVisibility(View.GONE);
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "createUserWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
////                                    updateUI(user);
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                    Toast.makeText(MainActivity.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
////                                    updateUI(null);
//                                }
//
//                            }
//                        });
            }
        });



        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        /*if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        Button subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Subscribing to weather topic");
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("weather")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END subscribe_topics]
            }
        });


        Button logTokenButton = findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                // [START retrieve_current_token]
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END retrieve_current_token]
            }
        });
*/
    }
}

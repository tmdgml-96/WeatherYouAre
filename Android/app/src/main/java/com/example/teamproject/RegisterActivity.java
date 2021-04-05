package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import static com.example.teamproject.MainActivity.mAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    EditText etv_email,etv_passwd,etv_passwd2;
    Button bt_join,bt_back;
    ImageView iv_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etv_email = findViewById(R.id.etv_register_email);
        etv_passwd= findViewById(R.id.etv_register_password);
        etv_passwd2 = findViewById(R.id.etv_register_password2);
        bt_join = findViewById(R.id.bt_register_joinus);
        bt_back = findViewById(R.id.bt_register_back);
        iv_check = findViewById(R.id.iv_passwordImage);

        etv_passwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(etv_passwd.getText().toString().equals(etv_passwd2.getText().toString())) {

                    iv_check.setImageResource(R.drawable.ic_baseline_check_24);
                }

                else {
                    iv_check.setImageResource(R.drawable.ic_baseline_close_24);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    bt_join.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String email = etv_email.getText().toString();
            String password = etv_passwd.getText().toString();

                if(etv_email.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(etv_passwd.getText().toString().isEmpty() || etv_passwd2.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this,"비밀번호를 입력해 주세요.",Toast.LENGTH_LONG).show();
                   return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:성공");
                                        Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();


//                                    updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:실패", task.getException());
                                        Toast.makeText(RegisterActivity.this, "회원가입 실패",
                                                Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                    }

                                }
                            });


        }
    });

    bt_back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    });

    }
}
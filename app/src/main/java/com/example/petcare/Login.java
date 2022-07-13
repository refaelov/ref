package com.example.petcare;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.petcare.databinding.ActivityLoginBinding;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN=100;
    private GoogleSignInClient googleSignInClient;
    //firebase
    private FirebaseAuth firebaseAuth;
    private static final String TAG ="GOOGLE_SIGN_IN_TAG";
    //facebook
    private static final String FTAG ="FACEBOOK_SIGN_IN_TAG";
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //--------google login start--------
        //-----/-----------------------/----
        //view binding Google login option
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //configure the Google SignIn
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        //google SignInButton Click to begin google SignIn
        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //begin google signIn
                Log.d(TAG, "onClick : begin Google SignIn");
                Intent intent=googleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
        //-------------google login ends--------------/
        //---------------------------------------------/

        //-----------facebook login start---------------/
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButtonFacebook = (LoginButton) findViewById(R.id.login_button_facebook);
        // Callback registration
        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startActivity(new Intent(Login.this,MainActivity.class));
                finish();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        loginButtonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile"));
            }
        });
    }


    private void checkUser() {
        //if user is already sign in than go to main activity google
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            Log.d(TAG,"checkUser already logged in");
            //
            startActivity(new Intent( this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null)
            updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser) {
        startActivity(new Intent(Login.this,MainActivity.class));
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result returned from the Intent from GoogleSignInApi
        if (requestCode==RC_SIGN_IN){
            Log.d(TAG,"onActivityResult: google signin intent result");
            Task<GoogleSignInAccount> accountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //success google SignIn , now auth with FireBase
                GoogleSignInAccount account=accountTask.getResult(ApiException.class);
                fireBaseAuthWithGoogleAccount(account);
            }
            catch (Exception e){
                //fail googleSign in
                Log.d(TAG,"onActivityResult : " +e.getMessage());
            }
        }
    }
    private void fireBaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG,"fireBaseAuthGoogleAccount: begin firebaseAuth with google account");
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //login success
                Log.d(TAG,"onSuccess: Logged In");
                //get logged in user
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                //get user info
                String uid=firebaseUser.getUid();
                String email=firebaseUser.getEmail();
                Log.d(TAG,"onSuccess : Email: +Email");
                Log.d(TAG,"onSuccess : UID:"+uid);
                //checks if user is new or existed
                if (authResult.getAdditionalUserInfo().isNewUser()){
                    //case user is new account created
                    Log.d(TAG, " onSuccess: account created");
                    Toast.makeText(Login.this," משתמש נוצר בהצלחה",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //existing user - logged in
                    Log.d(TAG,"onSuccess: existing user ");
                }
                //start mainActivity
                startActivity(new Intent(Login.this,MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //login failed
                Log.d(TAG,"onFailure: Login failed"+e.getMessage());
            }
        });
    }
}
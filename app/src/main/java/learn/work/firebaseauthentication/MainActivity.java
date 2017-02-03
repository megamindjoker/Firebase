package learn.work.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private List needpermissions = Arrays.asList("email", "public_profile");
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 0;
    private Button loginButton,signoutButton;

//    @BindView(R.id.signout_button)
//    Button signoutButton;
//
//    @BindView(R.id.signout_button1)
//    Button signoutButton1;
//
//    @BindView(R.id.login_button)
//    Button loginButton;
//
//    @BindView(R.id.login_button1)
//    Button loginButton1;
//
//
//    @OnClick(R.id.login_button) void setLoginButton()
//    {
//        login();
//
//    }
//
//    @OnClick(R.id.signout_button) void setSignoutButton()
//    {
//        signout();
//
//    }
//
//    @OnClick(R.id.login_button1) void setLoginButton1()
//    {
//        loginGoogle();
//
//    }
//
//    @OnClick(R.id.signout_button1) void setSignoutButton1()
//    {
//        signoutGoogle();
//
//    }




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_layout);
        FacebookSdk.sdkInitialize(getApplicationContext());


        loginButton = (Button) findViewById(R.id.login_button1);
        signoutButton = (Button) findViewById(R.id.signout_button1);

        callbackManager = CallbackManager.Factory.create();
//        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();


//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//
//            {
//                Toast.makeText(MainActivity.this, "Sucessful", Toast.LENGTH_SHORT).show();
//                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, needpermissions);
//
//                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        getToken(loginResult.getAccessToken());
//                        loginButton.setText("Signed in");
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//
//                    }
//                });
//            }
//        });

//        signoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                LoginManager.getInstance().logOut();
//                loginButton.setText("SIgn IN with Facebook");
//
//            }
//        });


        //Listeners

        loginButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.e("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("failed", "this");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    private void getToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        checkCredential(credential);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d("you are here", "you are here");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.d("connection failed","this time");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        checkCredential(credential);

    }

    private void checkCredential(AuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                       
                        if (!task.isSuccessful())
                        {
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }


    private void signoutGoogle() {
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status)
                    {
                        Log.d("this is logged out", "one ");
                    }
                });
    }

    private void loginGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        loginButton.setText(R.string.app_name);

    }

    private void login()
    {

        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, needpermissions);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                getToken(loginResult.getAccessToken());
                loginButton.setText("Signed in");

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signout_button1:
            signoutGoogle();
                break;


            case R.id.login_button1:
                loginGoogle();
                break;
        }
    }
}


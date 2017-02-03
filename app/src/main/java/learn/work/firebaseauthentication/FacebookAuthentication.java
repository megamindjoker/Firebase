package learn.work.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.*;

/**
 * Created by rakshak on 2/3/17.
 */


public class FacebookAuthentication extends AppCompatActivity implements View.OnClickListener
{
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button loginButton,signoutButton;
    private List needpermissions = Arrays.asList("email", "public_profile");


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_layout);
        sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        StateChangeListener();


        //BUtton initialization
        loginButton = (Button) findViewById(R.id.login_button);
        signoutButton = (Button) findViewById(R.id.signout_button);

        //ClickListenere

        loginButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);


        //Getting the device ID
        String deviceId = Settings.System.getString(getContentResolver(),
                Settings.System.ANDROID_ID);

        Toast.makeText(this, deviceId+"", Toast.LENGTH_SHORT).show();


    }

    private void StateChangeListener()

    {
        mAuthListener= new FirebaseAuth.AuthStateListener() {
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

            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }






    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onDestroy()
    {

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

    private void loginFacebook()
    {
        LoginManager.getInstance().logInWithReadPermissions(FacebookAuthentication.this, needpermissions);
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


    private void getToken(AccessToken accessToken)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
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
                            Toast.makeText(FacebookAuthentication.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }


    private void signoutFacebook()
    {
        FirebaseAuth.getInstance().signOut();

        LoginManager.getInstance().logOut();
        loginButton.setText(R.string.app_name);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.login_button:
                Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
                loginFacebook();
                break;

            case R.id.signout_button:
                Toast.makeText(this, "signout", Toast.LENGTH_SHORT).show();
                signoutFacebook();
                break;



        }
    }
}

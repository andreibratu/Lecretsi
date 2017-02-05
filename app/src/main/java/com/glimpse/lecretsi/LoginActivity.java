package com.glimpse.lecretsi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    //TODO How to link google log in and facebook log in to a single Firebase account
    //TODO https://firebase.google.com/docs/auth/android/account-linking


    /**
     * Google objects needed for logging in
     **/
    public GoogleSignInAccount acct;
    private GoogleApiClient mGoogleApiClient;
    final int RC_SIGN_IN = 9001; //Don't ask why google does it
    final String TAG = "SignInActivity";

    /**
     * Firebase objects needed to register the user in the app's Firebase db
     */
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Google [START] */
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("658445662003-vj5t8u34k68jsid641kom5dv479illvd.apps.googleusercontent.com")
                            .requestEmail()
                            .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage(this /* FragmentActivity */,
                            this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        /** Google [STOP]  */

        /** Firebase [START] */
            //Shared instance of the FirebaseAuth object
            mAuth = FirebaseAuth.getInstance();

            //AuthStateListener object that responds to changes in the user's sign-in state
            mAuthListener = new FirebaseAuth.AuthStateListener() {

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        //TODO Send the user to mainActivity
                        //TODO Read the docs about checking if user is already logged on
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };
        /** Firebase [STOP] */

        /** Sign In Button [START] */
            //TODO Write XML file for Google sign in
            SignInButton googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
            googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

            findViewById(R.id.sign_in_button).setOnClickListener(this);
            googleSignInButton.setVisibility(View.VISIBLE);
        /** Sign In Button [STOP */
    }

    /** Firebase methods [START] */

        /*
            After a user successfully signs in, get an ID token from the GoogleSignInAccount object,
            exchange it for a Firebase credential, and authenticate
            with Firebase using the Firebase credential
         */
        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getDisplayName());

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithCredential", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            /**
             *Use the getCurrentUser method to get the user's account data
             *You can get the user's basic profile information from the FirebaseUser
             */
        }

    /**Firebase [STOP] */

    /** Google methods [START] */
        private void signIn() {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        private void handleSignInResult(GoogleSignInResult result) {
            Log.d(TAG, "handleSignInResult:" + result.isSuccess());

            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                acct = result.getSignInAccount();
                assert acct != null;
                firebaseAuthWithGoogle(acct);

            } else {
                Toast error = Toast.makeText(getApplicationContext(),
                        "Google Sign In Failed", Toast.LENGTH_LONG);
                error.show();
            }
        }

        @Override
        ///Retrieve sign in result
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }

        @Override
        ///500 connection to Google failed
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed:" + connectionResult);
        }
    /** Google [STOP] */

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}

package com.breakout.sample.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;


public class GoogleSignInHelper {
    public interface GoogleLoginHelperListener {
        /**
         * {@link GoogleSignIn#getLastSignedInAccount(Context)}
         */
        void onCompleteCheckSignIn(GoogleSignInAccount account);

        /**
         * {@link GoogleSignInClient#getSignInIntent()}
         *
         * @param status 로그인 실패에 대한 {@link Status}
         */
        void onCompleteSignIn(GoogleSignInAccount account, Status status);

        /**
         * {@link GoogleSignInClient#signOut()}
         */
        void onCompleteSignOut();
    }

    private final String TAG = getClass().getName();
    private final AppCompatActivity _activity;
    private final GoogleLoginHelperListener _googleLoginHelperListener;
    private ActivityResultLauncher<Intent> _activityResultLauncher;
    private final String REQUEST_CODE = "requestCode";
    private final int RC_GOOGLE_SIGN_IN = 2002;
    /**
     * <h3>google sign-in</h3>
     * google : https://developers.google.com/identity/sign-in/android/sign-in
     * <p>
     * firebase : https://firebase.google.com/docs/auth/android/google-signin
     * <p>
     * TODO firebase-auth 테스트 필요
     */
    private GoogleSignInClient _googleSignInClient;


    public GoogleSignInHelper(AppCompatActivity activity, GoogleLoginHelperListener googleLoginHelperListener) {
        this._activity = activity;
        this._googleLoginHelperListener = googleLoginHelperListener;
        initGoogleSignInClient();
    }

    @SuppressWarnings("unused")
    public GoogleSignInClient getGoogleSignInClient() {
        return _googleSignInClient;
    }

    @SuppressWarnings("CommentedOutCode")
    private void initGoogleSignInClient() {
        /*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(_activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(_activity.getString(R.string.default_web_client_id))
                .requestServerAuthCode(_activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        _googleSignInClient = GoogleSignIn.getClient(_activity, gso);
    }

    public void logGoogleSignInAccount(GoogleSignInAccount account) {
        String idToken = account.getIdToken();
        String authCode = account.getServerAuthCode();
        String id = account.getId();
        String name = account.getDisplayName();
        String email = account.getEmail();
        Uri photoUri = account.getPhotoUrl();
        Log.d(TAG, String.format("\n---------------" +
                        "\n| GoogleSignInAccount " +
                        "\n| idToken    : %s" +
                        "\n| authCode   : %s" +
                        "\n| id         : %s" +
                        "\n| name       : %s" +
                        "\n| email      : %s" +
                        "\n| photoUri   : %s" +
                        "\n-------------------",
                idToken, authCode, id, name, email, photoUri));
    }

    public void checkGoogleSignIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(_activity);
        Log.d(TAG, "checkGoogleSignIn | " + account);
        if (account != null) {
            logGoogleSignInAccount(account);
        } else {
            /*
                TODO 로그인 만료시 어떤식으로 처리할지 적용 해야함
             */
            Log.d(TAG, "todo check login ...");
        }
        _googleLoginHelperListener.onCompleteCheckSignIn(account);
    }

    /**
     * google signIn
     */
    public void signIn() {
        if (_activityResultLauncher == null) {
            _activityResultLauncher = _activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                int resultCode = result.getResultCode();
                Intent intent = result.getData();
                if (intent != null) {
                    int requestCode = intent.getIntExtra(REQUEST_CODE, 0);
                    Log.d(TAG, String.format(
                            "ActivityResultLauncher onActivityResult request code %s / %s ",
                            RC_GOOGLE_SIGN_IN, requestCode
                    ));
                }
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "signIn onActivityResult code : " + Activity.RESULT_OK);
                        onActivityResultGoogleLogin(intent);
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "signIn onActivityResult code : " + Activity.RESULT_CANCELED);
                        onActivityResultGoogleLogin(intent);
                        break;
                    default:
                        Log.d(TAG, "signIn onActivityResult code : " + resultCode);
                        onActivityResultGoogleLogin(intent);
                        break;
                }
            });
        }
        Intent signInIntent = _googleSignInClient.getSignInIntent();
        signInIntent.putExtra(REQUEST_CODE, RC_GOOGLE_SIGN_IN);
        _activityResultLauncher.launch(signInIntent);
        //_activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    /**
     * Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
     */
    private void onActivityResultGoogleLogin(@Nullable Intent data) {
        // The Task returned from this call is always completed, no need to attach a listener.
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//        handleSignInResult(task);

        GoogleSignInAccount account = null;
        Status status = null;
        try {
            account = task.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            logGoogleSignInAccount(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            status = e.getStatus();
            Log.e(true, TAG, String.format(
                    "Google sign in failed code = %s, status = %s, msg = %s",
                    e.getStatusCode(), status, e.getMessage()
            ), e);
        }
        _googleLoginHelperListener.onCompleteSignIn(account, status);
    }

    /**
     * google signOut
     */
    public void signOut() {
        _googleSignInClient.signOut()
                .addOnCompleteListener(_activity, task -> {
                    Log.d(TAG, "signOut onComplete");
                    _googleLoginHelperListener.onCompleteSignOut();
                });
    }
}
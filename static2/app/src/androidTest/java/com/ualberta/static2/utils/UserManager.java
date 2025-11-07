package com.ualberta.static2.utils;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {

    private static String AUTH_TAG = "EventLotteryAuth";

    private static FirebaseAuth mAuth;
    private static FirebaseUser mCurrentUser;

    // https://www.google.com/search?q=android+java+callback
    public interface InitCallback {
        void onSuccess(String userId);
        void onFailure(Exception exception);
    }
    public static String getCurrentUserId() {
        if (mCurrentUser == null) {
            return "test_user_default_id";
        }
        return mCurrentUser.getUid();
    }

    // https://firebase.google.com/docs/auth/android/anonymous-auth
    public static void initializeUser(Activity activity, com.ualberta.eventlottery.utils.UserManager.InitCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mCurrentUser = mAuth.getCurrentUser();
                                callback.onSuccess(mCurrentUser.getUid());
                            } else {
                                callback.onFailure(task.getException());
                            }
                        }
                    });
        } else {
            Log.d(AUTH_TAG, "userAlreadySignedIn:userId=" + mCurrentUser.getUid());
            callback.onSuccess(mCurrentUser.getUid());
        }

    }
}




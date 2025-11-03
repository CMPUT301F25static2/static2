package com.ualberta.eventlottery.utils;

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

    // https://www.google.com/search?q=android+java+callback
    public interface InitCallback {
        void onSuccess(String userId);
        void onFailure(Exception exception);
    }

    // https://firebase.google.com/docs/auth/android/anonymous-auth
    public static void initializeUser(Activity activity, InitCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                callback.onSuccess(user.getUid());
                            } else {
                                callback.onFailure(task.getException());
                            }
                        }
                    });
        } else {
            Log.d(AUTH_TAG, "userAlreadySignedIn:userId=" + firebaseUser.getUid());
            callback.onSuccess(firebaseUser.getUid());
        }

    }
}



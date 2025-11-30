package com.ualberta.static2.testutils;

import com.ualberta.eventlottery.utils.UserManager;

import org.junit.rules.ExternalResource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UserManagerRule extends ExternalResource {
    @Override
    protected void before() throws Throwable {
        super.before();

        CountDownLatch latch = new CountDownLatch(1);

        UserManager.InitCallback callback = new UserManager.InitCallback() {
            @Override
            public void onSuccess(String userId) {
                latch.countDown();
            }

            @Override
            public void onFailure(Exception exception) {
                // Let the latch timeout
            }
        };
        UserManager.initializeUser(callback);
        latch.await(2000, TimeUnit.MILLISECONDS);
    }
}

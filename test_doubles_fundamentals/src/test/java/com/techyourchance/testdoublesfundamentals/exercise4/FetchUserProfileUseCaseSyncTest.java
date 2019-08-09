package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {

    private static final String USERID = "userid";
    private static final String FULL_NAME = "FULL NAME";
    private static final String IMAGE_URI = "IMAGE URI";

    UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSynctd;
    UsersCacheTd mUsersCacheTd;

    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        mUserProfileHttpEndpointSynctd = new UserProfileHttpEndpointSyncTd();
        mUsersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSynctd,mUsersCacheTd);
    }

    //Check if userid passed to endpoint
    @Test
    public void fetchUserProfileSync_success_returned() {
        SUT.fetchUserProfileSync(USERID);
        assertThat(mUserProfileHttpEndpointSynctd.userId, is(USERID));
    }

    //Check if user is cached
    @Test
    public void fectchUserProfileSync_success_userCached() {
        SUT.fetchUserProfileSync(USERID);
        User cachedUser = mUsersCacheTd.getUser(USERID);
        assertThat(cachedUser.getUserId(),is(USERID));
        assertThat(cachedUser.getFullName(), is(FULL_NAME));
        assertThat(cachedUser.getImageUrl(), is(IMAGE_URI));
        assertThat(cachedUser.getUserId(),is(USERID));
    }

    //Check if user is not cached general error
    @Test
    public void fetchUserProfileSync_generalError_userNotCached() {
        mUserProfileHttpEndpointSynctd.isGeneralError = true;
        SUT.fetchUserProfileSync(USERID);
        assertThat(mUsersCacheTd.getUser(USERID), is(nullValue()));

    }



    /*
    *
    * Helper Classes
    *
    * */

    public static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync{
        public String userId;
        public boolean isGeneralError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            this.userId = userId;
            if(isGeneralError){
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR,"","","");
            }else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, userId, FULL_NAME, IMAGE_URI);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {

        private List<User> mUsers = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User existingUser = getUser(user.getUserId());
            if (existingUser != null) {
                mUsers.remove(existingUser);
            }
            mUsers.add(user);
        }

        @Override
        @Nullable
        public User getUser(String userId) {
            for (User user : mUsers) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }
    }
}
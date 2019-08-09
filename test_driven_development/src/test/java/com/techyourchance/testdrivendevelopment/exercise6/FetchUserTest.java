package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserTest {

    private static final String USER_ID = "user_id";
    private static final String USERNAME = "user name";
    private static final User USER = new User(USER_ID,USERNAME);

    @Mock UsersCache usersCache;
    @Mock FetchUserHttpEndpointSync fetchUserHttpEndpointSync;

    FetchUserUseCaseSync SUT;
    @Before
    public void setUp() throws Exception {
        SUT = new FetchUser(fetchUserHttpEndpointSync, usersCache);
    }

    //user not in cache correct user id sent to endpoint

    @Test
    public void fetchUser_userNotInCache_useridSentToEndpoint() throws NetworkErrorException {
        userNotInCache();
        requestFromEndpoint();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser().getUserId(),is(USER_ID));

    }

    @Test
    public void fetchUser_userNotInCache_successReturned() throws NetworkErrorException {
        userNotInCache();
        requestFromEndpoint();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(),is(FetchUserUseCaseSync.Status.SUCCESS));

    }

    @Test
    public void fetchUser_userNotInCache_userReturned() throws NetworkErrorException {
        userNotInCache();
        requestFromEndpoint();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(),is(USER));

    }

    @Test
    public void fetchUser_userNotInCache_userCached() throws NetworkErrorException {
        userNotInCache();
        requestFromEndpoint();
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        verify(usersCache).cacheUser(argumentCaptor.capture());
        assertThat(result.getUser(),is(USER));
    }

    @Test
    public void fetchUser_userNotInCache_AuthError() throws NetworkErrorException {
        userNotInCache();
        authError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(),is(FetchUserUseCaseSync.Status.FAILURE));

    }

    @Test
    public void fetchUser_userNotInCache_AuthError_nullUserReturned() throws NetworkErrorException {
        userNotInCache();
        authError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(),nullValue());
    }

    @Test
    public void fetchUser_userNotInCache_AuthError_cacheNotInteracted() throws NetworkErrorException {
        userNotInCache();
        authError();
        SUT.fetchUserSync(USER_ID);
        verify(usersCache,never()).cacheUser(any(User.class));

    }

    @Test
    public void fetchUser_userNotInCache_GeneralError() throws NetworkErrorException {
        userNotInCache();
        generalError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(),is(FetchUserUseCaseSync.Status.FAILURE));

    }

    @Test
    public void fetchUser_userNotInCache_generalError_nullUserReturned() throws NetworkErrorException {
        userNotInCache();
        generalError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(),nullValue());
    }

    @Test
    public void fetchUser_userNotInCache_generalError_cacheNotInteracted() throws NetworkErrorException {
        userNotInCache();
        generalError();
        SUT.fetchUserSync(USER_ID);
        verify(usersCache,never()).cacheUser(any(User.class));

    }

    @Test
    public void fetchUser_userNotInCache_NetworkError() throws NetworkErrorException {
        userNotInCache();
        networkError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(),is(FetchUserUseCaseSync.Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUser_userNotInCache_NetworkError_nullUserReturned() throws NetworkErrorException {
        userNotInCache();
        networkError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(),nullValue());
    }

    @Test
    public void fetchUser_userNotInCache_NetworkError_cacheNotInteracted() throws NetworkErrorException {
        userNotInCache();
        networkError();
        SUT.fetchUserSync(USER_ID);
        verify(usersCache,never()).cacheUser(any(User.class));

    }

    @Test
    public void fetchUser_correctUserIdPassedToCache() throws NetworkErrorException {
        userInCache();
        SUT.fetchUserSync(USER_ID);
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        verify(usersCache).getUser(ac.capture());
        assertThat(ac.getValue(),is(USER_ID));
    }

    @Test
    public void fetchUser_correctUserReturned() throws NetworkErrorException {
        userInCache();
        SUT.fetchUserSync(USER_ID);
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        verify(usersCache).getUser(ac.capture());
        assertThat(ac.getValue(),is(USER_ID));
    }

    @Test
    public void fetchUser_successReturned() throws NetworkErrorException {
        userInCache();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    //need to check if user in cache then no need for endpoint connection
    @Test
    public void fetchUser_userInCache_endpointNotProvoked() throws NetworkErrorException {
        userInCache();
        SUT.fetchUserSync(USER_ID);
        verify(fetchUserHttpEndpointSync,never()).fetchUserSync(anyString());
    }

    private void userNotInCache() {
        when(usersCache.getUser(anyString())).thenReturn(null);
    }

    private void userInCache() {
        when(usersCache.getUser(USER_ID)).thenReturn(new User(USER_ID,USERNAME));
    }

    private void requestFromEndpoint() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString())).thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.SUCCESS,USER_ID,USERNAME));
    }

    private void authError() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString())).thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR,"",""));
    }

    private void generalError() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString())).thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR,"",""));
    }

    private void networkError() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString())).thenThrow(new NetworkErrorException());
    }
}
package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    private static final String USER_ID = "USER ID";
    private static final String USERNAME = "USERNAME";
    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSync;
    UsersCache usersCache;
    EventBusPoster eventBusPoster;
    UpdateUsernameUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {

        updateUsernameHttpEndpointSync = mock(UpdateUsernameHttpEndpointSync.class);
        usersCache = mock(UsersCache.class);
        eventBusPoster = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSync, usersCache, eventBusPoster);
    }

    @Test
    public void updateUsernameSyn_success_passedToEndpoint() throws Exception {
        success();
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(updateUsernameHttpEndpointSync, times(1)).updateUsername(argumentCaptor.capture(), argumentCaptor.capture());
        List<String> captures = argumentCaptor.getAllValues();
        Assert.assertThat(captures.get(0), is(USER_ID));
        Assert.assertThat(captures.get(1), is(USERNAME));

    }

    @Test
    public void updateUserNameSyn_success_userCached() throws Exception{
        success();
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(usersCache).cacheUser(argumentCaptor.capture());
        User captureUser = argumentCaptor.getValue();
        Assert.assertThat(captureUser.getUserId(), is(USER_ID));
        Assert.assertThat(captureUser.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsernameSyn_generalError_userNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(usersCache);

    }

    @Test
    public void updateUsernameSyn_authError_userNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(usersCache);

    }

    @Test
    public void updateUsernameSyn_serverError_userNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(usersCache);

    }

    @Test
    public void updateUsernameSyn_success_loggedinEventPosted() throws Exception {
        success();
        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(EventBusPoster.class);
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verify(eventBusPoster).postEvent(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue(),is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsernameSyn_generalError_eventNotPosted() throws Exception{
        generalError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(eventBusPoster);

    }

    @Test
    public void updateUsernameSyn_authError_eventNotPosted() throws Exception{
        authError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(eventBusPoster);

    }

    @Test
    public void updateUsernameSyn_serverError_eventNotPosted() throws Exception{
        serverError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(eventBusPoster);

    }

    @Test
    public void updateUsername_success_successReturned() throws Exception{
        success();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsername_generalError_failureReturned() throws Exception{
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));

    }

    @Test
    public void updateUsername_authError_failureReturned() throws Exception{
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));

    }

    @Test
    public void updateUsername_serverError_failureReturned() throws Exception{
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));

    }

    @Test
    public void updateUsername_networkError_networkErrorReturned() throws Exception{
        netWorkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));

    }

    private void netWorkError() throws NetworkErrorException{
        doThrow(new NetworkErrorException())
                .when(updateUsernameHttpEndpointSync).updateUsername(anyString(),anyString());
    }


    private void generalError() throws Exception{
        when(updateUsernameHttpEndpointSync.updateUsername(anyString(),anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(GENERAL_ERROR,"",""));
    }

    private void authError() throws Exception{
        when(updateUsernameHttpEndpointSync.updateUsername(anyString(),anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(AUTH_ERROR,"",""));
    }

    private void serverError() throws Exception{
        when(updateUsernameHttpEndpointSync.updateUsername(anyString(),anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(SERVER_ERROR,"",""));
    }

    private void success() throws Exception {
        when(updateUsernameHttpEndpointSync.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }
}
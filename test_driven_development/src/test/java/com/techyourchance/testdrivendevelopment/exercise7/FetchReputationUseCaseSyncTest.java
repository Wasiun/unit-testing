package com.techyourchance.testdrivendevelopment.exercise7;


import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    private static final int REPUTATION = 1;

    @Mock
    GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    FetchReputationUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        success();
        SUT = new FetchReputationUseCaseSync(getReputationHttpEndpointSync);

    }

    @Test
    public void requestReputation_success_successReturned() {
        FetchReputationUseCaseSync.EndpointResult result = SUT.requestReputation();
        assertThat(result.getUseCaseStatus(), is(FetchReputationUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void requestReputation_success_reputationReturned() {
        FetchReputationUseCaseSync.EndpointResult result = SUT.requestReputation();
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void requestReputation_generalError_failureReturned() {
        generalError();
        FetchReputationUseCaseSync.EndpointResult result = SUT.requestReputation();
        assertThat(result.getUseCaseStatus(),is(FetchReputationUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void requestReputation_generalError_reputation0Returned() {
        generalError();
        FetchReputationUseCaseSync.EndpointResult result = SUT.requestReputation();
        assertThat(result.getReputation(),is(0));
    }

    @Test
    public void requestReputation_NetworkError_reputation0Returned() {
        networkError();
        FetchReputationUseCaseSync.EndpointResult result = SUT.requestReputation();
        assertThat(result.getUseCaseStatus(),is(FetchReputationUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }


    private void success() {
        when(getReputationHttpEndpointSync.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS,REPUTATION));
    }

    private void generalError() {
        when(getReputationHttpEndpointSync.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR,REPUTATION));
    }
    private void networkError() {
        when(getReputationHttpEndpointSync.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR,REPUTATION));
    }
}
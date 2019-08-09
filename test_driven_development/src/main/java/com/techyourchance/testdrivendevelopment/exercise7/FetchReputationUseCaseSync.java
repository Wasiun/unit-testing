package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    GetReputationHttpEndpointSync getReputationHttpEndpointSync;


    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {

        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;

    }

    public EndpointResult requestReputation() {

        if (getReputationHttpEndpointSync.getReputationSync().getStatus() == GetReputationHttpEndpointSync.EndpointStatus.SUCCESS) {
            return new EndpointResult(UseCaseResult.SUCCESS, getReputationHttpEndpointSync.getReputationSync().getReputation());
        } else if(getReputationHttpEndpointSync.getReputationSync().getStatus() == GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR){
            return new EndpointResult(UseCaseResult.NETWORK_ERROR, 0);
        }else {
            return new EndpointResult(UseCaseResult.FAILURE,0);
        }
    }


    public enum UseCaseResult {SUCCESS, NETWORK_ERROR, FAILURE}

    public class EndpointResult {
        UseCaseResult useCaseResult;
        int reputation;

        public EndpointResult(UseCaseResult useCaseResult, int reputation) {
            this.useCaseResult = useCaseResult;
            this.reputation = reputation;
        }

        public UseCaseResult getUseCaseStatus() {
            return useCaseResult;
        }

        public int getReputation() {
            return reputation;
        }
    }
}

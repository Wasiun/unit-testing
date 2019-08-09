package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUser implements FetchUserUseCaseSync {

    FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    UsersCache usersCache;

    public FetchUser(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {

        FetchUserHttpEndpointSync.EndpointResult result = null;
        User user = usersCache.getUser(userId);
        if (user == null) {
            try {
                result = fetchUserHttpEndpointSync.fetchUserSync(userId);
                if (result.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.SUCCESS) {
                    user = new User(result.getUserId(), result.getUsername());
                    usersCache.cacheUser(user);
                } else {
                    return new UseCaseResult(Status.FAILURE, null);
                }

            } catch (NetworkErrorException e) {
                e.printStackTrace();
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }
        }
        return new UseCaseResult(Status.SUCCESS, user);
    }
}

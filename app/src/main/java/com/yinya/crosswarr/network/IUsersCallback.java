package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;

public interface IUsersCallback {
    void onSuccess(ArrayList<UserData> users);
    void onFailure(Exception e);
}

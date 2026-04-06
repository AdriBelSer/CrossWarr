package com.yinya.crosswarr.network;

import java.util.Map;

public interface IFirebaseCallback {
    void onSuccess(Map<String, Object> data);
    void onFailure(Exception e);
}

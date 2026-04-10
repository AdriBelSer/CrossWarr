package com.yinya.crosswarr.network;


import com.yinya.crosswarr.models.ChallengeData;

import java.util.ArrayList;

public interface IChallengesCallback {
    void onSuccess(ArrayList<ChallengeData> challenges);
    void onFailure(Exception e);

}

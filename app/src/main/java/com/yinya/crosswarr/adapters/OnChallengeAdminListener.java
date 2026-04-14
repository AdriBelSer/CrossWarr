package com.yinya.crosswarr.adapters;

import com.yinya.crosswarr.models.ChallengeData;

public interface OnChallengeAdminListener {
    void onChallengeClick(ChallengeData challenge, android.view.View view);
    void onDeleteClick(ChallengeData challenge);
}

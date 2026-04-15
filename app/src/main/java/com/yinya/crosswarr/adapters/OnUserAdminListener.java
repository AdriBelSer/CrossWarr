package com.yinya.crosswarr.adapters;

import com.yinya.crosswarr.models.UserData;

public interface OnUserAdminListener {
    void onUserClick(UserData user, android.view.View view);
    void onDeleteClick(UserData user);
}

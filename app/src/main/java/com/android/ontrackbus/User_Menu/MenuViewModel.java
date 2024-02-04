package com.android.ontrackbus.User_Menu;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class MenuViewModel  extends ViewModel {
    private Bundle loginBundle;

    public Bundle getLoginBundle() {
        return loginBundle;
    }

    public void setLoginBundle(Bundle bundle) {
        this.loginBundle = bundle;
    }
}

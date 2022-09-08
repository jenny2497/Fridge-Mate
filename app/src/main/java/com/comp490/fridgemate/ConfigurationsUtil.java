package com.comp490.fridgemate;

import android.annotation.SuppressLint;
import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.ActionCodeSettings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ConfigurationsUtil {

    @NonNull
    public static List<AuthUI.IdpConfig> getConfiguredProviders(@NonNull Context context) {
        List<AuthUI.IdpConfig> providers = new ArrayList<>();

            providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

            providers.add(new AuthUI.IdpConfig.FacebookBuilder().build());


        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("com.comp490.fridgemate", true, null)
                .setHandleCodeInApp(true)
                .setUrl("https://google.com")
                .build();

        providers.add(new AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(true)
                .enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings)
                .build());


        return providers;
    }
}

package com.breakout.sample.firebase;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Firebase analytics log helper
 */
public class AnalyticsHelper {

    public static void sendScreenViewEvent(FirebaseAnalytics firebaseAnalytics, String screenName, String className) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, className);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    public enum ContentClick {
        click_a, click_b, click_c
    }

    public static void sendContentClickEvent(FirebaseAnalytics firebaseAnalytics, ContentClick contentClick, String targetName) {
        Bundle bundle = new Bundle();
        bundle.putString("target", targetName);
        firebaseAnalytics.logEvent(contentClick.name(), bundle);
    }

}

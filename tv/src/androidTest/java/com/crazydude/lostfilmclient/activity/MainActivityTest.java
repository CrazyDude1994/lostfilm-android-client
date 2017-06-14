package com.crazydude.lostfilmclient.activity;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.crazydude.lostfilmclient.CleanPreferencesActivityRule;
import com.crazydude.lostfilmclient.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by CrazyDude on 6/7/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public CleanPreferencesActivityRule<MainActivity> mActivityRule = new CleanPreferencesActivityRule<>(
            MainActivity.class);

    @Test
    public void validation_emptyText() {
        Espresso.onView(withId(android.support.v17.leanback.R.id.page_indicator))
                .perform(ViewActions.click(), ViewActions.click(), ViewActions.click());
        Espresso.onView(withText(R.string.email))
                .check(ViewAssertions.matches(withText(R.string.email)));
        Espresso.onView()
    }

    @Test
    public void validation_emptyText2() {
        Espresso.onView(withId(android.support.v17.leanback.R.id.page_indicator))
                .perform(ViewActions.click(), ViewActions.click(), ViewActions.click());
    }
}
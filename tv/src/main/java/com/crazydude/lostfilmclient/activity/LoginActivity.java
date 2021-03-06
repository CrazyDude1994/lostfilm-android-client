package com.crazydude.lostfilmclient.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crazydude.common.events.LoginEvent;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.utils.EventBusWrapper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Crazy on 09.01.2017.
 */

public class LoginActivity extends FragmentActivity {

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handle(LoginEvent event) {
        Toast.makeText(this, getString(R.string.welcome_login, event.getUsername()), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        getLifecycle().addObserver(new EventBusWrapper(this));
    }
}

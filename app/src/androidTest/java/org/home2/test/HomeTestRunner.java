package org.home2.test;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import org.home2.HomeApplication;

/**
 * Created by mtkachenko on 26/10/17.
 */

public class HomeTestRunner extends AndroidJUnitRunner {
    HomeApplication homeApplication;

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        homeApplication = (HomeApplication) super.newApplication(cl, className, context);
        return homeApplication;
    }
}

package com.frf.app.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageHelper {
    public static final void setAppLocale(String language, Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Resources resources = activity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(new Locale(language));
            activity.getApplicationContext().createConfigurationContext(configuration);
        } else {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = activity.getResources().getConfiguration();
            config.locale = locale;
            activity.getResources().updateConfiguration(config,
                    activity.getResources().getDisplayMetrics());
        }

    }
}
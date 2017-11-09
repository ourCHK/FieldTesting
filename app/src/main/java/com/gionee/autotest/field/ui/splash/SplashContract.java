package com.gionee.autotest.field.ui.splash;

import com.gionee.autotest.field.ui.base.BaseView;

/**
 * Created by viking on 11/9/17.
 *
 * Contract for splash screen
 */

interface SplashContract {

    interface View extends BaseView {

        void navigateToMainScreen();

    }

    interface Presenter {

    }
}

package com.personal.diary.test;



import android.annotation.TargetApi;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Point;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;





import com.personal.bank.BankActivity;
import com.personal.bank.EditBankActivity;
import com.personal.bank.ViewBankActivity;
import com.robotium.solo.Solo;



/**
 * <p/>
 * Automated Funtional Unit testing using Robotium 
 * <p/>
 */
public class RobotiumAutomation  extends ActivityInstrumentationTestCase2<BankActivity> {

    private Solo solo;
    Button authButton;
    int WAIT_1000_MILLIS = 1000, WAIT_2000_MILLIS = 2000, WAIT_3000_MILLIS = 3000, WAIT_4000_MILLIS = 4000, WAIT_5000_MILLIS = 5000;
    int INT_0 = 0, INT_1 = 1;
    Instrumentation.ActivityMonitor monitor;
    Activity activity;

    private final static String TAG = RobotiumAutomation.class.toString();

    public RobotiumAutomation() {
        super(BankActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    // This test method need to be the first to launch the Monitor. Change the test<<MethodName>> in alphabeticall order
    public void testRanxLogin() throws Exception {
        // register next activity that need to be monitored.
        monitor = getInstrumentation().addMonitor(ViewBankActivity.class.getName(), null, false);

        assertNotNull(solo);
        Thread.sleep(WAIT_4000_MILLIS);
        activity = solo.getCurrentActivity();

        // Flow check for first time login
        // To find and test first time LoginActiviy and existing user to  &LoadingActivity
        if (solo.getCurrentActivity().getClass().getName().contains("LoginActivity")) {
            firstTimeUser();
            assertNotNull(solo);
            Thread.sleep(11000);
        } else {
            assertNotNull(solo);
            Thread.sleep(WAIT_3000_MILLIS);
        }

        //Waits with a delay to load Tutorial Activity after Video and Facebook profile sessions_OPEN
        navigateTutorialActivity();
        //Waits with a delay to load Home Activity
        solo.waitForActivity(EditBankActivity.class);

        assertNotNull(solo);
        Thread.sleep(WAIT_1000_MILLIS);
        openNavigationDrawer();
        assertNotNull(solo);
        Thread.sleep(WAIT_1000_MILLIS);

        //Click on profile settings and test it
        solo.clickOnText(solo.getCurrentActivity().getString(R.string.menu_profile));
        profileSettings();
        editProfileSettings();

        //Logout finially
        logoutRanx();

    }

    public void navigateTutorialActivity() throws Exception {
        solo.waitForActivity(TutorialActivity.class);

        Thread.sleep(WAIT_1000_MILLIS);
        solo.clickOnButton(solo.getCurrentActivity().getString(R.string.tutorial_1_button));
        Thread.sleep(WAIT_1000_MILLIS);
        solo.clickOnImage(INT_0);
    }

    public void logoutRanx() throws Exception {
        openNavigationDrawer();
        assertNotNull(solo);
        Thread.sleep(WAIT_1000_MILLIS);
        solo.clickOnText(solo.getCurrentActivity().getString(R.string.menu_logout));
        Thread.sleep(WAIT_2000_MILLIS);
        assertNotNull(solo);
    }

    // To check the flow with Facebook Login button for first time User
    public void firstTimeUser() throws Exception {
        CheckBox policyCheckbox = (CheckBox) solo.getView(R.id.login_policy_checkbox);
        CheckBox privacyCheckbox = (CheckBox) solo.getView(R.id.login_privacy_checkbox);

        assertNotNull(policyCheckbox);
        assertNotNull(privacyCheckbox);
        solo.clickOnView(policyCheckbox);
        solo.clickOnView(privacyCheckbox);

        authButton = (LoginButton) solo.getView((R.id.login_button));
        // To verify if the button still exists in INVISIBLE mode
        assertNotNull(authButton);
        assertEquals(authButton.getVisibility(), View.VISIBLE);
        //solo.clickOnButton(authButton.getText().toString());
        //Check and click on FaceBook login
        if (authButton != null)
            solo.clickOnButton(authButton.getText().toString());

    }

    /**
     * Open the navigation drawer with a drag gesture. Click based triggering is
     * flaky on SDK < 18
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void openNavigationDrawer() {
        Point deviceSize = new Point();
        solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);

        int screenWidth = deviceSize.x;
        int screenHeight = deviceSize.y;
        int fromX = INT_0;
        int toX = screenWidth / 2;
        int fromY = screenHeight / 2;
        int toY = fromY;

        solo.drag(fromX, toX, fromY, toY, INT_1);
    }

    public void navigationAssertion() throws Exception {
        Thread.sleep(WAIT_1000_MILLIS);
        assertNotNull(solo);
        Log.d(TAG, solo.getCurrentActivity().toString());
        solo.clickOnButton(INT_0);

    }

    public void profileSettings() throws Exception {
        assertNotNull(solo);
        Thread.sleep(WAIT_2000_MILLIS);
        // Opens fragement_profile.xml test Privacy
        solo.scrollDown();
        Thread.sleep(WAIT_1000_MILLIS);
        solo.scrollDown();
        Thread.sleep(WAIT_1000_MILLIS);
        solo.scrollToTop();
        Thread.sleep(WAIT_1000_MILLIS);
        //  Do following Actions on ProfileFragement.java // fragement_profile.xml
        solo.clickOnButton(solo.getCurrentActivity().getResources().getString(R.string.profile_privacy_btn_desc));
        Thread.sleep(WAIT_1000_MILLIS);

    }

    public void editProfileSettings() throws Exception {

        int buttonCount = solo.getCurrentViews(Button.class).size();

        for (int i = 0; i < buttonCount - 1; i++) {
            solo.clickOnButton(i);
            assertNotNull(solo);
        }


        View view1;
        assertNotNull(solo);
        assertNotNull(solo.getCurrentViews(Spinner.class, false));

        assertNotNull(solo.getCurrentViews(Spinner.class, false).size());

        // ProfileEditComponent.java init() method loads details
        int spinnerSize = solo.getCurrentViews(Spinner.class, false).size();
        for (int i = 0; i <= spinnerSize; i++) {
            view1 = solo.getView(Spinner.class, i);
            solo.clickOnView(view1);
            // to select the first item considering atleast 0 till nth item item
            solo.clickOnView(solo.getView(TextView.class, INT_1));
            //Thread.sleep(WAIT_1000_MILLIS);
            assertNotNull(solo);
        }

        solo.clickOnButton("OK");

        // TODO add privacy save button desc
      //  solo.clickOnButton(solo.getCurrentActivity().getResources().getString(R.string.privacy_save_btn_desc));


        // Check updating values in Profile screen Alter values and update
        Thread.sleep(WAIT_3000_MILLIS);
    }


    public void facebookProfileUpdate() throws Exception {
        Thread.sleep(WAIT_2000_MILLIS);
        assertNotNull(solo);
        Log.d(TAG, solo.getCurrentActivity().toString());
        solo.clickOnButton(5);
        solo.clickOnButton(6);
        solo.clickOnButton(13);

        //TODO more assertions and profile updates
    }

    @Override
    public void tearDown() throws Exception {
        Thread.sleep(WAIT_1000_MILLIS);
        solo.finishOpenedActivities();
        super.tearDown();
    }

}

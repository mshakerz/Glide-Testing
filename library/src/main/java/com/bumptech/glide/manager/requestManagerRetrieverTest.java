package com.bumptech.glide.manager;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;
import static com.bumptech.glide.tests.BackgroundUtil.testInBackground;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.robolectric.annotation.LooperMode.Mode.LEGACY;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentHostCallback;
import androidx.test.core.app.ApplicationProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.tests.BackgroundUtil.BackgroundTester;
import com.bumptech.glide.tests.TearDownGlide;
import com.bumptech.glide.tests.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class RequestManagerRetrieverTest {
  @Rule public TearDownGlide tearDownGlide = new TearDownGlide();

  private Context appContext;
  private int initialSdkVersion;
  private RequestManagerRetriever retriever;

  @Before
  public void setUp() {
    appContext = ApplicationProvider.getApplicationContext();

    retriever = new RequestManagerRetriever(/* factory= */ null);

    initialSdkVersion = Build.VERSION.SDK_INT;
    Util.setSdkVersionInt(18);
  }
  
  @Test(expected = NullPointerException.class)
  public void testThrowsSupportFragementNotAttached() {
    Fragment fragment = new Fragment();
    retriever.get(fragment);
  }
  
  @Test
  public void testDoesNotThrowWhenGetWithContextCalledFromBackgroundThread()
      throws InterruptedException {
    testInBackground(
        new BackgroundTester() {
          @Override
          public void runTest() {
            retriever.get(appContext);
          }
        });
  }
    @Test(expected = IllegalArgumentException.class)
  public void testThrowsFragmentNotAttached() {
    android.app.Fragment fragment = new android.app.Fragment();
    retriever.get(fragment);
  }
}
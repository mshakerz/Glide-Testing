package com.bumptech.glide;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestManagerTreeNode;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class RequestManagerISPTest {

    //        _______________________________________________________________________________________________
    //    Test: merges the new options into the existing default options
    @Test
    public void DefaultRequestOptionsTest() {

        RequestOptions defaultOptions = new RequestOptions().centerCrop();
        RequestOptions newOptions = new RequestOptions().override(100, 100);
        RequestOptions expected = defaultOptions.apply(newOptions);

        Glide glideMock = mock(Glide.class);
        GlideContext glideContextMock = mock(GlideContext.class);
        when(glideMock.getGlideContext()).thenReturn(glideContextMock);
        when(glideContextMock.getDefaultRequestOptions()).thenReturn(defaultOptions);
        when(glideContextMock.getDefaultRequestListeners()).thenReturn(Collections.emptyList());

        RequestManager manager = new RequestManager(
                glideMock,
                mock(Lifecycle.class),
                mock(RequestManagerTreeNode.class),
                mock(RequestTracker.class),
                mock(ConnectivityMonitorFactory.class),
                mock(Context.class)
        );

        manager.applyDefaultRequestOptions(newOptions);
        RequestOptions result = manager.getDefaultRequestOptions();

        assertEquals(expected, result);
    }
    //        _______________________________________________________________________________________________
//Test: when RequestTracker says it's paused,
// verifies logic that RequestManager.isPaused() is accurately functioning
    @Test
    public void returnsTrueIsPausedTest() {

        RequestTracker requestTrackerMock = mock(RequestTracker.class);
        when(requestTrackerMock.isPaused()).thenReturn(true);

        Glide glideMock = mock(Glide.class);
        GlideContext glideContextMock = mock(GlideContext.class);
        when(glideMock.getGlideContext()).thenReturn(glideContextMock);
        when(glideContextMock.getDefaultRequestListeners()).thenReturn(Collections.emptyList());
        when(glideContextMock.getDefaultRequestOptions()).thenReturn(new RequestOptions());

        RequestManager manager = new RequestManager(
                glideMock,
                mock(Lifecycle.class),
                mock(RequestManagerTreeNode.class),
                requestTrackerMock,
                mock(ConnectivityMonitorFactory.class),
                mock(Context.class)
        );

        boolean result = manager.isPaused();

        assertTrue(result);
    }

    @Test
    public void returnsTrueIsNotPausedTest() {

        RequestTracker requestTrackerMock = mock(RequestTracker.class);
        when(requestTrackerMock.isPaused()).thenReturn(false);

        Glide glideMock = mock(Glide.class);
        GlideContext glideContextMock = mock(GlideContext.class);
        when(glideMock.getGlideContext()).thenReturn(glideContextMock);
        when(glideContextMock.getDefaultRequestListeners()).thenReturn(Collections.emptyList());
        when(glideContextMock.getDefaultRequestOptions()).thenReturn(new RequestOptions());

        RequestManager manager = new RequestManager(
                glideMock,
                mock(Lifecycle.class),
                mock(RequestManagerTreeNode.class),
                requestTrackerMock,
                mock(ConnectivityMonitorFactory.class),
                mock(Context.class)
        );

        boolean result = manager.isPaused();
        assertFalse(result);
    }
}
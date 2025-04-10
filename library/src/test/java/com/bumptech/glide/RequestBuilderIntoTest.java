package com.bumptech.glide;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.annotation.SuppressLint;
import android.content.Context;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@SuppressWarnings("unchecked")
@SuppressLint("CheckResult")
public class RequestBuilderIntoTest {

    @Mock private Glide glide;
    @Mock private RequestManager requestManager;
    @Mock private Context context;
    @Mock private Target<Object> target;
    @Mock private Request previousRequest;
    @Mock private Request newRequest;

    private RequestBuilder<Object> builder;

    @Before
    public void setUp() {
        // Suppress "unused" warning for AutoCloseable
        @SuppressWarnings("unused")
        AutoCloseable ignored = MockitoAnnotations.openMocks(this);

        // Mock GlideContext and dependencies
        GlideContext glideContext = mock(GlideContext.class);
        when(glide.getGlideContext()).thenReturn(glideContext);
        when(requestManager.getDefaultRequestOptions()).thenReturn(new RequestOptions());

        // Create spy builder
        builder = spy(new RequestBuilder<>(glide, requestManager, Object.class, context));
        builder.load("model");

        // Stub buildRequest
        doReturn(newRequest).when(builder).buildRequest(
                any(Target.class),
                any(),
                any(),
                any()
        );
    }
    // --- Test Cases ---

    @Test(expected = NullPointerException.class)
    public void testInto_nullTarget_throws() {
        builder.into((Target<Object>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInto_loadNotCalled_throws() {
        RequestBuilder<Object> emptyBuilder =
                new RequestBuilder<>(glide, requestManager, Object.class, context);
        emptyBuilder.into(target);
    }

    @Test
    public void testInto_equivalentRequestNotRunning_beginsPrevious() {
        when(target.getRequest()).thenReturn(previousRequest);
        // Corrected: Stub newRequest's equivalence to previousRequest
        when(newRequest.isEquivalentTo(previousRequest)).thenReturn(true);
        when(previousRequest.isRunning()).thenReturn(false);

        builder.into(target);

        verify(previousRequest).begin();
        verify(requestManager, never()).clear(target);
    }

    @Test
    public void testInto_equivalentRequestRunning_doesNotRestart() {
        when(target.getRequest()).thenReturn(previousRequest);
        // Corrected: Stub newRequest's equivalence to previousRequest
        when(newRequest.isEquivalentTo(previousRequest)).thenReturn(true);
        when(previousRequest.isRunning()).thenReturn(true);

        builder.into(target);

        verify(previousRequest, never()).begin();
        verify(requestManager, never()).clear(target);
    }
}
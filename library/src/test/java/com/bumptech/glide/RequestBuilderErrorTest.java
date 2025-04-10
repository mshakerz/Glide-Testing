package com.bumptech.glide;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.content.Context;

import com.bumptech.glide.request.RequestOptions;

import org.junit.Test;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@SuppressWarnings({"unchecked", "rawtypes"})
public class RequestBuilderErrorTest {

    private RequestBuilder<Object> createBuilder(Context context) {
        Glide glide = mock(Glide.class);
        GlideContext glideContext = mock(GlideContext.class);
        when(glide.getGlideContext()).thenReturn(glideContext);

        RequestManager manager = mock(RequestManager.class);
        when(manager.getDefaultRequestOptions()).thenReturn(new RequestOptions());

        // Use raw type to avoid generic issues
        TransitionOptions transitionOptions = mock(TransitionOptions.class);

        when(manager.getDefaultTransitionOptions(any(Class.class)))
                .thenReturn(transitionOptions);
        when(manager.getDefaultRequestListeners()).thenReturn(new ArrayList<>());

        return new RequestBuilder<>(glide, manager, Object.class, context);
    }

    // Helper to retrieve the private errorBuilder field via reflection.
    private RequestBuilder<Object> getErrorBuilder(RequestBuilder<Object> builder) {
        try {
            java.lang.reflect.Field field = RequestBuilder.class.getDeclaredField("errorBuilder");
            field.setAccessible(true);
            return (RequestBuilder<Object>) field.get(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------- Test Cases for Graph-Based Testing of error() --------

    // Path A: Auto-clone disabled, errorBuilder is null.
    @Test
    public void testError_autoCloneDisabled_nullErrorBuilder() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        RequestBuilder<Object> builder = createBuilder(context);
        // Force auto-clone disabled
        RequestBuilder<Object> result = builder.error((RequestBuilder<Object>) null);
        // The internal errorBuilder should be set to null.
        assertNull("Expected errorBuilder to be null", getErrorBuilder(builder));
        // And the returned builder should be the same instance.
        assertSame("Returned builder should be the same", builder, result);
    }

    // Path B: Auto-clone disabled, errorBuilder is non-null.
    @Test
    public void testError_autoCloneDisabled_nonNullErrorBuilder() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        RequestBuilder<Object> builder = createBuilder(context);
        RequestBuilder<Object> errorBuilder = createBuilder(context);
        RequestBuilder<Object> result = builder.error(errorBuilder);
        // The internal errorBuilder should be set to the provided errorBuilder.
        assertSame("Expected errorBuilder to be set", errorBuilder, getErrorBuilder(builder));
        assertSame("Returned builder should be the same", builder, result);
    }

    // Path C: Auto-clone enabled, errorBuilder is null.
    @Test
    public void testError_autoCloneEnabled_nullErrorBuilder() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        // Use a spy to override isAutoCloneEnabled and clone()
        RequestBuilder<Object> builder = spy(createBuilder(context));
        doReturn(true).when(builder).isAutoCloneEnabled();
        // For simplicity, have clone() return a new instance.
        RequestBuilder<Object> cloned = spy(createBuilder(context));
        doReturn(cloned).when(builder).clone();

        RequestBuilder<Object> result = builder.error((RequestBuilder<Object>) null);
        // Verify that clone() was called.
        verify(builder, times(1)).clone();
        // The returned builder should be the clone.
        assertSame("Returned builder should be the cloned instance", cloned, result);
        // And the cloned instance's errorBuilder should be null.
        assertNull("Cloned builder's errorBuilder should be null", getErrorBuilder(cloned));
    }

    // Path D: Auto-clone enabled, errorBuilder is non-null.
    @Test
    public void testError_autoCloneEnabled_nonNullErrorBuilder() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        RequestBuilder<Object> builder = spy(createBuilder(context));
        doReturn(true).when(builder).isAutoCloneEnabled();
        RequestBuilder<Object> cloned = spy(createBuilder(context));
        doReturn(cloned).when(builder).clone();

        RequestBuilder<Object> errorBuilder = createBuilder(context);
        RequestBuilder<Object> result = builder.error(errorBuilder);
        verify(builder, times(1)).clone();
        // The returned builder should be the clone.
        assertSame("Returned builder should be the cloned instance", cloned, result);
        // And the cloned instance's errorBuilder should be set to errorBuilder.
        assertSame("Cloned builder's errorBuilder should be set", errorBuilder, getErrorBuilder(cloned));
    }
}

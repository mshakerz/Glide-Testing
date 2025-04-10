package com.bumptech.glide;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.annotation.SuppressLint;
import android.content.Context;

import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Test;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@SuppressWarnings({"unchecked", "rawtypes"})
@SuppressLint("CheckResult")
public class RequestBuilderThumbnailRequestTest {
    private RequestBuilder<Object> createBuilder() {
        Glide glide = mock(Glide.class);
        GlideContext glideContext = mock(GlideContext.class);
        when(glide.getGlideContext()).thenReturn(glideContext);

        RequestManager manager = mock(RequestManager.class);
        when(manager.getDefaultRequestOptions()).thenReturn(new RequestOptions());

        // Use raw type declaration
        TransitionOptions transitionOptions = mock(TransitionOptions.class);
        when(transitionOptions.clone()).thenReturn(transitionOptions);

        when(manager.getDefaultTransitionOptions(any(Class.class)))
                .thenReturn(transitionOptions);
        when(manager.getDefaultRequestListeners()).thenReturn(new ArrayList<>());

        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        return new RequestBuilder<>(glide, manager, Object.class, context);
    }

    private RequestBuilder<Object> getThumbnailBuilder(RequestBuilder<Object> builder) {
        try {
            Field field = RequestBuilder.class.getDeclaredField("thumbnailBuilder");
            field.setAccessible(true);
            return (RequestBuilder<Object>) field.get(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** * Path 1: When null is passed, the thumbnail field should be null.*/
    @Test
    public void testThumbnail_withNullRequest() {
        RequestBuilder<Object> builder = createBuilder();
        RequestBuilder<Object> result = builder.thumbnail((RequestBuilder<Object>) null);
        assertSame("The method should return the same builder instance", builder, result);
        assertNull("Expected thumbnailBuilder to be null when null is passed", getThumbnailBuilder(builder));
    }

    /** * Path 2: When a non-null thumbnail is passed, the thumbnail field is set accordingly. */
    @Test
    public void testThumbnail_withNonNullRequest() {
        RequestBuilder<Object> builder = createBuilder();
        RequestBuilder<Object> thumb = createBuilder();
        RequestBuilder<Object> result = builder.thumbnail(thumb);
        assertSame("The method should return the same builder instance", builder, result);
        assertSame("Expected thumbnailBuilder to be set to the provided thumbnail", thumb, getThumbnailBuilder(builder));
    }

    /** * Path 3: When auto-clone is enabled, the method should call clone() and set the thumbnail on the clone. */
    @Test
    public void testThumbnail_withAutoCloneEnabled() {
        RequestBuilder<Object> builder = spy(createBuilder());
        // Force auto-clone enabled.
        doReturn(true).when(builder).isAutoCloneEnabled();
        // For simplicity, let clone() return a new instance.
        RequestBuilder<Object> cloned = spy(createBuilder());
        doReturn(cloned).when(builder).clone();

        RequestBuilder<Object> thumb = createBuilder();
        RequestBuilder<Object> result = builder.thumbnail(thumb);

        // Verify that clone() was called.
        verify(builder, times(1)).clone();
        // The result should be the cloned instance.
        assertSame("Returned builder should be the clone", cloned, result);
        // The cloned instance's thumbnailBuilder should be set.
        assertSame("Cloned builder should have thumbnailBuilder set to the provided thumbnail", thumb, getThumbnailBuilder(cloned));
    }
}

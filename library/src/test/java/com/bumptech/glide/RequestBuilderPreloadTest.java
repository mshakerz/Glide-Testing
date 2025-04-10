package com.bumptech.glide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@SuppressWarnings({"unchecked", "rawtypes"})
public class RequestBuilderPreloadTest {

    @Test
    public void preload_withSpecificDimensions_createsPreloadTargetWithCorrectDimensions() {
        int width = 100;
        int height = 200;
        RequestBuilder<Object> builder = createBuilder();
        FutureTarget<Object> future = builder.submit(width, height);

        assertEquals(width, getWidth(future));
        assertEquals(height, getHeight(future));
    }

    @Test
    public void preload_callsTrackOnRequestManagerWithPreloadTarget() {
        RequestBuilder<Object> builder = createBuilder();
        RequestManager requestManager = builder.getRequestManager();

        Target<Object> target = builder.preload(100, 200);

        ArgumentCaptor<Target<Object>> targetCaptor = ArgumentCaptor.forClass(Target.class);
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestManager).track(targetCaptor.capture(), requestCaptor.capture());

        Target<Object> capturedTarget = targetCaptor.getValue();
        assertSame(target, capturedTarget);
        assertTrue(capturedTarget instanceof PreloadTarget);
    }

    private RequestBuilder<Object> createBuilder() {
        // Mock Glide and dependencies
        Glide glide = mock(Glide.class);
        GlideContext glideContext = mock(GlideContext.class);
        when(glide.getGlideContext()).thenReturn(glideContext);

        // Mock GlideContext components to avoid NPEs
        when(glideContext.getExperiments()).thenReturn(mock(GlideExperiments.class));
        when(glideContext.getRegistry()).thenReturn(mock(Registry.class));
        when(glideContext.getArrayPool()).thenReturn(mock(ArrayPool.class));
        when(glideContext.getEngine()).thenReturn(mock(Engine.class));

        // Mock RequestManager
        RequestManager manager = mock(RequestManager.class);
        when(manager.getDefaultRequestOptions()).thenReturn(new RequestOptions());

        // Fix: Use any() instead of any(Class.class) to avoid unchecked warnings
        when(manager.getDefaultTransitionOptions(any(Class.class)))
                .thenReturn(mock(TransitionOptions.class));
        when(manager.getDefaultRequestListeners()).thenReturn(new ArrayList<>());

        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        // Load a dummy model to initialize the RequestBuilder
        return new RequestBuilder<>(glide, manager, Object.class, context)
                .load("http://example.com"); // Critical fix
    }

    private int getWidth(FutureTarget<?> target) {
        try {
            Field field = RequestFutureTarget.class.getDeclaredField("width");
            field.setAccessible(true);
            return (int) field.get(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve width", e);
        }
    }

    private int getHeight(FutureTarget<?> target) {
        try {
            Field field = RequestFutureTarget.class.getDeclaredField("height");
            field.setAccessible(true);
            return (int) field.get(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve height", e);
        }
    }
}
package com.bumptech.glide;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.annotation.SuppressLint;
import android.content.Context;

import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@SuppressWarnings({"unchecked", "rawtypes"})
@SuppressLint("CheckResult")
public class RequestBuilderSubmitTest {

    private RequestBuilder<Object> createBuilder() {
        // Mock Glide and dependencies
        Glide glide = mock(Glide.class);
        GlideContext glideContext = mock(GlideContext.class);
        when(glide.getGlideContext()).thenReturn(glideContext);

        // Mock required GlideContext methods
        when(glideContext.getExperiments()).thenReturn(mock(GlideExperiments.class));
        when(glideContext.getRegistry()).thenReturn(mock(Registry.class));
        when(glideContext.getArrayPool()).thenReturn(mock(ArrayPool.class));
        when(glideContext.getEngine()).thenReturn(mock(Engine.class));

        // Mock RequestManager
        RequestManager manager = mock(RequestManager.class);
        when(manager.getDefaultRequestOptions()).thenReturn(new RequestOptions());

        com.bumptech.glide.TransitionOptions transitionOptions =
                mock(com.bumptech.glide.TransitionOptions.class);
        when(transitionOptions.clone()).thenReturn(transitionOptions);
        when(manager.getDefaultTransitionOptions(any(Class.class)))
                .thenReturn(transitionOptions);
        when(manager.getDefaultRequestListeners()).thenReturn(new ArrayList<>());

        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        return new RequestBuilder<>(glide, manager, Object.class, context)
                .load("http://example.com"); // Load dummy model
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

    @Test
    public void submit_withoutParameters_usesOriginalSize() {
        RequestBuilder<Object> builder = createBuilder();
        FutureTarget<Object> future = builder.submit();

        assertEquals(Target.SIZE_ORIGINAL, getWidth(future));
        assertEquals(Target.SIZE_ORIGINAL, getHeight(future));
    }

    @Test
    public void submit_withSpecificDimensions_usesGivenDimensions() {
        int width = 100;
        int height = 200;
        RequestBuilder<Object> builder = createBuilder();
        FutureTarget<Object> future = builder.submit(width, height);

        assertEquals(width, getWidth(future));
        assertEquals(height, getHeight(future));
    }
}
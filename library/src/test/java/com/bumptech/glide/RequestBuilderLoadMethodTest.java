package com.bumptech.glide;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.content.Context;

import com.bumptech.glide.request.RequestOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // You can adjust this to your preferred API level
@SuppressWarnings({"unchecked", "rawtypes"})
public class RequestBuilderLoadMethodTest {
    private RequestBuilder<Object> createBuilder() {
        // Mock Glide and its context
        Glide glide = mock(Glide.class);
        GlideContext glideContext = mock(GlideContext.class);
        when(glide.getGlideContext()).thenReturn(glideContext);

        // Mock RequestManager and its methods
        RequestManager manager = mock(RequestManager.class);
        when(manager.getDefaultRequestOptions()).thenReturn(new RequestOptions());
        when(manager.getDefaultTransitionOptions(any(Class.class)))
                .thenReturn(new GenericTransitionOptions<>());
        when(manager.getDefaultRequestListeners()).thenReturn(new java.util.ArrayList<>());

        // Use Robolectric's application context
        Context context = org.robolectric.RuntimeEnvironment.getApplication();

        // Create a RequestBuilder instance with all non-null arguments.
        return new RequestBuilder<>(glide, manager, Object.class, context);
    }


    @Test
    public void load_acceptsNull() {
        RequestBuilder<Object> builder = createBuilder();
        assertNotNull("Builder should handle null model", builder.load((Object) null));
    }

    @Test
    public void load_acceptsString() {
        RequestBuilder<Object> builder = createBuilder();
        assertNotNull("Builder should accept a non-empty String", builder.load("image.jpg"));
    }

    @Test
    public void load_acceptsEmptyString() {
        RequestBuilder<Object> builder = createBuilder();
        assertNotNull("Builder should accept an empty String", builder.load(""));
    }

    @Test
    public void load_acceptsInteger() {
        RequestBuilder<Object> builder = createBuilder();
        assertNotNull("Builder should accept an Integer", builder.load(42));
    }

    @Test
    public void load_acceptsMaxInteger() {
        RequestBuilder<Object> builder = createBuilder();
        assertNotNull("Builder should accept Integer.MAX_VALUE", builder.load(Integer.MAX_VALUE));
    }

    @Test
    public void load_acceptsBoolean() {
        RequestBuilder<Object> builder = createBuilder();
        assertNotNull("Builder should accept a boolean", builder.load(true));
    }

    @Test
    public void load_acceptsCustomObject() {
        RequestBuilder<Object> builder = createBuilder();
        class Custom {}
        assertNotNull("Builder should accept a custom object", builder.load(new Custom()));
    }

    @Test
    public void load_acceptsArray() {
        RequestBuilder<Object> builder = createBuilder();
        assertNotNull("Builder should accept an array", builder.load(new int[]{1, 2, 3}));
    }
}

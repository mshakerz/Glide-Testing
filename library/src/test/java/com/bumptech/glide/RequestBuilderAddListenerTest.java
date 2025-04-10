package com.bumptech.glide;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.annotation.SuppressLint;
import android.content.Context;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // You can adjust this to your preferred API level
@SuppressWarnings({"unchecked", "rawtypes"})
@SuppressLint("CheckResult")
public class RequestBuilderAddListenerTest {

    private RequestBuilder<Object> createBuilder() {
        // Get real application context
        Context context = ApplicationProvider.getApplicationContext();

        // Create and configure all required mocks
        Glide glide = mock(Glide.class);
        RequestManager requestManager = mock(RequestManager.class);
        GlideContext glideContext = mock(GlideContext.class);

        // Configure Glide mock
        when(glide.getGlideContext()).thenReturn(glideContext);

        // Configure RequestManager mock with proper non-null returns
        RequestOptions defaultOptions = new RequestOptions();
        when(requestManager.getDefaultRequestOptions()).thenReturn(defaultOptions);
        when(requestManager.getDefaultRequestListeners())
                .thenReturn(Collections.<RequestListener<Object>>emptyList());
        when(requestManager.getDefaultTransitionOptions(any(Class.class)))
                .thenReturn(new GenericTransitionOptions<>());

        // Ensure GlideContext has required methods mocked
        when(glideContext.getRegistry()).thenReturn(mock(Registry.class));

        return new RequestBuilder<>(glide, requestManager, Object.class, context);
    }

    private List<RequestListener<Object>> getListeners(RequestBuilder<Object> builder) throws Exception {
        try {
            Field field = RequestBuilder.class.getDeclaredField("requestListeners");
            field.setAccessible(true);
            return (List<RequestListener<Object>>) field.get(builder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access requestListeners field", e);
        }
    }

    @Test
    public void testAddListener_withNull() throws Exception {
        RequestBuilder<Object> builder = createBuilder();
        builder.addListener(null);
        assertNull(getListeners(builder));
    }

    @Test
    public void testAddListener_withNonNull() throws Exception {
        RequestBuilder<Object> builder = createBuilder();
        RequestListener<Object> listener = mock(RequestListener.class);
        builder.addListener(listener);

        List<RequestListener<Object>> listeners = getListeners(builder);
        assertNotNull(listeners);
        assertEquals(1, listeners.size());
        assertSame(listener, listeners.get(0));
    }

    @Test
    public void testAddListener_multipleListeners() throws Exception {
        RequestBuilder<Object> builder = createBuilder();
        RequestListener<Object> listener1 = mock(RequestListener.class);
        RequestListener<Object> listener2 = mock(RequestListener.class);

        builder.addListener(listener1);
        builder.addListener(listener2);

        List<RequestListener<Object>> listeners = getListeners(builder);
        assertEquals(2, listeners.size());
        assertSame(listener1, listeners.get(0));
        assertSame(listener2, listeners.get(1));
    }

    @Test
    public void testAddListener_withAutoCloneEnabled() throws Exception {
        // Create a spy of a real builder instead of trying to mock individual methods
        RequestBuilder<Object> builder = spy(createBuilder());
        RequestBuilder<Object> clone = createBuilder();

        // Use doReturn for the spy
        doReturn(true).when(builder).isAutoCloneEnabled();
        doReturn(clone).when(builder).clone();

        RequestListener<Object> listener = mock(RequestListener.class);
        builder.addListener(listener);

        verify(builder).clone();
        List<RequestListener<Object>> listeners = getListeners(clone);
        assertNotNull(listeners);
        assertTrue(listeners.contains(listener));
    }
}
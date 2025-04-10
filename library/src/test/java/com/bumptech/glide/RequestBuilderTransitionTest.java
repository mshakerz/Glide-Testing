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
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@SuppressWarnings({"unchecked", "rawtypes"})
@SuppressLint("CheckResult")
public class RequestBuilderTransitionTest {

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

    private TransitionOptions getTransitionOptions(RequestBuilder<?> builder) {
        try {
            Field field = RequestBuilder.class.getDeclaredField("transitionOptions");
            field.setAccessible(true);
            return (TransitionOptions) field.get(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean getIsDefaultTransitionOptionsSet(RequestBuilder<?> builder) {
        try {
            Field field = RequestBuilder.class.getDeclaredField("isDefaultTransitionOptionsSet");
            field.setAccessible(true);
            return field.getBoolean(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTransition_setsTransitionOptions_andDefaultsFlag() {
        RequestBuilder<Object> builder = createBuilder();
        TransitionOptions customTransition = mock(TransitionOptions.class);

        RequestBuilder<Object> result = builder.transition(customTransition);

        assertSame(builder, result);
        assertEquals(customTransition, getTransitionOptions(builder));
        assertFalse(getIsDefaultTransitionOptionsSet(builder));
    }

    @Test(expected = NullPointerException.class)
    public void testTransition_throwsNullPointer_whenNullPassed() {
        RequestBuilder<Object> builder = createBuilder();
        builder.transition(null);
    }

    @Test
    public void testTransition_withAutoCloneEnabled_clonesBuilder() {
        // Enable auto-clone directly on the builder
        RequestBuilder<Object> builder = createBuilder().autoClone();
        TransitionOptions defaultTransition = getTransitionOptions(builder);
        assertTrue(getIsDefaultTransitionOptionsSet(builder));

        TransitionOptions customTransition = mock(TransitionOptions.class);
        RequestBuilder<Object> result = builder.transition(customTransition);

        // Verify a new clone is created
        assertNotSame(builder, result);
        assertEquals(customTransition, getTransitionOptions(result));
        assertFalse(getIsDefaultTransitionOptionsSet(result));
    }


    @Test
    public void testTransition_multipleCalls_replacesPreviousTransition() {
        RequestBuilder<Object> builder = createBuilder();

        TransitionOptions firstTransition = mock(TransitionOptions.class);
        TransitionOptions secondTransition = mock(TransitionOptions.class);

        builder.transition(firstTransition);
        builder.transition(secondTransition);

        assertEquals(secondTransition, getTransitionOptions(builder));
    }

    @Test
    public void testClone_copiesTransitionOptionsAndFlag() {
        RequestBuilder<Object> original = createBuilder();
        TransitionOptions customTransition = mock(TransitionOptions.class);
        when(customTransition.clone()).thenReturn(customTransition);

        original.transition(customTransition);
        RequestBuilder<Object> clone = original.clone();

        assertEquals(customTransition, getTransitionOptions(clone));
        assertFalse(getIsDefaultTransitionOptionsSet(clone));
    }
}
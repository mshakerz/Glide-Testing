package com.bumptech.glide;
import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.provider.ResourceDecoderRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)

public class EntryHandlesTest {
    private Class<?> entryClass;
    private Constructor<?> constructor;
    private Method handlesMethod;


    @Before
    public void setUp() throws Exception {
        // Access private static inner class
        for (Class<?> c : ResourceDecoderRegistry.class.getDeclaredClasses()) {
            if (c.getSimpleName().equals("Entry")) {
                entryClass = c;
                break;
            }
        }
        assertNotNull(entryClass);

        constructor = entryClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        handlesMethod = entryClass.getDeclaredMethod("handles", Class.class, Class.class);
        handlesMethod.setAccessible(true);
    }

    @Test
    public void testHandles_exactClassMatch_returnsTrue() throws Exception {
        ResourceDecoder<String, Integer> decoder = mock(ResourceDecoder.class);

        Object entry = constructor.newInstance(String.class, Integer.class, decoder);

        boolean result = (boolean) handlesMethod.invoke(entry, String.class, Integer.class);
        assertTrue(result);
    }

    @Test
    public void testHandles_subTypeMatch_returnsTrue() throws Exception {
        ResourceDecoder<Number, Object> decoder = mock(ResourceDecoder.class);

        Object entry = constructor.newInstance(Number.class, Object.class, decoder);

        boolean result = (boolean) handlesMethod.invoke(entry, Integer.class, String.class);
        assertTrue(result);
    }

    @Test
    public void testHandles_dataClassMismatch_returnsFalse() throws Exception {
        ResourceDecoder<String, Object> decoder = mock(ResourceDecoder.class);

        Object entry = constructor.newInstance(String.class, Object.class, decoder);

        // String is not a superclass of Integer
        boolean result = (boolean) handlesMethod.invoke(entry, Integer.class, Object.class);
        assertFalse(result);
    }

    @Test
    public void testHandles_resourceClassMismatch_returnsFalse() throws Exception {
        ResourceDecoder<Object, String> decoder = mock(ResourceDecoder.class);

        Object entry = constructor.newInstance(Object.class, String.class, decoder);

        // Integer is not a superclass of String
        boolean result = (boolean) handlesMethod.invoke(entry, Object.class, Integer.class);
        assertFalse(result);
    }

    @Test
    public void testHandles_bothMismatch_returnsFalse() throws Exception {
        ResourceDecoder<String, Integer> decoder = mock(ResourceDecoder.class);

        Object entry = constructor.newInstance(String.class, Integer.class, decoder);

        boolean result = (boolean) handlesMethod.invoke(entry, Double.class, String.class);
        assertFalse(result);
    }

}

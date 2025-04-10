package com.bumptech.glide;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.provider.ResourceDecoderRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class AppendTest {
    private ResourceDecoderRegistry registry;

    static class DummyData {
    }

    static class DummyResource {
    }
//    @SuppressWarnings("unchecked")
//    private <T> T mockGeneric(Class<?> clazz) {
//        return (T) mock(clazz);
//    }

    @Before
    public void setUp() {
        registry = new ResourceDecoderRegistry();
    }

    @Test
    public void testAppend_validEntry_addsToBucket() {
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, DummyData.class, DummyResource.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(1, result.size());
        assertSame(decoder, result.get(0));
    }

    @Test
    public void testAppend_newBucket_isAdded() {
        String bucket = "newBucket";
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);

        registry.append(bucket, decoder, DummyData.class, DummyResource.class);

        // Confirm it's discoverable via getDecoders
        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertFalse(result.isEmpty());
        assertSame(decoder, result.get(0));
    }

    @Test
    public void testAppend_sameBucket_multipleDecoders() {
        String bucket = "default";
        ResourceDecoder<DummyData, DummyResource> decoder1 = mock(ResourceDecoder.class);
        ResourceDecoder<DummyData, DummyResource> decoder2 = mock(ResourceDecoder.class);

        registry.append(bucket, decoder1, DummyData.class, DummyResource.class);
        registry.append(bucket, decoder2, DummyData.class, DummyResource.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(2, result.size());
        assertTrue(result.contains(decoder1));
        assertTrue(result.contains(decoder2));
    }

    @Test(expected = NullPointerException.class)
    public void testAppend_nullBucket_throwsException() {
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.append(null, decoder, DummyData.class, DummyResource.class);
    }

    @Test(expected = NullPointerException.class)
    public void testAppend_nullDecoder_throwsException() {
        registry.append("default", null, DummyData.class, DummyResource.class);
    }

    @Test(expected = NullPointerException.class)
    public void testAppend_nullDataClass_throwsException() {
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, null, DummyResource.class);
    }

    @Test(expected = NullPointerException.class)
    public void testAppend_nullResourceClass_throwsException() {
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, DummyData.class, null);
    }
}



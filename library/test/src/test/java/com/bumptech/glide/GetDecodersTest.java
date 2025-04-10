package com.bumptech.glide;
import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.provider.ResourceDecoderRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class GetDecodersTest {
    private ResourceDecoderRegistry registry;

    static class DummyData {}
    static class DummyResource {}

    @Before
    public void setUp() {
        registry = new ResourceDecoderRegistry();
    }

    @Test
    public void testGetDecoders_noBuckets_returnsEmptyList() {
        List<ResourceDecoder<DummyData, DummyResource>> decoders =
                registry.getDecoders(DummyData.class, DummyResource.class);
        assertTrue("Expected empty decoder list when no buckets are set", decoders.isEmpty());
    }

    @Test
    public void testGetDecoders_bucketNoMatchingDecoder_returnsEmptyList() {
        registry.setBucketPriorityList(Collections.singletonList("default"));

        // decoder that doesn't match types
        ResourceDecoder<String, String> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, String.class, String.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertTrue("Expected no decoders to match wrong data/resource types", result.isEmpty());
    }

    @Test
    public void testGetDecoders_matchingDecoder_returnsListWithDecoder() {
        registry.setBucketPriorityList(Collections.singletonList("default"));

        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, DummyData.class, DummyResource.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(1, result.size());
        assertSame(decoder, result.get(0));
    }

    @Test
    public void testGetDecoders_multipleBuckets_correctPriorityOrder() {
        registry.setBucketPriorityList(Arrays.asList("high", "low"));

        ResourceDecoder<DummyData, DummyResource> decoder1 = mock(ResourceDecoder.class);
        ResourceDecoder<DummyData, DummyResource> decoder2 = mock(ResourceDecoder.class);

        registry.append("low", decoder2, DummyData.class, DummyResource.class);
        registry.append("high", decoder1, DummyData.class, DummyResource.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(2, result.size());
        assertSame(decoder1, result.get(0));
        assertSame(decoder2, result.get(1));
    }

    @Test
    public void testGetDecoders_duplicateMatchingEntries_allIncluded() {
        registry.setBucketPriorityList(Collections.singletonList("default"));

        ResourceDecoder<DummyData, DummyResource> decoder1 = mock(ResourceDecoder.class);
        ResourceDecoder<DummyData, DummyResource> decoder2 = mock(ResourceDecoder.class);

        registry.append("default", decoder1, DummyData.class, DummyResource.class);
        registry.append("default", decoder2, DummyData.class, DummyResource.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(2, result.size());
        assertTrue(result.contains(decoder1));
        assertTrue(result.contains(decoder2));
    }

    @Test
    public void testGetDecoders_handlesSuperTypeMatching() {
        registry.setBucketPriorityList(Collections.singletonList("default"));

        ResourceDecoder<Object, Object> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, Object.class, Object.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(1, result.size());
    }
}

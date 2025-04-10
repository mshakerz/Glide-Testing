package com.bumptech.glide;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.provider.ResourceDecoderRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class GetResourceClassTest {
    private ResourceDecoderRegistry registry;

    static class DummyData {}
    static class DummyResource {}

    @Before
    public void setUp() {
        registry = new ResourceDecoderRegistry();
    }

    @Test
    public void testNoBuckets_returnsEmptyList() {
        List<Class<DummyResource>> result =
                registry.getResourceClasses(DummyData.class, DummyResource.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testBucketWithNoEntries_returnsEmptyList() {
        registry.setBucketPriorityList(java.util.Arrays.asList("default"));
        List<Class<DummyResource>> result =
                registry.getResourceClasses(DummyData.class, DummyResource.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testNoMatchingEntries_returnsEmptyList() {
        registry.setBucketPriorityList(java.util.Arrays.asList("default"));
        ResourceDecoder<String, String> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, String.class, String.class);

        List<Class<DummyResource>> result =
                registry.getResourceClasses(DummyData.class, DummyResource.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMatchingEntry_returnsResourceClass() {
        registry.setBucketPriorityList(java.util.Arrays.asList("default"));
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.append("default", decoder, DummyData.class, DummyResource.class);

        List<Class<DummyResource>> result =
                registry.getResourceClasses(DummyData.class, DummyResource.class);
        assertEquals(1, result.size());
        assertEquals(DummyResource.class, result.get(0));
    }

    @Test
    public void testDuplicateMatchingEntries_resourceClassOnlyAddedOnce() {
        registry.setBucketPriorityList(java.util.Arrays.asList("default"));
        ResourceDecoder<DummyData, DummyResource> decoder1 = mock(ResourceDecoder.class);
        ResourceDecoder<DummyData, DummyResource> decoder2 = mock(ResourceDecoder.class);

        registry.append("default", decoder1, DummyData.class, DummyResource.class);
        registry.append("default", decoder2, DummyData.class, DummyResource.class);

        List<Class<DummyResource>> result =
                registry.getResourceClasses(DummyData.class, DummyResource.class);
        assertEquals(1, result.size());
        assertEquals(DummyResource.class, result.get(0));
    }

    @Test
    public void testSuperTypeMatching_returnsResourceClass() {
        registry.setBucketPriorityList(java.util.Arrays.asList("default"));
        ResourceDecoder<Object, Object> decoder = mock(ResourceDecoder.class);

        registry.append("default", decoder, Object.class, Object.class);

        List<Class<DummyResource>> result =
                registry.getResourceClasses(DummyData.class, DummyResource.class);

        assertEquals(1, result.size());
        assertEquals(Object.class, result.get(0));
    }
}

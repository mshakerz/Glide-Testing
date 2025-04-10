package com.bumptech.glide;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
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
public class PreprendTest {
    private ResourceDecoderRegistry registry;

    // Dummy types for testing generic parameters
    static class DummyData {}
    static class DummyResource {}
    @Before
    public void setUp() {
        registry = new ResourceDecoderRegistry();
    }
    @Test
    public void testPrepend_validInputs_insertsAtBeginning() {
        String bucket = "testBucket";

        // Create two mock decoders
        ResourceDecoder<DummyData, DummyResource> decoder1 = mock(ResourceDecoder.class);
        ResourceDecoder<DummyData, DummyResource> decoder2 = mock(ResourceDecoder.class);

        // Append one first
        registry.append(bucket, decoder1, DummyData.class, DummyResource.class);
        // Then prepend another
        registry.prepend(bucket, decoder2, DummyData.class, DummyResource.class);

        // Get the decoders and verify order (prepend should come first)
        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(2, result.size());
        assertSame(decoder2, result.get(0)); // prepended
        assertSame(decoder1, result.get(1)); // appended
    }
    //Good Partition: New bucket added via prepend (bucket not in list)
    @Test
    public void testPrepend_newBucket_createsBucketAndAddsDecoder() {
        String newBucket = "newBucket";
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);

        registry.prepend(newBucket, decoder, DummyData.class, DummyResource.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(1, result.size());
        assertSame(decoder, result.get(0));
    }

    // Good Partition: Prepend multiple decoders — order should reflect LIFO (last-in-first-out)
    @Test
    public void testPrepend_multipleTimes_orderIsLifo() {
        String bucket = "stackBucket";
        ResourceDecoder<DummyData, DummyResource> decoder1 = mock(ResourceDecoder.class);
        ResourceDecoder<DummyData, DummyResource> decoder2 = mock(ResourceDecoder.class);

        registry.prepend(bucket, decoder1, DummyData.class, DummyResource.class);
        registry.prepend(bucket, decoder2, DummyData.class, DummyResource.class);

        List<ResourceDecoder<DummyData, DummyResource>> result =
                registry.getDecoders(DummyData.class, DummyResource.class);

        assertEquals(2, result.size());
        assertSame(decoder2, result.get(0)); // last prepended
        assertSame(decoder1, result.get(1)); // first prepended
    }

    //Exception Partition: Null bucket — should throw exception
    @Test(expected = NullPointerException.class)
    public void testPrepend_nullBucket_throwsException() {
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.prepend(null, decoder, DummyData.class, DummyResource.class);
    }

    // Exception Partition: Null decoder — should throw exception
    @Test(expected = NullPointerException.class)
    public void testPrepend_nullDecoder_throwsException() {
        registry.prepend("default", null, DummyData.class, DummyResource.class);
    }

    // Exception Partition: Null dataClass — should throw exception
    @Test(expected = NullPointerException.class)
    public void testPrepend_nullDataClass_throwsException() {
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.prepend("default", decoder, null, DummyResource.class);
    }

    // Exception Partition: Null resourceClass — should throw exception
    @Test(expected = NullPointerException.class)
    public void testPrepend_nullResourceClass_throwsException() {
        ResourceDecoder<DummyData, DummyResource> decoder = mock(ResourceDecoder.class);
        registry.prepend("default", decoder, DummyData.class, null);
    }
}

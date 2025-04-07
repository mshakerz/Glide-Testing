package com.bumptech.glide;
import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;

import com.bumptech.glide.provider.ResourceDecoderRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class GetOrAddEntryListTest {
    private ResourceDecoderRegistry registry;

    @Before
    public void setUp() {
        registry = new ResourceDecoderRegistry();
    }

    @Test
    public void testNewBucket_addsToPriorityList_andInitializesEntryList() throws Exception {
        String bucket = "newBucket";

        // Use reflection to access private method
        Method method = ResourceDecoderRegistry.class.getDeclaredMethod(
                "getOrAddEntryList", String.class);
        method.setAccessible(true);

        List<?> entries = (List<?>) method.invoke(registry, bucket);

        // The entry list should not be null and should be empty
        assertNotNull(entries);
        assertTrue(entries.isEmpty());

        // Bucket should now be in the priority list
        Method bucketField = ResourceDecoderRegistry.class.getDeclaredMethod("getBucketPriorityList");
        bucketField.setAccessible(true);

        List<String> priorityBuckets = (List<String>) bucketField.invoke(registry);
        assertTrue(priorityBuckets.contains(bucket));
    }

    @Test
    public void testExistingBucket_returnsSameList() throws Exception {
        String bucket = "sharedBucket";

        // Access private method
        Method method = ResourceDecoderRegistry.class.getDeclaredMethod(
                "getOrAddEntryList", String.class);
        method.setAccessible(true);
        method.setAccessible(true);

        List<?> entries1 = (List<?>) method.invoke(registry, bucket);
        entries1.add("dummy");

        List<?> entries2 = (List<?>) method.invoke(registry, bucket);

        // Must return the same list (not a new one)
        assertEquals(entries1, entries2);
        assertEquals(1, entries2.size());
    }

    @Test(expected = NullPointerException.class)
    public void testNullBucket_throwsException() throws Exception {
        Method method = ResourceDecoderRegistry.class.getDeclaredMethod(
                "getOrAddEntryList", String.class);
        method.setAccessible(true);
        method.invoke(registry, (Object) null);
    }
}

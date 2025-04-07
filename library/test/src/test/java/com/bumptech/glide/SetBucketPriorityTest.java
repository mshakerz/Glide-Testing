package com.bumptech.glide;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



import com.bumptech.glide.provider.ResourceDecoderRegistry;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class SetBucketPriorityTest {
    @SuppressWarnings("unchecked")
    private List<String> getBucketList(ResourceDecoderRegistry registry) {
        try {
            java.lang.reflect.Field field =
                    ResourceDecoderRegistry.class.getDeclaredField("bucketPriorityList");
            field.setAccessible(true);
            return (List<String>) field.get(registry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private ResourceDecoderRegistry registry;

    @Before
    public void setUp() {
        registry = new ResourceDecoderRegistry();
    }

    /**

     Clause = true: Previous bucket "a" not in new list*/@Test
    public void testClauseTrue_AddsOldBucket() {
        registry.setBucketPriorityList(Arrays.asList("a", "b"));
        registry.setBucketPriorityList(Collections.singletonList("b"));

        List<String> expected = Arrays.asList("b", "a"); // "a" is preserved at the end
        assertEquals(expected, getBucketList(registry));
    }
    /**
     * Clause = false: All previous buckets are in new list
     */
    @Test
    public void testClauseFalse_DoesNotAddOldBucket() {
        registry.setBucketPriorityList(Arrays.asList("a", "b"));
        registry.setBucketPriorityList(Arrays.asList("b", "a")); // same buckets, diff order

        List<String> expected = Arrays.asList("b", "a");
        assertEquals(expected, getBucketList(registry));
    }

    /**
     * No previous buckets: loop not entered
     */
    @Test
    public void testNoPreviousBuckets() {
        registry.setBucketPriorityList(Arrays.asList("x", "y"));

        List<String> expected = Arrays.asList("x", "y");
        assertEquals(expected, getBucketList(registry));
    }

    /**
     * All previous buckets removed: clause true for all
     */
    @Test
    public void testAllPreviousBucketsRemoved() {
        registry.setBucketPriorityList(Arrays.asList("a", "b", "c"));
        registry.setBucketPriorityList(Collections.emptyList());

        List<String> expected = Arrays.asList("a", "b", "c");
        assertEquals(expected, getBucketList(registry));
    }

    /**
     * Mixed true/false clauses
     */
    @Test
    public void testMixedClauseOutcomes() {
        registry.setBucketPriorityList(Arrays.asList("a", "b", "c"));
        registry.setBucketPriorityList(Arrays.asList("b"));

        List<String> expected = Arrays.asList("b", "a", "c"); // a, c were removed → added at end
        assertEquals(expected, getBucketList(registry));
    }
}

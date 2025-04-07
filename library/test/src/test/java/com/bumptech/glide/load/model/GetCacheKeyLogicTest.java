package com.bumptech.glide.load.model;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
//LOGIC-BASED TESTING

public class GetCacheKeyLogicTest {
    //        _______________________________________________________________________________________________
//    getCacheKey Test (Method)
        // Test where the URL constructor is used
        @Test
        public void testGetCacheKeyUsingUrlConstructor() throws MalformedURLException {
            URL testUrl = new URL("http://example.com");
            GlideUrl glideUrl = new GlideUrl(testUrl);
            assertNotNull("GlideUrl instance should not be null", glideUrl);
            String expectedCacheKey = testUrl.toString();
            assertEquals("Cache key should equal the URL's string",
                    expectedCacheKey, glideUrl.getCacheKey());
        }

    // Test where the String constructor is used
        @Test
        public void testGetCacheKeyUsingStringConstructor() {
            String testUrl = "http://example.com";
            GlideUrl glideUrl = new GlideUrl(testUrl);
            assertNotNull("GlideUrl instance should not be null", glideUrl);
            String expectedCacheKey = testUrl;
            assertEquals("Cache key should match the string",
                    expectedCacheKey, glideUrl.getCacheKey());
        }
//        _______________________________________________________________________________________________
//    getCacheKeyBytes Test (Method)
        @Test
        public void testGetCacheKeyBytesLazyInitialization() throws Exception {
            // Using a valid URL string for the test.
            String urlString = "http://google.com";
            GlideUrl glideUrl = new GlideUrl(urlString);

            // Access the private getCacheKeyBytes method
            Method getCacheKeyBytesMethod = GlideUrl.class.getDeclaredMethod("getCacheKeyBytes");
            getCacheKeyBytesMethod.setAccessible(true);

            // Twice to verify initialization and caching
            byte[] firstCall = (byte[]) getCacheKeyBytesMethod.invoke(glideUrl);
            byte[] secondCall = (byte[]) getCacheKeyBytesMethod.invoke(glideUrl);

            assertNotNull("Cache key bytes should not be null", firstCall);
            assertTrue("getCacheKeyBytes() should cache and return the same byte array",
                    firstCall == secondCall);

            // CHARSET  UTF-8, check that the content of the byte array matches the expected cache key.
            String expectedCacheKey = glideUrl.getCacheKey();
            String actualCacheKey = new String(firstCall, "UTF-8");
            assertEquals("Cache key bytes should represent the cache key string",
                    expectedCacheKey, actualCacheKey);
        }
    }
package com.bumptech.glide.load.model;


import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.assertNotEquals;

import com.google.common.testing.EqualsTester;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)

public class GlideUrlMutationTest {
    // Test equality and hashCode when created from a String.
    @Test
    public void testEqualsAndHashCodeWithStringConstructor() {
        String urlStr = "http://google.com";
        GlideUrl glideUrl1 = new GlideUrl(urlStr);
        GlideUrl glideUrl2 = new GlideUrl(urlStr);
        new EqualsTester()
                .addEqualityGroup(glideUrl1, glideUrl2)
                .testEquals();
    }

    // Test equality and hashCode when created from a URL.
    @Test
    public void testEqualsAndHashCodeWithUrlConstructor() throws MalformedURLException {
        URL url = new URL("http://google.com");
        GlideUrl glideUrl1 = new GlideUrl(url);
        GlideUrl glideUrl2 = new GlideUrl(url);
        new EqualsTester()
                .addEqualityGroup(glideUrl1, glideUrl2)
                .testEquals();
    }

    // Verify that two GlideUrl inst with different  URLs are not equal.
    @Test
    public void testNotEqualForDifferentUrls() throws MalformedURLException {
        URL url1 = new URL("http://google.com");
        URL url2 = new URL("http://bing.com");
        GlideUrl glideUrl1 = new GlideUrl(url1);
        GlideUrl glideUrl2 = new GlideUrl(url2);
        assertNotEquals("GlideUrls with different URLs should not be equal", glideUrl1, glideUrl2);
    }

    // Verify that two GlideUrl inst with the same URL but different headers are not equal.
    @Test
    public void testNotEqualForDifferentHeaders() throws MalformedURLException {
        URL url = new URL("http://example.com");
        Headers defaultHeaders = Headers.DEFAULT;
        Headers customHeaders = new FakeHeaders();
        GlideUrl glideUrl1 = new GlideUrl(url, defaultHeaders);
        GlideUrl glideUrl2 = new GlideUrl(url, customHeaders);
        assertNotEquals("GlideUrls with different headers should not be equal", glideUrl1, glideUrl2);
    }

    //  Headers implementation to simulate a different header config. Implements from the Headers class
    private static class FakeHeaders implements Headers {
        @Override
        public Map<String, String> getHeaders() {
            return Collections.singletonMap("Fake", "Header");
        }
    }

}


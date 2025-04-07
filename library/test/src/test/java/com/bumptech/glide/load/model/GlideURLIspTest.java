package com.bumptech.glide.load.model;

import static com.bumptech.glide.RobolectricConstants.ROBOLECTRIC_SDK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = ROBOLECTRIC_SDK)
public class GlideURLIspTest {

    // ISP TESTING
    //______________________________________________________________________________________
    // Tests for GlideUrl(URL url) constructor (Method)

    // Valid case: HTTP URL.
    @Test
    public void testGlideUrlConstructorWithValidHttpUrl() throws MalformedURLException {
        URL validUrl = new URL("http://google.com");
        GlideUrl glideUrl = new GlideUrl(validUrl);
        assertNotNull("GlideUrl instance should be created", glideUrl);
        // Cache key should equal the URL's string representation.
        assertEquals(validUrl.toString(), glideUrl.getCacheKey());
        // toURL() should return a URL equal to the original.
        assertEquals(validUrl.toString(), glideUrl.toURL().toString());
    }

    // Valid case: URL with HTTPS.
    @Test
    public void testGlideUrlConstructorWithValidHttpsUrl() throws MalformedURLException {
        URL validUrl = new URL("https://google.com");
        GlideUrl glideUrl = new GlideUrl(validUrl);
        assertNotNull(glideUrl);
        assertEquals(validUrl.toString(), glideUrl.getCacheKey());
        assertEquals(validUrl.toString(), glideUrl.toURL().toString());
    }

    // Edge case: null URL, Should throw NullPointerException.
    @Test(expected = NullPointerException.class)
    public void testGlideUrlConstructorWithNullUrl() {
        new GlideUrl((URL) null);
    }

    //______________________________________________________________________________________
    // ISP Tests for GlideUrl(String url) constructor (Method)

    // Valid case: HTTP URL string.
    @Test
    public void testGlideUrlConstructorWithValidHttpString() throws MalformedURLException {
        String urlString = "http://google.com";
        GlideUrl glideUrl = new GlideUrl(urlString);
        assertNotNull("GlideUrl instance should be created", glideUrl);
        // The cache key should equal the original string.
        assertEquals(urlString, glideUrl.getCacheKey());
        // toURL() should produce a URL equal to the original string.
        assertEquals("http://google.com", glideUrl.toURL().toString());
    }

    // Valid case: HTTPS URL string.
    @Test
    public void testGlideUrlConstructorWithValidHttpsString() throws MalformedURLException {
        String urlString = "https://google.com";
        GlideUrl glideUrl = new GlideUrl(urlString);
        assertNotNull(glideUrl);
        assertEquals(urlString, glideUrl.getCacheKey());
        assertEquals("https://google.com", glideUrl.toURL().toString());
    }

    // Edge case: URL with a trailing slash.
    @Test
    public void testGlideUrlConstructorWithTrailingSlash() throws MalformedURLException {
        String urlString = "http://google.com/";
        GlideUrl glideUrl = new GlideUrl(urlString);
        assertNotNull(glideUrl);
        assertEquals(urlString, glideUrl.getCacheKey());
        assertEquals("http://google.com/", glideUrl.toURL().toString());
    }

    // Edge case: URL with a random query.
    @Test
    public void testGlideUrlConstructorWithQueryParameters() throws MalformedURLException {
        String urlString = "http://google.com/path?query=123&sort=asc";
        GlideUrl glideUrl = new GlideUrl(urlString);
        assertNotNull(glideUrl);
        assertEquals(urlString, glideUrl.getCacheKey());
        assertEquals(urlString, glideUrl.toURL().toString());
    }

    // Edge case: URL with spaces.
    @Test
    public void testGlideUrlConstructorWithSpaces() throws MalformedURLException {
        String urlString = "http://google.com/some path/with spaces";
        GlideUrl glideUrl = new GlideUrl(urlString);
        assertNotNull(glideUrl);
        assertEquals(urlString, glideUrl.getCacheKey());
        String expectedEncoded = "http://google.com/some%20path/with%20spaces";
        assertEquals(expectedEncoded, glideUrl.toURL().toString());
    }

    // Edge case: URL with non-ASCII characters.
    @Test
    public void testGlideUrlConstructorWithNonAsciiCharacters() throws MalformedURLException {
        String urlString = "http://google.com/mañana";
        GlideUrl glideUrl = new GlideUrl(urlString);
        assertNotNull(glideUrl);
        assertEquals(urlString, glideUrl.getCacheKey());
        String expectedEncoded = "http://google.com/ma%C3%B1ana";
        assertEquals(expectedEncoded, glideUrl.toURL().toString());
        // Expected: "mañana" is encoded as "%C3%B1ana"
    }

    // Edge case: URL that is only whitespace.
    @Test(expected = MalformedURLException.class)
    public void testGlideUrlConstructorWithWhitespaceOnly() throws MalformedURLException {
        String urlString = "   ";
        GlideUrl glideUrl = new GlideUrl(urlString);
        glideUrl.toURL();
    }

    // Edge case: URL that does not have a any http
    @Test(expected = MalformedURLException.class)
    public void testGlideUrlConstructorWithInvalidUrlFormat() throws MalformedURLException {
        String urlString = "www.google.com";
        GlideUrl glideUrl = new GlideUrl(urlString);
        glideUrl.toURL();
    }

    // Edge case: Empty string should throw IllegalArgumentException.
    @Test(expected = IllegalArgumentException.class)
    public void testGlideUrlConstructorWithEmptyString() {
        new GlideUrl("");
    }

    // Edge case: Null string should throw IllegalArgumentException.
    @Test(expected = IllegalArgumentException.class)
    public void testGlideUrlConstructorWithNullString() {
        new GlideUrl((String) null);
    }

}
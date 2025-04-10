package com.bumptech.glide.provider;
import com.bumptech.glide.load.Encoder; 
import static com.google.common.truth.Truth.assertThat; 
import org.junit.Before;
import org.junit.Test; 
import org.junit.runner.RunWith; 
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class) 
@Config(sdk = 18)

public class EncoderRegistryTest {
	private EncoderRegistry registry; 
	
	@Before
	public void setUp() {registry = new EncoderRegistry();} 
	
	@Test
	public void testWithEntry() {
		Encoder<String> stringEncoder = (data, file, options) -> false; 
		registry.append(String.class, stringEncoder);
		Encoder<String> result = registry.getEncoder(String.class); 
		assertThat(result).isNotNull();
		}
	
	@Test public void testWithoutEntry() {
		assertThat(registry.getEncoder(String.class)).isNull();
	}
}

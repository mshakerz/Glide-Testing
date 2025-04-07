package com.bumptech.glide;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;

import com.bumptech.glide.request.RequestOptions;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;

public class RequestBuilderThumbnailTest {

  private RequestBuilder<Object> createBuilder() {
    Glide glide = mock(Glide.class);
    RequestManager manager = mock(RequestManager.class);
    when(manager.getDefaultRequestOptions()).thenReturn(new RequestOptions());
    return new RequestBuilder<>(glide, manager, Object.class, mock(Context.class));
  }

  @Test
  public void thumbnail_withDifferentInputTypes() {
    RequestBuilder<Object> builder = createBuilder();
    RequestBuilder<Object> thumb1 = createBuilder();
    RequestBuilder<Object> thumb2 = createBuilder();

    // 1. Null input
    assertSame(builder, builder.thumbnail((RequestBuilder<Object>)null));

    // 2. Single thumbnail
    assertSame(builder, builder.thumbnail(thumb1));

    // 3. Multiple thumbnails (using list instead of array)
    List<RequestBuilder<Object>> thumbsList = Arrays.asList(thumb1, thumb2);
    assertSame(builder, builder.thumbnail(thumbsList));
  }

  @Test
  public void thumbnailSizeMultiplier_boundaryValues() {
    RequestBuilder<Object> builder = createBuilder();

    // Valid cases
    assertSame(builder, builder.thumbnail(0.0f));
    assertSame(builder, builder.thumbnail(0.5f));
    assertSame(builder, builder.thumbnail(1.0f));

    // Invalid cases
    assertThrows(IllegalArgumentException.class, () -> builder.thumbnail(-0.1f));
    assertThrows(IllegalArgumentException.class, () -> builder.thumbnail(1.1f));
  }
}
package ru.basecode.ide.rest.plugin.http;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author danblack
 */
@Value
@Builder
public class HttpResponse {
  String status;
  List<String> headers;
  String contentType;
  @Nullable
  String body;
}

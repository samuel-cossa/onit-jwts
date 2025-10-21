package com.onit.authentication.jwts.common.dto;

import java.util.List;

public record PageResponse<T>(
   List<T> content,
   int page,
   int size,
   long totalElements,
   int totalPages,
   boolean first,
   boolean last) {
}

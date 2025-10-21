package com.onit.authentication.jwts.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * HttpResponse
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/21/25
 */


@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResponse {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime timeStamp;

  private Map<String, Object> data;

  private String message;

  private HttpStatus status;

  private int statusCode;
}
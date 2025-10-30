package com.onit.authentication.jwts.common.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * StatusResponseDto
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/22/25
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusResponseDto {
  private String message;
  private HttpStatus status;
  private int statusCode;
}

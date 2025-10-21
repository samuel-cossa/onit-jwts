package com.onit.authentication.jwts.common.dto;

/**
 * Standard API response wrapper.
 * 
 * @author Samuel Cossa https://github.com/samuel-cossa
 */
public record ApiResponse(
		boolean success,
		String message,
		Object data

) {
}

package com.redfin.exercise;

public class RestaurantClientException extends RuntimeException {
  public RestaurantClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestaurantClientException(String message) {
    super(message);
  }
}

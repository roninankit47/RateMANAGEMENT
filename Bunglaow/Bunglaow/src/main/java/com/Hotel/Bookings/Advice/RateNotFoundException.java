package com.Hotel.Bookings.Advice;

public class RateNotFoundException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;

	public RateNotFoundException(String message) {
        super(message);
    }
}

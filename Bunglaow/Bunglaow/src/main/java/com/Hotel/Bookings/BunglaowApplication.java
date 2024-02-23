package com.Hotel.Bookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;

import com.Hotel.Bookings.Advice.MyControllerAdvice;

@SpringBootApplication
//@Import({MyControllerAdvice.class})
@Validated
public class BunglaowApplication {

	public static void main(String[] args) {
		SpringApplication.run(BunglaowApplication.class, args);
	}

}

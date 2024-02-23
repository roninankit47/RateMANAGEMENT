//package com.Hotel.Bookings.Advice;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class ApplicationexceptionHandler {
//	
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex){
//		
//		Map<String, String> errrorresponse=new HashMap<>();
//		ex.getBindingResult().getFieldErrors().forEach(errors->{
//			errrorresponse.put(errors.getField(), errors.getDefaultMessage());
//		});
//		return errrorresponse;
//	}
//
//}

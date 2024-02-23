package com.Hotel.Bookings.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Hotel.Bookings.Entity.Rate;
import com.Hotel.Bookings.Service.RateService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestController
public class RateController {

	@Autowired
	private RateService rateService;


	/**
	 * Constructor to inject the RateService dependency.
	 * 
	 * @param rateService The RateService to be injected.
	 */
	public RateController(RateService rateService) {
		super();
		this.rateService = rateService;
	}


	// get all rates
	//@return List of all rates.
	// Custom exception for bad requests
		@ResponseStatus(HttpStatus.BAD_REQUEST)
		public class BadRequestException extends RuntimeException {
			private static final long serialVersionUID=1L;
			public BadRequestException(String message) {
		        super(message);
		    }
		}

		/**
		 * Retrieve a specific rate by its ID.
		 *
		 * @param id The ID of the rate to retrieve.
		 * @return Optional containing the rate if found, empty otherwise.
		 */

		@GetMapping("/rates")
		public List<Rate> getRateById(@RequestParam(required = false) Long bunglaowId,
				@RequestParam(required = false) LocalDate date, 
				@RequestParam(required = false) LocalDate fromDate,
				@RequestParam(required = false) LocalDate toDate) {
			System.out.println( (fromDate == null && toDate != null) || (fromDate != null && toDate == null));
			if(bunglaowId != null && fromDate != null && toDate != null){
				List<Rate> rates = rateService.getRateByBugalowIdDateRange(bunglaowId,fromDate,toDate);
				return rates;
			}
			else if((fromDate == null && toDate != null) || (fromDate != null && toDate == null)){
				throw new BadRequestException("One or more required parameters are missing.");
			}
			else if(date != null && bunglaowId != null) {
				Rate rate = rateService.getRateByBugalowIdDate(bunglaowId,date);
				return List.of(rate);
			}else if(date != null && bunglaowId == null) {
				 throw new BadRequestException("One or more required parameters are missing.");
			}
			else if(bunglaowId != null) {
				return rateService.getAllRatesForBungalow(bunglaowId);
			}else if(toDate == null && fromDate == null && date == null && bunglaowId == null) {
				return rateService.getAllRates();
			}
			else {
				 // If any of the required parameters are null, throw BadRequestException
		        throw new BadRequestException("One or more required parameters are missing.");
			}
		}

	/**
	 * Retrieve a specific rate by its ID.
	 *
	 * @param id The ID of the rate to retrieve.
	 * @return Optional containing the rate if found, empty otherwise.
	 */

	@GetMapping("/rates/{id}")
	public Optional<Rate> getRateById(@PathVariable Long id) {
		return rateService.getRateById(id);
	}

	

	/**
	 * Create a new rate.
	 *
	 * @param rate The Rate object to be created.
	 * @return ResponseEntity with the created rate and HTTP status code.
	 */

	@PostMapping("/rates/save")
	public ResponseEntity<Object> createRate(@RequestBody Rate rate)
	{
		try {
			System.out.println("rate is"+ rate);
			Rate createdRate = this.rateService.createRate(rate);
			return new ResponseEntity<>(createdRate, HttpStatus.CREATED);
		} catch (ConstraintViolationException ex) {
			// Validation error handling
			List<String> errors = ex.getConstraintViolations()
					.stream()
					.map(ConstraintViolation::getMessage)
					.collect(Collectors.toList());

			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("timestamp", LocalDateTime.now());
			errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
			errorResponse.put("errors", errors);

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			// Other error handling (e.g., database-related errors)
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("timestamp", LocalDateTime.now());
			errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
			errorResponse.put("error", "Bad Body request");
			errorResponse.put("message", ex.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Update an existing rate.
	 *
	 * @param id     The ID of the rate to be updated.
	 * @param newRate The updated Rate object.
	 * @return The updated Rate object.
	 */
	@PutMapping("/rates/{id}")
	public Rate updateRate(@PathVariable Long id, @RequestBody Rate newRate) {
		return rateService.updateRate(id, newRate);
	}

	/**
	 * Delete a rate by its ID.
	 *
	 * @param id The ID of the rate to be deleted.
	 * @return ResponseEntity with a success message and HTTP status code.
	 */

	@DeleteMapping("/rates/{id}")
	public ResponseEntity<String>  deleteRate(@PathVariable("id") Long id) {
		rateService.deleteRate(id);
		return new ResponseEntity<String>("Rate deleted successfully", HttpStatus.OK);
	}

	@PostMapping("/import")
	public ResponseEntity<String> importRates(@RequestParam("file") MultipartFile file) {
		rateService.importRatesFromExcel(file);
		return new ResponseEntity<>("Rates imported successfully", HttpStatus.OK);
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> exportRates() {
		byte[] excelBytes = rateService.exportRatesToExcel();
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=rates_export.xlsx")
				.body(excelBytes);
	}
}
package com.Hotel.Bookings.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Hotel.Bookings.Entity.Rate;

@Service
public interface RateService {

	List<Rate> getAllRates();
	Optional<Rate> getRateById(Long id);
	List<Rate> getAllRatesForBungalow(Long bungalowId);
	public Rate createRate(Rate rate);
	Rate updateRate(Long id, Rate newRate);
	public void deleteRate(Long id);
	byte[] exportRatesToExcel();
	void importRatesFromExcel(MultipartFile file);
	List<Rate> getRateByBugalowIdDateRange(Long bunglaowId, LocalDate fromDate, LocalDate toDate);
	Rate getRateByBugalowIdDate(Long bunglaowId, LocalDate date);
	
}

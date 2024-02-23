package com.Hotel.Bookings.ServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Hotel.Bookings.Entity.ExcelUtils;
import com.Hotel.Bookings.Entity.Rate;
import com.Hotel.Bookings.Repository.RateRepository;
import com.Hotel.Bookings.Service.RateService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;

@Service
public class RateServiceImpl implements RateService {

	@Autowired
	private RateRepository rateRepository;

	public RateServiceImpl(RateRepository rateRepository) {
		super();
		this.rateRepository = rateRepository;
	}

	@Override
	public List<Rate> getAllRates() {
		return rateRepository.findAll();
		
	}
	
	@Override
	public Rate getRateByBugalowIdDate(Long bunglaowId,LocalDate date) {
		Rate rate = rateRepository.findRateForDay(bunglaowId, date)
		        .orElseThrow(() -> new IllegalArgumentException("No rate found in the database for this Bunglaow id: " + bunglaowId));
		return rate;
		
	}
	
	@Override
	public List<Rate> getRateByBugalowIdDateRange(Long bunglaowId,LocalDate fromDate,LocalDate toDate) {
		List<Rate> rates = rateRepository.findRatesForDateRange(bunglaowId, fromDate, toDate);
		return rates;
		
	}


	@Override
	public Optional<Rate> getRateById(Long id) {
		return Optional.of(rateRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No rate found in the database for this id: " + id)));
	}
	
	

	@Override
	public List<Rate> getAllRatesForBungalow(Long bungalowId) {
		List<Rate> rates = rateRepository.findAllByBungalowId(bungalowId);
		return rates;
	}

	public Rate createRate(Rate newRate) {
		validateRate(newRate);
		Long bungalowId = newRate.getBungalowId();
		List<Rate> overlappingRates = rateRepository.findRatesByBungalowIdAndNights(bungalowId, newRate.getNights());
		List<Rate> lappingRates = new ArrayList<Rate>();
		if (!overlappingRates.isEmpty()) {
			lappingRates = splitAndMergeRate(newRate, overlappingRates);
			if (lappingRates.size() != 0) {
				this.rateRepository.saveAll(lappingRates);
			} else {
				for (Rate overlappingRate : overlappingRates) {
					
					if (overlappingRate.equals(newRate)) {
						this.rateRepository.delete(overlappingRate);
						this.rateRepository.save(newRate);
						return newRate;
					} else if (overlappingRate.getValue() == newRate.getValue()
							&& overlappingRate.getStayDateFrom().isBefore(newRate.getStayDateFrom())
							&& overlappingRate.getStayDateTo().isAfter(newRate.getStayDateTo())) {
						throw new IllegalArgumentException(
								"A conflicting rate overlaps with the provided dates and rate value.");
					} else if (overlappingRate.getValue() == newRate.getValue()
							&& overlappingRate.getStayDateFrom().isEqual(newRate.getStayDateFrom())
							&& overlappingRate.getStayDateTo().isAfter(newRate.getStayDateTo())) {
						throw new IllegalArgumentException(
								"A conflicting rate starts before and ends after the provided rate.");
					} else if (overlappingRate.getValue() == newRate.getValue()
							&& overlappingRate.getStayDateTo().isEqual(newRate.getStayDateTo())
							&& overlappingRate.getStayDateFrom().isBefore(newRate.getStayDateFrom())) {
						throw new IllegalArgumentException(
								"A conflicting rate already exists for the provided duration and rate value.");
					}
				}
				this.rateRepository.save(newRate);
			}
		} else {
			this.rateRepository.save(newRate);
		}
		return newRate;

	}

	@Override
	public Rate updateRate(Long id, Rate newRate) {
		newRate.setId(id);
		validateRate(newRate);
		Long bungalowId = newRate.getBungalowId();
		List<Rate> overlappingRates = rateRepository.findRatesByBungalowIdAndNights(bungalowId, newRate.getNights());
		List<Rate> lappingRates = new ArrayList<Rate>();
		if (!overlappingRates.isEmpty()) {
			lappingRates = splitAndMergeRate(newRate, overlappingRates);
			if (lappingRates.size() != 0) {
				this.rateRepository.saveAll(lappingRates);
			} else {
				for (Rate overlappingRate : overlappingRates) {
					if (overlappingRate.equals(newRate)) {
						throw new IllegalArgumentException("The provided rate already exists in the system");
					} else if (overlappingRate.getValue() == newRate.getValue()
							&& overlappingRate.getStayDateFrom().isBefore(newRate.getStayDateFrom())
							&& overlappingRate.getStayDateTo().isAfter(newRate.getStayDateTo())) {
						throw new IllegalArgumentException(
								"A conflicting rate overlaps with the provided dates and rate value.");
					} else if (overlappingRate.getValue() == newRate.getValue()
							&& overlappingRate.getStayDateFrom().isEqual(newRate.getStayDateFrom())
							&& overlappingRate.getStayDateTo().isAfter(newRate.getStayDateTo())) {
						throw new IllegalArgumentException(
								"A conflicting rate starts before and ends after the provided rate.");
					} else if (overlappingRate.getValue() == newRate.getValue()
							&& overlappingRate.getStayDateTo().isEqual(newRate.getStayDateTo())
							&& overlappingRate.getStayDateFrom().isBefore(newRate.getStayDateFrom())) {
						throw new IllegalArgumentException(
								"A conflicting rate already exists for the provided duration and rate value.");
					}
				}
				this.rateRepository.save(newRate);
			}
		} else {
			this.rateRepository.save(newRate);
		}
		return newRate;
	}

	@Override
	public void deleteRate(Long rateId) {
		// Optional: You may want to check if the rate exists before trying to delete
		if (rateRepository.existsById(rateId)) {
			rateRepository.deleteById(rateId);
		} else {
			throw new IllegalArgumentException("no rate found in database for this id" + rateId);
		}
	}

	private void validateRate(Rate rate) {
	    if (rate.getStayDateFrom() == null) {
	        throw new IllegalArgumentException("Stay date from is required.");
	    }
	    if (rate.getStayDateTo() == null) {
	        throw new IllegalArgumentException("Stay date to is required.");
	    }
	    if (rate.getStayDateFrom().isAfter(rate.getStayDateTo())) {
	        throw new IllegalArgumentException("The stay dates are invalid: 'stayDateFrom' is after 'stayDateTo'.");
	    }
	    if (rate.getNights()==0 || rate.getNights() <= 0) {
	        throw new IllegalArgumentException("Nights is required and should be a positive number.");
	    }
	    if (rate.getValue() == 0 || rate.getValue() <= 0.0) {
	        throw new IllegalArgumentException("Value is required and should be a positive number.");
	    }

	    if (rate.getBungalowId() == null) {
	        throw new IllegalArgumentException("Bungalow ID is required.");
	    }
	    
	    Set<ConstraintViolation<Rate>> violations = Validation.buildDefaultValidatorFactory().getValidator()
	            .validate(rate);

	    if (!violations.isEmpty()) {
	        throw new ConstraintViolationException(violations);
	    }
	}


	/**
	 * Splits and merges rates based on overlapping scenarios with a new rate.
	 *
	 * @param newRate          The new rate to be added.
	 * @param overlappingRates List of existing rates that overlap with the new
	 *                         rate.
	 * @return List of rates after handling overlaps and merges.
	 */
	private List<Rate> splitAndMergeRate(Rate newRate, List<Rate> overlappingRates) {
		// Save the three new rates and update the existing rate
		List<Rate> lappingRates = new ArrayList<Rate>();
		Rate closedDate = new Rate();
		Boolean isMergeRate = false;
		Boolean isRateSplit = false;

		// Loop to handle overlapping scenarios
		for (Rate overlappingRate : overlappingRates) {
		
			// Case 1: Complete overlap, closing the existing rate
			if (newRate.getValue() != overlappingRate.getValue()
					&& (newRate.getStayDateFrom().isEqual(overlappingRate.getStayDateFrom()))
					&& newRate.getStayDateTo().isEqual(overlappingRate.getStayDateTo())) {
				overlappingRate.setClosedDate(LocalDateTime.now());
				closedDate = overlappingRate;
				isRateSplit = true;
			}
			// Case 2: Partial overlap from the start, splitting the existing rate
			else if (newRate.getValue() != overlappingRate.getValue()
					&& (newRate.getStayDateFrom().isEqual(overlappingRate.getStayDateFrom()))
					&& (newRate.getStayDateTo().isBefore(overlappingRate.getStayDateTo())
							|| newRate.getStayDateTo().isAfter(overlappingRate.getStayDateTo()))) {
				if (newRate.getStayDateTo().isAfter(overlappingRate.getStayDateTo())) {
					overlappingRate.setClosedDate(LocalDateTime.now());
					closedDate = overlappingRate;
				} else {
					System.out.println("first");
					System.out.println(overlappingRate);
					Rate splitRate = new Rate(overlappingRate);
					splitRate.setStayDateFrom(newRate.getStayDateTo().plusDays(1));
					overlappingRate.setClosedDate(LocalDateTime.now());
					lappingRates.add(splitRate);
					closedDate = overlappingRate;

				}
				isRateSplit = true;
				break;
			}
			// Case 3: Partial overlap in the middle, splitting and updating the existing
			// rate
			else if (newRate.getValue() != overlappingRate.getValue()
					&& (newRate.getStayDateFrom().isAfter(overlappingRate.getStayDateFrom()))
					&& (newRate.getStayDateFrom().isBefore(overlappingRate.getStayDateTo()))
					&& (newRate.getStayDateTo().isBefore(overlappingRate.getStayDateTo())
							|| newRate.getStayDateTo().isAfter(overlappingRate.getStayDateTo()))) {
				if (newRate.getStayDateTo().isAfter(overlappingRate.getStayDateTo())) {
					Rate splitRate = new Rate(overlappingRate);
					splitRate.setStayDateTo(newRate.getStayDateFrom().minusDays(1));
					lappingRates.add(splitRate);
					overlappingRate.setClosedDate(LocalDateTime.now());
					closedDate = overlappingRate;
				} else {
					System.out.println("second");
					System.out.println(overlappingRate);
					Rate firstPart = new Rate(overlappingRate);
					firstPart.setStayDateTo(newRate.getStayDateFrom().minusDays(1));
					Rate lastPart = new Rate(overlappingRate);
					lastPart.setStayDateFrom(newRate.getStayDateTo().plusDays(1));
					// Close the existing rate by setting CLOSED_DATE to the current date
					overlappingRate.setClosedDate(LocalDateTime.now());
					lappingRates.add(firstPart);
					lappingRates.add(lastPart);
					closedDate = overlappingRate;
				}
				isRateSplit = true;
				break;
			}
			// Case 4: Partial overlap at the end, splitting and updating the existing rate
			else if (newRate.getValue() != overlappingRate.getValue()
					&& (newRate.getStayDateTo().isEqual(overlappingRate.getStayDateTo()))
					&& newRate.getStayDateFrom().isBefore(overlappingRate.getStayDateTo())) {
				System.out.println("third");
				System.out.println(overlappingRate);
				Rate firstPart = new Rate(overlappingRate);
				firstPart.setStayDateTo(newRate.getStayDateFrom().minusDays(1));
				overlappingRate.setClosedDate(LocalDateTime.now());
				lappingRates.add(firstPart);
				closedDate = overlappingRate;
				isRateSplit = true;
				break;
			}
			// Case 5: Overlapping at the end(exact end date) of the existing rate, adjust
			// dates
			else if (newRate.getValue() != overlappingRate.getValue()
					&& (newRate.getStayDateFrom().isEqual(overlappingRate.getStayDateTo()))
					&& newRate.getStayDateTo().isAfter(overlappingRate.getStayDateTo())) {
				Rate firstPart = new Rate(overlappingRate);
				firstPart.setStayDateTo(newRate.getStayDateFrom().minusDays(1));
				lappingRates.add(firstPart);
				overlappingRate.setClosedDate(LocalDateTime.now());
				closedDate=overlappingRate;
				isRateSplit = true;
				break;
			}
			//overlapping before date in between
			else if(newRate.getValue() != overlappingRate.getValue()
					&& (newRate.getStayDateFrom().isBefore(overlappingRate.getStayDateFrom()))
					&& (newRate.getStayDateTo().isBefore(overlappingRate.getStayDateTo()))) {
				Rate splitPart = new Rate(overlappingRate);
				splitPart.setStayDateFrom(newRate.getStayDateTo().plusDays(1));
				lappingRates.add(splitPart);
				overlappingRate.setClosedDate(LocalDateTime.now());
				closedDate = overlappingRate;
				isRateSplit = true;
				break;
			}
			
			//overlapping before date till the end or extend after
			else if(newRate.getValue() != overlappingRate.getValue()
					&& (newRate.getStayDateFrom().isBefore(overlappingRate.getStayDateFrom()))
					&& (newRate.getStayDateTo().isEqual(overlappingRate.getStayDateTo()) 
							|| newRate.getStayDateTo().isAfter(overlappingRate.getStayDateTo()))) {
				overlappingRate.setClosedDate(LocalDateTime.now());
				closedDate = overlappingRate;
				isRateSplit = true;
				break;
			}
			
		}

		// Sort overlapping rates by stayDateFrom for accurate merging
		overlappingRates.sort(Comparator.comparing(Rate::getStayDateFrom));

		// Loop to merge overlapping rates
		for (int i = 0; i < overlappingRates.size(); i++) {
			Rate overlappingRate = overlappingRates.get(i);
			if (i <= overlappingRates.size() - 2) {
				Rate nextOverlappingRate = overlappingRates.get(i + 1);
				
				// Case 6: Merging between two rates when middle rate is not available
				if (overlappingRate.getValue() == newRate.getValue()
						&& nextOverlappingRate.getValue() == newRate.getValue()
						&& overlappingRate.getStayDateTo().plusDays(1).isEqual(newRate.getStayDateFrom())
						&& nextOverlappingRate.getStayDateFrom().minusDays(1).isEqual(newRate.getStayDateTo())) {
					overlappingRate.setStayDateTo(nextOverlappingRate.getStayDateTo());
					lappingRates.add(overlappingRate);
					this.rateRepository.delete(nextOverlappingRate);
					return lappingRates;
				}
			}

			if (i <= overlappingRates.size() - 3) {
				Rate nextOverlappingRate = overlappingRates.get(i + 2);
				// Case 7: Merging between rate when we are updating middle rate
				if (overlappingRate.getValue() == newRate.getValue()
						&& nextOverlappingRate.getValue() == newRate.getValue()
						&& overlappingRate.getStayDateTo().plusDays(1).isEqual(newRate.getStayDateFrom())
						&& nextOverlappingRate.getStayDateFrom().minusDays(1).isEqual(newRate.getStayDateTo())) {
					overlappingRate.setStayDateTo(nextOverlappingRate.getStayDateTo());
					lappingRates.add(overlappingRate);
					overlappingRates.get(i + 1).setClosedDate(LocalDateTime.now());
					lappingRates.add(overlappingRates.get(i + 1));
					this.rateRepository.delete(nextOverlappingRate);
					return lappingRates;
				}
			}
			// Case 8: Merging at the end of the existing rate without an intermediate rate
			if (overlappingRate.getValue() == newRate.getValue()
					&& overlappingRate.getStayDateTo().plusDays(1).isEqual(newRate.getStayDateFrom())) {
				overlappingRate.setStayDateTo(newRate.getStayDateTo());
				lappingRates.add(overlappingRate);
				System.out.println(overlappingRate);
				if (closedDate.getBungalowId() == null) {
					return lappingRates;
				} else {
					closedDate.setStayDateTo(newRate.getStayDateTo());
					closedDate.setClosedDate(LocalDateTime.now());
					lappingRates.add(closedDate);
				}

				isMergeRate = true;
				break;
			}
			// Case 9: Merging at the start of the existing rate
			else if (overlappingRate.getValue() == newRate.getValue()
					&& newRate.getStayDateTo().plusDays(1).isEqual(overlappingRate.getStayDateFrom())) {
				overlappingRate.setStayDateFrom(newRate.getStayDateFrom());
				lappingRates.add(overlappingRate);
				if (closedDate.getBungalowId() == null) {
					return lappingRates;
				} else {
					closedDate.setStayDateFrom(newRate.getStayDateFrom());
					closedDate.setClosedDate(LocalDateTime.now());
					lappingRates.add(closedDate);
				}
				isMergeRate = true;
				break;
			}
			// Case 10: Overlapping before the existing rate, adjusting the end date of the new rate
			else if (overlappingRate.getValue() == newRate.getValue()
					&& newRate.getStayDateTo().isAfter(overlappingRate.getStayDateFrom())
					&& newRate.getStayDateFrom().isBefore(overlappingRate.getStayDateFrom())
					&&newRate.getStayDateTo().isBefore(overlappingRate.getStayDateTo())||
					newRate.getStayDateTo().isEqual(overlappingRate.getStayDateTo())){
				
				newRate.setStayDateTo(overlappingRate.getStayDateTo());
				lappingRates.add(newRate);
				overlappingRate.setClosedDate(LocalDateTime.now());
				closedDate = overlappingRate;
				return lappingRates;
				
			}
			// Case 11:  if newRate start before and end before or on the same date as overlappingRate.
			else if (overlappingRate.getValue() == newRate.getValue()
					&&newRate.getStayDateFrom().isBefore(overlappingRate.getStayDateTo())
					&& newRate.getStayDateFrom().isAfter(overlappingRate.getStayDateFrom())
					&& newRate.getStayDateTo().isAfter(overlappingRate.getStayDateTo())||
					newRate.getStayDateTo().isEqual(overlappingRate.getStayDateTo())){
				
				overlappingRate.setStayDateTo(newRate.getStayDateTo());
				lappingRates.add(overlappingRate);
				return lappingRates;
				
			}
		 
		}

		// No merge happened, add closedDate and newRate to the result
		if (!isMergeRate && isRateSplit) {
			lappingRates.add(closedDate);
			System.out.println(closedDate);
			lappingRates.add(newRate);
		}

		return lappingRates;
	}

	@Override
	public void importRatesFromExcel(MultipartFile file) {
		try {
			Iterable<Rate> rates = ExcelUtils.readRatesFromExcelFile(file);
			List<Rate> exitingRates = new ArrayList<>();
			for (Rate rate : rates) {
				validateRate(rate);
				Long bungalowId = rate.getBungalowId();
				List<Rate> overlappingRates = rateRepository.findAllByBungalowId(bungalowId);
				List<Rate> lappingRates = new ArrayList<Rate>();
				if (!overlappingRates.isEmpty()) {
					lappingRates = splitAndMergeRate(rate, overlappingRates);
					if (lappingRates.size() != 0) {
						this.rateRepository.saveAll(lappingRates);
					} else {
						Boolean isRateExits = false;
						for (Rate overlappingRate : overlappingRates) {
							if (overlappingRate.equals(rate)) {
								exitingRates.add(rate);
								isRateExits = true;
							} else if (overlappingRate.getValue() == rate.getValue()
									&& overlappingRate.getStayDateFrom().isBefore(rate.getStayDateFrom())
									&& overlappingRate.getStayDateTo().isAfter(rate.getStayDateTo())) {
								exitingRates.add(rate);
								isRateExits = true;
							}
						}
						if (!isRateExits) {
							this.rateRepository.save(rate);
						}
					}
				} else {
					this.rateRepository.save(rate);
				}
			}

		} catch (IOException e) {

		}
	}

	@Override
	public byte[] exportRatesToExcel() {
		try (Workbook workbook = ExcelUtils.createWorkbook()) {
			Iterable<Rate> rates = rateRepository.findAll();
			ExcelUtils.writeRatesToSheet(workbook, rates);
			return ExcelUtils.writeWorkbookToBytes(workbook);
		} catch (IOException e) {

			return new byte[0];
		}
	}



}
package com.Hotel.Bookings.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Hotel.Bookings.Entity.Rate;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

	List<Rate> findAllByBungalowId(Long bungalowId);
	
	@Query("SELECT r FROM Rate r " +
	           "WHERE r.bungalowId = :bungalowId " +
	           "AND r.nights = :nights " +
	           "AND r.closedDate IS NULL")
	    List<Rate> findRatesByBungalowIdAndNights(
	            @Param("bungalowId") Long bungalowId,
	            @Param("nights") Integer nights
	    );
	
	@Query("SELECT r FROM Rate r " +
	           "WHERE r.bungalowId = :bungalowId " +
	           "AND :startDate <= r.stayDateTo AND :endDate >= r.stayDateFrom " +
	           "AND r.closedDate IS NULL")
	    List<Rate> findRatesForDateRange(
	            @Param("bungalowId") Long bungalowId,
	            @Param("startDate") LocalDate startDate,
	            @Param("endDate") LocalDate endDate
	    );
	
	@Query("SELECT r FROM Rate r " +
	           "WHERE r.bungalowId = :bungalowId " +
	           "AND :date BETWEEN r.stayDateFrom AND r.stayDateTo " +
	           "AND r.closedDate IS NULL")
	    Optional<Rate> findRateForDay(
	            @Param("bungalowId") Long bungalowId,
	            @Param("date") LocalDate date
	    );

}

package com.Hotel.Bookings.ServiceImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;


import com.Hotel.Bookings.Entity.ExcelUtils;
import com.Hotel.Bookings.Entity.Rate;
import com.Hotel.Bookings.Repository.RateRepository;



@SpringBootTest
public class RateServiceImplTest {

    @Mock
    private RateRepository rateRepository;
    
    @Mock
    private ExcelUtils excelUtils;

    @InjectMocks
    private RateServiceImpl rateService;
    
    @Captor
    private ArgumentCaptor<Iterable<Rate>> iterableArgumentCaptor;
    
   
    @Test
    public void testGetAllRates() {
        // Mock data
        List<Rate> rates = Arrays.asList(new Rate(), new Rate());

        // Mock the behavior of rateRepository.findAll() to return the list of rates
        when(rateRepository.findAll()).thenReturn(rates);

        // Perform the actual test
        List<Rate> result = rateService.getAllRates();

        // Verify that the method was called and returned the expected result
        verify(rateRepository).findAll();
        assertEquals(rates, result);
    }
    

    @Test
    public void testGetRateByBungalowIdAndDate() {
        // Mock data
        Long bungalowId = 1L;
        LocalDate date = LocalDate.now();
        Rate rate = new Rate(); // Create a sample Rate object

        // Mock the behavior of rateRepository.findRateForDay
        when(rateRepository.findRateForDay(bungalowId, date)).thenReturn(Optional.of(rate));

        // Perform the actual test
        Rate result = rateService.getRateByBugalowIdDate(bungalowId, date);

        // Assert the result
        assertEquals(rate, result);
    }
    
    @Test
    public void testGetRateByBungalowIdAndDateRange() {
        // Mock data
        Long bungalowId = 1L;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(7);
        List<Rate> rates = Arrays.asList(new Rate(), new Rate());

        // Mock the behavior of rateRepository.findRatesForDateRange
        when(rateRepository.findRatesForDateRange(bungalowId, fromDate, toDate)).thenReturn(rates);

        // Perform the actual test
        List<Rate> result = rateService.getRateByBugalowIdDateRange(bungalowId, fromDate, toDate);

        // Assert the result
        assertEquals(rates, result);
    }

    @Test
    public void testGetRateById() {
        // Mock data
        Long rateId = 1L;
        Rate rate = new Rate(); // Create a sample Rate object

        // Mock the behavior of rateRepository.findById
        when(rateRepository.findById(rateId)).thenReturn(Optional.of(rate));

        // Perform the actual test
        Optional<Rate> result = rateService.getRateById(rateId);

        // Assert the result
        assertTrue(result.isPresent());
        assertEquals(rate, result.get());
    }
    
    @Test
    public void testGetRateByIdNotFound() {
        Long rateId = 2L; // Assume this ID does not exist

        // Mock the behavior of rateRepository.findById to return an empty Optional
        when(rateRepository.findById(rateId)).thenReturn(Optional.empty());

        // Expect an IllegalArgumentException to be thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rateService.getRateById(rateId);
        });

        // Verify that the exception message is as expected
        assertTrue(exception.getMessage().contains("No rate found in the database for this id"));
    }
    

    @Test
    public void testCreateRate() {
        // Mock data
        Rate newRate = new Rate();

        // Set values for required fields
        newRate.setStayDateFrom(LocalDate.of(2022, 1, 1));
        newRate.setStayDateTo(LocalDate.of(2022, 1, 31));
        newRate.setNights(3);
        newRate.setValue(5000.0);
        newRate.setBungalowId(100L);

        // Mock the behavior of rateRepository.findRatesByBungalowIdAndNights to return an empty list
        when(rateRepository.findRatesByBungalowIdAndNights(anyLong(), anyInt())).thenReturn(new ArrayList<>());

        // Mock the behavior of rateRepository.save
        when(rateRepository.save(newRate)).thenReturn(newRate);

        // Perform the actual test
        Rate result = rateService.createRate(newRate);

        // Verify that the method was called and returned the expected result
        verify(rateRepository).save(newRate);
        assertEquals(newRate, result);
    }
    
    @Test
    public void testCreateRateWithInvalidStayDates() {
        Rate newRate = new Rate();
        // Invalid stay dates (stayDateFrom is after stayDateTo)
        newRate.setStayDateFrom(LocalDate.of(2022, 1, 31));
        newRate.setStayDateTo(LocalDate.of(2022, 1, 1));
        newRate.setNights(3);
        newRate.setValue(5000.0);
        newRate.setBungalowId(100L);

        // Expect an IllegalArgumentException to be thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rateService.createRate(newRate);
        });

        // Verify that the exception message is as expected
        assertTrue(exception.getMessage().contains("stayDateFrom' is after 'stayDateTo'"));
    }

    @Test
    public void testCreateRateWithInvalidNights() {
        Rate newRate = new Rate();
        // Invalid nights (negative value)
        newRate.setStayDateFrom(LocalDate.of(2022, 1, 1));
        newRate.setStayDateTo(LocalDate.of(2022, 1, 31));
        newRate.setNights(-3);
        newRate.setValue(5000.0);
        newRate.setBungalowId(100L);

        // Expect an IllegalArgumentException to be thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rateService.createRate(newRate);
        });

        // Verify that the exception message is as expected
        assertTrue(exception.getMessage().contains("Nights is required and should be a positive number"));
    }

    @Test
    public void testCreateRateWithInvalidValue() {
        Rate newRate = new Rate();
        // Invalid value (negative value)
        newRate.setStayDateFrom(LocalDate.of(2022, 1, 1));
        newRate.setStayDateTo(LocalDate.of(2022, 1, 31));
        newRate.setNights(3);
        newRate.setValue(-5000.0);
        newRate.setBungalowId(100L);

        // Expect an IllegalArgumentException to be thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rateService.createRate(newRate);
        });

        // Verify that the exception message is as expected
        assertTrue(exception.getMessage().contains("Value is required and should be a positive number"));
    }
    
    @Test
    public void testCreateRateWithInvalidBungalowId() {
        Rate newRate = new Rate();
        // Invalid bungalow ID (null)
        newRate.setStayDateFrom(LocalDate.of(2022, 1, 1));
        newRate.setStayDateTo(LocalDate.of(2022, 1, 31));
        newRate.setNights(3);
        newRate.setValue(5000.0);
        newRate.setBungalowId(null);

        // Expect an IllegalArgumentException to be thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rateService.createRate(newRate);
        });

        // Verify that the exception message is as expected
        assertTrue(exception.getMessage().contains("Bungalow ID is required"));
    }


    @Test
    public void testDeleteRateNotFound() {
        Long rateId = 1L; // Assume this ID does not exist

        // Mock the behavior of rateRepository.findById to return an empty Optional
        when(rateRepository.findById(rateId)).thenReturn(Optional.empty());

        // Mock the behavior of rateRepository.existsById to return false
        when(rateRepository.existsById(rateId)).thenReturn(false);

        // Perform the actual test
        assertThrows(IllegalArgumentException.class, () -> {
            rateService.deleteRate(rateId);
        });
    }


    @Test
    public void testUpdateRate() {
        // Mock data
        Long rateId = 1L;
        Rate newRate = new Rate();
        
        // Set values for required fields
        newRate.setStayDateFrom(LocalDate.of(2024, 1, 1));
        newRate.setStayDateTo(LocalDate.of(2024, 1, 31));
        newRate.setNights(3);
        newRate.setValue(5000.0);
        newRate.setBungalowId(100L);

        // Mock the behavior of rateRepository.findRatesByBungalowIdAndNights to return an empty list
        when(rateRepository.findRatesByBungalowIdAndNights(anyLong(), anyInt())).thenReturn(new ArrayList<>());

        // Mock the behavior of rateRepository.save
        when(rateRepository.save(newRate)).thenReturn(newRate);

        // Perform the actual test
        Rate result = rateService.updateRate(rateId, newRate);

        // Verify that the method was called and returned the expected result
        verify(rateRepository).save(newRate);
        assertEquals(newRate, result);
    }

    
    @Test
    public void testDeleteRate() {
        // Mock data
        Long rateId = 1L;
        Rate rate = new Rate();

        // Mock the behavior of rateRepository.findById
        when(rateRepository.findById(rateId)).thenReturn(Optional.of(rate));

        // Mock the behavior of rateRepository.existsById to return true
        when(rateRepository.existsById(rateId)).thenReturn(true);

        // Mock the behavior of rateRepository.delete
        doNothing().when(rateRepository).deleteById(rateId);

        // Perform the actual test
        assertDoesNotThrow(() -> rateService.deleteRate(rateId),
                "Deleting a rate should not throw an exception.");

        // Verify that the method was called
        verify(rateRepository).deleteById(rateId);
    }
}


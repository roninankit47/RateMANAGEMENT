package com.Hotel.Bookings.Controller;

import org.junit.jupiter.api.Test;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.Hotel.Bookings.Entity.Rate;
import com.Hotel.Bookings.Service.RateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


@SpringBootTest
public class RateControllerTest {

    @Mock
    private RateService rateService;

    @InjectMocks
    private RateController rateController;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void testGetAllRates() {
//        // Mock the behavior of rateService.getAllRates() to return a list of rates
//        List<Rate> rates = new ArrayList<>();
//        Mockito.when(rateService.getAllRates()).thenReturn(rates);
//
//        // Perform the actual test
//        List<Rate> result = rateController.getAllRates();
//
//        // Verify that the method was called and returned the expected result
//        Mockito.verify(rateService).getAllRates();
//        Assert.assertEquals(rates, result);
//    }

    @Test
    public void testGetRateById() {
        // Mock data
        Long rateId = 1L;
        Rate mockedRate = new Rate(); // Create a sample Rate object

        // Mock the behavior of rateService.getRateById
        when(rateService.getRateById(rateId)).thenReturn(Optional.of(mockedRate));

        // Perform the actual test
        Optional<Rate> rateOptional = rateController.getRateById(rateId);

        // Assert the result
        Assert.assertTrue(rateOptional.isPresent());
        ResponseEntity<Object> result = ResponseEntity.ok(rateOptional.get());
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assert.assertEquals(mockedRate, result.getBody());
    }


    @Test
    public void testCreateRate() {
        // Mock the behavior of rateService.createRate() to return the created rate
        Rate inputRate = new Rate();
        Rate createdRate = new Rate();
        Mockito.when(rateService.createRate(inputRate)).thenReturn(createdRate);

        // Perform the actual test
        ResponseEntity<Object> result = rateController.createRate(inputRate);

        // Verify that the method was called and returned the expected result
        Mockito.verify(rateService).createRate(inputRate);
        Assert.assertEquals(new ResponseEntity<>(createdRate, HttpStatus.CREATED), result);
    }

    @Test
    public void testUpdateRate() {
        // Mock the behavior of rateService.updateRate() to return the updated rate
        Long rateId = 1L;
        Rate inputRate = new Rate();
        Rate updatedRate = new Rate();
        Mockito.when(rateService.updateRate(rateId, inputRate)).thenReturn(updatedRate);

        // Perform the actual test
        Rate result = rateController.updateRate(rateId, inputRate);

        // Verify that the method was called and returned the expected result
        Mockito.verify(rateService).updateRate(rateId, inputRate);
        Assert.assertEquals(updatedRate, result);
    }

    @Test
    public void testDeleteRate() {
        // Mock the behavior of rateService.deleteRate() to delete the rate
        Long rateId = 1L;

        // Perform the actual test
        ResponseEntity<String> result = rateController.deleteRate(rateId);

        // Verify that the method was called and returned the expected result
        Mockito.verify(rateService).deleteRate(rateId);
        Assert.assertEquals(new ResponseEntity<>("Rate deleted successfully", HttpStatus.OK), result);
    }

    @Test
    public void testImportRates() {
        // Mock the behavior of rateService.importRatesFromExcel() to import rates
        MultipartFile file = Mockito.mock(MultipartFile.class);

        // Perform the actual test
        ResponseEntity<String> result = rateController.importRates(file);

        // Verify that the method was called and returned the expected result
        Mockito.verify(rateService).importRatesFromExcel(file);
        Assert.assertEquals(new ResponseEntity<>("Rates imported successfully", HttpStatus.OK), result);
    }

    @Test
    public void testExportRates() {
        // Mock the behavior of rateService.exportRatesToExcel() to export rates
        byte[] excelBytes = new byte[0];
        Mockito.when(rateService.exportRatesToExcel()).thenReturn(excelBytes);

        // Perform the actual test
        ResponseEntity<byte[]> result = rateController.exportRates();

        // Verify that the method was called and returned the expected result
        Mockito.verify(rateService).exportRatesToExcel();
        Assert.assertEquals(ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=rates_export.xlsx")
                .body(excelBytes), result);
    }

    

}


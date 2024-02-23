//package com.Hotel.Bookings;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.Hotel.Bookings.Controller.RateController;
//import com.Hotel.Bookings.Entity.Rate;
//import com.Hotel.Bookings.Service.RateService;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@WebMvcTest(RateController.class)
//class BunglaowApplicationTests {
//
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private RateService rateService;
//
//    private Rate sampleRate;
//
//    @Before
//    public void setUp() {
//        sampleRate = new Rate();
//        sampleRate.setId(1L);
//        sampleRate.setValue(100);
//        // Set other properties as needed
//    }
//
//    @Test
//    public void getAllRates_ShouldReturnListOfRates() throws Exception {
//        List<Rate> rates = Arrays.asList(sampleRate, new Rate());
//        
//        when(rateService.getAllRates()).thenReturn(rates);
//
//        mockMvc.perform(get("/rates"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[0].value", is(100)))
//                // Add more assertions for other properties
//                .andExpect(jsonPath("$[1].id", notNullValue()));
//        
//        verify(rateService, times(1)).getAllRates();
//        verifyNoMoreInteractions(rateService);
//    }
//
//    @Test
//    public void getRateById_ShouldReturnRateIfExists() throws Exception {
//        when(rateService.getRateById(1L)).thenReturn(Optional.of(sampleRate));
//
//        mockMvc.perform(get("/rates/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.value", is(100)));
//        
//        verify(rateService, times(1)).getRateById(1L);
//        verifyNoMoreInteractions(rateService);
//    }
//
//    @Test
//    public void getRateById_ShouldReturn404IfRateNotFound() throws Exception {
//        when(rateService.getRateById(2L)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/rates/2"))
//                .andExpect(status().isNotFound());
//
//        verify(rateService, times(1)).getRateById(2L);
//        verifyNoMoreInteractions(rateService);
//    }
//    
//    @Test
//    public void getAllRatesForBungalow_ShouldReturnRatesForBungalow() throws Exception {
//        Long bungalowId = 1L;
//        List<Rate> rates = Arrays.asList(sampleRate, new Rate());
//
//        when(rateService.getAllRatesForBungalow(bungalowId)).thenReturn(rates);
//
//        mockMvc.perform(get("/bungalow/{bungalowId}", bungalowId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[0].value", is(100)))
//                // Add more assertions for other properties
//                .andExpect(jsonPath("$[1].id", notNullValue()));
//
//        verify(rateService, times(1)).getAllRatesForBungalow(bungalowId);
//        verifyNoMoreInteractions(rateService);
//    }
//
//    @Test
//    public void createRate_ShouldReturnCreatedRate() throws Exception {
//        when(rateService.createRate(any(Rate.class))).thenReturn(sampleRate);
//
//        mockMvc.perform(post("/rates/save")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"value\": 100}")) // Adjust JSON content based on your Rate class
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.value", is(100)));
//        
//        verify(rateService, times(1)).createRate(any(Rate.class));
//        verifyNoMoreInteractions(rateService);
//    }
//
//    @Test
//    public void updateRate_ShouldReturnUpdatedRate() throws Exception {
//        when(rateService.updateRate(eq(1L), any(Rate.class))).thenReturn(sampleRate);
//
//        mockMvc.perform(put("/rates/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"value\": 100}")) // Adjust JSON content based on your Rate class
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.value", is(100)));
//        
//        verify(rateService, times(1)).updateRate(eq(1L), any(Rate.class));
//        verifyNoMoreInteractions(rateService);
//    }
//
//    @Test
//    public void deleteRate_ShouldReturnSuccessMessage() throws Exception {
//        Long rateId = 1L;
//
//        mockMvc.perform(delete("/rates/{id}", rateId))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Rate deleted successfully"));
//        
//        verify(rateService, times(1)).deleteRate(rateId);
//        verifyNoMoreInteractions(rateService);
//    }
//
//    @Test
//    public void importRates_ShouldReturnSuccessMessage() throws Exception {
//        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.ms-excel", "content".getBytes());
//
//        mockMvc.perform(multipart("/import").file(file))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Rates imported successfully"));
//        
//        verify(rateService, times(1)).importRatesFromExcel(file);
//        verifyNoMoreInteractions(rateService);
//    }
//
//    @Test
//    public void exportRates_ShouldReturnExcelFile() throws Exception {
//        byte[] excelContent = "dummy excel content".getBytes();
//        when(rateService.exportRatesToExcel()).thenReturn(excelContent);
//
//        mockMvc.perform(get("/export"))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Content-Disposition", "attachment; filename=rates_export.xlsx"))
//                .andExpect(content().bytes(excelContent));
//        
//        verify(rateService, times(1)).exportRatesToExcel();
//        verifyNoMoreInteractions(rateService);
//    }
//}
//

package com.demo.upgrade;

import com.demo.upgrade.controller.BookingController;
import com.demo.upgrade.model.Reservation;
import com.demo.upgrade.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookingController.class)
public class ControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Reservation reservation1 = new Reservation("A", "A@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private Reservation reservation2 = new Reservation("B", "B@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    //private Reservation reservation3 = new Reservation("C", "C@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private Reservation reservation4 = new Reservation("C", "C@email.com", LocalDate.now().minusDays(1), LocalDate.now().plusDays(2));

    @Test
    void whenGetStatus_thenReturnsOK() throws Exception {
        when(bookingService.verifyDates(any(), any(), eq(false))).thenReturn(true);
        when(bookingService.getAvaliableDays(any(), any())).thenReturn(Arrays.asList(""));

        mockMvc.perform(get("/api/booking/status")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetStatusWithWrongDates_thenReturnsBadRequest() throws Exception {

        mockMvc.perform(get("/api/booking/status?startDate=2021-01-01&endDate=2021-01-10")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/booking/status?startDate=2021-02-01&endDate=2021-03-10")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenReserve_thenReturnsOK() throws Exception {
        when(bookingService.verifyDates(any(), any(), eq(true))).thenReturn(true);
        when(bookingService.reserve(any())).thenReturn(Long.valueOf(11));
        when(bookingService.isAvaliable(any(), any(), eq(null))).thenReturn(true);
        mockMvc.perform(post("/api/booking/reserve")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservation2)))
                .andExpect(status().isCreated());
    }

    @Test
    void whenReserveWithWrongDates_thenReturnsBR() throws Exception {
        when(bookingService.verifyDates(any(), any(), eq(true))).thenReturn(false);
        mockMvc.perform(post("/api/booking/reserve")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservation4)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenReserveWithoutAvaliablity_thenReturnsBR() throws Exception {
        when(bookingService.isAvaliable(any(), any(), anyLong())).thenReturn(false);
        mockMvc.perform(post("/api/booking/reserve")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservation2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenChange_thenReturnsOK() throws Exception {
        when(bookingService.verifyDates(any(), any(), eq(true))).thenReturn(true);
        when(bookingService.getReservation(anyLong())).thenReturn(reservation1);
        when(bookingService.isAvaliable(any(), any(), anyLong())).thenReturn(true);
        when(bookingService.update(anyLong(), any())).thenReturn(reservation2);
        mockMvc.perform(put("/api/booking/change?id=1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservation2)))
                .andExpect(status().isOk());
    }

    @Test
    void whenChangeNotFOUND_thenReturnsNotFound() throws Exception {
        when(bookingService.getReservation(anyLong())).thenReturn(null);
        mockMvc.perform(put("/api/booking/change?id=1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservation2)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenChangeWithWrongDates_thenReturnsBR() throws Exception {
        when(bookingService.getReservation(anyLong())).thenReturn(reservation1);
        when(bookingService.verifyDates(any(), any(), eq(true))).thenReturn(false);
        mockMvc.perform(put("/api/booking/change?id=1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservation4)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenClear_thenReturnsOK() throws Exception {
        when(bookingService.getReservation(anyLong())).thenReturn(reservation1);
        mockMvc.perform(put("/api/booking/cancel?id=1")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void whenClearNotFound_thenReturnsNotFound() throws Exception {
        when(bookingService.getReservation(anyLong())).thenReturn(null);
        mockMvc.perform(put("/api/booking/cancel?id=1")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

}

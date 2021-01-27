package com.demo.upgrade;

import com.demo.upgrade.model.Reservation;
import com.demo.upgrade.repository.ReservationRepository;
import com.demo.upgrade.service.BookingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceUnitTest {

    @MockBean
    private ReservationRepository reservationRepository;

    @Autowired
    private BookingService bookignService;

    @BeforeEach
    public void setUp() {
        Reservation res1 = new Reservation("A", "A@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Reservation res2 = new Reservation("B", "B@email.com", LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        Reservation res3 = new Reservation("C", "C@email.com", LocalDate.now().plusDays(3), LocalDate.now().plusDays(4));
        Reservation res4 = new Reservation("D", "D@email.com", LocalDate.now().plusDays(4), LocalDate.now().plusDays(5));

        List<Reservation> list = new ArrayList<>();
        list.add(res1);
        list.add(res2);
        list.add(res3);
        list.add(res4);
        when(reservationRepository.findAvaliable(any(), any())).thenReturn(list);
        when(reservationRepository.findById(eq(Long.valueOf(1)))).thenReturn(Optional.of(res1));
        when(reservationRepository.save(any())).thenReturn(res1);
    }

    @Test
    public void whenGetAvaileDays_thenReturnStringList() {
        List<String> result = bookignService.getAvaliableDays(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Assertions.assertEquals(result.get(0), LocalDate.now().plusDays(1).toString());
    }

    @Test
    public void whenIsAvaliable_thenReturnTrue() {
        Boolean result = bookignService.isAvaliable(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), Long.valueOf(1));
        Assertions.assertTrue(result);
    }

    @Test
    public void whenReserver_thenReturnId() {
        Reservation res1 = new Reservation("A", "A@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Long id = bookignService.reserve(res1);
        Assertions.assertNotEquals(id, null);
    }

    @Test
    public void whenGetReservation_thenReturnRes() {
        Reservation res = bookignService.getReservation(Long.valueOf(1));
        Assertions.assertNotEquals(res, null);
    }

    @Test
    public void whenNotFoundReservation_thenReturnNull() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());
        Reservation res = bookignService.getReservation(Long.valueOf(1));
        Assertions.assertEquals(res, null);
    }

    @Test void whenUpdate_thenReturnRes() {
        Reservation res1 = new Reservation("A", "A@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Reservation res = bookignService.update(Long.valueOf(1), res1);
        Assertions.assertNotEquals(res, null);
    }

    @Test void whenUpdateNotFound_thenReturnNull() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());
        Reservation res1 = new Reservation("A", "A@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Reservation res = bookignService.update(Long.valueOf(1), res1);
        Assertions.assertEquals(res, null);
    }

    @Test
    public void whenVerifyDates() {
        LocalDate a = LocalDate.now().plusDays(1);
        LocalDate b = LocalDate.now().plusDays(3);
        LocalDate c = LocalDate.now().plusDays(5);
        LocalDate d = LocalDate.now().plusMonths(2);
        LocalDate e = LocalDate.now();

        Assertions.assertTrue(bookignService.verifyDates(a, b, false));
        Assertions.assertTrue(bookignService.verifyDates(a, c, false));
        Assertions.assertFalse(bookignService.verifyDates(a, c, true));
        Assertions.assertFalse(bookignService.verifyDates(d, b, false));
        Assertions.assertFalse(bookignService.verifyDates(e, b, false));
        Assertions.assertFalse(bookignService.verifyDates(a, d, false));
        Assertions.assertFalse(bookignService.verifyDates(a, e, false));
        Assertions.assertFalse(bookignService.verifyDates(b, a, false));
        Assertions.assertFalse(bookignService.verifyDates(d, b, true));
        Assertions.assertFalse(bookignService.verifyDates(e, b, true));
        Assertions.assertFalse(bookignService.verifyDates(a, d, true));
        Assertions.assertFalse(bookignService.verifyDates(a, e, true));
        Assertions.assertFalse(bookignService.verifyDates(b, a, true));
    }

}

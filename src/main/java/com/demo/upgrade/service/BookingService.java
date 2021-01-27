package com.demo.upgrade.service;

import com.demo.upgrade.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<String> getAvaliableDays(LocalDate startDate, LocalDate endDate);

    Boolean isAvaliable(LocalDate startDate, LocalDate endDate, Long id);

    Long reserve(Reservation reservation);

    Reservation getReservation(Long id);

    Reservation update(Long id, Reservation reservation);

    void cancel(Long id);

    boolean verifyDates(LocalDate start, LocalDate end, boolean isReserve);
}

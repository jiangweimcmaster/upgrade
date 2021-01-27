package com.demo.upgrade.repository;

import com.demo.upgrade.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = "SELECT * FROM reservations WHERE end_date>=?1 and start_date<=?2 and cancelled=0", nativeQuery = true)
    List<Reservation> findAvaliable(LocalDate startDate, LocalDate endDate);
}

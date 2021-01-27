package com.demo.upgrade.controller;

import com.demo.upgrade.model.Reservation;
import com.demo.upgrade.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private BookingService bookingService;

    private Logger logger = LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/booking/status")
    public ResponseEntity<List<String>> getStatus(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        try {
            LocalDate start = startDate == null ? LocalDate.now().plusDays(1) : LocalDate.parse(startDate);
            LocalDate end = endDate == null ? LocalDate.now().plusMonths(1) : LocalDate.parse(endDate);
            if (!bookingService.verifyDates(start, end, false)) {
                return new ResponseEntity<>(Collections.singletonList("Start or end date is not correct"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(bookingService.getAvaliableDays(start, end), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/booking/reserve")
    public ResponseEntity<String> reserve(@RequestBody Reservation reservation) {
        try {
            if (!bookingService.verifyDates(reservation.getStartDate(), reservation.getEndDate(), true)) {
                return new ResponseEntity<>("Start or end date is not correct", HttpStatus.BAD_REQUEST);
            }
            else if (!bookingService.isAvaliable(reservation.getStartDate(), reservation.getEndDate(), null)) {
                return new ResponseEntity<>("Select time range is not avaliable", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(bookingService.reserve(reservation).toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/booking/change")
    public ResponseEntity<String> update (@RequestParam(required = true) long id, @RequestBody Reservation reservation) {
        try{
            if(bookingService.getReservation(id) != null) {
                if (!bookingService.verifyDates(reservation.getStartDate(), reservation.getEndDate(), true)) {
                    return new ResponseEntity<>("Start or end date is not correct", HttpStatus.BAD_REQUEST);
                }
                if (!bookingService.isAvaliable(reservation.getStartDate(), reservation.getEndDate(), id)) {
                    return new ResponseEntity<>("Select time range is not avaliable", HttpStatus.BAD_REQUEST);
                }
                bookingService.update(id, reservation);
                return new ResponseEntity<>("Reservation updated", HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Not found the reservation", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/booking/cancel")
    public ResponseEntity<String> cancel (@RequestParam(required = true) long id) {
        try{
            if(bookingService.getReservation(id) != null) {
                bookingService.cancel(id);
                return new ResponseEntity<>("Reservation is cancelled", HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Not found the reservation", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

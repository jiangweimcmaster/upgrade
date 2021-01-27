package com.demo.upgrade.service;

import com.demo.upgrade.model.Reservation;
import com.demo.upgrade.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    @Value("${upgrade.reservation.size}")
    private int size;

    private ReservationRepository reservationRepository;

    public BookingServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<String> getAvaliableDays(LocalDate startDate, LocalDate endDate) {
        List<String> result = new ArrayList<>();
        List<Reservation> resList = reservationRepository.findAvaliable(startDate, endDate);
        Map<String, Integer> bookedMap = getBookedMap(resList);
        for(LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if(bookedMap.isEmpty() || !bookedMap.containsKey(date.toString()) || bookedMap.get(date.toString()) < size) {
                result.add(date.toString());
            }
        }
        return result;
    }

    public Boolean isAvaliable(LocalDate startDate, LocalDate endDate, Long id) {
        List<Reservation> resList = reservationRepository.findAvaliable(startDate, endDate);
        if (id != null) {
            resList.removeIf(reservation -> reservation.getId() == id);
        }
        Map<String, Integer> bookedMap = getBookedMap(resList);
        for(LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if(bookedMap != null && bookedMap.containsKey(date.toString()) && bookedMap.get(date.toString()) >= size) {
                return false;
            }
        }
        return true;
    }

    public Long reserve(Reservation reservation) {
        Reservation res = reservationRepository.save(reservation);
        return res.getId();
    }

    public Reservation getReservation(Long id) {
        Optional<Reservation> result = reservationRepository.findById(id);
        if(result.isPresent() && !result.get().isCancelled()) {
            return result.get();
        }
        return null;
    }

    public Reservation update(Long id, Reservation reservation) {
        Optional<Reservation> result = reservationRepository.findById(id);
        if(result.isPresent()) {
            Reservation res = result.get();
            res.setName(reservation.getName());
            res.setEmail(reservation.getEmail());
            res.setStartDate(reservation.getStartDate());
            res.setEndDate(reservation.getEndDate());
            return reservationRepository.save(res);
        }
        return null;
    }

    public void cancel(Long id) {
        Optional<Reservation> result = reservationRepository.findById(id);
        if(result.isPresent()) {
            Reservation res = result.get();
            res.setCancelled(true);
            reservationRepository.save(res);
        }
    }

    public boolean verifyDates(LocalDate start, LocalDate end, boolean isReserve) {
        LocalDate now = LocalDate.now();
        if(start.compareTo(now.plusDays(1)) < 0 || start.compareTo(now.plusMonths(1)) > 0)
            return false;
        if(end.compareTo(now.plusDays(1)) < 0 || end.compareTo(now.plusMonths(1)) > 0)
            return false;
        if(start.compareTo(end) > 0)
            return false;
        return !isReserve || start.compareTo(end.minusDays(4)) > 0;
    }

    private Map<String, Integer> getBookedMap(List<Reservation> resList) {
        Map<String, Integer> bookedMap = new HashMap<>();
        for (Reservation res : resList) {
            for (LocalDate i = res.getStartDate(); i.isBefore(res.getEndDate()); i = i.plusDays(1)) {
                if (bookedMap.containsKey(i.toString())) {
                    bookedMap.replace(i.toString(), bookedMap.get(i.toString())+1 );
                }
                else {
                    bookedMap.put(i.toString(), 1);
                }
            }
        }
        return bookedMap;
    }

}

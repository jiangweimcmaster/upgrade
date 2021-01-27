package com.demo.upgrade;

import com.demo.upgrade.model.Reservation;
import com.demo.upgrade.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@DataJpaTest
public class JPAUnitTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    ReservationRepository reservationRepository;



    @Test
    public void should_find_no_reservations_if_repository_is_empty() {
        Iterable<Reservation> reservations = reservationRepository.findAll();

        assertThat(reservations).isEmpty();
    }

    @Test
    public void should_save_a_reservation() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);
        Reservation reservation = reservationRepository.save(new Reservation("Name", "email@email.com", start, end));

        assertThat(reservation).hasFieldOrPropertyWithValue("name", "Name");
        assertThat(reservation).hasFieldOrPropertyWithValue("email", "email@email.com");
        assertThat(reservation.getStartDate().toString()).isEqualTo(start.toString());
        assertThat(reservation.getEndDate().toString()).isEqualTo(end.toString());
        assertThat(reservation).hasFieldOrPropertyWithValue("cancelled", false);
    }

    @Test
    public void should_find_all_reservations() {
        Reservation reservation1 = new Reservation("Reservation#1", "1@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        testEntityManager.persist(reservation1);

        Reservation reservation2 = new Reservation("Reservation#2", "2@email.com", LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        testEntityManager.persist(reservation2);

        Reservation reservation3 = new Reservation("Reservation#3", "3@email.com", LocalDate.now().plusDays(5), LocalDate.now().plusDays(6));
        testEntityManager.persist(reservation3);

        Iterable<Reservation> reservations = reservationRepository.findAvaliable(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        assertThat(reservations).hasSize(2).contains(reservation1, reservation2);
    }

    @Test
    public void should_find_reservation_by_id() {
        Reservation reservation1 = new Reservation("Reservation#1", "1@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        testEntityManager.persist(reservation1);

        Reservation reservation2 = new Reservation("Reservation#2", "2@email.com", LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        testEntityManager.persist(reservation2);

        Reservation foundReservation = reservationRepository.findById(reservation2.getId()).get();

        assertThat(foundReservation).isEqualTo(reservation2);
    }

    @Test
    public void should_update_reservation_by_id() {
        Reservation reservation1 = new Reservation("Reservation#1", "1@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        testEntityManager.persist(reservation1);

        Reservation reservation2 = new Reservation("Reservation#2", "2@email.com", LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        testEntityManager.persist(reservation2);

        Reservation updatedReservation = new Reservation("updated Reservation#2", "updated2@email.com", LocalDate.now().plusDays(3), LocalDate.now().plusDays(4));

        Reservation reservation = reservationRepository.findById(reservation2.getId()).get();
        reservation.setName(updatedReservation.getName());
        reservation.setEmail(updatedReservation.getEmail());
        reservation.setStartDate(updatedReservation.getStartDate());
        reservation.setEndDate(updatedReservation.getEndDate());
        reservationRepository.save(reservation);

        Reservation checkReservation = reservationRepository.findById(reservation2.getId()).get();

        assertThat(checkReservation.getId()).isEqualTo(reservation2.getId());
        assertThat(checkReservation.getName()).isEqualTo(updatedReservation.getName());
        assertThat(checkReservation.getEmail()).isEqualTo(updatedReservation.getEmail());
        assertThat(checkReservation.getStartDate().toString()).isEqualTo(updatedReservation.getStartDate().toString());
        assertThat(checkReservation.getEndDate().toString()).isEqualTo(updatedReservation.getEndDate().toString());
    }

    @Test
    public void should_cancel_reservation_by_id() {
        Reservation reservation1 = new Reservation("Reservation#1", "1@email.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        testEntityManager.persist(reservation1);

        Reservation reservation2 = new Reservation("Reservation#2", "2@email.com", LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        testEntityManager.persist(reservation2);


        Reservation reservation = reservationRepository.findById(reservation2.getId()).get();
        reservation.setCancelled(true);
        reservationRepository.save(reservation);

        Reservation checkReservation = reservationRepository.findById(reservation2.getId()).get();

        assertThat(checkReservation.getId()).isEqualTo(reservation2.getId());
        assertThat(checkReservation.isCancelled()).isEqualTo(true);
    }


}

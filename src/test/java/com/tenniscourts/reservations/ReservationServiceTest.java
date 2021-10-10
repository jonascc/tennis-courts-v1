package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestDTO;
import com.tenniscourts.guests.GuestMapperImpl;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleMapperImpl;
import com.tenniscourts.schedules.ScheduleService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ReservationService.class)
public class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReservationMapperImpl reservationMapper;
    @Mock
    GuestService guestService;
    @Mock
    GuestMapperImpl guestMapper;
    @Mock
    ScheduleService scheduleService;
    @Mock
    ScheduleMapperImpl scheduleMapper;

    static CreateReservationRequestDTO createReservationRequestDTO;
    static GuestDTO guestDTO;
    static ScheduleDTO scheduleDTO;

    @BeforeClass
    public static void init() {
        createReservationRequestDTO = new CreateReservationRequestDTO(1L, 1L);
        guestDTO = new GuestDTO();
        guestDTO.setId(1L);
        guestDTO.setName("Guest");

        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(1L);
    }

    @Test
    @DisplayName("It should return full value refund")
    public void getRefundValueFullRefund() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal(10), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test
    public void getRefundValue75PercentRefund() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(13);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal("7.50"), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test
    public void getRefundValue50PercentRefund() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(3);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal("5.0"), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test
    public void getRefundValue25PercentRefund() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal("2.50"), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test
    public void getRefundValueZeroPercentRefund() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusSeconds(1);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal("0"), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test
    public void bookReservationWithPastDate() {
        scheduleDTO.setStartDateTime(LocalDateTime.now().minusDays(1));
        Mockito.when(guestService.findGuestById(1L)).thenReturn(guestDTO);
        Mockito.when(scheduleService.findSchedule(1L)).thenReturn(scheduleDTO);

        Mockito.when(guestMapper.map(Mockito.any(GuestDTO.class))).thenCallRealMethod();
        Mockito.when(scheduleMapper.map(Mockito.any(ScheduleDTO.class))).thenCallRealMethod();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.bookReservation(createReservationRequestDTO);
        });

        String expectedMessage = "Can schedule only future dates.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void bookReservationWithFutureDate() {
        scheduleDTO.setStartDateTime(LocalDateTime.now().plusDays(2));
        Mockito.when(guestService.findGuestById(1L)).thenReturn(guestDTO);
        Mockito.when(scheduleService.findSchedule(1L)).thenReturn(scheduleDTO);

        Mockito.when(guestMapper.map(Mockito.any(GuestDTO.class))).thenCallRealMethod();
        Mockito.when(scheduleMapper.map(Mockito.any(ScheduleDTO.class))).thenCallRealMethod();
        Mockito.when(reservationMapper.map(Mockito.any(CreateReservationRequestDTO.class))).thenCallRealMethod();
        Mockito.when(reservationMapper.map(Mockito.any(Reservation.class))).thenCallRealMethod();

        Reservation reservation = new Reservation();
        reservation.setValue(new BigDecimal("10"));
        Mockito.when(reservationRepository.saveAndFlush(Mockito.any(Reservation.class))).thenReturn(reservation);

        assertEquals(new BigDecimal("10"), reservationService.bookReservation(createReservationRequestDTO).getValue());
    }

    @Test
    public void reservationNotFound() {
        Optional<Reservation> empty = Optional.empty();

        Mockito.when(reservationRepository.findById(1L)).thenReturn(empty);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reservationService.findReservation(1L);
        });

        String expectedMessage = "Reservation not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void reservationFound() {
        Reservation res = new Reservation();
        res.setValue(new BigDecimal("10"));

        Optional<Reservation> reservation = Optional.of(res);

        Mockito.when(reservationRepository.findById(1L)).thenReturn(reservation);
        Mockito.when(reservationMapper.map(Mockito.any(Reservation.class))).thenCallRealMethod();
        assertEquals(new BigDecimal("10"), reservationService.findReservation(1L).getValue());
    }

    @Test
    public void cancelReservationNotFound() {
        Optional<Reservation> empty = Optional.empty();
        Mockito.when(reservationRepository.findById(1L)).thenReturn(empty);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reservationService.cancelReservation(1L);
        });

        String expectedMessage = "Reservation not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void cancelReservationAlreadyCanceled() {
        Reservation res = new Reservation();
        res.setReservationStatus(ReservationStatus.CANCELLED);

        Optional<Reservation> reservation = Optional.of(res);

        Mockito.when(reservationRepository.findById(1L)).thenReturn(reservation);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(1L);
        });

        String expectedMessage = "Cannot cancel/reschedule because it's not in ready to play status.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void cancelReservationPastDate() {
        Reservation res = new Reservation();
        Schedule schedule = new Schedule();
        schedule.setStartDateTime(LocalDateTime.now().minusDays(1));
        res.setSchedule(schedule);

        Optional<Reservation> reservation = Optional.of(res);

        Mockito.when(reservationRepository.findById(1L)).thenReturn(reservation);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(1L);
        });

        String expectedMessage = "Can cancel/reschedule only future dates.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void cancelReservationSuccessfully() {
        Schedule schedule = new Schedule();
        schedule.setStartDateTime(LocalDateTime.now().plusDays(2));
        Reservation res = new Reservation();
        res.setSchedule(schedule);
        res.setValue(new BigDecimal("10"));

        Optional<Reservation> reservation = Optional.of(res);
        Mockito.when(reservationRepository.findById(1L)).thenReturn(reservation);
        Mockito.when(reservationRepository.save(res)).thenReturn(res);
        Mockito.when(reservationMapper.map(Mockito.any(Reservation.class))).thenCallRealMethod();

        assertEquals(new BigDecimal("10"),  reservationService.cancelReservation(1L).getRefundValue());
    }

    @Test
    public void rescheduleReservationToSameSlot() {
        Schedule schedule = new Schedule();
        schedule.setId(3L);

        Reservation res = new Reservation();
        res.setSchedule(schedule);

        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(3L);
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setSchedule(scheduleDTO);

        Optional<Reservation> reservation = Optional.of(res);

        Mockito.when(reservationRepository.findById(1L)).thenReturn(reservation);
        Mockito.when(reservationMapper.map(Mockito.any(ReservationDTO.class))).thenReturn(res);
        Mockito.when(reservationMapper.map(Mockito.any(Reservation.class))).thenReturn(reservationDTO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.rescheduleReservation(1L, 3L);
        });

        String expectedMessage = "Cannot reschedule to the same slot.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
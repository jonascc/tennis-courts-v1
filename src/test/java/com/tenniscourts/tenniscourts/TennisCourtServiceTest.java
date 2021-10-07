package com.tenniscourts.tenniscourts;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleService;
import com.tenniscourts.tenniscourts.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TennisCourtService.class)
public class TennisCourtServiceTest {

    @InjectMocks
    TennisCourtService tennisCourtService;
    @Mock
    TennisCourtRepository tennisCourtRepository;
    @Mock
    ScheduleService scheduleService;
    @Mock
    TennisCourtMapperImpl tennisCourtMapper;

    @Test
    public void tennisCourtNotFound() {
        Optional<TennisCourt> empty = Optional.empty();

        Mockito.when(tennisCourtRepository.findById(1L)).thenReturn(empty);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tennisCourtService.findTennisCourtById(1L);
        });

        String expectedMessage = "Tennis Court not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void tennisCourtFound() {
        TennisCourt court = new TennisCourt();
        court.setName("Tennis Court 1");

        Optional<TennisCourt> tennisCourt = Optional.of(court);

        Mockito.when(tennisCourtRepository.findById(1L)).thenReturn(tennisCourt);
        Mockito.when(tennisCourtMapper.map(Mockito.any(TennisCourt.class))).thenCallRealMethod();
        assertEquals("Tennis Court 1", tennisCourtService.findTennisCourtById(1L).getName());
    }

    @Test
    public void addTennisCourtSuccessfully() {
        TennisCourtDTO tennisCourt = new TennisCourtDTO();
        tennisCourt.setId(1L);
        tennisCourt.setName("Tennis Court 1");

        TennisCourt court = new TennisCourt();
        court.setName("Tennis Court 1");

        Mockito.when(tennisCourtMapper.map(Mockito.any(TennisCourtDTO.class))).thenCallRealMethod();
        Mockito.when(tennisCourtMapper.map(Mockito.any(TennisCourt.class))).thenCallRealMethod();
        Mockito.when(tennisCourtRepository.saveAndFlush(Mockito.any(TennisCourt.class))).thenReturn(court);

        assertEquals("Tennis Court 1", tennisCourtService.addTennisCourt(tennisCourt).getName());
    }
}

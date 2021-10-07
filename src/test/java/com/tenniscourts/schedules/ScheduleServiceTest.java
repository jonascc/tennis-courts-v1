package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.tenniscourts.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ScheduleService.class)
public class ScheduleServiceTest {

//    @Autowired
//    ApplicationContext context;

    @InjectMocks
    ScheduleService scheduleService;
    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    ScheduleMapperImpl scheduleMapper;
    @Mock
    TennisCourtMapperImpl tennisCourtMapper;
//    @Mock
//    TennisCourtService tennisCourtService;

//    @BeforeEach
//    public void setUp() {
//        // This cannot be a mocked field or else it will reinitialise thing.
//        TennisCourtService tennisCourtService = Mockito.mock(TennisCourtService.class);
//        context.getBean(ScheduleService.class).setTennisCourtService(tennisCourtService);
////        scheduleService.setTennisCourtService(tennisCourtService);
////        MockitoAnnotations.initMocks(this);
//    }

//    @Bean
//    public TennisCourtService getTennisCourtService() {
//        return new TennisCourtService();
//    }

    @Test
    public void addScheduleStartDateTimeNotInformed() {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setTennisCourtId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.addSchedule(1L, createScheduleRequestDTO);
        });

        String expectedMessage = "Start date and time not informed.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void addExistingSchedule() {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setTennisCourtId(1L);
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.now().plusDays(2));

        TennisCourtDTO tennisCourtDTO = new TennisCourtDTO();
        tennisCourtDTO.setId(1L);
        tennisCourtDTO.setName("Tennis Court 1");

        TennisCourtService tennisCourtService = Mockito.mock(TennisCourtService.class);
        scheduleService.setTennisCourtService(tennisCourtService);
        //MockitoAnnotations.initMocks(this);

        Mockito.when(scheduleService.getTennisCourtService().findTennisCourtById(1L)).thenReturn(tennisCourtDTO);
        Mockito.when(tennisCourtMapper.map((TennisCourtDTO) Mockito.any())).thenCallRealMethod();

        TennisCourt tennisCourt = new TennisCourt();
        tennisCourt.setId(1L);
        Schedule schedule = new Schedule();
        schedule.setTennisCourt(tennisCourt);
        List<Schedule> scheduleList = List.of(schedule);

        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setTennisCourt(tennisCourtDTO);
        List<ScheduleDTO> list = List.of(scheduleDTO);

        Mockito.when(scheduleRepository.findByStartDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(scheduleList);
        Mockito.when(scheduleMapper.map(Mockito.anyList())).thenReturn(list);

        AlreadyExistsEntityException exception = assertThrows(AlreadyExistsEntityException.class, () -> {
            scheduleService.addSchedule(1L, createScheduleRequestDTO);
        });

        String expectedMessage = "This schedule already exists.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}

package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import com.tenniscourts.tenniscourts.TennisCourtMapper;
import com.tenniscourts.tenniscourts.TennisCourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final TennisCourtMapper tennisCourtMapper;

    private TennisCourtService tennisCourtService;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        if (createScheduleRequestDTO.getStartDateTime() == null) {
            throw new IllegalArgumentException("Start date and time not informed.");
        }
        TennisCourtDTO tennisCourtDTO = tennisCourtService.findTennisCourtById(tennisCourtId);
        Schedule schedule = new Schedule();
        schedule.setTennisCourt(tennisCourtMapper.map(tennisCourtDTO));
        LocalDateTime start = createScheduleRequestDTO.getStartDateTime();
        LocalDateTime end = calculateEndTime(start);
        boolean scheduleAlreadyExists = checkScheduleSlotExistsForTennisCourt(tennisCourtId, start, end);
        if(scheduleAlreadyExists) {
            throw new AlreadyExistsEntityException("This schedule already exists.");
        }
        schedule.setStartDateTime(start);
        schedule.setEndDateTime(end);
        return scheduleMapper.map(scheduleRepository.saveAndFlush(schedule));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.map(scheduleRepository.findByStartDateTimeBetween(startDate, endDate));
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleMapper.map(scheduleRepository.findById(scheduleId).orElseThrow(() -> {
            throw new EntityNotFoundException("Schedule not found.");
        }));
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    // Setter injection used as workaround to circular dependency
    @Autowired
    public void setTennisCourtService(TennisCourtService tennisCourtService) {
        this.tennisCourtService = tennisCourtService;
    }

    public TennisCourtService getTennisCourtService() {
        return tennisCourtService;
    }

    private LocalDateTime calculateEndTime(LocalDateTime start) {
        return start.plusHours(1L);
    }

    public boolean checkScheduleSlotExistsForTennisCourt(Long tennisCourtId, LocalDateTime start, LocalDateTime end) {
        List<ScheduleDTO> schedules = findSchedulesByDates(start, end.minusSeconds(1L));
        return schedules.stream().anyMatch(s -> s.getTennisCourt().getId().equals(tennisCourtId));
    }

}

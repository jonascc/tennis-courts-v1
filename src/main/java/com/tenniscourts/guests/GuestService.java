package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    private final GuestMapper guestMapper;

    public GuestDTO addGuest(GuestDTO guest) {
        return guestMapper.map(guestRepository.saveAndFlush(guestMapper.map(guest)));
    }

    public GuestDTO findGuestById(Long id) {
        return guestRepository.findById(id).map(guestMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
        });
    }

    public List<GuestDTO> findAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        if (guests.isEmpty()) {
            throw new EntityNotFoundException("No guests found.");
        }
        return guests.stream().map(guest -> guestMapper.map(guest)).collect(Collectors.toList());
    }

    public GuestDTO updateGuestById(GuestDTO guest) {
        findGuestById(guest.getId());
        return guestMapper.map(guestRepository.saveAndFlush(guestMapper.map(guest)));
    }

    public void deleteGuestById(Long id) {
        findGuestById(id);
        guestRepository.deleteById(id);
    }

    public GuestDTO findGuestByName(GuestDTO guestDTO) {
        if (guestDTO.getName() == null) {
            throw new IllegalArgumentException("'name' not informed.");
        }
        return guestRepository.findGuestByName(guestDTO.getName()).map(guestMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
        });
    }

}

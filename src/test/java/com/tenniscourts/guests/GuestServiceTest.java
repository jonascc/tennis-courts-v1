package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import org.junit.BeforeClass;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = GuestService.class)
public class GuestServiceTest {

    @InjectMocks
    GuestService guestService;
    @Mock
    GuestRepository guestRepository;
    @Mock
    GuestMapperImpl guestMapper;

    static List<GuestDTO> guestsDTO;
    static List<Guest> guests;

    @BeforeClass
    public static void init() {
        GuestDTO guest1DTO = new GuestDTO();
        guest1DTO.setId(1L);
        guest1DTO.setName("Guest 1");

        GuestDTO guest2DTO = new GuestDTO();
        guest2DTO.setId(2L);
        guest2DTO.setName("Guest 2");

        guestsDTO = List.of(guest1DTO, guest2DTO);

        Guest guest1 = new Guest();
        guest1.setId(1L);
        guest1.setName("Guest 1");

        Guest guest2 = new Guest();
        guest2.setId(2L);
        guest2.setName("Guest 2");

        guests = List.of(guest1, guest2);
    }

    @Test
    public void addGuestSuccessfully() {
        GuestDTO guestDTO = new GuestDTO();
        guestDTO.setId(1L);
        guestDTO.setName("Guest 1");

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setName("Guest 1");

        Mockito.when(guestMapper.map(Mockito.any(GuestDTO.class))).thenCallRealMethod();
        Mockito.when(guestMapper.map(Mockito.any(Guest.class))).thenCallRealMethod();
        Mockito.when(guestRepository.saveAndFlush(Mockito.any(Guest.class))).thenReturn(guest);

        assertEquals("Guest 1", guestService.addGuest(guestDTO).getName());
    }

    @Test
    public void guestNotFound() {
        Optional<Guest> empty = Optional.empty();

        Mockito.when(guestRepository.findById(1L)).thenReturn(empty);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            guestService.findGuestById(1L);
        });

        String expectedMessage = "Guest not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void noGuestsFound() {
        Mockito.when(guestRepository.findAll()).thenReturn(List.of());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            guestService.findAllGuests();
        });

        String expectedMessage = "No guests found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void guestsFound() {
        Mockito.when(guestRepository.findAll()).thenReturn(guests);

        Mockito.when(guestMapper.map(Mockito.any(Guest.class))).thenCallRealMethod();

        guestService.findAllGuests();

        assertEquals(2, guestService.findAllGuests().size());
    }

    @Test
    public void updateGuestSuccessfully() {
        GuestDTO guestFound = new GuestDTO();
        guestFound.setId(1L);
        guestFound.setName("Guest 1");

        GuestDTO guestDTO = new GuestDTO();
        guestDTO.setId(1L);
        guestDTO.setName("Guest");

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setName("Guest");
        Optional<Guest> optional = Optional.of(guest);

        Mockito.when(guestMapper.map(Mockito.any(GuestDTO.class))).thenCallRealMethod();
        Mockito.when(guestMapper.map(Mockito.any(Guest.class))).thenCallRealMethod();
        Mockito.when(guestRepository.findById(1L)).thenReturn(optional);
        Mockito.when(guestRepository.saveAndFlush(Mockito.any(Guest.class))).thenReturn(guest);

        assertEquals("Guest", guestService.updateGuestById(guestDTO).getName());
    }

    @Test
    public void findGuestByNameWithNameNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            guestService.findGuestByName(new GuestDTO());
        });

        String expectedMessage = "'name' not informed.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void guestNotFoundByName() {
        GuestDTO guestDTO = new GuestDTO();
        guestDTO.setId(1L);
        guestDTO.setName("Guest 1");
        Optional<Guest> empty = Optional.empty();

        Mockito.when(guestRepository.findGuestByName(guestDTO.getName())).thenReturn(empty);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            guestService.findGuestByName(guestDTO);
        });

        String expectedMessage = "Guest not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}

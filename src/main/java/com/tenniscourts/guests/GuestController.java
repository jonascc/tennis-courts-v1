package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import com.tenniscourts.config.swagger.SwaggerConfig;
import com.tenniscourts.exceptions.ErrorDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/guests")
@Api(tags = { SwaggerConfig.GUEST_TAG })
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @ApiOperation(value = "Create guest")
    @ApiResponses(value = {
            @ApiResponse(code=201, message = "Guest created."),
            @ApiResponse(code=400, message = "Bad syntax, please refer to the API description.", response = ErrorDetails.class)} )
    @PostMapping
    public ResponseEntity<Void> addGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.addGuest(guestDTO).getId())).build();
    }

    @ApiOperation(value = "Find guest by id")
    @ApiResponses(value = {
            @ApiResponse(code=404, message = "Guest not found.", response = ErrorDetails.class)} )
    @GetMapping("/{id}")
    public ResponseEntity<GuestDTO> findGuestById(@PathVariable("id") Long guestId) {
        return ResponseEntity.ok(guestService.findGuestById(guestId));
    }

    @ApiOperation(value = "List all guests")
    @ApiResponses(value = {
            @ApiResponse(code=404, message = "No guests found.", response = ErrorDetails.class)} )
    @GetMapping
    public ResponseEntity<List<GuestDTO>> findAllGuests() {
        return ResponseEntity.ok(guestService.findAllGuests());
    }

    @ApiOperation(value = "Update guest")
    @ApiResponses(value = {
            @ApiResponse(code=400, message = "Bad syntax, please refer to the API description.", response = ErrorDetails.class),
            @ApiResponse(code=404, message = "Guest not found.", response = ErrorDetails.class)} )
    @PutMapping("/guest")
    public ResponseEntity<GuestDTO> updateGuestById(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.updateGuestById(guestDTO));
    }

    @ApiOperation(value = "Delete guest by id")
    @ApiResponses(value = {
            @ApiResponse(code=404, message = "Guest not found.", response = ErrorDetails.class)} )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuestById(@PathVariable("id") Long guestId) {
        guestService.deleteGuestById(guestId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Find guest by name")
    @ApiResponses(value = {
            @ApiResponse(code=400, message = "Bad syntax, please refer to the API description.", response = ErrorDetails.class),
            @ApiResponse(code=404, message = "Guest not found.", response = ErrorDetails.class)} )
    @PostMapping("/guest")
    public ResponseEntity<GuestDTO> findGuestByName(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.findGuestByName(guestDTO));
    }

}

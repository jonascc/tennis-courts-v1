package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.BaseRestController;
import com.tenniscourts.config.swagger.SwaggerConfig;
import com.tenniscourts.exceptions.ErrorDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/tennis-courts")
@Api(tags = { SwaggerConfig.TENNIS_COURT_TAG })
public class TennisCourtController extends BaseRestController {

    private final TennisCourtService tennisCourtService;

    @ApiOperation(value = "Create tennis court")
    @ApiResponses(value = {
            @ApiResponse(code=201, message = "Schedule created."),
            @ApiResponse(code=400, message = "Bad syntax, please refer to the API description.", response = ErrorDetails.class)} )
    @PostMapping
    public ResponseEntity<Void> addTennisCourt(@RequestBody TennisCourtDTO tennisCourtDTO) {
        return ResponseEntity.created(locationByEntity(tennisCourtService.addTennisCourt(tennisCourtDTO).getId())).build();
    }

    @ApiOperation(value = "Find tennis court by id")
    @ApiResponses(value = {
            @ApiResponse(code=404, message = "Tennis court not found.", response = ErrorDetails.class)} )
    @GetMapping("/{id}")
    public ResponseEntity<TennisCourtDTO> findTennisCourtById(@PathVariable("id") Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtById(tennisCourtId));
    }

    @ApiOperation(value = "Find tennis court with schedules by id")
    @ApiResponses(value = {
            @ApiResponse(code=404, message = "Tennis court not found.", response = ErrorDetails.class)} )
    @GetMapping("/{id}/schedules")
    public ResponseEntity<TennisCourtDTO> findTennisCourtWithSchedulesById(@PathVariable("id") Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtWithSchedulesById(tennisCourtId));
    }
}

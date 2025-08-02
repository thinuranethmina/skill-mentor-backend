package com.skillmentor.root.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmentor.root.common.Constants;
import com.skillmentor.root.dto.MentorDTO;
import com.skillmentor.root.exception.MentorException;
import com.skillmentor.root.service.FileService;
import com.skillmentor.root.service.MentorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/academic")
@Slf4j
@Tag(name = "Mentor Management", description = "Endpoints for managing mentors and their related data")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    @Autowired
    private FileService fileService;

    public MentorController() {
    }

    @Operation(summary = "Create a new mentor", description = "Creates a mentor along with subject and classroom associations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid mentor data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @PostMapping(value = "/mentor", consumes = Constants.MULTIPART_FORM_DATA, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> createMentor(
            @Parameter(description = "Mentor details to be created", required = true)
            @RequestPart String mentorJson,
            @Parameter(description = "Mentor image file", required = true)
            @RequestPart(required = true) MultipartFile mentor_image) {
        try {
            MentorDTO mentorDTO = getMentorDTO(mentorJson);
            final MentorDTO savedDTO = mentorService.createMentor(mentorDTO, mentor_image);
            return ResponseEntity.ok(savedDTO);
        } catch (MentorException mentorException) {
            return ResponseEntity.badRequest().body(mentorException.getMessage());
        }
    }
//    public ResponseEntity<?> createMentor(
//            @Parameter(description = "Mentor details to be created", required = true)
//            @Valid @RequestBody MentorDTO mentorDTO,
//            @Parameter(description = "Mentor image file", required = true)
//            @RequestPart(required = true) MultipartFile file) {
//        try {
//            final MentorDTO savedDTO = mentorService.createMentor(mentorDTO, file);
//            return ResponseEntity.ok(savedDTO);
//        } catch (MentorException mentorException) {
//            return ResponseEntity.badRequest().body(mentorException.getMessage());
//        }
//    }


    @Operation(summary = "Get all mentors", description = "Retrieves all mentors with optional filtering by name or subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentors retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No mentors found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/mentor", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<List<MentorDTO>> getAllMentors(
            @Parameter(description = "Mentor search text", required = false) @RequestParam(name = "search", required = false) String search
    ) {
        final List<MentorDTO> mentorDTOS = mentorService.getAllMentors(search);
        return ResponseEntity.ok(mentorDTOS);
    }


    // TODO: Remove this method after demonstrations
    @Operation(summary = "Get mentor by ID", description = "Retrieves a single mentor using their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid mentor ID"),
            @ApiResponse(responseCode = "404", description = "Mentor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/mentor/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> findMentorById(
            @Parameter(description = "ID of the mentor to retrieve", required = true)
            @PathVariable @Min(value = 1, message = "Mentor ID must be a positive integer") Integer id) {
        try {
            final MentorDTO mentor = mentorService.findMentorById(id);
            return ResponseEntity.ok(mentor);
        } catch (MentorException mentorException) {
            return ResponseEntity.badRequest().body(mentorException.getMessage());
        }
    }

    @Operation(summary = "Update a mentor", description = "Updates an existing mentor's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid mentor data"),
            @ApiResponse(responseCode = "404", description = "Mentor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @PutMapping(value = "/mentor", consumes = Constants.MULTIPART_FORM_DATA, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> updateMentor(
            @Parameter(description = "Mentor details to update", required = true)
            @RequestPart String mentorJson,
            @Parameter(description = "Mentor image", required = false)
            @RequestPart(required = false) MultipartFile mentorImage) {
        try {
            MentorDTO mentorDTO = getMentorDTO(mentorJson);
            final MentorDTO mentor = mentorService.updateMentorById(mentorDTO, mentorImage);
            return ResponseEntity.ok(mentor);
        } catch (MentorException mentorException) {
            return new ResponseEntity<>(mentorException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // TODO: Remove this method after demonstrations
//    @Operation(summary = "Delete mentor by ID", description = "Deletes a mentor by their unique identifier")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Mentor deleted successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid mentor ID"),
//            @ApiResponse(responseCode = "404", description = "Mentor not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    @DeleteMapping(value = "/mentor/{id}", produces = Constants.APPLICATION_JSON)
//    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
//    public ResponseEntity<?> deleteMentor(
//            @Parameter(description = "ID of the mentor to delete", required = true)
//            @PathVariable @Min(value = 1, message = "Mentor ID must be a positive integer") Integer id) {
//        try {
//            final MentorDTO mentor = mentorService.deleteMentorById(id);
//            return ResponseEntity.ok(mentor);
//        } catch (MentorException mentorException) {
//            return ResponseEntity.badRequest().body(mentorException.getMessage());
//        }
//    }

//    @Operation(summary = "Get mentor by Clerk ID", description = "Retrieves a mentor using their unique Clerk ID")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Mentor retrieved successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid Clerk ID"),
//            @ApiResponse(responseCode = "404", description = "Mentor not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
//    @GetMapping(value = "/mentor/{id}", produces = Constants.APPLICATION_JSON)
//    public ResponseEntity<?> findMentorByClerkId(
//            @Parameter(description = "Clerk ID of the mentor to retrieve", required = true)
//            @PathVariable @NotNull String id) {
//        try {
//            final MentorDTO mentor = mentorService.findMentorByClerkId(id);
//            return ResponseEntity.ok(mentor);
//        } catch (MentorException mentorException) {
//            return ResponseEntity.badRequest().body(mentorException.getMessage());
//        }
//    }

    @Operation(summary = "Delete mentor by Clerk ID", description = "Deletes a mentor using their unique Clerk ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Clerk ID"),
            @ApiResponse(responseCode = "404", description = "Mentor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @DeleteMapping(value = "/mentor/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> deleteMentorByClerkId(
            @Parameter(description = "Clerk ID of the mentor to delete", required = true)
            @PathVariable @NotNull String id) {
        try {
            final MentorDTO mentor = mentorService.deleteMentorByClerkId(id);
            return ResponseEntity.ok(mentor);
        } catch (MentorException mentorException) {
            return ResponseEntity.badRequest().body(mentorException.getMessage());
        }
    }

    private MentorDTO getMentorDTO(String mentorJson) {
        MentorDTO mentorDTO = new MentorDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mentorDTO = objectMapper.readValue(mentorJson, MentorDTO.class);
        } catch(JsonProcessingException e) {
            log.info("Exception in converting string to JSON : {}", e.getMessage());
        }

        return mentorDTO;
    }
}

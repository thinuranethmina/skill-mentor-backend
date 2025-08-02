package com.skillmentor.root.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skillmentor.root.common.Constants;
import com.skillmentor.root.dto.MentorDTO;
import com.skillmentor.root.dto.SessionDTO;
import com.skillmentor.root.dto.SessionLiteDTO;
import com.skillmentor.root.dto.StudentDTO;
import com.skillmentor.root.exception.SessionException;
import com.skillmentor.root.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/academic")
@Slf4j
@Tag(name = "Session Management", description = "Endpoints for creating and retrieving academic sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Operation(
            summary = "Create a new session",
            description = "Creates a new academic session with details about class, mentor, and time"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid session data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
//    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION) // TODO: Change to STUDENT_ROLE_PERMISSION after configurations
    @PostMapping(value = "/session", consumes = Constants.MULTIPART_FORM_DATA, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<SessionLiteDTO> createSession(
            @Parameter(description = "Session details to be create", required = true)
            @RequestPart String sessionJson,
            @Parameter(description = "Payment receipt image", required = true)
            @RequestPart(required = true) @NotNull MultipartFile paymentReceipt) {
        SessionLiteDTO sessionDTO = getSessionLiteDTO(sessionJson);
        final SessionLiteDTO savedDTO = sessionService.createSession(sessionDTO, paymentReceipt);
        return new ResponseEntity<>(savedDTO, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all sessions",
            description = "Retrieves all academic sessions with extended student, mentor, and class data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No sessions found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @GetMapping(value = "/session", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<List<SessionDTO>> getAllSessions(
            @Parameter(description = "Session status list", required = false) @RequestParam(name = "statuses", required = false) List<Constants.SessionStatus> statuses,
            @Parameter(description = "Session class id", required = false) @RequestParam(name = "classId", required = false) Integer classId,
            @Parameter(description = "Session student id", required = false) @RequestParam(name = "studentId", required = false) Integer studentId,
            @Parameter(description = "Session mentor id", required = false) @RequestParam(name = "mentorId", required = false) Integer mentorId,
            @Parameter(description = "Session search text", required = false) @RequestParam(name = "search", required = false) String search
    ) {
        final List<SessionDTO> sessionDTOS = sessionService.getAllSessions(search, statuses, classId, studentId, mentorId);
        return new ResponseEntity<>(sessionDTOS, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all sessions for a student",
            description = "Retrieves all academic sessions for a specific student"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student sessions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No sessions found for the student"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid student data"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @GetMapping(value = "/session/student/{clerkId}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<List<SessionDTO>> getAllStudentSessions(
            @Parameter(description = "Clerk ID of the student", required = true)
            @PathVariable @NotNull String clerkId) {
        final List<SessionDTO> sessionDTOS = sessionService.getAllStudentSessions(clerkId);
        return new ResponseEntity<>(sessionDTOS, HttpStatus.OK);
    }

    @Operation(
            summary = "Update session",
            description = "Updates the details of an existing session"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session updated successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid session data"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @PutMapping(value = "/session", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<SessionDTO> updateSessionStatus(
            @Parameter(description = "Session details to update", required = true)
            @Valid @RequestBody SessionDTO sessionDTO) {
        final SessionDTO updatedSession = sessionService.updateSession(sessionDTO);
        return new ResponseEntity<>(updatedSession, HttpStatus.OK);
    }

    @Operation(
            summary = "Update session status",
            description = "Updates the status of an existing session"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid session data"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @PutMapping(value = "/session/{sessionId}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<SessionDTO> updateSessionStatus(
            @Parameter(description = "ID of the session to update", required = true)
            @PathVariable @NotNull Integer sessionId,
            @Parameter(description = "New status for the session", required = true)
            @RequestParam @NotNull Constants.SessionStatus sessionStatus) {
        final SessionDTO updatedSession = sessionService.updateSessionStatus(sessionId, sessionStatus);
        return new ResponseEntity<>(updatedSession, HttpStatus.OK);
    }

    @Operation(summary = "Delete session by ID", description = "Deletes a session by it's unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid session ID"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @DeleteMapping(value = "/session/{sessionId}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> deleteSession(
            @Parameter(description = "ID of the session to delete", required = true)
            @PathVariable @NotNull Integer sessionId
    ){
        try {
            final SessionDTO session = sessionService.deleteSession(sessionId);
            return ResponseEntity.ok(session);
        } catch (SessionException sessionException) {
            return ResponseEntity.badRequest().body(sessionException.getMessage());
        }
    }

    private SessionLiteDTO getSessionLiteDTO(String sessionJson) {
        SessionLiteDTO sessionLiteDTO = new SessionLiteDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            sessionLiteDTO = objectMapper.readValue(sessionJson, SessionLiteDTO.class);
            System.out.println(sessionLiteDTO);
        } catch(JsonProcessingException e) {
            log.info("Exception in converting string to JSON : {}", e.getMessage());
        }
        return sessionLiteDTO;
    }
}

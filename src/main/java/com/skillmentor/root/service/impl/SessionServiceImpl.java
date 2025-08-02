package com.skillmentor.root.service.impl;

import com.skillmentor.root.common.Constants;
import com.skillmentor.root.dto.*;
import com.skillmentor.root.entity.LiteSessionEntity;
import com.skillmentor.root.entity.SessionEntity;
import com.skillmentor.root.exception.MentorException;
import com.skillmentor.root.mapper.AuditDTOEntityMapper;
import com.skillmentor.root.mapper.LiteSessionEntityDTOMapper;
import com.skillmentor.root.mapper.MentorEntityDTOMapper;
import com.skillmentor.root.mapper.SessionDTOEntityMapper;
import com.skillmentor.root.repository.LiteSessionRepository;
import com.skillmentor.root.repository.SessionRepository;
import com.skillmentor.root.service.ClassRoomService;
import com.skillmentor.root.service.FileService;
import com.skillmentor.root.service.SessionService;
import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private LiteSessionRepository liteSessionRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.images}")
    private String path;
    @Autowired
    private ClassRoomService classRoomService;

    public SessionServiceImpl() {
    }

    @Override
    public SessionLiteDTO createSession(final SessionLiteDTO sessionDTO, MultipartFile file) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("Session data must not be null.");
        }
        LiteSessionEntity sessionEntity = LiteSessionEntityDTOMapper.map(sessionDTO);

        try {
            String uploadedFileName = fileService.uploadFile(path, file, "payment_");
            sessionEntity.setPaymentReciept(uploadedFileName);
        } catch (IOException e) {
            throw new MentorException("Failed to upload image file");
        }

        LiteSessionEntity savedEntity = liteSessionRepository.save(sessionEntity);
        return LiteSessionEntityDTOMapper.map(savedEntity);
    }

    @Override
    public List<SessionDTO> getAllSessions(String search, List<Constants.SessionStatus> status, Integer classId, Integer studentId, Integer mentorId) {
        return sessionRepository.findAll().stream()
                .filter(session -> {
                    boolean statusMatches = status == null || status.isEmpty() || status.contains(session.getSessionStatus());

                    boolean classIdMatches = classId == null || classId<=0 || classId==session.getClassRoomEntity().getClassRoomId();

                    boolean studentIdMatches = studentId == null || studentId<=0 || studentId==session.getStudentEntity().getStudentId();

                    boolean mentortIdMatches = mentorId == null || mentorId<=0 || mentorId==session.getMentorEntity().getMentorId();

                    boolean searchMatches = true;
                    if (search != null && !search.trim().isEmpty()) {
                        String lowerSearch = search.toLowerCase();

                        searchMatches =
                                (session.getTopic() != null &&
                                        session.getTopic().toLowerCase().contains(lowerSearch)) ||

                                        (session.getMentorEntity() != null && (
                                                (session.getMentorEntity().getSessionFee() != null &&
                                                        session.getMentorEntity().getSessionFee().toString().toLowerCase().contains(lowerSearch)) ||

                                                        (((session.getMentorEntity().getFirstName() != null ? session.getMentorEntity().getFirstName() : "") + " " +
                                                                (session.getMentorEntity().getLastName() != null ? session.getMentorEntity().getLastName() : ""))
                                                                .toLowerCase()
                                                                .contains(lowerSearch))
                                        )) ||

                                        (session.getClassRoomEntity() != null &&
                                                session.getClassRoomEntity().getTitle() != null &&
                                                session.getClassRoomEntity().getTitle().toLowerCase().contains(lowerSearch)) ||

                                        (session.getStudentEntity() != null && (
                                                        (session.getStudentEntity().getEmail() != null &&
                                                                session.getStudentEntity().getEmail().toLowerCase().contains(lowerSearch)) ||

                                                        (((session.getStudentEntity().getFirstName() != null ? session.getStudentEntity().getFirstName() : "") + " " +
                                                                (session.getStudentEntity().getLastName() != null ? session.getStudentEntity().getLastName() : ""))
                                                                .toLowerCase()
                                                                .contains(lowerSearch))
                                        ));
                    }

                    return statusMatches && searchMatches && classIdMatches && studentIdMatches && mentortIdMatches;
                })
                .filter(session-> session.getDeletedAt()==null && session.getClassRoomEntity().getDeletedAt()==null)
                .sorted(Comparator.comparing(SessionEntity::getStartTime).reversed())
                .map(SessionDTOEntityMapper::map).collect(Collectors.toList());
    }

    @Override
    public List<AuditDTO> getAllAudits() {
        List<SessionEntity> sessions = sessionRepository.findAll();
        return sessions.stream().map(AuditDTOEntityMapper::map).toList();
    }

    @Override
    public List<PaymentDTO> findMentorPayments(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }
        List<Object> rawResults = sessionRepository.findMentorPayments(startDate, endDate);
        if (rawResults == null || rawResults.isEmpty()) {
            return Collections.emptyList();
        }
        return rawResults.stream().map(obj -> {
            Object[] row = (Object[]) obj;
            Integer mentorId = (Integer) row[0];
            String mentorName = (String) row[1];
            Double totalFee = (Double) row[2];
            return new PaymentDTO(mentorId, mentorName, totalFee);
        }).toList();
    }

    @Override
    public List<SessionDTO> getAllStudentSessions(String studentClerkId) {
        List<SessionEntity> sessions = sessionRepository.findAll();
        return sessions.stream()
                .filter(session -> session.getStudentEntity().getClerkStudentId().equals(studentClerkId))
                .filter(session-> session.getDeletedAt()==null && session.getClassRoomEntity().getDeletedAt()==null)
                .map(SessionDTOEntityMapper::map)
                .toList();
    }

    @Override
    public SessionDTO updateSessionStatus(Integer sessionId, Constants.SessionStatus sessionStatus) {
        Optional<SessionEntity> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) {
            throw new IllegalArgumentException("Session with ID " + sessionId + " not found.");
        }
        SessionEntity sessionEntity = optionalSession.get();
        sessionEntity.setSessionStatus(sessionStatus);
        SessionEntity updatedEntity = sessionRepository.save(sessionEntity);
        return SessionDTOEntityMapper.map(updatedEntity);
    }

    @Override
    public SessionDTO updateSession(SessionDTO sessionDTO) {
        Optional<SessionEntity> optionalSession = sessionRepository.findById(sessionDTO.getSessionId());
        if (optionalSession.isEmpty()) {
            throw new IllegalArgumentException("Session with ID " + sessionDTO.getSessionId() + " not found.");
        }
        SessionEntity sessionEntity = optionalSession.get();
        sessionEntity.setStartTime(sessionDTO.getStartTime());
        sessionEntity.setEndTime(sessionDTO.getEndTime());
        sessionEntity.setSessionStatus(sessionDTO.getSessionStatus());
        SessionEntity updatedEntity = sessionRepository.save(sessionEntity);

        List<SessionEntity> entrolledEntities = sessionRepository.findByClassRoomEntity_ClassRoomIdAndSessionStatusIn(updatedEntity.getClassRoomEntity().getClassRoomId(), List.of(Constants.SessionStatus.COMPLETED, Constants.SessionStatus.ACCEPTED));

        updatedEntity.getClassRoomEntity().setEnrolledStudentCount(entrolledEntities.size());

        updatedEntity = sessionRepository.save(updatedEntity);

        return SessionDTOEntityMapper.map(updatedEntity);
    }

    @Override
    public SessionDTO deleteSession(Integer sessionId) throws SessionException{
        final SessionEntity sessionEntity = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Cannot delete. Session not found with ID: " + sessionId));
        sessionRepository.deleteById(sessionId);
        return SessionDTOEntityMapper.map(sessionEntity);
    }
}

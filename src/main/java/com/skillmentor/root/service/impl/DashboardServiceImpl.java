package com.skillmentor.root.service.impl;

import com.skillmentor.root.common.Constants;
import com.skillmentor.root.entity.SessionEntity;
import com.skillmentor.root.repository.ClassRoomRepository;
import com.skillmentor.root.repository.MentorRepository;
import com.skillmentor.root.repository.SessionRepository;
import com.skillmentor.root.repository.StudentRepository;
import com.skillmentor.root.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    public ClassRoomRepository classRoomRepository;

    @Autowired
    public SessionRepository sessionRepository;

    @Autowired
    public MentorRepository mentorRepository;

    @Autowired
    public StudentRepository studentRepository;

    @Override
    public Map<String, Integer> getDashboard() {
        List<SessionEntity> pendingSessions = sessionRepository.findBySessionStatus(Constants.SessionStatus.PENDING);
        List<SessionEntity> accepted_sessions = sessionRepository.findBySessionStatus(Constants.SessionStatus.ACCEPTED);
        List<SessionEntity> completed_sessions = sessionRepository.findBySessionStatus(Constants.SessionStatus.COMPLETED);

        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("classrooms", classRoomRepository.findAll().size());
        data.put("students", studentRepository.findAll().size());
        data.put("mentors", mentorRepository.findAll().size());
        data.put("pending_sessions", pendingSessions.size());
        data.put("accepted_sessions", accepted_sessions.size());
        data.put("completed_sessions", completed_sessions.size());

        return data;
    }
}

package com.skillmentor.root.entity;

import com.skillmentor.root.common.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Instant;

@Data
@Entity
@Table(name = "session")
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;
    @NotNull(message = "Student must not be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    private StudentEntity studentEntity;
    @NotNull(message = "Classroom must not be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_room_id", referencedColumnName = "class_room_id", nullable = false)
    private ClassRoomEntity classRoomEntity;
    @NotNull(message = "Mentor must not be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id", referencedColumnName = "mentor_id", nullable = false)
    private MentorEntity mentorEntity;
    @NotBlank(message = "Topic must not be blank")
    @Column(name = "topic", nullable = false)
    private String topic;
    @NotNull(message = "Start time must not be null")
    @Column(name = "start_time", nullable = false)
    private Instant startTime;
    @NotNull(message = "End time must not be null")
    @Column(name = "end_time", nullable = false)
    private Instant endTime;
    @NotNull(message = "Session status must not be null")
    @Column(name = "session_status", nullable = false)
    @Enumerated(EnumType.STRING) // Stores enum as a string in the database
    private Constants.SessionStatus sessionStatus = Constants.SessionStatus.PENDING;
}

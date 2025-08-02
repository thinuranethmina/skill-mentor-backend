package com.skillmentor.root.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "classroom")
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE user SET deleted_at = now() WHERE id = ?")
@Schema(description = "Represents a classroom entity in the system")
public class ClassRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_room_id")
    @Schema(description = "Unique ID of the classroom", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer classRoomId;
    @NotNull(message = "Title must not be null")
    @Column(name = "title", nullable = false)
    @Schema(description = "Title of the classroom", example = "Math 101")
    private String title;
    @NotNull(message = "Enrolled student count must not be null")
    @Column(name = "enrolled_student_count", nullable = false)
    @Schema(description = "Number of students enrolled", example = "30")
    private Integer enrolledStudentCount;
    @NotNull(message = "Class image must not be null")
    @Column(name = "class_image", nullable = false)
    @Schema(description = "Image URL for the classroom", example = "http://example.com/classroom.jpg")
    private String classImage;

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "mentor_id", referencedColumnName = "mentor_id")
//    @Schema(description = "Mentor assigned to this classroom")
//    private MentorEntity mentor;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id", referencedColumnName = "mentor_id", nullable = false)
    @Schema(description = "Mentor assigned to this classroom")
    private MentorEntity mentorEntity;

    @OneToMany(mappedBy = "classRoomEntity", fetch = FetchType.EAGER)
    @Schema(description = "List of sessions associated with this classroom")
    private List<SessionEntity> sessionEntityList = new ArrayList<>();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

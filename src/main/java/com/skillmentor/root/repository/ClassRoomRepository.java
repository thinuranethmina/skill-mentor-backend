package com.skillmentor.root.repository;

import com.skillmentor.root.entity.ClassRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoomEntity, Integer> {

    @Query("SELECT c FROM ClassRoomEntity c WHERE c.mentorEntity.mentorId = :mentorId")
    List<ClassRoomEntity> findByMentorId(@Param("mentorId") int mentorId);

    List<ClassRoomEntity> findByMentorEntity_MentorId(int mentorId);

}
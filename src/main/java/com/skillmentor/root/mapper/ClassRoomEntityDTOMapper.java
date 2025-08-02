package com.skillmentor.root.mapper;

import com.skillmentor.root.dto.ClassRoomDTO;
import com.skillmentor.root.entity.ClassRoomEntity;
import com.skillmentor.root.entity.MentorEntity;

public class ClassRoomEntityDTOMapper {
    public static ClassRoomDTO map(ClassRoomEntity classroomEntity) {
        ClassRoomDTO classroomDTO = new ClassRoomDTO();
        classroomDTO.setClassRoomId(classroomEntity.getClassRoomId());
        classroomDTO.setTitle(classroomEntity.getTitle());
        classroomDTO.setEnrolledStudentCount(classroomEntity.getEnrolledStudentCount());
        classroomDTO.setClassImage(classroomEntity.getClassImage());
        return classroomDTO;
    }

    public static ClassRoomEntity map(ClassRoomDTO classroomDTO) {
        ClassRoomEntity classroomEntity = new ClassRoomEntity();
        classroomEntity.setClassRoomId(classroomDTO.getClassRoomId());
        classroomEntity.setTitle(classroomDTO.getTitle());
        classroomEntity.setEnrolledStudentCount(classroomDTO.getEnrolledStudentCount());
        classroomEntity.setClassImage(classroomDTO.getClassImage());
//        if(classroomDTO.getMentorDTO() != null) {
//            MentorEntity mentorEntity = MentorEntityDTOMapper.map(classroomDTO.getMentorDTO());
//            classroomEntity.setMentor(mentorEntity);
//        }
        if(classroomDTO.getMentorDTO() != null) {
            MentorEntity mentor = new MentorEntity();
            mentor.setMentorId(classroomDTO.getMentorDTO().getMentorId());
            classroomEntity.setMentorEntity(mentor);
        }
        return classroomEntity;
    }
}

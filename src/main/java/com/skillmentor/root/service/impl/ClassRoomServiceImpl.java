package com.skillmentor.root.service.impl;

import com.skillmentor.root.dto.ClassRoomDTO;
import com.skillmentor.root.dto.MentorDTO;
import com.skillmentor.root.entity.ClassRoomEntity;
import com.skillmentor.root.exception.ClassRoomException;
import com.skillmentor.root.exception.MentorException;
import com.skillmentor.root.mapper.ClassRoomEntityDTOMapper;
import com.skillmentor.root.mapper.MentorEntityDTOMapper;
import com.skillmentor.root.repository.ClassRoomRepository;
import com.skillmentor.root.service.ClassRoomService;
import com.skillmentor.root.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClassRoomServiceImpl implements ClassRoomService {
    @Autowired
    private ClassRoomRepository classRoomRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.images}")
    private String path;

    @Override
    public List<ClassRoomDTO> getAllClassRooms(final String search, Integer mentorId) {
        final List<ClassRoomEntity> classRoomEntities = classRoomRepository.findAll();
        return classRoomEntities.stream()
                .filter(classroom -> {

                    if (classroom.getDeletedAt() != null) {
                        return false;
                    }

                    boolean mentortIdMatches = mentorId == null || mentorId<=0 || mentorId==classroom.getMentorEntity().getMentorId();

                    boolean searchMatches = true;

                    if (search != null && !search.trim().isEmpty()) {
                        String lowerSearch = search.toLowerCase();
                        searchMatches = (classroom.getTitle() != null && classroom.getTitle().toLowerCase().contains(lowerSearch)) ||
                                (classroom.getEnrolledStudentCount() != null && classroom.getEnrolledStudentCount().toString().toLowerCase().contains(lowerSearch)) ||

                                (classroom.getMentorEntity() != null && (
                                        (classroom.getMentorEntity().getEmail() != null &&
                                                classroom.getMentorEntity().getEmail().toLowerCase().contains(lowerSearch)) ||
                                                (((classroom.getMentorEntity().getFirstName() != null ? classroom.getMentorEntity().getFirstName() : "") + " " +
                                                        (classroom.getMentorEntity().getLastName() != null ? classroom.getMentorEntity().getLastName() : ""))
                                                        .toLowerCase()
                                                        .contains(lowerSearch))
                                ));
                    }
                    return searchMatches && mentortIdMatches;
                })
                .map(
                entity -> {
                    final ClassRoomDTO classRoomDTO = ClassRoomEntityDTOMapper.map(entity);
                    if (entity.getMentorEntity() != null) {
                        final MentorDTO mentorDTO = MentorEntityDTOMapper.map(entity.getMentorEntity());
                        classRoomDTO.setMentorDTO(mentorDTO);
                    }
                    return classRoomDTO;
                }
        ).toList();
    }

    @Override
    public List<ClassRoomDTO> getClassRoomsByMentor(Integer id) {
        final List<ClassRoomEntity> classRoomEntities = classRoomRepository.findByMentorId(id);
        return classRoomEntities.stream().map(ClassRoomEntityDTOMapper::map).toList();
    }

    @Override
    public ClassRoomDTO findClassRoomById(Integer id) {
        final Optional<ClassRoomEntity> classRoomEntity = classRoomRepository.findById(id);
        if (classRoomEntity.isEmpty()) {
            throw new ClassRoomException("ClassRoom not found");
        }

        final ClassRoomEntity entity = classRoomEntity.get();
        final ClassRoomDTO classRoomDTO = ClassRoomEntityDTOMapper.map(entity);
        if (entity.getMentorEntity() != null) {
            final MentorDTO mentorDTO = MentorEntityDTOMapper.map(entity.getMentorEntity());
            classRoomDTO.setMentorDTO(mentorDTO);
        }

        return classRoomDTO;
    }

    @Override
    public ClassRoomDTO deleteClassRoomById(Integer id) {
//        final Optional<ClassRoomEntity> classRoomEntity = classRoomRepository.findById(id);
//        if (classRoomEntity.isEmpty()) {
//            throw new ClassRoomException("ClassRoom not found");
//        }
//        classRoomRepository.deleteById(id);
//        return ClassRoomEntityDTOMapper.map(classRoomEntity.get());
        return this.softDelete(id);
    }

    public ClassRoomDTO softDelete(Integer id) {
        ClassRoomEntity classRoomEntity = classRoomRepository.findById(id)
                .orElseThrow(() -> new ClassRoomException("ClassRoom not found"));
        classRoomEntity.setDeletedAt(LocalDateTime.now());
        classRoomRepository.save(classRoomEntity);
        return ClassRoomEntityDTOMapper.map(classRoomEntity);
    }

    @Override
    public ClassRoomDTO updateClassRoom(ClassRoomDTO classRoomDTO, MultipartFile file) {
        Optional<ClassRoomEntity> classRoomEntity = classRoomRepository.findById(classRoomDTO.getClassRoomId());
        if (classRoomEntity.isEmpty()) {
            throw new ClassRoomException("ClassRoom not found");
        }
        final ClassRoomEntity updatedEntity = classRoomEntity.get();

        if (file != null && !file.isEmpty()) {
            try {
                String uploadedFileName = fileService.uploadFile(path, file, "classroom_");
                fileService.deleteFile(path, classRoomEntity.get().getClassImage());
                updatedEntity.setClassImage(uploadedFileName);
            } catch (IOException e) {
                throw new ClassRoomException("Failed to upload image file");
            }
        }

        updatedEntity.setTitle(classRoomDTO.getTitle());
        updatedEntity.setEnrolledStudentCount(classRoomDTO.getEnrolledStudentCount());
        updatedEntity.setMentorEntity(MentorEntityDTOMapper.map(classRoomDTO.getMentorDTO()));
        final ClassRoomEntity savedEntity = classRoomRepository.save(updatedEntity);
        return ClassRoomEntityDTOMapper.map(savedEntity);
    }

    @Override
    public ClassRoomDTO createClassRoom(ClassRoomDTO classRoomDTO, MultipartFile classroomImage) {
        final ClassRoomEntity classRoomEntity = ClassRoomEntityDTOMapper.map(classRoomDTO);

        try {
            String uploadedFileName = fileService.uploadFile(path, classroomImage, "classroom_");
            classRoomEntity.setClassImage(uploadedFileName);
        } catch (IOException e) {
            throw new ClassRoomException("Failed to upload image file");
        }
        final ClassRoomEntity savedEntity = classRoomRepository.save(classRoomEntity);
        return ClassRoomEntityDTOMapper.map(savedEntity);
    }
}
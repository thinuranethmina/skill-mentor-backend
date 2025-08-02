package com.skillmentor.root.service.impl;

import com.skillmentor.root.dto.ClassRoomDTO;
import com.skillmentor.root.dto.MentorDTO;
import com.skillmentor.root.entity.ClassRoomEntity;
import com.skillmentor.root.entity.MentorEntity;
import com.skillmentor.root.exception.MentorException;
import com.skillmentor.root.mapper.ClassRoomEntityDTOMapper;
import com.skillmentor.root.mapper.MentorEntityDTOMapper;
import com.skillmentor.root.repository.ClassRoomRepository;
import com.skillmentor.root.repository.MentorRepository;
import com.skillmentor.root.service.FileService;
import com.skillmentor.root.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorServiceImpl implements MentorService {

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private ClassRoomRepository classRoomRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.images}")
    private String path;

    public MentorServiceImpl() {
    }

    @Override
    public MentorDTO createMentor(MentorDTO mentorDTO, MultipartFile file) throws MentorException {
        final MentorEntity mentorEntity = MentorEntityDTOMapper.map(mentorDTO);
//        if (mentorDTO.getClassRoomId() != null) {
//        final ClassRoomEntity classRoomEntity = classRoomRepository.findById(mentorDTO.getClassRoomId())
//                    .orElseThrow(() -> new MentorException("Classroom not found with ID: " + mentorDTO.getClassRoomId()));
//        classRoomEntity.setMentor(mentorEntity);

        try {
            String uploadedFileName = fileService.uploadFile(path, file, "mentor_");
            mentorEntity.setMentorImage(uploadedFileName);
        } catch (IOException e) {
            throw new MentorException("Failed to upload image file");
        }

          final MentorEntity savedMentor = mentorRepository.save(mentorEntity);
//          classRoomRepository.save(classRoomEntity);
          return MentorEntityDTOMapper.map(savedMentor);
//          }
//          final MentorEntity savedEntity = mentorRepository.save(mentorEntity);
//          return MentorEntityDTOMapper.map(savedEntity);
    }

    @Override
    public List<MentorDTO> getAllMentors(String search) {
        return mentorRepository.findAll().stream()
                .filter(mentor -> {
                    if (mentor.getDeletedAt() != null) {
                        return false;
                    }
                    boolean searchMatches = true;
                    if (search != null && !search.trim().isEmpty()) {
                        String lowerSearch = search.toLowerCase();
                        String fullName = ((mentor.getFirstName() != null ? mentor.getFirstName() : "") + " " +
                                (mentor.getLastName() != null ? mentor.getLastName() : "")).toLowerCase();

                        searchMatches =(fullName != null && fullName.toLowerCase().contains(lowerSearch)) ||
                                        (mentor.getClerkMentorId() != null && mentor.getClerkMentorId().toLowerCase().contains(lowerSearch)) ||
                                        (mentor.getEmail() != null && mentor.getEmail().toLowerCase().contains(lowerSearch)) ||
                                        (mentor.getSessionFee() != null && mentor.getSessionFee().toString().toLowerCase().contains(lowerSearch)) ||
                                        (mentor.getAddress() != null && mentor.getAddress().toLowerCase().contains(lowerSearch));
                    }

                    return searchMatches;
                })
                .map(mentor -> {
                    MentorDTO dto = MentorEntityDTOMapper.map(mentor);

                    // Fetch classrooms for each mentor and set to DTO
                    List<ClassRoomDTO> classRooms = classRoomRepository.findByMentorId(mentor.getMentorId())
                            .stream()
                            .map(ClassRoomEntityDTOMapper::map)
                            .toList();

                    dto.setClassRooms(classRooms);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public MentorDTO findMentorById(Integer id) throws MentorException {
        MentorDTO mentor = mentorRepository.findById(id)
                .map(MentorEntityDTOMapper::map)
                .orElseThrow(() -> new MentorException("Mentor not found with ID: " + id));

        List<ClassRoomDTO> classroomDTOs = classRoomRepository.findByMentorId(id)
                .stream()
                .map(ClassRoomEntityDTOMapper::map)
                .toList();

        mentor.setClassRooms(classroomDTOs);
        return mentor;

    }

    @Override
    public MentorDTO updateMentorById(MentorDTO mentorDTO, MultipartFile file) throws MentorException {
        final MentorEntity mentorEntity = mentorRepository.findById(mentorDTO.getMentorId())
                .orElseThrow(() -> new MentorException("Cannot update. Mentor not found with ID: " + mentorDTO.getMentorId()));
        mentorEntity.setFirstName(mentorDTO.getFirstName());
        mentorEntity.setLastName(mentorDTO.getLastName());
        mentorEntity.setEmail(mentorDTO.getEmail());
        mentorEntity.setPhoneNumber(mentorDTO.getPhoneNumber());
        mentorEntity.setTitle(mentorDTO.getTitle());
        mentorEntity.setProfession(mentorDTO.getProfession());
        mentorEntity.setSubject(mentorDTO.getSubject());
        mentorEntity.setAddress(mentorDTO.getAddress());
        mentorEntity.setSessionFee(mentorDTO.getSessionFee());
        mentorEntity.setQualification(mentorDTO.getQualification());

        if (file != null && !file.isEmpty()) {
            try {
                String uploadedFileName = fileService.uploadFile(path, file, "mentor_");
                fileService.deleteFile(path, mentorEntity.getMentorImage());
                mentorEntity.setMentorImage(uploadedFileName);
            } catch (IOException e) {
                throw new MentorException("Failed to upload image file");
            }
        }

        final MentorEntity updatedEntity = mentorRepository.save(mentorEntity);
        return MentorEntityDTOMapper.map(updatedEntity);
    }

    @Override
    public MentorDTO deleteMentorById(Integer id) throws MentorException {
        final MentorEntity mentorEntity = mentorRepository.findById(id)
                .orElseThrow(() -> new MentorException("Cannot delete. Mentor not found with ID: " + id));
        mentorRepository.deleteById(id);
        return MentorEntityDTOMapper.map(mentorEntity);
    }

    @Override
    public MentorDTO findMentorByClerkId(String clerkId) throws MentorException {
        return mentorRepository.findByClerkMentorId(clerkId)
                .map(MentorEntityDTOMapper::map)
                .orElseThrow(() -> new MentorException("Mentor not found with Clerk ID: " + clerkId));
    }

    @Override
    public MentorDTO deleteMentorByClerkId(String clerkId) throws MentorException {
        final MentorEntity mentorEntity = mentorRepository.findByClerkMentorId(clerkId)
                .orElseThrow(() -> new MentorException("Cannot delete. Mentor not found with Clerk ID: " + clerkId));
        mentorRepository.delete(mentorEntity);
        return MentorEntityDTOMapper.map(mentorEntity);
    }
}

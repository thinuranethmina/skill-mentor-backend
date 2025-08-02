package com.skillmentor.root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MentorDTO {
    @JsonProperty("mentor_id")
    private Integer mentorId;
    @NotBlank(message = "Clerk mentor ID must not be blank")
    @JsonProperty("clerk_mentor_id")
    private String clerkMentorId;
    @NotBlank(message = "First name must not be blank")
    @Size(max = 255, message = "First name must not exceed 255 characters")
    @JsonProperty("first_name")
    private String firstName;
    @NotBlank(message = "Last name must not be blank")
    @Size(max = 255, message = "Last name must not exceed 255 characters")
    @JsonProperty("last_name")
    private String lastName;
    @NotBlank(message = "Address must not be blank")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @JsonProperty("address")
    private String address;
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email must not be blank")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @JsonProperty("email")
    private String email;
    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @JsonProperty("title")
    private String title;
    @NotNull(message = "Session fee must not be null")
    @Min(value = 0, message = "Session fee must be zero or positive")
    @JsonProperty("session_fee")
    private Double sessionFee;
    @NotBlank(message = "Profession must not be blank")
    @Size(max = 255, message = "Profession must not exceed 255 characters")
    @JsonProperty("profession")
    private String profession;
    @NotBlank(message = "Subject must not be blank")
    @Size(max = 255, message = "Subject must not exceed 255 characters")
    @JsonProperty("subject")
    private String subject;
    @NotBlank(message = "Phone number must not be blank")
    @JsonProperty("phone_number")
    private String phoneNumber;
    @NotBlank(message = "Qualification must not be blank")
    @JsonProperty("qualification")
    @Size(max = 255, message = "Qualification must not exceed 255 characters")
    private String qualification;
    @NotNull(message = "mentor_image must not be null")
    @JsonProperty("mentor_image")
    private String mentorImage;
    @NotBlank(message = "Bio must not be blank")
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    @JsonProperty("bio")
    private String bio;
    @JsonProperty("classrooms")
    private List<ClassRoomDTO> classRooms;
}
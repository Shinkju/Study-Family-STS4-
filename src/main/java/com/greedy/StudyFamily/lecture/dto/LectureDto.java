package com.greedy.StudyFamily.lecture.dto;

import org.springframework.web.multipart.MultipartFile;

import com.greedy.StudyFamily.professor.dto.ProfessorDto;
import com.greedy.StudyFamily.subject.dto.SubjectDto;

import lombok.Data;

@Data
public class LectureDto {

	private Long lectureCode;
	private SubjectDto subject;
	private Long capacity;
	private ProfessorDto professor;
	private String lectureName;
	private Long lecturePersonnel;
	private String openingDate;
	
	//파일 업로드 로직 작성 시 활용할 필드
//	private MultipartFile lectureFile;
	
}

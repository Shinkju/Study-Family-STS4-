package com.greedy.StudyFamily.lecture.dto;

import java.sql.Date;

import com.greedy.StudyFamily.student.dto.StudentDto;

import lombok.Data;

@Data
public class QaBoardDto {

	private Long qaCode;
	private String qaTitle;
	private String qaContent;
	private Date qaRegisdate;
	private Date qaModidate;
	private Date qaDeledate;
	private String qaStatus;
	private LectureDto lectureCode;
	private StudentDto studentCode;
	
}

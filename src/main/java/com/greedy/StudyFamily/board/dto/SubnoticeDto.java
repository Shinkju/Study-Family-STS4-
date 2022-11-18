package com.greedy.StudyFamily.board.dto;

import java.sql.Date;

import com.greedy.StudyFamily.lecture.dto.LectureDto;

import lombok.Data;

@Data
public class SubnoticeDto {

	private Long subnoticeCode;
	private String subnoticeTitle;
	private String content;
	private Date registrationDate;
	private Date modifiedDate;
	private Date deleteDate;
	private LectureDto lecture;
	
}

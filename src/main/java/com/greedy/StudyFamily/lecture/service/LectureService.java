package com.greedy.StudyFamily.lecture.service;

import java.io.IOException;
import java.util.UUID;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.greedy.StudyFamily.admin.dto.FileDto;
import com.greedy.StudyFamily.admin.entity.File;
import com.greedy.StudyFamily.admin.repository.FileRepository;
import com.greedy.StudyFamily.lecture.dto.LectureDto;
import com.greedy.StudyFamily.lecture.entity.Lecture;
import com.greedy.StudyFamily.lecture.entity.LectureWeek;
import com.greedy.StudyFamily.lecture.repository.LectureRepository;
import com.greedy.StudyFamily.professor.dto.ProfessorDto;
import com.greedy.StudyFamily.professor.entity.Professor;
import com.greedy.StudyFamily.professor.repository.ProfessorRepository;
import com.greedy.StudyFamily.student.dto.StudentDto;
import com.greedy.StudyFamily.student.entity.Student;
import com.greedy.StudyFamily.student.repository.StudentRepository;
import com.greedy.StudyFamily.util.FileUploadUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LectureService {

	private final FileRepository fileRepository;
	private final ProfessorRepository professorRepository;
	private final LectureRepository lectureRepository;
	private final StudentRepository studentRepository;
	private final ModelMapper modelMapper;
	
	
	@Value("${file.file-dir}")
	private String FILE_DIR;
	@Value("${file.file-url}")
	private String FILE_URL;
	
	
	public LectureService(FileRepository fileRepository, ProfessorRepository professorRepository, 
			LectureRepository lectureRepository, StudentRepository studentRepository, ModelMapper modelMapper) {
		this.professorRepository = professorRepository;
		this.lectureRepository = lectureRepository;
		this.studentRepository = studentRepository;
		this.fileRepository = fileRepository;
		this.modelMapper = modelMapper;
	}

	
	
	//강좌 목록 조회 - 학생
	public Page<LectureDto> selectLectureStuList(int page, StudentDto student) {
		
		log.info("[LectureService] selectLectureStuList Start =====================" );
		
		Pageable pageable = PageRequest.of(page -1, 10, Sort.by("lectureCode").descending());
		
		/* 학생 엔티티 조회 */
		Student findStudent = studentRepository.findById(student.getStudentNo())
				.orElseThrow(() -> new IllegalArgumentException("해당 학생이 없습니다. studentNo= " + student.getStudentNo()));
		
		Page<Lecture> lectureStuList = lectureRepository.findByStudent(pageable, findStudent);
		Page<LectureDto> lectureDtoStuList = lectureStuList.map(lecture -> modelMapper.map(lecture, LectureDto.class));
		
		log.info("[ProductService] lectureDtoStuList : {}", lectureDtoStuList.getContent());
		log.info("[LectureService] selectLectureStuList End =====================" );
		
		return lectureDtoStuList;
	}
	
	

	//강좌 목록 조회 - 교수
	public Page<LectureDto> selectLectureProList(int page, ProfessorDto professor) {
		
		log.info("[LectureService] selectLectureProList Start =====================" );
		
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("lectureCode").descending());
		
		/* 교수 엔티티 조회 */
		Professor findProfessor = professorRepository.findById(professor.getProfessorCode())
				.orElseThrow(() -> new IllegalArgumentException("해당 교수가 없습니다. professorCode = " + professor.getProfessorCode()));
		

		Page<Lecture> lectureProList = lectureRepository.findByProfessor(pageable, findProfessor);
		Page<LectureDto> lectureDtoProList = lectureProList.map(lecture -> modelMapper.map(lecture, LectureDto.class));
		
		log.info("[ProductService] lectureDtoProList : {}", lectureDtoProList.getContent());
		log.info("[LectureService] selectLectureProList End =====================" );
		
		return lectureDtoProList;
	}



	//강좌 상세 조회 - 학생
	public LectureDto selectLectureDetailStu(Long lectureCode, Long studentNo) {
		
		log.info("[LectureService] selectLectureDetailStu Start =====================" );
		log.info("[LectureService] lectureCode : {}", lectureCode );
		log.info("[LectureService] studentNo : {}", studentNo );
		
		LectureDto lectureDto = modelMapper.map(lectureRepository.findByLectureCode(lectureCode), LectureDto.class);
		
		log.info("[LectureService] lectureCode : {}", lectureCode );
		log.info("[LectureService] selectLectureDetailStu End =====================" );
		
		return lectureDto;
	}

	

	//강좌 상세 조회 - 교수
	public LectureDto selectLectureDetailPro(Long lectureCode, Long professorCode) {
		
		log.info("[LectureService] selectLectureDetailPro Start =====================" );
		log.info("[LectureService] lectureCode : {}", lectureCode );
		log.info("[LectureService] professorCode : {}", professorCode );
		
		LectureDto lectureDto = modelMapper.map(lectureRepository.findByLectureCode(lectureCode), LectureDto.class);
		
		log.info("[LectureService] lectureCode : {}", lectureCode );
		log.info("[LectureService] selectLectureDetailPro End =====================" );
		
		
		return lectureDto;
	}


	//수업 자료 등록 - 교수
	@Transactional
	public FileDto insertLectureFile(FileDto fileDto) {
		
		log.info("[LectureService] insertLectureFile Start =====================" );
		log.info("[LectureService] fileDto : {}", fileDto );
		
		String fileName = UUID.randomUUID().toString().replace("-", "");
		String replaceFileName = null;
		
		try {
			replaceFileName = FileUploadUtils.saveFile(FILE_DIR, fileName, fileDto.getLectureFiles());
			fileDto.setSavedRoute(replaceFileName);
		
			log.info("[ProductService] replaceFileName : {}", replaceFileName);
			
			//DB 저장
			fileRepository.save(modelMapper.map(fileDto, File.class));
		
		} catch (IOException e) {
			e.printStackTrace();
			try {					
				FileUploadUtils.deleteFile(FILE_DIR, replaceFileName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
		log.info("[LectureService] insertLectureFile End =====================" );
		
		return fileDto;
	}


	

	//수업 자료 수정 - 교수
	@Transactional
	public FileDto updateLectureFile(FileDto fileDto) {
		
		log.info("[LectureService] updateLectureFile Start =====================" );
		log.info("[LectureService] fileDto : {}", fileDto );
		
		String replaceFileName = null;
		
		try {
			
			File oriFiles = fileRepository.findById(fileDto.getFileCode())
					.orElseThrow(() -> new IllegalArgumentException("해당 자료가 존재하지 않습니다. fileCode=" + fileDto.getFileCode()));
			String oriFile = oriFiles.getSavedRoute();
			
			log.info("[LectureService] oriFileCode : {}", oriFiles);
			
			//이미지 변경 
			if(fileDto.getLectureFiles() != null) {
				
				String fileName = UUID.randomUUID().toString().replace("_", "");
				
				replaceFileName = FileUploadUtils.saveFile(FILE_DIR, fileName, fileDto.getLectureFiles());
				fileDto.setSavedRoute(replaceFileName);
				
				//기존 파일 삭제
				FileUploadUtils.deleteFile(FILE_DIR, oriFile);
			
				//미 변경 시
			} else {
				fileDto.setSavedRoute(oriFile);
			}
			
			//파일 외 내용 수정
			oriFiles.lectureUpdate(
					fileDto.getOriginName(),
					fileDto.getSavedRoute(),
					modelMapper.map(fileDto.getLectureWeek(), LectureWeek.class),
					fileDto.getStartDate(),
					fileDto.getEndDate(),
					fileDto.getFileType()
			);
			
			fileRepository.save(oriFiles);
			
			
		} catch (IOException e) {
			e.printStackTrace();
			 try {
				 FileUploadUtils.deleteFile(FILE_DIR, replaceFileName);
			 } catch (IOException e1) {
				 e1.printStackTrace();
			
			}
		}
		
		log.info("[LectureService] updateLectureFile End ===========================");
		
		return fileDto;
	}

	
	



	
	

	
	
}

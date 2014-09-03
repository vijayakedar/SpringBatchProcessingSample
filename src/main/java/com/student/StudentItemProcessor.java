package com.student;


import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;

/**
 * Intermediate processor for Student
 * @author Vijaya
 *
 */

public class StudentItemProcessor implements ItemProcessor<Student, Student>{

	private static final Logger log = Logger.getLogger(StudentItemProcessor.class);
	
	@Override
	public Student process(final Student student) throws Exception {
		
		int score = student.getScore();
		
		if(score>=0)
		{
			String grade = "F";
			
			if(score >= 90)
			{
				grade = "A";
			}
			else if(score>=80 && score<=89)
			{
				grade = "B";
			}
			else if(score>=70 && score<=79)
			{
				grade = "C";
			}
			else if(score>=60 && score<=69)
			{
				grade = "D";
			}
			
			student.setGrade(grade);
		}
		else
		{
			log.error("Can not process grade for studentId:" + student.getStudentId() + ". Score can not be negative");
		}
		
		return student;
	}

}

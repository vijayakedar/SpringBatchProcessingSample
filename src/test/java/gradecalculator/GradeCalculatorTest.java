package gradecalculator;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.student.Application;
import com.student.Student;

public class GradeCalculatorTest {

	private List<Student> studentList = new ArrayList<Student>();
	
	@Before
	public void setUp() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/scores.txt")));
		String line = "";
		
		while((line = reader.readLine()) !=null)
		{
			StringTokenizer st = new StringTokenizer(line, ",");
			Student student = new Student();
			student.setCourseId(st.nextToken());
			student.setStudentId(Long.valueOf(st.nextToken().trim()));
			student.setScore(Integer.valueOf(st.nextToken().trim()));
			studentList.add(student);
		}
		reader.close();
	}

	@Test
	public void testGradeCalculator() {
		ApplicationContext ctx = SpringApplication.run(Application.class, new String[]{});

		List<Student> results = ctx.getBean(JdbcTemplate.class).query("SELECT student_id, course_id, score, grade FROM student", new RowMapper<Student>() {
			@Override
			public Student mapRow(ResultSet rs, int row) throws SQLException {
				return new Student(rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getString(4));
			}
		});

		assertEquals(studentList.size(), results.size());
		
		for (Student student : results) {
			assertEquals(true, studentList.contains(student));
		}
	}
}

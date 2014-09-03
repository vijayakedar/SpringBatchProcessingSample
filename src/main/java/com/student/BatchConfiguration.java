package com.student;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Bean
	public ItemReader<Student> reader() {

		FlatFileItemReader<Student> reader = new FlatFileItemReader<Student>();

		reader.setResource(new ClassPathResource("scores.txt"));

		reader.setLineMapper(new DefaultLineMapper<Student>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] { "courseId", "studentId", "score" });
			}});

			setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {{
				setTargetType(Student.class);
			}});
		}});
		return reader;
	}

	@Bean
	public ItemProcessor<Student, Student> processor() {
		return new StudentItemProcessor();
	}
	
	@Bean
    public ItemWriter<Student> writer(DataSource dataSource) {
        JdbcBatchItemWriter<Student> writer = new JdbcBatchItemWriter<Student>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Student>());
        writer.setSql("INSERT INTO student (student_id, course_id, score, grade) VALUES (:studentId, :courseId, :score, :grade)");
        writer.setDataSource(dataSource);
        return writer;
    }
	
	@Bean
    public Job importUserJob(JobBuilderFactory jobs, Step s1) {
        return jobs.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Student> reader,
            ItemWriter<Student> writer, ItemProcessor<Student, Student> processor) {
        return stepBuilderFactory.get("step1")
                .<Student, Student> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

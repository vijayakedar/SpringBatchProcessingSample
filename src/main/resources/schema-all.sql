DROP TABLE student IF EXISTS;

CREATE TABLE student  (
    student_id BIGINT not null,
    course_id VARCHAR(10) not null,
    score integer,     
    grade VARCHAR(2),
    primary key (student_id,course_id)
);
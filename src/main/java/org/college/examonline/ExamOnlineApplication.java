package org.college.examonline;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("org.college.examonline.mapper")
@EnableScheduling
public class ExamOnlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamOnlineApplication.class, args);
    }
}


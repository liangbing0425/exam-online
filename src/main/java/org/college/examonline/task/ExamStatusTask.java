package org.college.examonline.task;

import org.college.examonline.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 考试状态定时任务
 */
@Component
public class ExamStatusTask {
    
    @Autowired
    private ExamService examService;
    
    /**
     * 每10秒执行一次，更新考试状态
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void updateExamStatus() {
        try {
            examService.updateExamStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package org.college.examonline.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Exam;
import java.util.List;
import java.util.Map;

public interface ExamService extends IService<Exam> {
    
    Page<Exam> getExamPage(Integer pageNum, Integer pageSize, String status);
    
    Result getExamDetail(Long id);
    
    Result addExam(Exam exam);
    
    Result updateExam(Exam exam);
    
    Result deleteExam(Long id);
    
    Result enterExam(Long examId, Long studentId);
    
    Result submitExam(Long examId, Long studentId, List<Map<String, Object>> answers);
    
    /**
     * 更新所有考试的状态（根据当前时间与考试开始/结束时间比较）
     */
    void updateExamStatus();
}

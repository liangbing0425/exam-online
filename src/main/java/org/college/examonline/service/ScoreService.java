package org.college.examonline.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Score;
import java.util.Map;

public interface ScoreService extends IService<Score> {
    
    Page<Score> getScorePage(Integer pageNum, Integer pageSize, Long examId, String studentNo);
    
    Result getMyScores(Long studentId);
    
    Result getExamStatistics(Long examId);
    
    Result gradePaper(Map<String, Object> gradeInfo);
}

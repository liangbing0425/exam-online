package org.college.examonline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.college.examonline.entity.Exam;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
    
    /**
     * 获取试卷中的所有题目（按排序）
     * @param paperId 试卷ID
     * @return 题目列表（包含question_id, question_score, sort_order）
     */
    @Select("SELECT question_id, question_score, sort_order FROM paper_question WHERE paper_id = #{paperId} ORDER BY sort_order ASC")
    List<Map<String, Object>> getPaperQuestions(Long paperId);
}

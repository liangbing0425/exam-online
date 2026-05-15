package org.college.examonline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.college.examonline.entity.Paper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaperMapper extends BaseMapper<Paper> {
    
    /**
     * 插入试卷题目关联
     */
    @Insert("INSERT INTO paper_question (paper_id, question_id, question_type, question_content, question_score, sort_order) " +
            "VALUES (#{paperId}, #{questionId}, #{questionType}, #{questionContent}, #{questionScore}, #{sortOrder})")
    int insertPaperQuestion(@Param("paperId") Long paperId, 
                           @Param("questionId") Long questionId,
                           @Param("questionType") String questionType,
                           @Param("questionContent") String questionContent,
                           @Param("questionScore") java.math.BigDecimal questionScore,
                           @Param("sortOrder") Integer sortOrder);
    
    /**
     * 获取试卷的题目列表
     */
    @Select("SELECT pq.question_id, pq.question_type, pq.question_content, pq.question_score, pq.sort_order, " +
            "q.type, q.content, q.options, q.answer, q.difficulty, q.subject, q.chapter, q.score as original_score " +
            "FROM paper_question pq " +
            "LEFT JOIN question q ON pq.question_id = q.id " +
            "WHERE pq.paper_id = #{paperId} " +
            "ORDER BY pq.sort_order ASC")
    List<Map<String, Object>> getPaperQuestions(Long paperId);
}

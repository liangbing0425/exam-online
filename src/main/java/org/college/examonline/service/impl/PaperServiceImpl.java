package org.college.examonline.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Paper;
import org.college.examonline.entity.Question;
import org.college.examonline.mapper.PaperMapper;
import org.college.examonline.service.PaperService;
import org.college.examonline.service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {
    
    @Autowired
    private QuestionService questionService;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Page<Paper> getPaperPage(Integer pageNum, Integer pageSize, String subject, String status) {
        Page<Paper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(subject)) {
            wrapper.eq(Paper::getSubject, subject);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Paper::getStatus, status);
        }
        
        wrapper.orderByDesc(Paper::getCreateTime);
        return this.page(page, wrapper);
    }
    
    @Override
    public Result getPaperDetail(Long id) {
        Paper paper = this.getById(id);
        return paper != null ? Result.success(paper) : Result.error("试卷不存在");
    }
    
    @Override
    @Transactional
    public Result addPaper(Paper paper) {
        // 设置默认值
        if (paper.getStatus() == null) {
            paper.setStatus("draft");
        }
        if (paper.getPassScore() == null) {
            paper.setPassScore(new BigDecimal("60"));
        }
        if (paper.getQuestionCount() == null) {
            paper.setQuestionCount(0);
        }
        if (paper.getTotalScore() == null) {
            paper.setTotalScore(new BigDecimal("100"));
        }
        
        return this.save(paper) ? Result.success() : Result.error("添加失败");
    }
    
    @Override
    public Result updatePaper(Paper paper) {
        return this.updateById(paper) ? Result.success() : Result.error("更新失败");
    }
    
    @Override
    @Transactional
    public Result deletePaper(Long id) {
        return this.removeById(id) ? Result.success() : Result.error("删除失败");
    }
    
    @Override
    @Transactional
    public Result autoGeneratePaper(Map<String, Object> config) {
        try {
            // 1. 获取配置参数
            String name = (String) config.get("name");
            String subject = (String) config.get("subject");
            Integer duration = (Integer) config.get("duration");
            Long creatorId = config.get("creatorId") != null ? ((Number) config.get("creatorId")).longValue() : null;
            String creatorName = (String) config.get("creatorName");
            
            // 难度分布
            Integer easyCount = config.get("easyCount") != null ? ((Number) config.get("easyCount")).intValue() : 0;
            Integer mediumCount = config.get("mediumCount") != null ? ((Number) config.get("mediumCount")).intValue() : 0;
            Integer hardCount = config.get("hardCount") != null ? ((Number) config.get("hardCount")).intValue() : 0;
            
            // 题型分布
            Integer singleCount = config.get("singleCount") != null ? ((Number) config.get("singleCount")).intValue() : 0;
            Integer multipleCount = config.get("multipleCount") != null ? ((Number) config.get("multipleCount")).intValue() : 0;
            Integer judgeCount = config.get("judgeCount") != null ? ((Number) config.get("judgeCount")).intValue() : 0;
            Integer essayCount = config.get("essayCount") != null ? ((Number) config.get("essayCount")).intValue() : 0;
            
            // 2. 验证参数
            if (StrUtil.isBlank(name) || StrUtil.isBlank(subject)) {
                return Result.error("试卷名称和科目不能为空");
            }
            if (creatorId == null || StrUtil.isBlank(creatorName)) {
                return Result.error("创建者信息不能为空");
            }
            
            // 3. 根据配置从题库中随机抽题
            List<Question> selectedQuestions = new ArrayList<>();
            
            // 按题型和难度抽题
            Map<String, Integer> typeMap = new HashMap<>();
            typeMap.put("single", singleCount);
            typeMap.put("multiple", multipleCount);
            typeMap.put("judge", judgeCount);
            typeMap.put("essay", essayCount);
            
            // 计算总题目数
            int totalQuestionCount = (easyCount != null ? easyCount : 0) + 
                                    (mediumCount != null ? mediumCount : 0) + 
                                    (hardCount != null ? hardCount : 0);
            
            // 遍历每种题型进行抽题
            for (Map.Entry<String, Integer> typeEntry : typeMap.entrySet()) {
                String type = typeEntry.getKey();
                int typeTargetCount = typeEntry.getValue();
                
                if (typeTargetCount <= 0) continue;
                
                // 将该题型的数量按难度平均分配
                int difficultyCount = 0;
                if (easyCount != null && easyCount > 0) difficultyCount++;
                if (mediumCount != null && mediumCount > 0) difficultyCount++;
                if (hardCount != null && hardCount > 0) difficultyCount++;
                
                if (difficultyCount == 0) {
                    // 如果没有设置难度分布，全部按中等难度抽取
                    selectedQuestions.addAll(selectQuestionsByCriteria(subject, "medium", type, typeTargetCount));
                } else {
                    // 按难度平均分配
                    int perDifficultyCount = typeTargetCount / difficultyCount;
                    int remaining = typeTargetCount % difficultyCount;
                    
                    // 简单难度
                    if (easyCount != null && easyCount > 0) {
                        int count = perDifficultyCount + (remaining > 0 ? 1 : 0);
                        selectedQuestions.addAll(selectQuestionsByCriteria(subject, "easy", type, count));
                        if (remaining > 0) remaining--;
                    }
                    
                    // 中等难度
                    if (mediumCount != null && mediumCount > 0) {
                        int count = perDifficultyCount + (remaining > 0 ? 1 : 0);
                        selectedQuestions.addAll(selectQuestionsByCriteria(subject, "medium", type, count));
                        if (remaining > 0) remaining--;
                    }
                    
                    // 困难难度
                    if (hardCount != null && hardCount > 0) {
                        int count = perDifficultyCount + (remaining > 0 ? 1 : 0);
                        selectedQuestions.addAll(selectQuestionsByCriteria(subject, "hard", type, count));
                    }
                }
            }
            
            // 4. 计算总分
            BigDecimal totalScore = selectedQuestions.stream()
                    .map(Question::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 5. 创建试卷
            Paper paper = new Paper();
            paper.setName(name);
            paper.setSubject(subject);
            paper.setDuration(duration != null ? duration : 60);
            paper.setTotalScore(totalScore);
            paper.setPassScore(new BigDecimal("60"));
            paper.setQuestionCount(selectedQuestions.size());
            paper.setDifficulty(calculateDifficulty(easyCount, mediumCount, hardCount));
            paper.setStatus("draft");
            paper.setCreatorId(creatorId);
            paper.setCreatorName(creatorName);
            
            // 保存组卷配置
            Map<String, Object> paperConfig = new HashMap<>();
            paperConfig.put("easyCount", easyCount);
            paperConfig.put("mediumCount", mediumCount);
            paperConfig.put("hardCount", hardCount);
            paperConfig.put("singleCount", singleCount);
            paperConfig.put("multipleCount", multipleCount);
            paperConfig.put("judgeCount", judgeCount);
            paperConfig.put("essayCount", essayCount);
            paper.setConfig(objectMapper.writeValueAsString(paperConfig));
            
            // 6. 保存试卷
            boolean saved = this.save(paper);
            
            if (!saved) {
                return Result.error("试卷创建失败");
            }
            
            // 7. 将题目关联到试卷（插入paper_question表）
            int sortOrder = 1;
            for (Question question : selectedQuestions) {
                baseMapper.insertPaperQuestion(
                    paper.getId(),
                    question.getId(),
                    question.getType(),
                    question.getContent(),
                    question.getScore(),
                    sortOrder++
                );
            }
            
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("paperId", paper.getId());
            resultData.put("questionCount", selectedQuestions.size());
            resultData.put("totalScore", totalScore);
            return Result.success(resultData);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("自动组卷失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据条件随机抽取试题
     */
    private List<Question> selectQuestionsByCriteria(String subject, String difficulty, String type, int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getSubject, subject)
               .eq(Question::getDifficulty, difficulty)
               .eq(Question::getType, type)
               .eq(Question::getStatus, "active")
               .orderByAsc(Question::getId); // 使用固定排序，然后随机选取
        
        List<Question> questions = questionService.list(wrapper);
        
        if (questions.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 随机打乱并选取指定数量
        Collections.shuffle(questions);
        return questions.subList(0, Math.min(count, questions.size()));
    }
    
    /**
     * 计算整体难度
     */
    private String calculateDifficulty(Integer easyCount, Integer mediumCount, Integer hardCount) {
        int total = (easyCount != null ? easyCount : 0) + 
                    (mediumCount != null ? mediumCount : 0) + 
                    (hardCount != null ? hardCount : 0);
        
        if (total == 0) {
            return "medium";
        }
        
        int easy = easyCount != null ? easyCount : 0;
        int hard = hardCount != null ? hardCount : 0;
        
        double easyRatio = (double) easy / total;
        double hardRatio = (double) hard / total;
        
        if (easyRatio > 0.6) {
            return "easy";
        } else if (hardRatio > 0.4) {
            return "hard";
        } else {
            return "medium";
        }
    }
    
    @Override
    public Result getPaperQuestions(Long paperId) {
        try {
            List<Map<String, Object>> questions = baseMapper.getPaperQuestions(paperId);
            return Result.success(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取试卷题目失败: " + e.getMessage());
        }
    }
}

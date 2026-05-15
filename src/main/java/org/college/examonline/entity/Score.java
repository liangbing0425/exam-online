package org.college.examonline.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("score")
public class Score implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long examId;
    
    private String examName;
    
    private Long studentId;
    
    private String studentNo;
    
    private String studentName;
    
    private BigDecimal totalScore;
    
    private BigDecimal objectiveScore;
    
    private BigDecimal subjectiveScore;
    
    private Integer rank;
    
    private String status;
    
    private LocalDateTime submitTime;
    
    private LocalDateTime gradeTime;
    
    private Long graderId;
    
    private String graderName;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 暂时禁用逻辑删除功能
    // @TableLogic
    // private Integer deleted;
}

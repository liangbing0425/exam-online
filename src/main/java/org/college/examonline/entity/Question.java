package org.college.examonline.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String type;
    
    private String content;
    
    private String options;
    
    private String answer;
    
    private String analysis;
    
    private String difficulty;
    
    private String subject;
    
    private String chapter;
    
    private String tags;
    
    private BigDecimal score;
    
    private Long creatorId;
    
    private String creatorName;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 暂时禁用逻辑删除功能
    // @TableLogic
    // private Integer deleted;
}

package org.college.examonline.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("paper")
public class Paper implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String subject;
    
    private Integer duration;
    
    private BigDecimal totalScore;
    
    private BigDecimal passScore;
    
    private Integer questionCount;
    
    private String difficulty;
    
    private String description;
    
    private String config;
    
    private String status;
    
    private Long creatorId;
    
    private String creatorName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 暂时禁用逻辑删除功能
    // @TableLogic
    // private Integer deleted;
}

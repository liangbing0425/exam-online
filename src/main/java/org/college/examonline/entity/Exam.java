package org.college.examonline.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long paperId;
    
    private String paperName;
    
    private String subject;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer duration;
    
    private String status;
    
    private Integer participantCount;
    
    private Integer submittedCount;
    
    private Integer allowReview;
    
    private Integer randomOrder;
    
    private Integer antiCheat;
    
    private Integer maxSwitchTimes;
    
    private String description;
    
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

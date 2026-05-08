package com.ran.commons.dto.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(name = "Application record for joining space/enterprise")
public class ApplyRecordVO {

    @Schema(description = "Application ID")
    private Long id;

    @Schema(description = "Enterprise team ID")
    private Long enterpriseId;

    @Schema(description = "Space ID")
    private Long spaceId;

    @Schema(description = "Applicant UID")
    private String applyUid;

    @Schema(description = "Applicant nickname")
    private String applyNickname;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "Application time")
    private LocalDateTime applyTime;

    @Schema(description = "Application status: 1 pending, 2 approved, 3 rejected")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "Review time")
    private LocalDateTime auditTime;

    @Schema(description = "Reviewer UID")
    private String auditUid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

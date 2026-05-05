package com.ran.commons.dto.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(name = "Enterprise team user")
public class EnterpriseUserVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Enterprise ID")
    private Long enterpriseId;

    @Schema(description = "User ID")
    private String uid;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "User nickname")
    private String nickname;

    @Schema(description = "Role: 1 super admin, 2 admin, 3 member")
    private Integer role;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

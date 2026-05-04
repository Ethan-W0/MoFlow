package com.ran.commons.entity.space;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName("agent_enterprise_user")
@Schema(name = "AgentEnterpriseUser", description = "Enterprise team user")

public class EnterpriseUser implements Serializable {
    private static final long serialVersionUID = 1L;

}

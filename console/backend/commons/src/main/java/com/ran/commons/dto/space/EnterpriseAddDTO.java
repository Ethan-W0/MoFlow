package com.ran.commons.dto.space;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(name = "Add Enterprise Team Request Parameters")
public class EnterpriseAddDTO {

    @Schema(description = "Team name")
    @NotEmpty(message = "Team name cannot be empty")
    private String name;

    @Schema(description = "Avatar URL")
    private String avatarUrl;

}
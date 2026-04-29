package com.ran.workflow.engine.domain.chain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeMeta {

    @JsonProperty("nodeType")
    private String nodeType;

    /**
     * Human-readable alias name
     */
    @JsonProperty("aliasName")
    private String aliasName;
}

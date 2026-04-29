package com.ran.workflow.engine.domain.chain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeData {
    /**
     * 输入参数列表
     */
    @JsonProperty("inputs")
    private List<InputItem> inputs = new ArrayList<>();

    /**
     * 节点元信息（名称、描述等）
     */
    @JsonProperty("nodeMeta")
    private NodeMeta nodeMeta;

    /**
     * 节点专用参数
     */
    @JsonProperty("nodeParam")
    private Map<String, Object> nodeParam = new HashMap<>();

    /**
     * 输出参数列表
     */
    @JsonProperty("outputs")
    private List<OutputItem> outputs = new ArrayList<>();

    /**
     * 重试配置
     */
    @JsonProperty("retryConfig")
    private RetryConfig retryConfig;



}

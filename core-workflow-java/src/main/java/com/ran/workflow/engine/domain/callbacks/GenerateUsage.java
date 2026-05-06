package com.ran.workflow.engine.domain.callbacks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateUsage {
    @JsonProperty("completion_tokens")
    private int completionTokens = 0;

    @JsonProperty("prompt_tokens")
    private int promptTokens = 0;

    @JsonProperty("total_tokens")
    private int totalTokens = 0;

    public void add(GenerateUsage usage) {
        this.completionTokens += usage.completionTokens;
        this.promptTokens += usage.promptTokens;
        this.totalTokens += usage.totalTokens;
    }
}

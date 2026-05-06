package com.ran.workflow.engine.domain.chain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ran.workflow.engine.constants.NodeStatusEnum;
import com.ran.workflow.engine.constants.NodeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node{
    @JsonProperty("id")
    private String id;

    @JsonProperty("data")
    private NodeData data;

    private NodeStatusEnum status;

    /**
     * 前置Node，前面的这些node都执行完毕之后，才会执行当前node
     */
    private List<Node> preNodes;

    /**
     * 后置Node，当前node执行成功之后，执行后续的node
     */
    private List<Node> nextNodes;

    /**
     * 失败Node，当前node执行失败之后，执行后续的node
     */
    private List<Node> failNodes;

    /**
     * 当前node已经执行了多少次
     */
    private AtomicInteger executedCount;

    public NodeTypeEnum getNodeType() {
        if (id != null && id.contains("::")) {
            return NodeTypeEnum.fromValue(id.split("::")[0]);
        }
        return null;
    }
    public void init() {
        status = NodeStatusEnum.INIT;
        preNodes = new ArrayList<>();
        nextNodes = new ArrayList<>();
        failNodes = new ArrayList<>();
        executedCount = new AtomicInteger(0);
    }

}
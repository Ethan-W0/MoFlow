package com.ran.workflow.flow.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ran.workflow.engine.domain.WorkflowDSL;
import com.ran.workflow.flow.entity.WorkflowEntity;
import com.ran.workflow.flow.mapper.WorkflowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class WorkflowService {
    private final WorkflowMapper workflowMapper;

    public WorkflowService(WorkflowMapper workflowMapper) {
        this.workflowMapper = workflowMapper;
    }

    public WorkflowDSL getWorkflowDSL(String workflowId) {
        if (log.isDebugEnabled()) {
            log.debug("Loading workflow: {}", workflowId);
        }

        LambdaQueryWrapper<WorkflowEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkflowEntity::getId, workflowId);

        WorkflowEntity entity = workflowMapper.selectOne(queryWrapper);

        if (entity == null) {
            throw new IllegalArgumentException("Workflow not found: " + workflowId);
        }

        String dslData = JSONObject.parse(entity.getData()).getString("data");
        WorkflowDSL dsl = JSON.parseObject(dslData, WorkflowDSL.class);
        dsl.setFlowId(workflowId);

        if (log.isDebugEnabled()) {
            log.info("Loaded workflow: id={}, nodes={}, edges={}",
                    workflowId, dsl.getNodes().size(), dsl.getEdges().size());
        }
        return dsl;
    }

    public WorkflowEntity saveWorkflow(WorkflowAddRequest request) {
        WorkflowEntity entity = new WorkflowEntity();
        entity.setId(IdUtil.genId());
        entity.setGroupId(request.getGroupId() != null ? request.getGroupId() : IdUtil.genId());
        entity.setName(request.getName() != null ? request.getName() : "");
        entity.setDescription(request.getDescription() != null ? request.getDescription() : "");
        entity.setAppId(request.getAppId() != null ? request.getAppId() : "");
        entity.setSource(request.getSource() != null ? request.getSource() : 0);
        entity.setVersion(request.getVersion() != null ? request.getVersion() : "");
        entity.setTag(request.getTag() != null ? request.getTag() : 0);

        // Convert data map to JSON string
        if (request.getData() != null) {
            entity.setData(JSON.toJSONString(request.getData()));
        } else {
            entity.setData("{}");
        }

        entity.setCreateAt(LocalDateTime.now());
        entity.setUpdateAt(LocalDateTime.now());

        workflowMapper.insert(entity);
        return entity;
    }
}

package com.ran.workflow.engine;

import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多个节点产生的输出，这些节点要被后续节点以“节点ID+输出名”的方式取出来
 *
 */
@Slf4j
public class VariablePool {

    // 1.选择并设计存储结构 Map<节点ID+Map<输出名，值>>
    private final Map<String, Map<String, Object>> variables = new ConcurrentHashMap<>();

    // 2.简单的set和get
    public void set(String nodeId, String outputName, Object value) {
        // 4. 不需要转换的类型直接放行
        if (ClassUtil.isPrimitiveWrapper(value.getClass())
                || value instanceof Number
                || value instanceof String
                || value instanceof JSONArray
                || value instanceof JSONObject
                || value instanceof UUID) {
            // 什么都不做
        }
        // 5. List 转成 JSONArray
        else if (value instanceof List<?>) {
            value = JSON.parseArray(JSON.toJSONString(value));
        }
        // 6. 其他对象转成 JSONObject
        else {
            value = JSON.parseObject(JSON.toJSONString(value));
        }

        variables.computeIfAbsent(nodeId, k -> new ConcurrentHashMap<>())
                .put(outputName, value);
    }
    public Object get(String nodeId, String outputName) {
        Map target = variables.getOrDefault(nodeId, Map.of());
        return getVal(target, outputName);
    }

    public Map<String, Object> get(String nodeId) {
        return variables.getOrDefault(nodeId, Map.of());
    }

    private Object getVal(Map map, String key) {
        int index = key.indexOf(".");
        if (index < 0) {
            return map.get(key);
        }

        String rootKey = key.substring(0, index);
        String subKey = key.substring(index + 1);

        Map subMap;

        // 如果subKey中形如 xxx[0]，则需要进一步提取
        index = rootKey.indexOf("[");
        if (index > 0 && rootKey.endsWith("]")) {
            String subIndex = rootKey.substring(index + 1, rootKey.length() - 1);
            rootKey = rootKey.substring(0, index);

            subMap = (Map) ((List) map.get(rootKey)).get(Integer.parseInt(subIndex));
        } else {
            subMap = (Map) map.get(rootKey);
        }


        return getVal(subMap, subKey);
    }

    public void clear() {
        variables.clear();
    }
}

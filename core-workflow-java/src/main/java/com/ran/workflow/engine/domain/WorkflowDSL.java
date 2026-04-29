package com.ran.workflow.engine.domain;

public class WorkflowDSL {
    public List<Edge> getOutgoingEdges(String sourceNodeId) {
        List<Edge> outgoing = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(sourceNodeId)) {
                outgoing.add(edge);
            }
        }
        return outgoing;
    }


}

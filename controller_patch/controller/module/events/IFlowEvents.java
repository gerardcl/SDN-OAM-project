package dxat.controller.module.events;

public interface IFlowEvents {
    public final static String PUSH_FLOW_SRC_TERMINAL_NOT_FOUND = "PUSH_FLOW_SRC_TERMINAL_NOT_FOUND";
    public final static String PUSH_FLOW_DST_TERMINAL_NOT_FOUND = "PUSH_FLOW_DST_TERMINAL_NOT_FOUND";
    public final static String PUSH_FLOW_UNREACHABLE_TERMINALS = "PUSH_FLOW_UNREACHABLE_TERMINALS";
    public final static String PUSH_FLOW_ILLEGAL_FLOW_ENTRY = "PUSH_FLOW_ILLEGAL_FLOW_ENTRY";
    public final static String PUSH_FLOW_FLOW_ALREADY_EXIST = "PUSH_FLOW_FLOW_ALREADY_EXIST";
    public final static String PUSH_FLOW_SUCCESS = "PUSH_FLOW_SUCCESS";

    public final static String DELETE_FLOW_FAILED = "DELETE_FLOW_FAILED";
    public final static String DELETE_FLOW_SUCCESS = "DELETE_FLOW_SUCCESS";
    public final static String ALL_FLOWS_DELETED = "ALL_FLOWS_DELETED";

    public final static String REROUTE_FLOW_SRC_TERMINAL_NOT_FOUND = "REROUTE_FLOW_SRC_TERMINAL_NOT_FOUND";
    public final static String REROUTE_FLOW_DST_TERMINAL_NOT_FOUND = "REROUTE_FLOW_DST_TERMINAL_NOT_FOUND";
    public final static String REROUTE_FLOW_UNREACHABLE_TERMINALS = "REROUTE_FLOW_UNREACHABLE_TERMINALS";
    public final static String REROUTE_FLOW_ILLEGAL_FLOW_ENTRY = "REROUTE_FLOW_ILLEGAL_FLOW_ENTRY";
    public final static String REROUTE_FLOW_SUCCESS = "REROUTE_FLOW_SUCCESS";

    public final static String FLOWS_COLLECTION = "FLOWS_COLLECTION";
}

package dxat.controller.module.events;

public interface IFlowEvents {
    public final static String PUSH_FLOW_SRC_TERMINAL_NOT_FOUND = "FLOW_PUSH_SRC_TERMINAL_NOT_FOUND";
    public final static String PUSH_FLOW_DST_TERMINAL_NOT_FOUND = "FLOW_PUSH_DST_TERMINAL_NOT_FOUND";
    public final static String PUSH_FLOW_UNREACHABLE_TERMINALS = "FLOW_PUSH_UNREACHABLE_TERMINALS";
    public final static String PUSH_FLOW_ILLEGAL_FLOW_ENTRY = "FLOW_PUSH_ILLEGAL_FLOW_ENTRY";
    public final static String PUSH_FLOW_FLOW_ALREADY_EXIST = "FLOW_PUSH_FLOW_ALREADY_EXIST";
    public final static String PUSH_FLOW_SUCCESS = "FLOW_PUSH_SUCCESS";

    public final static String DELETE_FLOW_FAILED = "FLOW_DELETE_FAILED";
    public final static String DELETE_FLOW_SUCCESS = "FLOW_DELETE_SUCCESS";
    public final static String ALL_FLOWS_DELETED = "FLOW_ALL_DESLETED";

    public final static String REROUTE_FLOW_SRC_TERMINAL_NOT_FOUND = "FLOW_REROUTE_SRC_TERMINAL_NOT_FOUND";
    public final static String REROUTE_FLOW_DST_TERMINAL_NOT_FOUND = "FLOW_REROUTE_DST_TERMINAL_NOT_FOUND";
    public final static String REROUTE_FLOW_UNREACHABLE_TERMINALS = "FLOW_REROUTE_UNREACHABLE_TERMINALS";
    public final static String REROUTE_FLOW_ILLEGAL_FLOW_ENTRY = "FLOW_REROUTE_ILLEGAL_FLOW_ENTRY";
    public final static String REROUTE_FLOW_SUCCESS = "FLOW_REROUTE_SUCCESS";

    public final static String FLOWS_COLLECTION = "FLOW_COLLECTION";
}

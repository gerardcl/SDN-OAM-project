package dxat.controller.module.exceptions;

import dxat.controller.module.pojos.Flow;

/**
 * Created by xavier on 1/11/14.
 */
public class IllegalFlowEntryException extends Exception {
    public IllegalFlowEntryException(Flow flow, String dpid, String matchString) {
        super("Illegal flow entry with flow id '" + flow.getFlowId() + "' at the switch with id '" + dpid +
                "' with the match string '" + matchString + "'");
    }
}
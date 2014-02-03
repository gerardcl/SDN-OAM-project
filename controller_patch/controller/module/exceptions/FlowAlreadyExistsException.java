package dxat.controller.module.exceptions;

import dxat.controller.module.pojos.Flow;

/**
 * Created by xavier on 1/11/14.
 */
public class FlowAlreadyExistsException extends Exception {
    public FlowAlreadyExistsException(Flow flow) {
        super("The terminals with identifier '" + flow.getFlowId() + "' already exists");
    }
}
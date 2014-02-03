package dxat.controller.module.exceptions;

import dxat.controller.module.pojos.Flow;

/**
 * Created by xavier on 1/11/14.
 */
public class UnreachableTerminalsException extends Exception {
    public UnreachableTerminalsException(Flow flow) {
        super("The terminals with IPv4 '" + flow.getDstIpAddr() + "' and '" + flow.getSrcIpAddr() + "' are unreachable");
    }
}
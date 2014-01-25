package dxat.controller.module.exceptions;

import dxat.controller.module.pojos.Flow;

/**
 * Created by xavier on 1/11/14.
 */
public class DstTerminalNotFoundException extends Exception {
    public DstTerminalNotFoundException(Flow flow) {
        super("Terminal with IPv4 '" + flow.getDstIpAddr() + "' not found.");
    }
}
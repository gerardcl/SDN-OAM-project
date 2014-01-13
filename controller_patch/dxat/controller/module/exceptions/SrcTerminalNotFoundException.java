package dxat.controller.module.exceptions;

import dxat.controller.module.pojos.Flow;

/**
 * Created by xavier on 1/11/14.
 */
public class SrcTerminalNotFoundException extends Exception {
    public SrcTerminalNotFoundException(Flow flow) {
        super("Source terminal with IPv4 '"+flow.getSrcIpAddr()+"' not found.");
    }
}
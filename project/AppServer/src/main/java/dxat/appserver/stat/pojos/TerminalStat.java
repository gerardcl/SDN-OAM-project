package dxat.appserver.stat.pojos;

import dxat.appserver.topology.pojos.Terminal;

/**
 * Created by xavier on 1/28/14.
 */
public class TerminalStat extends Terminal {
    private double in;
    private double out;

    public double getIn() {
        return in;
    }

    public void setIn(double in) {
        this.in = in;
    }

    public double getOut() {
        return out;
    }

    public void setOut(double out) {
        this.out = out;
    }
}

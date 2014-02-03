package dxat.appserver.stat.pojos;

import dxat.appserver.topology.pojos.Link;

/**
 * Created by xavier on 1/28/14.
 */
public class LinkStat extends Link {
    private double rate;

    public LinkStat(Link link) {
        super(link);
    }

    public LinkStat() {
        super();
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}

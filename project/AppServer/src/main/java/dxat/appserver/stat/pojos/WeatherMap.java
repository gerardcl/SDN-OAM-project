package dxat.appserver.stat.pojos;

import java.util.List;

/**
 * Created by xavier on 1/28/14.
 */
public class WeatherMap {
    private List<LinkStat> linkStats;

    public List<LinkStat> getLinkStats() {
        return linkStats;
    }

    public void setLinkStats(List<LinkStat> linkStats) {
        this.linkStats = linkStats;
    }
}

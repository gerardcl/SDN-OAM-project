package dxat.controller.module;

import net.floodlightcontroller.routing.Link;

import java.util.HashMap;

/**
 * Created by xavier on 1/18/14.
 */
public class LinkCost {
    private HashMap<Link, Integer> costs;

    public LinkCost() {
        costs = new HashMap<Link, Integer>();
    }

    public void resetCosts() {
        costs.clear();
    }

    public void putCost(Link link, Integer cost) {
        // If costs does not contain the link create it
        if (costs.containsKey(link) == false) {
            costs.put(link, 0);
        }

        Integer linkCost = costs.get(link);
        linkCost += cost;
    }

    public void removeCost(Link link, Integer cost) {
        // If costs does not contain the link create it
        if (costs.containsKey(link) == false) {
            costs.put(link, 0);
        }

        Integer linkCost = costs.get(link);
        if (linkCost > cost) {
            linkCost -= cost;
        } else {
            linkCost = 0;
        }
    }

    public HashMap<Link, Integer> getCosts() {
        return costs;
    }
}

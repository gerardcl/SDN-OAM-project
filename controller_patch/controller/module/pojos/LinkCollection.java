package dxat.controller.module.pojos;

import java.util.ArrayList;
import java.util.List;

public class LinkCollection {
    private List<TransferLink> links;

    public LinkCollection() {
        links = new ArrayList<TransferLink>();
    }

    public List<TransferLink> getLinks() {
        return links;
    }

    public void setLinks(List<TransferLink> links) {
        this.links = links;
    }

}

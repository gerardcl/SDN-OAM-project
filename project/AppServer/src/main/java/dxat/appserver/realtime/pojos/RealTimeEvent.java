package dxat.appserver.realtime.pojos;

import java.util.ArrayList;
import java.util.List;

import dxat.appserver.topology.db.DbUpdate;

public class RealTimeEvent {
	private Long timestamp;
	private String event = "";
	private List<DbUpdate> updates = null;

	public RealTimeEvent() {
		super();
		updates = new ArrayList<DbUpdate>();
	}

	public Long  getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public List<DbUpdate> getUpdates() {
		return updates;
	}

	public void setUpdates(List<DbUpdate> updates) {
		this.updates = updates;
	}

}

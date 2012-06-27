package com.imeeting.mvc.model.group.message;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imeeting.mvc.model.group.GroupDB;
import com.imeeting.mvc.model.group.GroupModel;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;

public class LoadGroupAttendeesMsg implements IGroupMessage {
	private static Log log = LogFactory.getLog(LoadGroupAttendeesMsg.class);

	@Override
	public void onReceive(GroupModel model) throws Exception {
		List<Map<String, Object>> list = GroupDB.getGroupAttendees(model.getGroupId());
		for (Map<String, Object> map : list) {
			String name = (String) map.get("username");
			if (!name.equals(model.getOwner())) {
				model.addAttendee(new AttendeeBean(name));
			}
		}
	}

}

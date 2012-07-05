package com.imeeting.mvc.model.group.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.group.GroupDB;
import com.imeeting.mvc.model.group.GroupModel;
import com.imeeting.mvc.model.group.attendee.AttendeeAction;
import com.imeeting.mvc.model.group.attendee.AttendeeBean;
import com.imeeting.mvc.model.group.attendee.AttendeeBean.OnlineStatus;
import com.richitec.notify.Notifier;

public class AddAttendeesToGroupMessage implements IGroupMessage {
	private static Log log = LogFactory
			.getLog(AddAttendeesToGroupMessage.class);

	private String attendeeListString;

	public AddAttendeesToGroupMessage(String attendeesString) {
		this.attendeeListString = attendeesString;
	}

	@Override
	public void onReceive(GroupModel model) throws Exception {
		JSONArray attendeesJsonArray = new JSONArray();
		attendeesJsonArray = new JSONArray(this.attendeeListString);
		
		log.info("attendees size: " + attendeesJsonArray.length());

		// remove the duplicated attendee in attendee array to be added
		JSONArray newAttendeesJsonArray = new JSONArray();
		for (int i = 0; i < attendeesJsonArray.length(); i++) {
			String username = attendeesJsonArray.getString(i);
			AttendeeBean ab = model.findAttendee(username);
			if (ab == null) {
				newAttendeesJsonArray.put(username);
			}
		}
		
		if (newAttendeesJsonArray.length() <= 0) {
			return;
		}
		
		GroupDB.insertAttendees(model.getGroupId(), newAttendeesJsonArray);

		List<AttendeeBean> attendeesArrayList = new ArrayList<AttendeeBean>();
		for (int i = 0; i < newAttendeesJsonArray.length(); i++) {
			try {
				attendeesArrayList.add(new AttendeeBean(newAttendeesJsonArray
						.getString(i), OnlineStatus.offline));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		model.addAttendees(attendeesArrayList);
		
		// broadcast attendees adding message
		JSONObject msg = new JSONObject();
		msg.put("groupId", model.getGroupId());
		msg.put("action", AttendeeAction.update_attendee_list.name());
		
		Notifier nf = ContextLoader.getNotifier();
		nf.notifyWithHttpPost(model.getGroupId(), msg.toString());
	}

}

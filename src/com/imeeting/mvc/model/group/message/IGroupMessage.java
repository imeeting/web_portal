package com.imeeting.mvc.model.group.message;

import com.imeeting.mvc.model.group.GroupModel;

public interface IGroupMessage {

	public void onReceive(GroupModel model) throws Exception;
	
}

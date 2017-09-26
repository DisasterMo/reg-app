package edu.kit.scc.regapp.register;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.UserEntity;

public class GroupUpdateStructure {

	private Map<GroupEntity, Set<UserEntity>> updateMap;
	
	public GroupUpdateStructure() {
		updateMap = new HashMap<GroupEntity, Set<UserEntity>>();
	}
	
	public void addGroup(GroupEntity group, Set<UserEntity> users) {
		updateMap.put(group, users);
	}
	
	public Set<GroupEntity> getGroups() {
		return updateMap.keySet();
	}
	
	public Set<UserEntity> getUsersForGroup(GroupEntity group) {
		return updateMap.get(group);
	}
}

package edu.kit.scc.regapp.bpm;

public interface BpmProcessService {

	void reload();

	void init();

	void replaceAllInRules(String regex, String replacement);

}
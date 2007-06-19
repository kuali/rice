package edu.iu.uis.eden.testclient1;

import java.util.ArrayList;
import java.util.List;

public class TestClient1ObjectToBeRemoted implements RemotedObject {

	public static List<String> CALL_RECORDER = new ArrayList<String>();
	public static final String METHOD_INVOKED = "method_invoked";

	public String invoke(String input) {
		CALL_RECORDER.add(input);
		return METHOD_INVOKED;
	}
}
package sejong.corona;

import javax.swing.*;

public class ManagerChatData {
	private JTextArea ta;

	public void addObj(JTextArea ta) {
		this.ta = ta;
	}

	public void refreshData(String msg) {
		ta.append(msg);
	}
}
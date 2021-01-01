package sejong.corona;

import java.io.*;
import java.awt.event.*;
import java.net.*;

import com.google.gson.Gson;

import java.util.logging.*;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public class UserChatController extends Thread {
	private final UserChatUI view;
	private final ChatData chatData;
	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;
	private Gson gson = new Gson();
	private Logger logger;
	private Socket socket;
	private boolean status = true;
	Thread thread;
	public int num = 2;
	public String id = "User".concat(Integer.toString(num));
	private Message m = new Message("", "", "", "");

	public UserChatController(ChatData chatData, UserChatUI view) {
		logger = Logger.getLogger(this.getClass().getName());
		this.view = view;
		this.chatData = chatData;
	}

	void appMain() {
		chatData.addObj(view.msgOut);
		connectServer();
		view.id = m.getId();
		view.addButtonActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				if (obj == view.msgInput) {
					outMsg.println(gson.toJson(new Message(view.id, "", view.msgInput.getText(), "msg")));
					view.msgInput.setText("");
				}
			}
		});

	}

	public void connectServer() {
		try {
			socket = new Socket("127.0.0.1", 8888);
			logger.log(INFO, "[Client]Server 연결 성공!!");

			inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outMsg = new PrintWriter(socket.getOutputStream(), true);

			m = new Message("", "", "", "");

			outMsg.println(gson.toJson(m));

			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			logger.log(WARNING, "[UserChatController]connectServer() Exception 발생!!");
			e.printStackTrace();
		}
	}

	public void run() {
		String msg;
		status = true;

		while (status) {
			try {
				msg = inMsg.readLine();
				m = gson.fromJson(msg, Message.class);

				chatData.refreshData(m.getId() + ">" + m.getMsg() + "\n");
				view.msgOut.setCaretPosition(view.msgOut.getDocument().getLength());
			} catch (IOException e) {
				logger.log(WARNING, "[UserChatController]메시지 스트림 종료!!");
			}
		}
		logger.info("[UserChatController]" + thread.getName() + " 메시지 수신 스레드 종료됨!!");
	}
}
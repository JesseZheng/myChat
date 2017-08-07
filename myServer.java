package myZZ;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class myServer {
	private Text txtServerIP;
	private Text txtPort;
	private Text txtReceivedMessage;
	private Text txtSendingMsg;

	private org.eclipse.swt.widgets.List userList;
	
	private Button btnListening;
	private Button btnClose;
	private Button btnSend;
	
	private ServerSocket serverSocket;
	private ServerThread serverThread;
	
	private ArrayList<ClientThread> clients;
	private boolean isStart = false;
	
	Display display;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			myServer window = new myServer();
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(1213, 752);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());
		
		Group group = new Group(shell, SWT.NONE);
		group.setText("\u670D\u52A1\u5668\u529F\u80FD");
		group.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(0, 154);
		fd_group.top = new FormAttachment(0, 10);
		fd_group.left = new FormAttachment(0, 10);
		fd_group.right = new FormAttachment(100, -24);
		group.setLayoutData(fd_group);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 24);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("\u670D\u52A1\u5668IP\u5730\u5740\uFF1A");
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 21);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("\u670D\u52A1\u5668\u7AEF\u53E3\uFF1A");
		
		txtServerIP = new Text(group, SWT.BORDER);
		txtServerIP.setText("127.0.0.1");
		fd_lblNewLabel.top = new FormAttachment(txtServerIP, 3, SWT.TOP);
		FormData fd_txtServerIP = new FormData();
		fd_txtServerIP.left = new FormAttachment(lblNewLabel, 37);
		fd_txtServerIP.top = new FormAttachment(0, 23);
		txtServerIP.setLayoutData(fd_txtServerIP);
		
		txtPort = new Text(group, SWT.BORDER);
		txtPort.setText("7887");
		FormData fd_txtPort = new FormData();
		fd_txtPort.right = new FormAttachment(txtServerIP, 0, SWT.RIGHT);
		fd_txtPort.top = new FormAttachment(lblNewLabel_1, -3, SWT.TOP);
		fd_txtPort.left = new FormAttachment(txtServerIP, 0, SWT.LEFT);
		txtPort.setLayoutData(fd_txtPort);
		
		btnListening = new Button(group, SWT.NONE);
		
		//服务器侦听(多线程)+循环接受客户端信息
		btnListening.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isStart) {
					JOptionPane.showMessageDialog(null, "服务器已处于启动状态，不要重复启动！",
							"错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int port;
				try {
					port = Integer.parseInt(txtPort.getText());
					serverStart(port);//启动服务器
					txtReceivedMessage.append("服务器已成功启动！端口：" + port
							+ "\n");
					JOptionPane.showMessageDialog(null, "服务器成功启动!");
					btnListening.setEnabled(false);
					txtPort.setEnabled(false);
					btnClose.setEnabled(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		fd_txtServerIP.right = new FormAttachment(100, -225);
		FormData fd_btnListening = new FormData();
		fd_btnListening.right = new FormAttachment(txtServerIP, 188, SWT.RIGHT);
		fd_btnListening.top = new FormAttachment(lblNewLabel, -5, SWT.TOP);
		fd_btnListening.left = new FormAttachment(txtServerIP, 85);
		btnListening.setLayoutData(fd_btnListening);
		btnListening.setText("\u4FA6\u542C");
		
		btnClose = new Button(group, SWT.NONE);
		//关闭服务器
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!isStart) {
					JOptionPane.showMessageDialog(null, "服务器还未启动，无需停止！", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					closeServer();//关闭服务器
					btnListening.setEnabled(true);
					txtPort.setEnabled(true);
					btnClose.setEnabled(false);
					txtReceivedMessage.append("服务器成功停止!\n");
					JOptionPane.showMessageDialog(null, "服务器成功停止！");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(null, "停止服务器发生异常！", "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		FormData fd_btnClose = new FormData();
		fd_btnClose.right = new FormAttachment(btnListening, 0, SWT.RIGHT);
		fd_btnClose.top = new FormAttachment(lblNewLabel_1, -5, SWT.TOP);
		fd_btnClose.left = new FormAttachment(btnListening, 0, SWT.LEFT);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.setText("\u65AD\u5F00\u8FDE\u63A5");
		
		Group group_1 = new Group(shell, SWT.NONE);
		group_1.setText("\u5728\u7EBF\u7528\u6237");
		group_1.setLayout(new FormLayout());
		FormData fd_group_1 = new FormData();
		fd_group_1.top = new FormAttachment(group, 6);
		fd_group_1.left = new FormAttachment(0, 10);
		fd_group_1.bottom = new FormAttachment(100, -10);
		fd_group_1.right = new FormAttachment(0, 201);
		group_1.setLayoutData(fd_group_1);
		
		Group group_2 = new Group(shell, SWT.NONE);
		group_2.setText("\u6536\u53D1\u6570\u636E");
		group_2.setLayout(new FormLayout());
		FormData fd_group_2 = new FormData();
		fd_group_2.right = new FormAttachment(group, 0, SWT.RIGHT);
		fd_group_2.top = new FormAttachment(group, 6);
		fd_group_2.left = new FormAttachment(group_1, 20);
		
		userList = new org.eclipse.swt.widgets.List(group_1, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_userList = new FormData();
		fd_userList.top = new FormAttachment(0, 10);
		fd_userList.left = new FormAttachment(0, 10);
		fd_userList.bottom = new FormAttachment(100, -4);
		fd_userList.right = new FormAttachment(0, 178);
		userList.setLayoutData(fd_userList);
		fd_group_2.bottom = new FormAttachment(100, -10);
		group_2.setLayoutData(fd_group_2);
		
		
		txtReceivedMessage = new Text(group_2, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		FormData fd_txtReceivedMessage = new FormData();
		fd_txtReceivedMessage.top = new FormAttachment(0, 10);
		fd_txtReceivedMessage.left = new FormAttachment(0, 10);
		fd_txtReceivedMessage.right = new FormAttachment(100, -13);
		txtReceivedMessage.setLayoutData(fd_txtReceivedMessage);
		fd_txtReceivedMessage.bottom = new FormAttachment(100, -260);
		
		txtSendingMsg = new Text(group_2, SWT.BORDER);
		FormData fd_txtSendingMsg = new FormData();
		fd_txtSendingMsg.top = new FormAttachment(txtReceivedMessage, 16);
		fd_txtSendingMsg.right = new FormAttachment(txtReceivedMessage, -3, SWT.RIGHT);
		fd_txtSendingMsg.left = new FormAttachment(txtReceivedMessage, 0, SWT.LEFT);
		fd_txtSendingMsg.bottom = new FormAttachment(100, -98);
		txtSendingMsg.setLayoutData(fd_txtSendingMsg);
		
		btnSend = new Button(group_2, SWT.NONE);
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				send();
			}
		});
		FormData fd_btnSend = new FormData();
		fd_btnSend.left = new FormAttachment(100, -117);
		fd_btnSend.bottom = new FormAttachment(100, -34);
		fd_btnSend.right = new FormAttachment(100, -42);
		btnSend.setLayoutData(fd_btnSend);
		btnSend.setText("\u53D1\u9001");
		

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	// 执行消息发送
		public void send() {
			if (!isStart) {
				JOptionPane.showMessageDialog(null, "服务器还未启动,不能发送消息！", "错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (clients.size() == 0) {
				JOptionPane.showMessageDialog(null, "没有用户在线,不能发送消息！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String message = txtSendingMsg.getText().trim();
			if (message == null || message.equals("")) {
				JOptionPane.showMessageDialog(null, "消息不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			sendServerMessage(message);// 群发服务器消息
			txtReceivedMessage.append("服务器说：" + txtSendingMsg.getText() + "\n");
			txtSendingMsg.setText("");
		}

		// 启动服务器
		public void serverStart(int port) {
			try {
				clients = new ArrayList<ClientThread>();
				serverSocket = new ServerSocket(port);
				serverThread = new ServerThread(serverSocket);
				serverThread.start();
				isStart = true;
			} catch (Exception e) {
				isStart = false;
			} 
		}
		
		// 关闭服务器
		public void closeServer() {
			try {
				if (serverThread != null)
					serverThread.stop();// 停止服务器线程
				for (int i = clients.size() - 1; i >= 0; i--) {
					// 给所有在线用户发送关闭命令
					clients.get(i).getWriter().println("CLOSE");
					clients.get(i).getWriter().flush();
					// 释放资源
					clients.get(i).stop();// 停止此条为客户端服务的线程
					clients.get(i).inFromClient.close();
					clients.get(i).outToClient.close();
					clients.get(i).socket.close();
					clients.remove(i);
				}
				if (serverSocket != null) {
					serverSocket.close();// 关闭服务器端连接
				}
				userList.removeAll();// 清空用户列表
				isStart = false;
			} catch (IOException e) {
				e.printStackTrace();
				isStart = true;
			}
		}
		
		// 群发服务器消息
		public void sendServerMessage(String message) {
			for (int i = clients.size() - 1; i >= 0; i--) {
				clients.get(i).getWriter().println("服务器：" + message + "(群发)");
				clients.get(i).getWriter().flush();
			}
		}
		
		
		// 服务器线程
		class ServerThread extends Thread {
			private ServerSocket serverSocket;
			// 服务器线程的构造方法
			public ServerThread(ServerSocket serverSocket) {
				this.serverSocket = serverSocket;
			}
			public void run() {
				while (true) {// 不停的等待客户端的链接
					try {
						Socket socket = serverSocket.accept();
						ClientThread client = new ClientThread(socket);
						client.start();// 开启对此客户端服务的线程
						clients.add(client);
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								userList.add(client.getUser().getName());// 更新在线列表
								txtReceivedMessage.append(client.getUser().getName() + client.getUser().getIp() + "上线!\r\n");
							}
						});	
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
		
		// 为一个客户端服务的线程
		class ClientThread extends Thread {
			private Socket socket;
			private BufferedReader inFromClient;
			private PrintWriter outToClient;
			private User user;
			public BufferedReader getReader() {
				return inFromClient;
			}
			public PrintWriter getWriter() {
				return outToClient;
			}
			public User getUser() {
				return user;
			}
			// 客户端线程的构造方法
			public ClientThread(Socket socket) {
				try {
					this.socket = socket;
					inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					outToClient = new PrintWriter(socket.getOutputStream());
					// 接收客户端的基本用户信息
					String inf = inFromClient.readLine();
					StringTokenizer st = new StringTokenizer(inf, "@");
					user = new User(st.nextToken(), st.nextToken());
					// 反馈连接成功信息
					outToClient.println(user.getName() + user.getIp() + "与服务器连接成功!");
					outToClient.flush();
					// 反馈当前在线用户信息
					if (clients.size() > 0) {
						String temp = "";
						for (int i = clients.size() - 1; i >= 0; i--) {
							temp += (clients.get(i).getUser().getName()  + "/" + clients.get(i).getUser().getIp()) + "@";
						}
						outToClient.println("USERLIST@" + clients.size() + "@" + temp);
						outToClient.flush();
					}
					// 向所有在线用户发送该用户上线命令
					for (int i = clients.size() - 1; i >= 0; i--) {
						clients.get(i).getWriter().println("ADD@" + user.getName() + user.getIp());
						clients.get(i).getWriter().flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 循环接收客户端的消息并进行处理
			public void run() {
				String message = "";
				while (true) {
					try {
						message = inFromClient.readLine();// 接收客户端消息
						if (message.equals("CLOSE"))// 下线命令
						{
							String name = this.getUser().getName();
							String ip = this.getUser().getIp();
							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									txtReceivedMessage.append(name + ip + "下线!\n");
								}
							});
							
							// 断开连接释放资源
							inFromClient.close();
							outToClient.close();
							socket.close();

							// 向所有在线用户发送该用户的下线命令
							for (int i = clients.size() - 1; i >= 0; i--) {
								clients.get(i).getWriter().println(
										"DELETE@" + user.getName());
								clients.get(i).getWriter().flush();
							}

							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									userList.remove(user.getName());// 更新在线列表
								}
							});
							// 停止客户端服务线程
							for (int i = clients.size() - 1; i >= 0; i--) {
								if (clients.get(i).getUser() == user) {
									ClientThread temp = clients.get(i);
									clients.remove(i); // 删除此用户的服务线程
									temp.stop(); //停止这条服务线程
									return;
								}
							}
						} else {
							dispatcherMessage(message);// 转发消息给其他客户端
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// 转发消息
		public void dispatcherMessage(String message) {
			StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
			String command = stringTokenizer.nextToken();
			// 群发
			if(command.equals("ALL"))
			{	
				String source = stringTokenizer.nextToken();
				String content = stringTokenizer.nextToken();
				String amessage = source + ":" + content;
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						txtReceivedMessage.append(amessage + "\n");
					}
				});	
				for (int i = clients.size() - 1; i >= 0; i--) {
				clients.get(i).getWriter().println(amessage);
				clients.get(i).getWriter().flush();
				}
			}
			// 发给一个好友
			else if(command.equals("FRIEND"))
			{
				String source = stringTokenizer.nextToken();
				String destination = stringTokenizer.nextToken();
				String content = stringTokenizer.nextToken();
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						txtReceivedMessage.append(source + "对" + destination + "说:" + content + "\n");
					}
				});	
				for (int i = clients.size() - 1; i >= 0; i--) {
					if(clients.get(i).getUser().getName().equals(destination)){
						clients.get(i).getWriter().println(source + "对你说:" + content);
						clients.get(i).getWriter().flush();
					}
				}
			}
			
		}
		
		
}


package myZZ;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.swt.widgets.List;

public class MyClient {

	protected Shell shell;
	private Text txtServerIP;
	private Text txtPort;
	private Text txtUsername;
	private Text txtPsw;
	private Text txtReceivedMessage;
	private Text txtSendingMsg;
	private Text txtFriend;
	
	Button btnLogin;
	Button btnLoginout;
	Button btnRegister;
	
	private List userList;
	
    private boolean isConnected = false; 
	
	private Socket clientSocket = null;

	private PrintWriter outToServer;  
	private BufferedReader inFromServer;  
	private MessageThread messageThread;
	
	Display display;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MyClient window = new MyClient();
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
		
		
		shell = new Shell();
		shell.setSize(1213, 752);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());
		
		
		Group group = new Group(shell, SWT.NONE);
		group.setText("\u5BA2\u6237\u7AEF\u7CFB\u7EDF");
		group.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(0, 10);
		fd_group.left = new FormAttachment(0, 21);
		fd_group.bottom = new FormAttachment(0, 164);
		fd_group.right = new FormAttachment(100, -24);
		group.setLayoutData(fd_group);
		
		Group group_1 = new Group(shell, SWT.NONE);
		group_1.setText("\u597D\u53CB\u5217\u8868");
		group_1.setLayout(new FormLayout());
		FormData fd_group_1 = new FormData();
		fd_group_1.right = new FormAttachment(group, 200);
		fd_group_1.bottom = new FormAttachment(100, -23);
		fd_group_1.top = new FormAttachment(group, 28);
		fd_group_1.left = new FormAttachment(group, 0, SWT.LEFT);
		group_1.setLayoutData(fd_group_1);
		
		Group group_2 = new Group(shell, SWT.NONE);
		group_2.setText("\u6536\u53D1\u6570\u636E");
		group_2.setLayout(new FormLayout());
		FormData fd_group_2 = new FormData();
		fd_group_2.left = new FormAttachment(group_1, 27);
		fd_group_2.top = new FormAttachment(group_1, 0, SWT.TOP);
		fd_group_2.bottom = new FormAttachment(group_1, 0, SWT.BOTTOM);
		
		userList = new List(group_1, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_userList = new FormData();
		fd_userList.top = new FormAttachment(100, -454);
		fd_userList.right = new FormAttachment(0, 179);
		fd_userList.bottom = new FormAttachment(100, -19);
		fd_userList.left = new FormAttachment(0, 13);
		userList.setLayoutData(fd_userList);
		fd_group_2.right = new FormAttachment(group, 0, SWT.RIGHT);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 24);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("\u670D\u52A1\u5668IP\u5730\u5740\uFF1A");
		
		Label label = new Label(group, SWT.NONE);
		fd_lblNewLabel.left = new FormAttachment(label, 0, SWT.LEFT);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 69);
		fd_label.left = new FormAttachment(0, 27);
		label.setLayoutData(fd_label);
		label.setText("\u7AEF\u53E3\uFF1A");
		
		txtServerIP = new Text(group, SWT.BORDER);
		txtServerIP.setText("127.0.0.1");
		FormData fd_txtServerIP = new FormData();
		fd_txtServerIP.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_txtServerIP.left = new FormAttachment(lblNewLabel, 6);
		fd_txtServerIP.right = new FormAttachment(100, -719);
		txtServerIP.setLayoutData(fd_txtServerIP);
		
		txtPort = new Text(group, SWT.BORDER);
		txtPort.setText("7887");
		FormData fd_txtPort = new FormData();
		fd_txtPort.bottom = new FormAttachment(100, -39);
		fd_txtPort.left = new FormAttachment(0, 133);
		fd_txtPort.right = new FormAttachment(100, -722);
		txtPort.setLayoutData(fd_txtPort);
		
		Label label_1 = new Label(group, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
		fd_label_1.left = new FormAttachment(txtServerIP, 86);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("\u7528\u6237\u540D\uFF1A");
		
		Label label_2 = new Label(group, SWT.NONE);
		FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(label, 0, SWT.TOP);
		fd_label_2.left = new FormAttachment(label_1, 0, SWT.LEFT);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("\u5BC6\u7801\uFF1A");
		
		txtUsername = new Text(group, SWT.BORDER);
		FormData fd_txtUsername = new FormData();
		fd_txtUsername.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
		fd_txtUsername.left = new FormAttachment(label_1, 17);
		fd_txtUsername.right = new FormAttachment(100, -324);
		txtUsername.setLayoutData(fd_txtUsername);
		
		txtPsw = new Text(group, SWT.BORDER | SWT.PASSWORD);
		FormData fd_txtPsw = new FormData();
		fd_txtPsw.right = new FormAttachment(100, -321);
		fd_txtPsw.left = new FormAttachment(label_2, 34);
		fd_txtPsw.top = new FormAttachment(label, 0, SWT.TOP);
		txtPsw.setLayoutData(fd_txtPsw);
		
		//登录账号验证+连接服务器
		btnLogin = new Button(group, SWT.NONE);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//连接服务器，并进入循环接收状态
				String loginMessage = txtUsername.getText() + " " + txtPsw.getText();
		    	Boolean flag1 = false;
		    	int port;  
		    	if (isConnected) {  
	                   JOptionPane.showMessageDialog(null, "已处于连接上状态，不要重复连接!", "错误", JOptionPane.ERROR_MESSAGE);  
	                   return;  
	               }  
				try{ //第一个flag：验证账号密码后返回的值
					String str = "";
					FileInputStream fis = new  FileInputStream("user.txt");
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader br = new BufferedReader(isr);
					while((str=br.readLine())!=null){
						if(str.equals(loginMessage)){
							flag1=true;
							break;
						}	
					}
					if(flag1){ //账号密码正确，连接到服务器
						port = Integer.parseInt(txtPort.getText().trim());     
	                    String serverIP = txtServerIP.getText().trim();  
	                    String name = txtUsername.getText().trim();  
	                    if (name.equals("") || serverIP.equals("")) {  
	                        throw new Exception("账号、服务器IP和端口不能为空!"); 
	                    }  
	                    boolean flag2 = connectServer(port, serverIP, name);  //第二个flag：连接服务器是否成功
	                    if (flag2 == false) {  
	                        throw new Exception("与服务器连接失败!");  
	                    }  
	                    shell.setText(name);  
	                    JOptionPane.showMessageDialog(null, "成功登录!");  
				    	txtUsername.setEditable(false);
				    	txtPsw.setEditable(false);
				    	btnLogin.setEnabled(false);
				    	btnRegister.setEnabled(false);
				    }
					else{
						if(!flag1)
							JOptionPane.showMessageDialog(null, "账号或密码错误");
						else
							JOptionPane.showMessageDialog(null, "连接服务器失败");
					}	
				    br.close();
				    isr.close();
				    fis.close();
				}catch(Exception e1){
				}
			}
		});
		FormData fd_btnLogin = new FormData();
		fd_btnLogin.top = new FormAttachment(lblNewLabel, -5, SWT.TOP);
		fd_btnLogin.left = new FormAttachment(txtUsername, 56);
		btnLogin.setLayoutData(fd_btnLogin);
		btnLogin.setText("\u767B\u5F55");
		
		//注销，与服务器断开连接
		btnLoginout = new Button(group, SWT.NONE);
		btnLoginout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!isConnected) {  
                    JOptionPane.showMessageDialog(null, "已处于断开状态，不要重复断开!", "错误", JOptionPane.ERROR_MESSAGE);  
                    return;  
                }  
                try {  
                    boolean flag = closeConnection();// 断开连接  
                    if (flag == false) {  
                        throw new Exception("断开连接发生异常！");  
                    }
                    JOptionPane.showMessageDialog(null, "注销成功!"); 
                    txtUsername.setEditable(true);
			    	txtPsw.setEditable(true);
			    	btnLogin.setEnabled(true);
			    	btnRegister.setEnabled(true);
			    	txtUsername.setText("");
			    	txtPsw.setText(""); 
			    	txtReceivedMessage.setText("");
			    	userList.removeAll();
                } catch (Exception exc) {  
                    JOptionPane.showMessageDialog(null, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);  
                }  

			}
		});
		fd_btnLogin.right = new FormAttachment(btnLoginout, 0, SWT.RIGHT);
		FormData fd_btnLoginout = new FormData();
		fd_btnLoginout.right = new FormAttachment(100, -176);
		fd_btnLoginout.left = new FormAttachment(txtPsw, 53);
		fd_btnLoginout.top = new FormAttachment(label, -5, SWT.TOP);
		btnLoginout.setLayoutData(fd_btnLoginout);
		btnLoginout.setText("\u6CE8\u9500");
		
		//注册账号
		btnRegister = new Button(group, SWT.NONE);
		btnRegister.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					String username=txtUsername.getText();
					String psw=txtPsw.getText();
					byte[] buffer=new byte[]{};
					String message=username + " " + psw + "\r\n";
					buffer = message.getBytes();
					FileOutputStream out =new FileOutputStream("user.txt",true);
					out.write(buffer);
					JOptionPane.showMessageDialog(null, "注册成功");
					txtUsername.setText("");
					txtPsw.setText("");
					out.close();
				} catch(Exception e3){
				}

			}
		});
		FormData fd_btnRegister = new FormData();
		fd_btnRegister.right = new FormAttachment(btnLoginout, 133, SWT.RIGHT);
		fd_btnRegister.bottom = new FormAttachment(100, -61);
		fd_btnRegister.left = new FormAttachment(btnLoginout, 41);
		btnRegister.setLayoutData(fd_btnRegister);
		btnRegister.setText("\u6CE8\u518C");
		group_2.setLayoutData(fd_group_2);
		
		txtReceivedMessage = new Text(group_2, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		FormData fd_txtReceivedMessage = new FormData();
		fd_txtReceivedMessage.top = new FormAttachment(0, 10);
		fd_txtReceivedMessage.right = new FormAttachment(100, -15);
		fd_txtReceivedMessage.left = new FormAttachment(0, 13);
		txtReceivedMessage.setLayoutData(fd_txtReceivedMessage);
		
		txtSendingMsg = new Text(group_2, SWT.BORDER | SWT.V_SCROLL);
		fd_txtReceivedMessage.bottom = new FormAttachment(100, -257);
		FormData fd_txtSendingMsg = new FormData();
		fd_txtSendingMsg.top = new FormAttachment(txtReceivedMessage, 17);
		fd_txtSendingMsg.right = new FormAttachment(txtReceivedMessage, 0, SWT.RIGHT);
		fd_txtSendingMsg.bottom = new FormAttachment(100, -120);
		fd_txtSendingMsg.left = new FormAttachment(0, 23);
		txtSendingMsg.setLayoutData(fd_txtSendingMsg);
		
		Button btnSend = new Button(group_2, SWT.NONE);
		// 群发消息
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!isConnected) {  
		            JOptionPane.showMessageDialog(null, "还没有连接服务器，无法发送消息！", "错误", JOptionPane.ERROR_MESSAGE);  
		            return;  
		        }  
		        String message = txtSendingMsg.getText().trim();  
		        if (message == null || message.equals("")) {  
		            JOptionPane.showMessageDialog(null, "消息不能为空！", "错误", JOptionPane.ERROR_MESSAGE);  
		            return;  
		        }  
		        sendMessage("ALL@" + shell.getText() + "@" + message); 
		        txtSendingMsg.setText(""); 
			}
		});
		FormData fd_btnSend = new FormData();
		fd_btnSend.left = new FormAttachment(100, -98);
		fd_btnSend.top = new FormAttachment(txtSendingMsg, 49);
		fd_btnSend.right = new FormAttachment(100, -30);
		btnSend.setLayoutData(fd_btnSend);
		btnSend.setText("\u7FA4\u53D1");
		
		txtFriend = new Text(group_2, SWT.BORDER);
		FormData fd_txtFriend = new FormData();
		fd_txtFriend.top = new FormAttachment(btnSend, 2, SWT.TOP);
		fd_txtFriend.right = new FormAttachment(btnSend, -48);
		txtFriend.setLayoutData(fd_txtFriend);
		
		
		Button btnSendFriend = new Button(group_2, SWT.NONE);
		//发信息给好友
		btnSendFriend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!isConnected) {  
		            JOptionPane.showMessageDialog(null, "还没有连接服务器，无法发送消息！", "错误", JOptionPane.ERROR_MESSAGE);  
		            return;  
		        }
		        String message = txtSendingMsg.getText().trim(); 
		        String friend = txtFriend.getText();
		        if (message == null || message.equals("")) {  
		            JOptionPane.showMessageDialog(null, "消息不能为空！", "错误", JOptionPane.ERROR_MESSAGE);  
		            return;  
		        }
		        else if (friend == null || friend.equals("")) {  
		            JOptionPane.showMessageDialog(null, "好友名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);  
		            return;  
		        }  
		        sendMessage("FRIEND@" + shell.getText() + "@" + friend + "@" + message); 
		        txtSendingMsg.setText(""); 
		        txtReceivedMessage.append("我对" + friend + "说:" + message + "\n");
			}
		});
		fd_txtFriend.left = new FormAttachment(btnSendFriend, 17);
		FormData fd_btnSendFriend = new FormData();
		fd_btnSendFriend.right = new FormAttachment(100, -257);
		fd_btnSendFriend.top = new FormAttachment(btnSend, 0, SWT.TOP);
		btnSendFriend.setLayoutData(fd_btnSendFriend);
		btnSendFriend.setText("\u53D1\u9001\u7ED9\uFF1A");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	

    // 连接服务器 
    public boolean connectServer(int port, String serverIP, String name) {
        try {  
            clientSocket = new Socket(serverIP, port);
            outToServer = new PrintWriter(clientSocket.getOutputStream());  
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  
            // 发送客户端用户基本信息(用户名和IP地址)  
            sendMessage(name + "@" + clientSocket.getInetAddress().toString());  
            // 开启接收消息的线程  
            messageThread = new MessageThread(inFromServer, txtReceivedMessage);  
            messageThread.start(); 
            isConnected = true;
            return true;  
        } catch (Exception e) {  
            isConnected = false;
            return false;  
        }  
    }  
    
    //发送消息
    public void sendMessage(String message) {  
        outToServer.println(message);  
        outToServer.flush();  
    }  
    
    //关闭连接
	public boolean closeConnection() {  
        try {  
            sendMessage("CLOSE");// 发送断开连接命令给服务器  
            messageThread.stop();// 停止接受消息线程  
            if (inFromServer != null) {  
                inFromServer.close();  
            }  
            if (outToServer != null) {  
                outToServer.close();  
            }  
            if (clientSocket != null) {  
            	clientSocket.close();  
            }  
            isConnected = false;  
            return true;  
        } catch (IOException e1) {  
            e1.printStackTrace();  
            isConnected = true;  
            return false;  
        }  
    }  
	
	 
    // 不断接收消息的线程  
    class MessageThread extends Thread {  
        private BufferedReader inFromServer;  
        private Text txtReceivedMessage;   
        public MessageThread(BufferedReader inFromServer, Text txtReceivedMessage) {  
            this.inFromServer = inFromServer;  
            this.txtReceivedMessage = txtReceivedMessage;  
        }  
        //由于服务器关闭而被动关闭函数
        public void closeCon() throws Exception {  
        	display.asyncExec(new Runnable() {
				@Override
				public void run() {
                    txtUsername.setEditable(true);
			    	txtPsw.setEditable(true);
			    	btnLogin.setEnabled(true);
			    	btnRegister.setEnabled(true);
			    	txtUsername.setText("");
			    	txtPsw.setText(""); 
			    	userList.removeAll();
				}
			});
            if (inFromServer != null) {  
                inFromServer.close();  
            }  
            if (outToServer != null) {  
                outToServer.close();  
            }  
            if (clientSocket != null) {  
            	clientSocket.close();  
            }  
            isConnected = false;
        }  
        //线程运行
        public void run() {    
            while (true) {  
                try {  
                    final String message = inFromServer.readLine();  
                    StringTokenizer stringTokenizer = new StringTokenizer(  
                            message, "/@");  
                    String command = stringTokenizer.nextToken();//获取命令  
                    // 服务器已关闭
                    if (command.equals("CLOSE"))
                    {  
                    	display.asyncExec(new Runnable() {
							@Override
							public void run() {
								txtReceivedMessage.append("服务器已关闭!\n");
							}
						});
                        closeCon();
                        return;
                    }
                    // 好友上线更新列表  
                    else if (command.equals("ADD")) {
                        final String username = stringTokenizer.nextToken();  
                        final String userIp = stringTokenizer.nextToken();  
                        if (username != null && userIp != null) {  
                            User user = new User(username, userIp);   
                            display.asyncExec(new Runnable() {
								@Override
								public void run() {
									userList.add(user.getName());
									txtReceivedMessage.append(username + "已上线！\n");
								}
							});
                        }  
                     } 
                    // 好友下线更新列表  
                     else if (command.equals("DELETE")) {
                         final String username = stringTokenizer.nextToken();  
                         display.asyncExec(new Runnable() {
							 @Override
							 public void run() {
								 userList.remove(username);
								 txtReceivedMessage.append(username + "已下线！\n");
							 }
						 });
                     }  
                    // 加载在线好友列表   
                    else if (command.equals("USERLIST")) {
                        int size = Integer.parseInt(stringTokenizer.nextToken());  
                        String username = null;  
                        String userIp = null;  
                        for (int i = 0; i < size; i++) {  
                            username = stringTokenizer.nextToken();  
                            userIp = stringTokenizer.nextToken();  
                            User user = new User(username, userIp);   
                            display.asyncExec(new Runnable() {
								@Override
								public void run() {
									userList.add(user.getName());
								}
							});
                              
                        }  
                    }
                    // 普通消息 
                    else { 
                    	display.asyncExec(new Runnable() {
							@Override
							public void run() {
								txtReceivedMessage.append(message + "\n");
							}
                    	});
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }
}


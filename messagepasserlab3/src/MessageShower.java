import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;


//VS4E -- DO NOT REMOVE THIS LINE!
public class MessageShower extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel jLabelProcessName;
	private JTextArea jTextMessage;
	private JScrollPane jScrollPane0;
	private JButton jButton1;
	private JButton jButton0;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JTextField jTextField0;
	private JTextField jTextField1;
	private JTextField jTextField2;
	private JLabel jLabel2;
	private JButton jButton2;
	private JTextPane jTextStatus;
	private JScrollPane jScrollPane1;
	private JButton jButton3;
	private JButton jButton4;
	private JButton jButton5;
	private JLabel jLabel0;
	private JTextArea jTextNodeConfig;
	private JScrollPane jScrollPane2;
	private JLabel jLabel1;
	private JTextArea jTextGroupConfig;
	private JScrollPane jScrollPane3;
	private JLabel jLabel5;
	private JTextArea jTextPeer;
	private JScrollPane jScrollPane4;
	private JLabel jLabel6;
	private JLabel jLabel7;
	private JTextArea jTextSendRule;
	private JScrollPane jScrollPane6;
	private JLabel jLabel8;
	private JTextArea jTextRecvRule;
	private JScrollPane jScrollPane5;
	private JLabel jLabel9;
	private JLabel jLabelStatus;
	private JLabel jLabel10;
	private JLabel jLabel11;
	private JTextField jTextField3;
	private JButton jButton6;
	private String processName;
	private String configFilePath;
	private String clockType;
	private JLabel jLabel12;
	private JLabel jLabel13;
	private JTextField jTextField4;
	private JTextField jTextField5;
	public MessageShower() {
		initComponents();
		this.setTitle("Client Process");
		this.setVisible(true);
	}
	
	public MessageShower(String configFilePath, String clockType) {
		initComponents();
		this.configFilePath = configFilePath;
		this.clockType = clockType;
		this.setTitle("Client Process");
		this.setVisible(true);
	}

	private void initComponents() {
		setTitle("Client Process");
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(getJButton0(), new Constraints(new Leading(640, 100, 10, 10), new Leading(112, 10, 10)));
		add(getJButton1(), new Constraints(new Leading(640, 100, 10, 10), new Leading(152, 10, 10)));
		add(getJButton4(), new Constraints(new Leading(640, 100, 10, 10), new Leading(199, 12, 12)));
		add(getJLabel8(), new Constraints(new Leading(27, 232, 12, 12), new Leading(593, 10, 10)));
		add(getJLabel1(), new Constraints(new Leading(216, 147, 12, 12), new Leading(288, 12, 12)));
		add(getJLabel0(), new Constraints(new Leading(27, 12, 12), new Leading(289, 12, 12)));
		add(getJLabel4(), new Constraints(new Leading(638, 10, 10), new Leading(59, 12, 12)));
		add(getJTextField2(), new Constraints(new Leading(638, 236, 12, 12), new Leading(78, 12, 12)));
		add(getJLabel2(), new Constraints(new Leading(638, 12, 12), new Leading(12, 12, 12)));
		add(getJTextField0(), new Constraints(new Leading(637, 109, 28, 135), new Leading(32, 12, 12)));
		add(getJLabel3(), new Constraints(new Leading(762, 10, 10), new Leading(12, 12, 12)));
		add(getJTextField1(), new Constraints(new Leading(763, 111, 12, 12), new Leading(32, 12, 12)));
		add(getJButton3(), new Constraints(new Leading(772, 98, 10, 10), new Leading(112, 12, 12)));
		add(getJButton2(), new Constraints(new Leading(772, 12, 12), new Leading(156, 10, 10)));
		add(getJButton5(), new Constraints(new Leading(772, 98, 12, 12), new Leading(199, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Leading(27, 584, 12, 12), new Leading(50, 232, 12, 12)));
		add(getJLabel10(), new Constraints(new Leading(27, 12, 12), new Leading(28, 12, 12)));
		add(getJLabel9(), new Constraints(new Leading(517, 12, 12), new Leading(336, 12, 12)));
		add(getJLabelStatus(), new Constraints(new Leading(517, 386, 12, 12), new Leading(312, 12, 12)));
		add(getJLabel6(), new Constraints(new Leading(517, 12, 12), new Leading(289, 12, 12)));
		add(getJLabel5(), new Constraints(new Leading(423, 12, 12), new Leading(287, 12, 12)));
		add(getJLabel7(), new Constraints(new Leading(26, 209, 12, 12), new Leading(470, 10, 10)));
		add(getJScrollPane4(), new Constraints(new Leading(423, 78, 10, 10), new Leading(311, 153, 12, 12)));
		add(getJScrollPane2(), new Constraints(new Leading(26, 189, 10, 10), new Leading(313, 151, 12, 12)));
		add(getJScrollPane3(), new Constraints(new Leading(218, 202, 12, 12), new Leading(313, 151, 12, 12)));
		add(getJScrollPane1(), new Constraints(new Leading(517, 354, 12, 12), new Leading(362, 102, 12, 12)));
		add(getJScrollPane6(), new Constraints(new Leading(27, 843, 12, 12), new Leading(490, 90, 10, 10)));
		add(getJScrollPane5(), new Constraints(new Leading(27, 842, 12, 12), new Leading(612, 105, 10, 10)));
		add(getJLabel12(), new Constraints(new Leading(640, 12, 12), new Leading(239, 12, 12)));
		add(getJLabel13(), new Constraints(new Leading(772, 12, 12), new Leading(239, 12, 12)));
		add(getJTextField5(), new Constraints(new Leading(773, 96, 12, 12), new Leading(257, 10, 10)));
		add(getJTextField4(), new Constraints(new Bilateral(643, 171, 96), new Leading(259, 12, 12)));
		add(getJTextField3(), new Constraints(new Leading(334, 90, 12, 12), new Leading(4, 12, 12)));
		add(getJButton6(), new Constraints(new Leading(433, 12, 12), new Leading(6, 17, 12, 12)));
		add(getJLabel11(), new Constraints(new Leading(234, 94, 12, 12), new Leading(8, 12, 12)));
		add(getJLabelProcessName(), new Constraints(new Leading(234, 167, 12, 12), new Leading(9, 17, 12, 12)));
		setSize(899, 761);
	}

	private JTextField getJTextField5() {
		if (jTextField5 == null) {
			jTextField5 = new JTextField();
			jTextField5.setText("0");
			jTextField5.setBorder(null);
			jTextField5.setEditable(false);
		}
		return jTextField5;
	}

	private JTextField getJTextField4() {
		if (jTextField4 == null) {
			jTextField4 = new JTextField();
			jTextField4.setText("0");
			jTextField4.setBorder(null);
			jTextField4.setEditable(false);
		}
		return jTextField4;
	}

	private JLabel getJLabel13() {
		if (jLabel13 == null) {
			jLabel13 = new JLabel();
			jLabel13.setText("Recv Num :");
		}
		return jLabel13;
	}

	private JLabel getJLabel12() {
		if (jLabel12 == null) {
			jLabel12 = new JLabel();
			jLabel12.setText("Sent Num :");
		}
		return jLabel12;
	}

	private JButton getJButton6() {
		if (jButton6 == null) {
			jButton6 = new JButton();
			jButton6.setText("set");
			jButton6.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton6MouseMouseClicked(event);
				}
			});
		}
		return jButton6;
	}

	private JTextField getJTextField3() {
		if (jTextField3 == null) {
			jTextField3 = new JTextField();
			jTextField3.setText("");
		}
		return jTextField3;
	}

	private JLabel getJLabel11() {
		if (jLabel11 == null) {
			jLabel11 = new JLabel();
			jLabel11.setText("Process Name:");
		}
		return jLabel11;
	}

	private JLabel getJLabel10() {
		if (jLabel10 == null) {
			jLabel10 = new JLabel();
			jLabel10.setText("Messages :");
		}
		return jLabel10;
	}

	private JLabel getJLabelStatus() {
		if (jLabelStatus == null) {
			jLabelStatus = new JLabel();
			jLabelStatus.setText("Idle");
		}
		jLabelStatus.setForeground(Color.RED);
		return jLabelStatus;
	}

	private JLabel getJLabel9() {
		if (jLabel9 == null) {
			jLabel9 = new JLabel();
			jLabel9.setText("Log :");
		}
		return jLabel9;
	}

	private JScrollPane getJScrollPane5() {
		if (jScrollPane5 == null) {
			jScrollPane5 = new JScrollPane();
			jScrollPane5.setViewportView(getJTextRecvRule());
		}
		return jScrollPane5;
	}

	private JTextArea getJTextRecvRule() {
		if (jTextRecvRule == null) {
			jTextRecvRule = new JTextArea();
			jTextRecvRule.setText("");
		}
		return jTextRecvRule;
	}

	private JLabel getJLabel8() {
		if (jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText("Receive rules from the config file are :");
		}
		return jLabel8;
	}

	private JScrollPane getJScrollPane6() {
		if (jScrollPane6 == null) {
			jScrollPane6 = new JScrollPane();
			jScrollPane6.setViewportView(getJTextSendRule());
		}
		return jScrollPane6;
	}

	private JTextArea getJTextSendRule() {
		if (jTextSendRule == null) {
			jTextSendRule = new JTextArea();
			jTextSendRule.setText("");
		}
		return jTextSendRule;
	}

	private JLabel getJLabel7() {
		if (jLabel7 == null) {
			jLabel7 = new JLabel();
			jLabel7.setText("Send rules from the config file are :");
		}
		return jLabel7;
	}

	private JLabel getJLabel6() {
		if (jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setText("Status :");
		}
		return jLabel6;
	}

	private JScrollPane getJScrollPane4() {
		if (jScrollPane4 == null) {
			jScrollPane4 = new JScrollPane();
			jScrollPane4.setViewportView(getJTextPeer());
		}
		return jScrollPane4;
	}

	private JTextArea getJTextPeer() {
		if (jTextPeer == null) {
			jTextPeer = new JTextArea();
		}
		return jTextPeer;
	}

	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Peers are :");
		}
		return jLabel5;
	}

	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setViewportView(getJTextArea2());
		}
		return jScrollPane3;
	}

	private JTextArea getJTextArea2() {
		if (jTextGroupConfig == null) {
			jTextGroupConfig = new JTextArea();
			jTextGroupConfig.setText("");
		}
		return jTextGroupConfig;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Voters are :");
		}
		return jLabel1;
	}

	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getJTextArea1());
		}
		return jScrollPane2;
	}

	private JTextArea getJTextArea1() {
		if (jTextNodeConfig == null) {
			jTextNodeConfig = new JTextArea();
			jTextNodeConfig.setText("");
		}
		return jTextNodeConfig;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Nodes in config file are :");
		}
		return jLabel0;
	}

	private JButton getJButton5() {
		if (jButton5 == null) {
			jButton5 = new JButton();
			jButton5.setText("Release");
			jButton5.setEnabled(false);
			jButton5.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton5MouseMouseClicked(event);
				}
			});
		}
		return jButton5;
	}

	private JButton getJButton4() {
		if (jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setText("GetMutex");
			jButton4.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					try {
						setJTextStatus("Requesting mutex!");
						jButton0.setEnabled(false);
						jButton1.setEnabled(false);
						jButton2.setEnabled(false);
						jButton3.setEnabled(false);
						jButton4.setEnabled(false);
						setJLabelStatus("WAITING MUTEX...");
						setJTextMessage("YOU ARE WAITING THE MUTEX...\n");
						jButton4MouseMouseClicked(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return jButton4;
	}

	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText("Multicast");
			jButton3.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton3MouseMouseClicked(event);
				}
			});
		}
		return jButton3;
	}

	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJTextStatus());
		}
		return jScrollPane1;
	}

	private JTextPane getJTextStatus() {
		if (jTextStatus == null) {
			jTextStatus = new JTextPane();
			jTextStatus.setForeground(Color.BLUE);
		}
		return jTextStatus;
	}

	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("TimeStamp");
			jButton2.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton2MouseMouseClicked(event);
				}
			});
		}
		return jButton2;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("dest");
		}
		return jLabel2;
	}

	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField();
			jTextField2.setText("");
		}
		return jTextField2;
	}

	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setText("");
		}
		return jTextField1;
	}

	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
			jTextField0.setText("");
		}
		return jTextField0;
	}

	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("message");
		}
		return jLabel4;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("kind");
		}
		return jLabel3;
	}

	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("Send");
			jButton0.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton0MouseMouseClicked(event);
				}
			});
		}
		return jButton0;
	}

	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Receive");
			jButton1.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton1MouseMouseClicked(event);
				}
			});
		}
		return jButton1;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJTextMessage());
		}
		return jScrollPane0;
	}

	private JTextArea getJTextMessage() {
		if (jTextMessage == null) {
			jTextMessage = new JTextArea();
			jTextMessage.setText("");
		}
		return jTextMessage;
	}

	private JLabel getJLabelProcessName() {
		if (jLabelProcessName == null) {
			jLabelProcessName = new JLabel();
			jLabelProcessName.setText("");
		}
		return jLabelProcessName;
	}
	
	public String getJTextContent() {
		return this.jTextMessage.getText();
	}
	
	public String getJStatusContent() {
		return this.jTextStatus.getText();
	}
	
	public void setJTextMessage(String str) {
		this.jTextMessage.append(str);
		jTextMessage.setCaretPosition(jTextMessage.getText().length());
	}
	
	public void setJLabelProcessName(String str) {
		this.jLabelProcessName.setText(str);
	}
	
	public void setJTextStatus(String str) {
		StringBuffer strbuf = new StringBuffer(jTextStatus.getText());
		strbuf.append(str);
		this.jTextStatus.setText(strbuf.toString());
		jTextStatus.setCaretPosition(jTextStatus.getText().length());
	}

	private void jButton1MouseMouseClicked(MouseEvent event) {
		TimestampedMessage m = null;
		m = (TimestampedMessage)Client.getMp().receive();
		if (m != null) {
			setJTextMessage(m.print());
		} else {
			this.setJLabelStatus("No new messages!");
		}
	}

	private void jButton0MouseMouseClicked(MouseEvent event) {
		String dest = this.jTextField0.getText();
		String kind = this.jTextField1.getText();
		String message = this.jTextField2.getText();
		
		TimestampedMessage m = new TimestampedMessage(dest, kind, message);
		boolean ret = Client.getMp().send(m);
		if (ret == true) {
			setJLabelStatus("Message sent!");
		}
		else {
			setJLabelStatus("Failed to send!");
		}
	}

	private void jButton3MouseMouseClicked(MouseEvent event) {
		String group = this.jTextField0.getText();
		String kind = this.jTextField1.getText();
		String message = this.jTextField2.getText();

		TimestampedMessage m = new TimestampedMessage("", kind, message);
		Client.getMcs().rMulticast(group, m);
		setJLabelStatus("Message sent!");
	}

	private void jButton2MouseMouseClicked(MouseEvent event) {
		Timestamp tm = null;
		try {
			tm = Client.getClock().getTimestamp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(tm!= null)
		{
			setJLabelStatus("Print timestamp!");
			this.setJTextMessage("Here is the current timestamp: \n\t" + tm.toString() + "\n");
		}
		else
		{
			this.setJLabelStatus("No timestamp is available!");
		}
	}

	private void jButton4MouseMouseClicked(MouseEvent event) throws Exception {
		Client.getMetux().requestMutex();		
		this.setJLabelStatus("GOT MUTEX!!!!");
		this.setJTextMessage("YOU HAVE THE MUTEX!!!!\n");
		jButton5.setEnabled(true);
	}

	private void jButton5MouseMouseClicked(MouseEvent event) {
		Client.getMetux().releaseMutex();
		this.setJLabelStatus("MUTEX RELEASED!!!");
		this.setJTextMessage("YOU HAVE RELEASED THE MUTEX!!!!!\n");
		jButton5.setEnabled(false);
		jButton0.setEnabled(true);
		jButton1.setEnabled(true);
		jButton2.setEnabled(true);
		jButton3.setEnabled(true);
		jButton4.setEnabled(true);
	}
	
	public void setJTextNodeConfig(String str) {
		this.jTextNodeConfig.setText(str);
	}
	
	public void setJTextGroupConfig(String str) {
		this.jTextGroupConfig.setText(str);
	}
	
	public void setJTextPeer(String str) {
		this.jTextPeer.setText(str);
	}
	
	public void setJTextSendRule(String str) {
		jTextSendRule.setText(str);
	}
	
	public void setJTextRecvRule(String str) {
		jTextRecvRule.setText(str);
	}
	
	public void setJLabelStatus(String str) {
		jLabelStatus.setText(str);
	}

	private void jButton6MouseMouseClicked(MouseEvent event) {
		processName = jTextField3.getText();
		jTextField3.setVisible(false);		
		try {
			setJLabelProcessName("Process Name : " + processName);			
			setJTextStatus("");
			Client.initClock(configFilePath, processName, clockType);
			
			Client.setMp(new MessagePasser(configFilePath, processName, Client.getClock()));
			Client.setMcs(new MulticastService(configFilePath, processName, Client.getMp()));
			Client.getMp().setMulticastService(Client.getMcs());
			
			Client.setMetux(new MutualExclusionService(processName, Client.getMp(), Client.getMcs()));
			Client.getMp().setMutexService(Client.getMetux());
			Client.getMcs().setMutexService(Client.getMetux());
			
		} catch (ArrayIndexOutOfBoundsException e) {
			setJTextStatus("Invalid args : Expecting Client configFilePath processName clockType");
		} catch (Exception e) {
			e.printStackTrace();
			setJTextStatus("Exception : " + e.getMessage());
		}
		jLabel11.setVisible(false);
		jButton6.setVisible(false);
		UpdatingCount UC = new UpdatingCount();
		UC.start();
	}
	
	public void setJTextField5(String str) {
		jTextField5.setText(str);
	}

	public void setJTextField4(String str) {
		jTextField4.setText(str);
	}
}

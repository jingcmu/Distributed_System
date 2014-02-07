import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;

public class ConsoleApplication {
	public static void main(String args[]) {
		if (args.length != 4) {
			System.out
					.println("Usage : Application <Configuration File Name> <Local Name> <Clock Service Type> <Logger Name>");
			System.exit(1);
		} else {
			ClockService.createInstance(args[0], args[1], args[2]);
			MessagePasser.createInstance(args[0], args[1]);
		}

		MessagePasser msgPasser = MessagePasser.getInstance();
		if (msgPasser == null) {
			System.out.println("Failed to create MessagePasser.");
			System.exit(1);
		}
		List<Node> nodes = msgPasser.getNodeList();

		System.out.println("Welcome to the Communicator!");
		System.out.println("---------------------");
		Scanner reader = new Scanner(System.in);
		while (true) {
			try {
				System.out
						.println("Please select one of the following options");
				System.out.println("1> Send");
				System.out.println("2> Send and Log");
				System.out.println("3> Receive");
				System.out.println("4> Receive and Log");
				System.out.println("5> TimeStamp");
				System.out.println("6> Exit");
				System.out.print("Select option: ");

				int option = reader.nextInt();
				reader.nextLine();

				switch (option) {
				case 1: {
					System.out.println();
					System.out.println("Please select a destination");
					int i = 0;
					for (Node node : nodes) {
						i++;
						System.out.println(i + "> " + node.getName());
					}
					option = 0;
					while (option < 1 || option > nodes.size()) {
						System.out.print("Select destination: ");
						option = reader.nextInt();
					}
					reader.nextLine();
					System.out.println();

					String kind = "";
					while (kind.toString().equals("")) {
						System.out.print("Kind of message: ");
						kind = reader.nextLine();
					}
					System.out.println();

					String message = "";
					while (message.toString().equals("")) {
						System.out.print("Message text: ");
						message = reader.nextLine();
					}
					System.out.println();

					TimeStampedMessage sendMsg = new TimeStampedMessage(nodes
							.get(option - 1).getName(), kind, message);
					msgPasser.send(sendMsg);
					System.out.println("Finished sending message.");
					System.out.println();
					break;
				}
				case 2: {
					System.out.println();
					System.out.println("Please select a destination");
					int i = 0;
					for (Node node : nodes) {
						i++;
						System.out.println(i + "> " + node.getName());
					}
					option = 0;
					while (option < 1 || option > i) {
						System.out.print("Select destination: ");
						option = reader.nextInt();
					}
					reader.nextLine();
					System.out.println();

					String kind = "";
					while (kind.toString().equals("")) {
						System.out.print("Kind of message: ");
						kind = reader.nextLine();
					}
					System.out.println();

					String message = "";
					while (message.toString().equals("")) {
						System.out.print("Message text: ");
						message = reader.nextLine();
					}
					System.out.println();

					TimeStampedMessage sendMsg = new TimeStampedMessage(nodes
							.get(option - 1).getName(), kind, message);
					String action = msgPasser.send(sendMsg);
					System.out.println("Finished sending message.");
					
					//If the msg's kind is delay, the log message should also be delayed
					//If the msg's kind is drop, then no log message 
					//If the msg's kind is dup, then log message should be duplicate, but here we only log the first message
					if (action == null || !action.equals(Constants.actionDelay)) {
						Message logSendMsg = new Message(args[3], Constants.logSendKind, sendMsg);
						msgPasser.send(logSendMsg);
					}
					else {
						//Message logSendMsg = new Message(args[3], Constants.logSendKind, sendMsg);
						//msgPasser.add2sendDelayBuf(logSendMsg);
					}
					
					System.out.println("Finished logging message.");
					System.out.println();
					break;
				}
				case 3: {
					System.out.println();
					TimeStampedMessage recvMsg = (TimeStampedMessage) msgPasser
							.receive();
					if (recvMsg != null) {
						System.out.println("Src: " + recvMsg.getSrc());
						System.out.println("Dest: " + recvMsg.getDest());
						System.out.println("Kind: " + recvMsg.getKind());
						System.out.println("Data: " + recvMsg.getData());
						System.out.println("SeqNum: " + recvMsg.getSeqNum());
						System.out.println("Dupe: " + recvMsg.isDupe());
						System.out.println("TimeStamp: "
								+ recvMsg.getTimeStamp().printTimeStamp());
					} else {
						System.out.println("No pending messages.");
					}
					System.out.println();
					break;
				}
				case 4: {
					System.out.println();
					TimeStampedMessage recvMsg = (TimeStampedMessage) msgPasser
							.receive();
					if (recvMsg != null) {
						System.out.println("Src: " + recvMsg.getSrc());
						System.out.println("Dest: " + recvMsg.getDest());
						System.out.println("Kind: " + recvMsg.getKind());
						System.out.println("Data: " + recvMsg.getData());
						System.out.println("SeqNum: " + recvMsg.getSeqNum());
						System.out.println("Dupe: " + recvMsg.isDupe());
						System.out.println("TimeStamp: "
								+ recvMsg.getTimeStamp().printTimeStamp());
						
						Message logSendMsg = new Message(args[3], Constants.logRecvKind, recvMsg);
						msgPasser.send(logSendMsg);
						System.out.println();
						System.out.println("Finished logging message.");
					} else {
						System.out.println("No pending messages.");
					}
					System.out.println();
					break;
				}
				case 5: {
					System.out.println();
					System.out.println("TimeStamp: "
							+ ClockService.getInstance().getTimeStamp()
									.printTimeStamp());
					System.out.println();
					break;
				}
				case 6: {
					System.out.println();
					System.out.println("==========================");
					System.out.println("  Have a Good Day! :)");
					System.out.println("==========================");
					System.exit(0);
				}
				default: {
					System.out.println("Invalid input. Please try again");
					System.out.println();
				}
				}
			} catch (Exception e) {
				reader = new Scanner(System.in);
				System.out.println("Invalid input! Please try again");
				System.out.println();
			}
		}
	}
}

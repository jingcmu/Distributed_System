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
			try {
				MessagePasser.createInstance(args[0], args[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		MessagePasser msgPasser = MessagePasser.getInstance();
		if (msgPasser == null) {
			System.out.println("Failed to create MessagePasser.");
			System.exit(1);
		}
		List<Node> nodes = msgPasser.getNodeList();
		List<Group> groups = msgPasser.getGroupList();

		System.out.println("Welcome to the Communicator!");
		System.out.println("---------------------");
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);
		while (true) {
			try {
				System.out.print("This is " + args[1] + "===>");
				System.out.println("Please select one of the following options");
				System.out.println("1> Send to single dest");
				System.out.println("2> Send to single dest and Log to '" + args[3] + "' using " + args[2] + " clock");
				System.out.println("3> Send to a group");
				System.out.println("4> Send to a group and Log to '" + args[3] + "' using " + args[2] + " clock");
				System.out.println("5> Receive");
				System.out.println("6> Receive and Log to '" + args[3] + "' using " + args[2] + " clock");
				System.out.println("7> TimeStamp");
				System.out.println("8> Exit");
				System.out.print("Select option: ");

				int option = reader.nextInt();
				reader.nextLine();
				
				if(option == 1 || option == 2) {
					System.out.println();
					System.out.println("Please select a destination");
					int i = 0;
					for (Node node : nodes) {
						i++;
						System.out.println(i + "> " + node.getName());
					}
					int optionTmp = 0;
					while (optionTmp < 1 || optionTmp > nodes.size()) {
						System.out.print("Select destination: ");
						optionTmp = reader.nextInt();
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

					TimeStampedMessage TSMsg = new TimeStampedMessage(nodes.get(optionTmp - 1).getName(), kind, message);
					msgPasser.send(TSMsg);
					System.out.println("Finished sending message.");
					if(option == 2) {
						Message logSendMsg = new Message(args[3], Constants.logSendKind, TSMsg);
						msgPasser.send(logSendMsg);
						System.out.println("Finished logging message.");
					}
					System.out.println();
				}
				else if(option == 3 || option == 4) {
					System.out.println();
					System.out.println("Please select a destination");
					int i = 0;
					for (Group g : groups) {
						i++;
						System.out.println(i + "> " + g.getName());
					}
					int optionTmp = 0;
					while (optionTmp < 1 || optionTmp > groups.size()) {
						System.out.print("Select destination: ");
						optionTmp = reader.nextInt();
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

					TimeStampedMessage TSMsg = new TimeStampedMessage(groups.get(optionTmp - 1).getName(), kind, message);
					msgPasser.send(TSMsg);
					System.out.println("Finished sending message.");
					if(option == 4) {
						Message logSendMsg = new Message(args[3], Constants.logSendKind, TSMsg);
						msgPasser.send(logSendMsg);
						System.out.println("Finished logging message.");
					}
					System.out.println();
				}
				else if(option == 5 || option == 6) {
					TimeStampedMessage recvMsg = (TimeStampedMessage) msgPasser.receive();
					if (recvMsg != null) {
						System.out.println("Src: " + recvMsg.getSrc());
						System.out.println("Dest: " + recvMsg.getDest());
						System.out.println("Kind: " + recvMsg.getKind());
						System.out.println("Data: " + recvMsg.getData());
						System.out.println("SeqNum: " + recvMsg.getSeqNum());
						System.out.println("Dupe: " + recvMsg.isDupe());
						System.out.println("TimeStamp: " + recvMsg.getTimeStamp().printTimeStamp());
						if(option == 6) {
							Message logSendMsg = new Message(args[3], Constants.logRecvKind, recvMsg);
							msgPasser.send(logSendMsg);
							System.out.println();
							System.out.println("Finished logging message.");
						}
					} else {
						System.out.println("No pending messages.");
					}
					System.out.println();
				}
				else if(option == 7) {
					System.out.println();
					System.out.println("TimeStamp: "
							+ ClockService.getInstance().getTimeStamp()
									.printTimeStamp());
					System.out.println();
				}
				else if(option == 8) {
					System.out.println();
					System.out.println("==========================");
					System.out.println("  Have a Good Day! :)");
					System.out.println("==========================");
					System.exit(0);
				}
				else {
					System.out.println("Invalid input. Please try again");
					System.out.println();
				}
			} catch (Exception e) {
				reader = new Scanner(System.in);
				System.out.println("Invalid input. Please try again");
				System.out.println();
				e.printStackTrace();
			}
		}
	}
}

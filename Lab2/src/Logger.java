import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Logger {
	public static void main(String args[]) {
		if(args.length != 3) {
			System.out
					.println("Usage : Application <Configuration File Name> <Local Name> <Clock Service Type>");
			System.exit(1);
		} else {
			ClockService.createInstance(args[0], args[1], args[2]);
			MessagePasser.createInstance(args[0], args[1]);
		}
		
		MessagePasser msgPasser = MessagePasser.getInstance();
		if(msgPasser == null) {
			System.out.println("Failed to create MessagePasser.");
			System.exit(1);
		}
		List<Message> logs = new LinkedList<Message>();
		
		System.out.println("Welcome to the Logger!");
		System.out.println("---------------------");
		Scanner reader = new Scanner(System.in);
		while (true) {
			try {
				System.out
						.println("Please select one of the following options");
				System.out.println("1> Show logs");
				System.out.println("2> Exit");
				System.out.print("Select option: ");

				int option = reader.nextInt();
				reader.nextLine();

				switch (option) {
				case 1:
					System.out.println();
					Message msg = null;
					while((msg = msgPasser.receive()) != null) {
						logs.add(msg);
					}
					Collections.sort(logs, new LogMessageComparator());
					Message prevMsg = null;
					for(Message m : logs) {
						TimeStampedMessage logMsg = (TimeStampedMessage)m.getData();
						if(prevMsg == null || logMsg.getTimeStamp().compareTo(((TimeStampedMessage)prevMsg.getData()).getTimeStamp()) != 0) {
							System.out.println("=====================");
						} else {
							System.out.println("---------------------");
						}
						System.out.println("Log Src: " + m.getSrc());
						System.out.println("Log Kind: " + m.getKind());
						System.out.println("Log Message:");
						System.out.println("\tSrc: " + logMsg.getSrc());
						System.out.println("\tDest: " + logMsg.getDest());
						System.out.println("\tKind: " + logMsg.getKind());
						System.out.println("\tData: " + logMsg.getData());
						System.out.println("\tSeqNum: " + logMsg.getSeqNum());
						System.out.println("\tDupe: " + logMsg.isDupe());
						System.out.println("\tTimeStamp: " + logMsg.getTimeStamp().printTimeStamp());
						prevMsg = m;
					}
					System.out.println("=====================");
					System.out.println();
					break;
					
				case 2:
					System.out.println();
					System.out.println("==========================");
					System.out.println("  Have a Good Day! :)");
					System.out.println("==========================");
					System.exit(0);
					
				default:
					System.out.println("Invalid input. Please try again");
					System.out.println();
				}
			} catch(Exception e) {
				reader = new Scanner(System.in);
				System.out.println("Invalid input. Please try again");
				System.out.println();
			}
		}
	}
}

class LogMessageComparator implements Comparator<Message> {
	public int compare(Message m1, Message m2) {
		return ((TimeStampedMessage)m1.getData()).getTimeStamp().compareTo(((TimeStampedMessage)m2.getData()).getTimeStamp());
	}
}

package Lab0;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ConfigChecker {
	long lastModified;
	
	String configurationFile;
	
	String local_name;
	
	MessagePasser messagePasser;

	public ConfigChecker(String configurationFile, String local_name, MessagePasser messagePasser) throws UnknownHostException, IOException {
		ConfigHandler.config(configurationFile, local_name, messagePasser);
		this.configurationFile = configurationFile;
		this.local_name = local_name;
		this.messagePasser = messagePasser;
	}

	public void check() {
		Timer timer = new Timer();
		final File conFile = new File(configurationFile);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (lastModified != conFile.lastModified()) {
					lastModified = conFile.lastModified();
					try {
						ConfigHandler.config(configurationFile, local_name, messagePasser);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, new Date(System.currentTimeMillis()), 200);
	}
}
	
		

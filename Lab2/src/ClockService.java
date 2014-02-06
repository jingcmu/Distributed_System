import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public abstract class ClockService {
	private static ClockService clockService;	// Singleton pattern
	protected String configFileName;
	protected String localName;
	protected Configuration config;
	
	public ClockService(String configFileName, String localName) {
		super();
		
		this.configFileName = configFileName;
		this.localName = localName;
		this.parseConfigFile(this.configFileName);
		if (this.config == null) {
			System.out.println("Failed to create ClockService.");
			System.exit(1);
		}
	}
	
	private void parseConfigFile(String fileName) {
		try {
			File file = new File(fileName);
			InputStream is = new FileInputStream(file);
			Yaml yaml = new Yaml(new Constructor(Configuration.class));
			this.config = (Configuration) yaml.load(is);
		} catch (Exception e) {
			System.out.println("Failed to parse configuration file.");
		}
	}
	
	public abstract TimeStamp getTimeStamp();
	
	public abstract void updateTimeStamp(TimeStamp timeStamp);
	
	/*
	 * Create an instance of ClockService based on clockServiceType as singleton pattern
	 */
	public static void createInstance(String configFileName, String localName, String clockServiceType) {
		if(clockServiceType.equals(Constants.logicalClockService)) {
			ClockService.clockService = new LogicalClockService(configFileName, localName);
		} else if(clockServiceType.equals(Constants.vectorClockService)) {
			ClockService.clockService = new VectorClockService(configFileName, localName);
		}
	}
	
	/*
	 * Get an instance of ClockService as singleton pattern
	 * This should be invoked after createInstance is invoked
	 */
	public static ClockService getInstance() {
		return ClockService.clockService;
	}
}

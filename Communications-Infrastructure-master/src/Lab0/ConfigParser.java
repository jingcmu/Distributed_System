package Lab0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import distSysLab0.RuleBean.RuleAction;

public class ConfigParser {
    public static int NUM_NODE;
    public static String configurationFile;

    /**
     * Read configuration part in config file.
     */
    public static HashMap<String, NodeBean> readConfig() throws UnknownHostException {
        HashMap<String, NodeBean> nodeList = new HashMap<String, NodeBean>();
        Map<String, ArrayList<Map<String, Object>>> obj = init();

        for(Map.Entry<String, ArrayList<Map<String, Object>>> entrys : obj.entrySet()) {
            Iterator<Map<String, Object>> i = entrys.getValue().iterator();
            while(i.hasNext()) {
                Map<String, Object> node = (Map<String, Object>) i.next();
                NodeBean bean = new NodeBean();
                for(Map.Entry<String, Object> entry : node.entrySet()) {
                    if(entry.getKey().equalsIgnoreCase("Name"))
                        bean.setName(entry.getValue().toString());
                    if(entry.getKey().equalsIgnoreCase("IP"))
                        bean.setIp(entry.getValue().toString());
                    if(entry.getKey().equalsIgnoreCase("Port"))
                        bean.setPort(Integer.parseInt((entry.getValue().toString())));
                }
                nodeList.put(bean.getName(), bean);
            }
        }
        return nodeList;
    }

    /**
     * Read send rule part in config file.
     */
    public static ArrayList<RuleBean> readSendRules() {
        ArrayList<RuleBean> sendRules = new ArrayList<RuleBean>();
        Map<String, ArrayList<Map<String, Object>>> obj = init();

        for(Map.Entry<String, ArrayList<Map<String, Object>>> entrys : obj.entrySet()) {
            Iterator<Map<String, Object>> i = entrys.getValue().iterator();
            if(!entrys.getKey().equals("sendRules")) continue;
            while (i.hasNext()) {
                Map<String, Object> details = (Map<String, Object>) i.next();
                RuleBean bean = new RuleBean();
                for (Map.Entry<String, Object> innerdetails : details.entrySet()) {
                    if (innerdetails.getKey().equalsIgnoreCase("Action")) {
                        if (innerdetails.getValue().toString().equalsIgnoreCase("Drop"))
                            bean.setAction(RuleAction.DROP);
                        if (innerdetails.getValue().toString().equalsIgnoreCase("Delay"))
                            bean.setAction(RuleAction.DELAY);
                        if (innerdetails.getValue().toString().equalsIgnoreCase("Duplicate"))
                            bean.setAction(RuleAction.DUPLICATE);
                    }

                    if (innerdetails.getKey().equalsIgnoreCase("Src")) {
                        bean.setSrc(innerdetails.getValue().toString());
                    }
                    if (innerdetails.getKey().equalsIgnoreCase("Dest")) {
                        bean.setDest(innerdetails.getValue().toString());
                    }
                    if (innerdetails.getKey().equalsIgnoreCase("Kind")) {
                        bean.setKind(innerdetails.getValue().toString());
                    }
                    if (innerdetails.getKey().equalsIgnoreCase("seqNum")) {
                        bean.setSeqNum((int)innerdetails.getValue());
                    }
                }
                sendRules.add(bean);
            }
        }
        return sendRules;
    }

    /**
     * Read receive rule part in config file.
     */
    public static ArrayList<RuleBean> readRecvRules() {
        ArrayList<RuleBean> recvRules = new ArrayList<RuleBean>();
        Map<String, ArrayList<Map<String, Object>>> obj = init();

        for(Map.Entry<String, ArrayList<Map<String, Object>>> entrys : obj.entrySet()) {
            Iterator<Map<String, Object>> i = entrys.getValue().iterator();
            if(!entrys.getKey().equals("receiveRules")) continue;
            while (i.hasNext()) {
                Map<String, Object> details = (Map<String, Object>) i.next();
                RuleBean bean = new RuleBean();
                for (Map.Entry<String, Object> entry : details.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("Action")) {
                        if (entry.getValue().toString().equalsIgnoreCase("Drop"))
                            bean.setAction(RuleAction.DROP);
                        if (entry.getValue().toString().equalsIgnoreCase("Delay"))
                            bean.setAction(RuleAction.DELAY);
                        if (entry.getValue().toString().equalsIgnoreCase("Duplicate"))
                            bean.setAction(RuleAction.DUPLICATE);
                    }

                    if (entry.getKey().equalsIgnoreCase("Src")) {
                        bean.setSrc(entry.getValue().toString());
                    }
                    if (entry.getKey().equalsIgnoreCase("Dest")) {
                        bean.setDest(entry.getValue().toString());
                    }
                    if (entry.getKey().equalsIgnoreCase("Kind")) {
                        bean.setKind(entry.getValue().toString());
                    }
                    if (entry.getKey().equalsIgnoreCase("seqNum")) {
                        bean.setSeqNum((int)entry.getValue());
                    }
                    if (entry.getKey().equalsIgnoreCase("Duplicate")) {
                        if((entry.getValue()).toString().equalsIgnoreCase("True")) {
                            bean.setDuplicate(true);
                        }
                        else if((entry.getValue()).toString().equalsIgnoreCase("False")) {
                            bean.setDuplicate(false);
                        }
                    }
                }
                recvRules.add(bean);
            }
        }
        return recvRules;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ArrayList<Map<String, Object>>> init() {
        Map<String, ArrayList<Map<String, Object>>> raw = null;
        try {
            // Reads the file and builds configure map
            Yaml yaml = new Yaml();
            File optionFile = new File(configurationFile);
            FileReader fr = new FileReader(optionFile);
            BufferedReader br = new BufferedReader(fr);
            raw =  (Map<String, ArrayList<Map<String, Object>>>) yaml.load(br);
        }
        catch (Exception e) {
            System.out.println("File Read Error: " + e.getMessage());
        }
        return raw;
    }

    /**
     * Generate checksum array of a input file.
     */
    private static byte[] createChecksum(String filename) {
        InputStream fis;
        MessageDigest complete = null;

        try {
            fis = new FileInputStream(filename);

            byte[] buffer = new byte[1024];
            complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            fis.close();
        }
        catch (IOException | NoSuchAlgorithmException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return complete.digest();
    }

    /**
     * Generate MD5 value of a input file.
     * @param filename The input file.
     * @return The MD5 value of input file.
     */
    public synchronized static String getMD5Checksum(String filename) {
        byte[] b;
        String result = "";
        b = createChecksum(filename);

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16)
                    .substring(1);
        }

        return result;
    }
}

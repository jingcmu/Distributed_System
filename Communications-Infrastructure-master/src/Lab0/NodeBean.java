package Lab0;

import java.io.Serializable;

public class NodeBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String ip;
    private int port;

    public NodeBean() {

    }

    public NodeBean(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return name + " " + ip + " " + port;
    }
}

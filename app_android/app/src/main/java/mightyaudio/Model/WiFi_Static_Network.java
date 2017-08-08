package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class WiFi_Static_Network extends MightyObject
{
    public String IPAddress;
    public String DNSAddress;
    public String NetworkMask;
    public String GatewayAddress;

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getDNSAddress() {
        return DNSAddress;
    }

    public void setDNSAddress(String DNSAddress) {
        this.DNSAddress = DNSAddress;
    }

    public String getNetworkMask() {
        return NetworkMask;
    }

    public void setNetworkMask(String networkMask) {
        NetworkMask = networkMask;
    }

    public String getGatewayAddress() {
        return GatewayAddress;
    }

    public void setGatewayAddress(String gatewayAddress) {
        GatewayAddress = gatewayAddress;
    }


    public WiFi_Static_Network()
    {
        IPAddress = "192.168.1.110";
        DNSAddress = "255.255.0.0";
        NetworkMask = "255.255.255.255";
        GatewayAddress = "192.168.1.0";
    }

    public WiFi_Static_Network(String IPAddress, String DNSAddress, String networkMask, String gatewayAddress)
    {
        this.IPAddress = IPAddress;
        this.DNSAddress = DNSAddress;
        NetworkMask = networkMask;
        GatewayAddress = gatewayAddress;
    }


}

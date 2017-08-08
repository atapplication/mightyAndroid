package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class Power extends MightyObject
{
    public int power_mode;
    public int sleep_timeout;

    public int getPower_mode() {
        return power_mode;
    }

    public void setPower_mode(int power_mode) {
        this.power_mode = power_mode;
    }

    public int getSleet_timeout() {
        return sleep_timeout;
    }

    public void setSleet_timeout(int sleet_timeout) {
        this.sleep_timeout = sleet_timeout;
    }

    public Power()
    {
        power_mode = 40;
        sleep_timeout = 70;
    }

    public Power(int power_mode, int sleet_timeout) {
        this.power_mode = power_mode;
        this.sleep_timeout = sleet_timeout;
    }
}

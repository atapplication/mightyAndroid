package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class Battery_Info extends MightyObject
{
    public int Status;
    public int Capacity;
    public int AvailablePercentage;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public int getAvailablePercentage() {
        return AvailablePercentage;
    }

    public void setAvailablePercentage(int availablePercentage) {
        AvailablePercentage = availablePercentage;
    }
}

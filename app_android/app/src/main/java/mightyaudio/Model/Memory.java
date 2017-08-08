package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class Memory extends MightyObject
{
   public  float Ram_total;
   public  float Storage_total;
   public  float Ram_free;
   public  float Storage_free;
   public  int storage =0;

    public float getRam_total() {
        return Ram_total;
    }

    public void setRam_total(int ram_total) {
        Ram_total = ram_total;
    }

    public float getStorage_total() {
        return Storage_total;
    }

    public void setStorage_total(int storage_total) {
        Storage_total = storage_total;
    }

    public float getRam_free() {
        return Ram_free;
    }

    public void setRam_free(int ram_free) {
        Ram_free = ram_free;
    }

    public float getStorage_free() {
        return Storage_free;
    }

    public void setStorage_free(int storage_free) {
        Storage_free = storage_free;
    }

    public int getStorageFullPercent()
    {
        try{
            storage = (int) ((Storage_total - Storage_free) * 100/ Storage_total);
            System.out.print("Storageinpercent" + storage);
        }catch (Exception e){
            e.printStackTrace();
        }

        //DecimalFormat myFormatter = new DecimalFormat("##.##");
       // return Float.parseFloat(myFormatter.format(storage));
        return storage;

    }

    public Memory()
    {

    }

    public Memory(float ram_total,float storage_total,float ram_free,float storage_free) {
        Ram_total = ram_total;
        Storage_total = storage_total;
        Ram_free = ram_free;
        Storage_free = storage_free;
    }
}

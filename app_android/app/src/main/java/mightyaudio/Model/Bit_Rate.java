package mightyaudio.Model;



public class Bit_Rate {
    private int BitRateMode;
    public static float download_quality ;


    public static Float downloadQuality(int bitrate){
        switch (bitrate){
            case 0 :
                download_quality = 2.88f;
                break;
            case 1 :
                download_quality = 4.88f;
                break;
            case 2 :
                download_quality = 9.6f;
                break;
        }

        return download_quality;
    }


    public int getBitRateMode() {
        return BitRateMode;
    }

    public void setBitRateMode(int bitRateMode) {
        BitRateMode = bitRateMode;
    }


}

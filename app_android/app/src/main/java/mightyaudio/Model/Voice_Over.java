package mightyaudio.Model;


public class Voice_Over extends MightyObject
{

   public  float speech_rate;
   public  String voice_name;
   public  String language;
   public  int default_voice;

    public float getSpeech_rate() {
        return speech_rate;
    }

    public void setSpeech_rate(float speech_rate) {
        this.speech_rate = speech_rate;
    }

    public String getVoice_name() {
        return voice_name;
    }

    public void setVoice_name(String voice_name) {
        this.voice_name = voice_name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getDefault_voice() {
        return default_voice;
    }

    public void setDefault_voice(int default_voice) {
        this.default_voice = default_voice;
    }

    public Voice_Over()
    {
         speech_rate = 1.2f;
         voice_name = "James";
         language = "Latin america";
         default_voice = 1;
    }

    public Voice_Over(float speech_rate, String voice_name, String language, short default_voice) {
        this.speech_rate = speech_rate;
        this.voice_name = voice_name;
        this.language = language;
        this.default_voice = default_voice;
    }
}

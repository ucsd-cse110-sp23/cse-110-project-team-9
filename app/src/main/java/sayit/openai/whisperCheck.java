package sayit.openai;

import java.io.IOException;
import java.io.InputStream;

//base functionality to handle whisper errors, should be transposed to main app when we get everything working
public class whisperCheck {

    IWhisper instance;

    public whisperCheck(IWhisper w, InputStream input){
        this.instance = w;
    }

    public String output(){
        try{
             return instance.transcribe(null);
        }
        catch (Exception e){
            return e.getMessage();
        }    
    }
    
}

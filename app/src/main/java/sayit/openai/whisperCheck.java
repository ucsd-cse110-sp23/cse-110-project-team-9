package sayit.openai;

import java.io.IOException;
import java.io.InputStream;

//base functionality to handle whisper errors, should be transposed to main app when we get everything working
public class WhisperCheck {

    IWhisper instance;
    InputStream input;

    public WhisperCheck(IWhisper w, InputStream i){
        this.instance = w;
        this.input = i;
    }

    public String output(){
        try{
             return instance.transcribe(input);
        }
        catch (Exception e){
            return e.getMessage();
        }    
    }
    
}

import java.io.File;
import javax.sound.sampled.*;

public class MusicPlayer {

    private Clip clip;

    public void play(String filePath) {
        try {
            File musicFile = new File(filePath);
            System.out.println("Playing: " + musicFile.getAbsolutePath());
            
            if (!musicFile.exists()) {
                System.out.println("ERROR: Music file not found!");
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously
            System.out.println("✓ Music playing - Check your volume!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
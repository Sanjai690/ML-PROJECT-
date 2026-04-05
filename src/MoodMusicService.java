import java.util.HashMap;
import java.util.Map;

public class MoodMusicService {

    private Map<Mood, String> moodPlaylist;
    private MusicPlayer player;

    public MoodMusicService() {
        moodPlaylist = new HashMap<>();
        player = new MusicPlayer();

        // Map moods to music files
        moodPlaylist.put(Mood.HAPPY, "music/Happy.wav");
        moodPlaylist.put(Mood.SAD, "music/Sad.wav");
        moodPlaylist.put(Mood.RELAX, "music/Relax.wav");
    }

    public void playMusic(Mood mood) {
        player.stop();
        String file = moodPlaylist.get(mood);

        if (file != null) {
            player.play(file);
        } else {
            System.out.println("No music found for mood: " + mood);
        }
    }
}
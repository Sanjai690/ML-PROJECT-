# Mood Canvas - Emotion Music Player

A Java Swing desktop app that plays music based on your mood.

You can:
- Pick a mood manually (Happy, Sad, Relax)
- Use webcam-based auto-detection (demo mode cycles detected expressions)
- Enjoy a styled, animated UI with mood-based color transitions

## Features

- Mood-based music playback using Java Clip API
- Modern Swing UI with gradients, rounded buttons, and smooth color transitions
- Auto-detect mood flow through webcam integration
- Simple architecture using OOP concepts (enum, service layer, UI layer)

## Tech Stack

- Java (compiled with Java 8 compatibility)
- Swing/AWT for UI
- Java Sound (`javax.sound.sampled`) for audio playback
- Webcam Capture library (`com.github.sarxos:webcam-capture`) for camera input

## Project Structure

```text
MoodMusicPlayerJavaProject/
  lib/
    webcam-capture-0.3.12.jar
    slf4j-api-1.7.36.jar
    slf4j-simple-1.7.36.jar
    bridj-0.7.0.jar
  music/
    Happy.wav
    Sad.wav
    Relax.wav
  src/
    BaseMoodFrame.java
    FacialExpressionDetector.java
    Main.java
    Mood.java
    MoodMusicService.java
    MoodMusicUI.java
    MusicPlayer.java
  run.bat
  README.txt
  CREATIVE_ENHANCEMENTS.md
  FACIAL_RECOGNITION_SETUP.md
```

## Requirements

- Windows (for the included `run.bat` script)
- JDK 8 or newer available in PATH
- Webcam (optional, for auto-detect flow)
- Audio files in `music/`:
  - `Happy.wav`
  - `Sad.wav`
  - `Relax.wav`

Important:
- File names are case-sensitive in the code (`Happy.wav`, `Sad.wav`, `Relax.wav`).
- If files are missing, the app opens but music will not play.

## Quick Start (Windows)

Run from project root:

```bat
run.bat
```

What it does:
- Compiles Java files in `src/`
- Includes required camera/logging jars from `lib/`
- Launches the app

## Manual Compile and Run

From project root:

```bat
javac -source 8 -target 8 -cp "lib\webcam-capture-0.3.12.jar;lib\slf4j-api-1.7.36.jar" src\*.java
java -cp "lib\webcam-capture-0.3.12.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-simple-1.7.36.jar;lib\bridj-0.7.0.jar;src" Main
```

## How to Use

1. Launch the app.
2. Click one of the mood buttons:
   - Happy
   - Sad
   - Relax
3. Or click Auto Detect My Mood (AI Camera):
   - Opens camera window
   - Starts detection cycle
   - Updates active mood and playback automatically

## Auto-Detection Notes

Current implementation is demo-oriented:
- Webcam feed opens and runs
- Mood detection is simulated in a cycle (smiling, calm, thoughtful)
- Each detected expression maps to a mood and triggers playback

For production-grade emotion recognition guidance, see:
- `FACIAL_RECOGNITION_SETUP.md`

## Troubleshooting

### Music not playing

- Ensure `music/Happy.wav`, `music/Sad.wav`, `music/Relax.wav` exist.
- Check system volume/output device.
- Watch console output for missing file messages.

### Camera not opening

- Close apps that may be using webcam (Teams, Zoom, etc.).
- Confirm webcam permissions in Windows privacy settings.
- Re-run the app.

### Build errors

- Confirm JDK is installed and `javac` is available.
- Verify all jar files are present in `lib/`.
- Run from project root so relative paths resolve correctly.

## Design and Enhancement Notes

See:
- `CREATIVE_ENHANCEMENTS.md` for UI styling details
- `FACIAL_RECOGNITION_SETUP.md` for upgrading to real emotion detection

## Future Improvements

- Replace demo mood cycle with real ML-based facial emotion inference
- Add playlist support per mood
- Add mute/volume and track controls
- Add settings panel for custom mood-to-track mapping

## License

Personal/educational project.
Add a license file if you plan to distribute publicly.


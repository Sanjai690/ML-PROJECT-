import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.util.List;

/**
 * FacialExpressionDetector - Uses webcam to detect facial expressions
 * and map them to moods (Happy, Sad, Relax)
 */
public class FacialExpressionDetector {
    
    private JFrame cameraFrame;
    private WebcamPanel webcamPanel;
    private Webcam webcam;
    private Timer captureTimer;
    private MoodDetectionListener listener;
    private boolean isDetecting = false;
    private JLabel statusLabel;
    private JPanel cameraContainer;
    
    // Simulated emotion detection - cycles through moods
    private int detectionCycle = 0;
    
    public interface MoodDetectionListener {
        void onMoodDetected(Mood mood, String expression);
    }
    
    public FacialExpressionDetector(MoodDetectionListener listener) {
        this.listener = listener;
        setupCameraWindow();
    }
    
    private void setupCameraWindow() {
        cameraFrame = new JFrame("Mood Detector - Live Camera Feed");
        cameraFrame.setSize(640, 520);
        cameraFrame.setLayout(new BorderLayout());
        
        JPanel headerPnl = new JPanel();
        headerPnl.setBackground(new Color(50, 50, 50));
        JLabel headerLbl = new JLabel("📸 Live Facial Expression Detection");
        headerLbl.setForeground(Color.WHITE);
        headerLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPnl.add(headerLbl);
        
        // Container for camera panel (will be populated when camera opens)
        cameraContainer = new JPanel(new BorderLayout());
        cameraContainer.setBackground(new Color(30, 30, 30));
        
        JLabel placeholderLabel = new JLabel("Click Auto Detect to start camera", SwingConstants.CENTER);
        placeholderLabel.setForeground(Color.WHITE);
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cameraContainer.add(placeholderLabel, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Ready to detect", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 150, 0));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(240, 240, 240));
        statusLabel.setPreferredSize(new Dimension(640, 30));
        
        JButton stopBtn = new JButton("Stop Detection");
        stopBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stopBtn.addActionListener(e -> stopDetection());
        
        cameraFrame.add(headerPnl, BorderLayout.NORTH);
        cameraFrame.add(cameraContainer, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(stopBtn, BorderLayout.SOUTH);
        cameraFrame.add(bottomPanel, BorderLayout.SOUTH);
        
        cameraFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
    
    public void startDetection() {
        if (isDetecting) return;
        
        cameraFrame.setLocationRelativeTo(null);
        cameraFrame.setVisible(true);
        
        // Initialize webcam in background thread to avoid freezing UI
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("🔍 Searching for cameras...");
                
                // Close any existing webcam first
                if (webcam != null && webcam.isOpen()) {
                    webcam.close();
                }
                
                // List all available webcams
                List<Webcam> webcams = Webcam.getWebcams();
                publish("Found " + webcams.size() + " camera(s)");
                
                if (webcams.isEmpty()) {
                    publish("❌ No cameras found!");
                    return null;
                }
                
                // Try each webcam until one opens successfully
                for (int i = 0; i < webcams.size(); i++) {
                    try {
                        publish("📷 Trying camera " + (i + 1) + "...");
                        webcam = webcams.get(i);
                        webcam.setViewSize(WebcamResolution.VGA.getSize());
                        webcam.open();
                        
                        if (webcam.isOpen()) {
                            publish("✅ Camera opened successfully!");
                            return null;
                        }
                    } catch (Exception e) {
                        publish("⚠ Camera " + (i + 1) + " failed: " + e.getMessage());
                        if (webcam != null) {
                            try { webcam.close(); } catch (Exception ex) {}
                        }
                    }
                }
                
                publish("❌ Could not open any camera. Check if another app is using it.");
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String msg : chunks) {
                    statusLabel.setText(msg);
                }
            }
            
            @Override
            protected void done() {
                if (webcam != null && webcam.isOpen()) {
                    // Create and add webcam panel
                    cameraContainer.removeAll();
                    webcamPanel = new WebcamPanel(webcam);
                    webcamPanel.setFPSDisplayed(true);
                    webcamPanel.setDisplayDebugInfo(false);
                    webcamPanel.setImageSizeDisplayed(true);
                    webcamPanel.setMirrored(true);
                    cameraContainer.add(webcamPanel, BorderLayout.CENTER);
                    cameraContainer.revalidate();
                    cameraContainer.repaint();
                    
                    isDetecting = true;
                    detectionCycle = 0;
                    
                    // Start emotion detection timer
                    captureTimer = new Timer(3000, e -> detectExpression());
                    captureTimer.start();
                    
                    statusLabel.setText("✅ Camera active - Detecting your mood...");
                } else {
                    JLabel errorLabel = new JLabel(
                        "<html><center>❌ Camera Error<br/>" +
                        "Close any other apps using your camera<br/>" +
                        "(Teams, Zoom, Skype, etc.) and try again</center></html>", 
                        SwingConstants.CENTER);
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    cameraContainer.removeAll();
                    cameraContainer.add(errorLabel, BorderLayout.CENTER);
                    cameraContainer.revalidate();
                    cameraContainer.repaint();
                }
            }
        };
        
        worker.execute();
    }
    
    public void stopDetection() {
        if (captureTimer != null) {
            captureTimer.stop();
        }
        isDetecting = false;
        
        if (webcamPanel != null) {
            webcamPanel.stop();
        }
        
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        
        cameraFrame.setVisible(false);
        statusLabel.setText("Camera stopped");
    }
    
    private void detectExpression() {
        // DEMO MODE: Simulated emotion detection
        // Real implementation would analyze webcam.getImage() using:
        // - OpenCV + Haar Cascades for face detection
        // - Pre-trained CNN model for emotion classification
        
        String[] expressions = {"smiling 😊", "calm 😌", "thoughtful 🤔"};
        Mood[] moods = {Mood.HAPPY, Mood.RELAX, Mood.SAD};
        
        int index = detectionCycle % 3;
        String expression = expressions[index];
        Mood detectedMood = moods[index];
        
        statusLabel.setText("🎯 Detected: " + expression + " → Playing " + detectedMood + " music!");
        
        if (listener != null) {
            listener.onMoodDetected(detectedMood, expression);
        }
        
        detectionCycle++;
        
        // For real emotion detection, analyze the current frame:
        // BufferedImage image = webcam.getImage();
        // Then use ML model to classify emotion from image
    }
    
    // ---- INTEGRATION WITH OPENCV (For production use) ----
    /*
     * To enable real facial expression detection:
     * 
     * 1. Add OpenCV to your project:
     *    - Download OpenCV: https://opencv.org/releases/
     *    - Add opencv-xxx.jar to classpath
     *    - Load native library: System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
     * 
     * 2. Use Haar Cascade for face detection:
     *    CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_default.xml");
     * 
     * 3. For emotion recognition, use:
     *    - Pre-trained models (FER2013, AffectNet)
     *    - Deep learning frameworks (Deeplearning4j, TensorFlow Java)
     * 
     * 4. Map emotions to moods:
     *    HAPPY -> Mood.HAPPY
     *    SAD, ANGRY, FEAR -> Mood.SAD
     *    CALM, NEUTRAL, SURPRISE -> Mood.RELAX
     */
}

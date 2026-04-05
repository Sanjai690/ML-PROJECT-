import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

public class MoodMusicUI extends BaseMoodFrame implements FacialExpressionDetector.MoodDetectionListener {

    private final JButton happyBtn;
    private final JButton sadBtn;
    private final JButton relaxBtn;
    private final JButton autoDetectBtn;
    private final JLabel nowPlayingLabel;
    private final JLabel quoteLabel;
    private final JPanel moodPanel;
    private JPanel headerPanel;
    private JPanel footerPanel;

    private final Map<Mood, Color> moodColors = new HashMap<>();
    private final Map<Mood, Color[]> moodGradients = new HashMap<>();
    private final Map<Mood, String[]> moodQuotes = new HashMap<>();
    private final Random random = new Random();
    private Timer pulseTimer;
    private Timer colorTransitionTimer;
    private float pulseAlpha = 0.0f;
    private Mood currentMood = null;

    private final MoodMusicService musicService;
    private final FacialExpressionDetector faceDetector;

    public MoodMusicUI() {
        super("🎵 Mood Canvas - Emotion Music Player", 620, 480);
        musicService = new MoodMusicService();
        faceDetector = new FacialExpressionDetector(this);
        initializeMoodData();

        applyWindowTheme(new Color(245, 248, 255), new Color(100, 120, 200));
        setUndecorated(false);

        headerPanel = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color[] gradient = currentMood != null ? moodGradients.get(currentMood) : new Color[]{new Color(100, 120, 255), new Color(150, 90, 255)};
                GradientPaint gp = new GradientPaint(0, 0, gradient[0], 0, getHeight(), gradient[1]);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("🎨 Pick a Mood, Paint Your Soundtrack ✨", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        nowPlayingLabel = new JLabel("🎵 Ready to vibe...", SwingConstants.CENTER);
        nowPlayingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        nowPlayingLabel.setForeground(new Color(255, 255, 255, 230));

        headerPanel.add(titleLabel);
        headerPanel.add(nowPlayingLabel);

        moodPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        moodPanel.setBackground(new Color(245, 248, 255));
        moodPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel manualPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        manualPanel.setBackground(new Color(245, 248, 255));

        happyBtn = createMoodButton("😄 Happy");
        sadBtn = createMoodButton("🌧 Sad");
        relaxBtn = createMoodButton("🌿 Relax");

        manualPanel.add(happyBtn);
        manualPanel.add(sadBtn);
        manualPanel.add(relaxBtn);

        autoDetectBtn = createAutoDetectButton();
        
        moodPanel.add(manualPanel);
        moodPanel.add(autoDetectBtn);

        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(240, 244, 255));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 210, 240)),
            BorderFactory.createEmptyBorder(12, 25, 18, 25)
        ));

        quoteLabel = new JLabel("💫 Choose your emotion... Let the music flow 🎵", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        quoteLabel.setForeground(new Color(80, 90, 140));
        footerPanel.add(quoteLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(moodPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        happyBtn.addActionListener(this::handleMoodSelection);
        sadBtn.addActionListener(this::handleMoodSelection);
        relaxBtn.addActionListener(this::handleMoodSelection);
        autoDetectBtn.addActionListener(e -> faceDetector.startDetection());

        setVisible(true);
    }

    private void handleMoodSelection(ActionEvent e) {
        if (e.getSource() == happyBtn) {
            playMood(Mood.HAPPY, "Happy");
        }

        if (e.getSource() == sadBtn) {
            playMood(Mood.SAD, "Sad");
        }

        if (e.getSource() == relaxBtn) {
            playMood(Mood.RELAX, "Relax");
        }
    }

    private JButton createMoodButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, new Color(220, 230, 255), 0, getHeight(), new Color(190, 200, 240));
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(240, 245, 255));
                } else {
                    gp = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(245, 248, 255));
                }
                
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Border with shadow effect
                g2d.setColor(new Color(150, 160, 200, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(new Color(60, 70, 120));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 70));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(100, 120, 255));
            }
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(60, 70, 120));
            }
        });
        
        return button;
    }

    private JButton createAutoDetectButton() {
        JButton button = new JButton("📸 Auto-Detect My Mood (AI Camera)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Animated gradient
                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, new Color(80, 100, 220), 0, getHeight(), new Color(120, 70, 200));
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, new Color(120, 140, 255), getWidth(), getHeight(), new Color(180, 100, 255));
                } else {
                    gp = new GradientPaint(0, 0, new Color(100, 120, 255), getWidth(), getHeight(), new Color(150, 90, 255));
                }
                
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Glow effect
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 30, 30);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(500, 65));
        
        return button;
    }

    @Override
    public void onMoodDetected(Mood mood, String expression) {
        SwingUtilities.invokeLater(() -> {
            String moodName = mood.toString().charAt(0) + mood.toString().substring(1).toLowerCase();
            playMood(mood, moodName);
            nowPlayingLabel.setText("✨ AI Detected: " + expression + " → " + moodName + " vibes 🎶");
            startPulseAnimation();
        });
    }
    
    private void startPulseAnimation() {
        if (pulseTimer != null) pulseTimer.stop();
        pulseAlpha = 1.0f;
        pulseTimer = new Timer(50, e -> {
            pulseAlpha -= 0.05f;
            if (pulseAlpha <= 0) {
                ((Timer)e.getSource()).stop();
            }
            nowPlayingLabel.repaint();
        });
        pulseTimer.start();
    }

    private void playMood(Mood mood, String moodName) {
        musicService.playMusic(mood);
        currentMood = mood;
        animateColorTransition(mood);
        nowPlayingLabel.setText("Now playing: " + moodName + " vibes 🎶");
        quoteLabel.setText(getRandomQuote(mood));
    }
    
    private void animateColorTransition(Mood mood) {
        if (colorTransitionTimer != null) colorTransitionTimer.stop();
        
        Color targetBg = moodColors.get(mood);
        Color currentBg = getContentPane().getBackground();
        
        final int steps = 20;
        final int[] step = {0};
        
        colorTransitionTimer = new Timer(20, e -> {
            float progress = (float) step[0] / steps;
            
            int r = (int) (currentBg.getRed() + (targetBg.getRed() - currentBg.getRed()) * progress);
            int g = (int) (currentBg.getGreen() + (targetBg.getGreen() - currentBg.getGreen()) * progress);
            int b = (int) (currentBg.getBlue() + (targetBg.getBlue() - currentBg.getBlue()) * progress);
            
            Color transitionColor = new Color(r, g, b);
            getContentPane().setBackground(transitionColor);
            moodPanel.setBackground(transitionColor);
            
            // Update footer gradient
            Color footerColor = new Color(
                Math.min(255, r + 10),
                Math.min(255, g + 10),
                Math.min(255, b + 10)
            );
            footerPanel.setBackground(footerColor);
            
            headerPanel.repaint();
            
            step[0]++;
            if (step[0] > steps) {
                ((Timer)e.getSource()).stop();
            }
        });
        colorTransitionTimer.start();
    }

    private String getRandomQuote(Mood mood) {
        String[] quotes = moodQuotes.get(mood);
        return quotes[random.nextInt(quotes.length)];
    }

    private void initializeMoodData() {
        // Background colors for each mood
        moodColors.put(Mood.HAPPY, new Color(255, 248, 214));  // Warm yellow
        moodColors.put(Mood.SAD, new Color(220, 230, 250));    // Cool blue
        moodColors.put(Mood.RELAX, new Color(225, 245, 230));  // Soft green
        
        // Gradient colors for header [start, end]
        moodGradients.put(Mood.HAPPY, new Color[]{
            new Color(255, 200, 100),  // Golden yellow
            new Color(255, 150, 80)    // Orange
        });
        moodGradients.put(Mood.SAD, new Color[]{
            new Color(100, 130, 200),  // Deep blue
            new Color(80, 100, 180)    // Darker blue
        });
        moodGradients.put(Mood.RELAX, new Color[]{
            new Color(100, 200, 150),  // Mint green
            new Color(80, 180, 120)    // Forest green
        });

        moodQuotes.put(Mood.HAPPY, new String[]{
                "☀️ Let the sunshine dance through your playlist.",
                "🎉 Today sounds like confetti and rhythm!",
                "😄 Smiles are louder with good music.",
                "✨ Happiness is a melody away!"
        });
        moodQuotes.put(Mood.SAD, new String[]{
                "🌧️ Soft songs can hold heavy hearts.",
                "💙 Rainy feelings deserve gentle melodies.",
                "🌙 Breathe in, let the notes carry the weight.",
                "💫 It's okay to feel blue sometimes."
        });
        moodQuotes.put(Mood.RELAX, new String[]{
                "🌿 Slow down, your calm soundtrack has arrived.",
                "🧘 Peace sounds like this moment.",
                "🍃 Exhale and let the music do the rest.",
                "☮️ Find your zen in every note."
        });
    }
}
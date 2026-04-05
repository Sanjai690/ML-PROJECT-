import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

/**
 * Base window for mood-related UI screens.
 * Demonstrates a custom inheritance layer beyond Swing framework classes.
 */
public abstract class BaseMoodFrame extends JFrame {

    protected BaseMoodFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setLayout(new BorderLayout(15, 15));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    protected void applyWindowTheme(Color backgroundColor, Color borderColor) {
        getContentPane().setBackground(backgroundColor);
        getRootPane().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, borderColor));
    }
}

package View;

import javax.swing.*;
import java.awt.*;

public class ArkaPlanPanel extends JPanel {
    private Image img;

    public ArkaPlanPanel() {
        try {
            img = new ImageIcon("background.jpg").getImage();
        } catch (Exception e) {
            img = null;
        }
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null && img.getWidth(null) > 0) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(new GradientPaint(0, 0, new Color(200, 230, 255), 0, getHeight(), new Color(255, 255, 255)));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
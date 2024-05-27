import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThemeChooser extends JDialog implements ActionListener {
    private JTextArea txt;
    private JComboBox<String> themeComboBox;
    private JFrame frame;
    private JMenuBar menuBar;
    private JPanel themePreviewPanel;
    private String selectedTheme;

    public ThemeChooser(JFrame parent, JTextArea text, JMenuBar menu) {
        super(parent, "Themes", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        this.frame = parent;
        this.txt = text;
        this.menuBar = menu;

        selectedTheme = getCurrentThemeName();

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        add(centerPanel, BorderLayout.CENTER);

        String[] themes = {"Light", "Dark", "Jungle", "Ethnic", "Aquamarine", "Lemon", "Apple"};

        themeComboBox = new JComboBox<>(themes);
        if (selectedTheme != null) {
            themeComboBox.setSelectedItem(selectedTheme);
        }

        centerPanel.add(themeComboBox);
        centerPanel.add(new JLabel("Select Theme:"));

        themePreviewPanel = new JPanel();
        themePreviewPanel.setPreferredSize(new Dimension(200, 100));
        themePreviewPanel.setBorder(BorderFactory.createTitledBorder("Theme Preview: " + selectedTheme));
        centerPanel.add(themePreviewPanel);

        JButton btn = new JButton("Apply");
        add(btn, BorderLayout.SOUTH);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applySelectedTheme();
                dispose();
            }
        });
        themeComboBox.addActionListener(this);
        themeComboBox.setActionCommand("Theme");
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("Theme")) {
            selectedTheme = (String) themeComboBox.getSelectedItem();
            updateThemePreview(selectedTheme);
        }
    }

    private void applySelectedTheme() {
        switch (selectedTheme) {
            case "Light":
                applyLightTheme();
                break;
            case "Dark":
                applyDarkTheme();
                break;
            case "Jungle":
                applyJungleTheme();
                break;
            case "Ethnic":
                applyEthnicTheme();
                break;
            case "Aquamarine":
                applyAquamarine();
                break;
            case "Lemon":
                applyLemon();
                break;
            case "Apple":
                applyApple();
                break;
        }
    }

    private void applyDarkTheme() {
        UIManager.put("control", new Color(80, 80, 80));
        UIManager.put("nimbusBase", new Color(59, 59, 59));
        UIManager.put("nimbusSelectedText", new Color(234, 217, 212));
        UIManager.put("nimbusFocus", new Color(32, 32, 32));

        txt.setBackground(new Color(32, 32, 32));
        txt.setForeground(Color.WHITE);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void applyLightTheme() {
        txt.setBackground(Color.WHITE);
        txt.setForeground(Color.BLACK);
        UIManager.put("control", Color.WHITE);
    }

    private void applyJungleTheme() {
        UIManager.put("control", new Color(108, 155, 108));
        UIManager.put("nimbusBase", new Color(22, 206, 22));
        UIManager.put("nimbusSelectedText", new Color(234, 217, 212));
        UIManager.put("nimbusFocus", new Color(49, 89, 49));

        txt.setBackground(new Color(49, 89, 49));
        txt.setForeground(Color.WHITE);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void applyEthnicTheme() {
        UIManager.put("control", new Color(163, 112, 63));
        UIManager.put("nimbusBase", new Color(139, 69, 19));
        UIManager.put("nimbusSelectedText", new Color(234, 217, 212));
        UIManager.put("nimbusFocus", new Color(139, 69, 19));

        txt.setBackground(new Color(139, 69, 19));
        txt.setForeground(Color.BLACK);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void applyAquamarine() {
        UIManager.put("control", new Color(178, 229, 237, 255));
        UIManager.put("nimbusBase", new Color(32, 245, 204, 226));
        UIManager.put("nimbusSelectedText", new Color(234, 217, 212));
        UIManager.put("nimbusFocus", new Color(178, 229, 237, 255));

        txt.setBackground(new Color(178, 229, 237, 255));
        txt.setForeground(Color.BLACK);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void applyLemon() {
        UIManager.put("control", new Color(216, 236, 96, 255));
        UIManager.put("nimbusBase", new Color(220, 241, 23, 195));
        UIManager.put("nimbusSelectedText", new Color(234, 217, 212));
        UIManager.put("nimbusFocus", new Color(215, 229, 126, 255));

        txt.setBackground(new Color(215, 229, 126, 255));
        txt.setForeground(Color.BLACK);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void applyApple() {
        UIManager.put("control", new Color(249, 216, 214, 255));
        UIManager.put("nimbusBase", new Color(234, 76, 68, 231));
        UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
        UIManager.put("nimbusFocus", new Color(249, 216, 214, 255));

        txt.setBackground(new Color(249, 216, 214, 255));
        txt.setForeground(Color.BLACK);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private String getCurrentThemeName() {
        Color bgColor = txt.getBackground();
        if (bgColor.equals(Color.WHITE)) {
            return "Light";
        } else if (bgColor.equals(new Color(32, 32, 32))) {
            return "Dark";
        } else if (bgColor.equals(new Color(32, 48, 32))) {
            return "Jungle";
        } else if (bgColor.equals(new Color(139, 69, 19))) {
            return "Ethnic";
        } else if (bgColor.equals(new Color(178, 229, 237, 255))) {
            return "Aquamarine";
        } else if (bgColor.equals(new Color(239, 249, 218, 255))) {
            return "Lemon";
        } else if (bgColor.equals(new Color(249, 216, 214, 255))) {
            return "Apple";
        }
        return null;
    }

    private void updateThemePreview(String themeName) {
        themePreviewPanel.removeAll();
        themePreviewPanel.setBorder(BorderFactory.createTitledBorder("Theme Preview: " + themeName));

        switch (themeName) {
            case "Light":
                themePreviewPanel.setBackground(Color.WHITE);
                break;
            case "Dark":
                themePreviewPanel.setBackground(new Color(32, 32, 32));
                break;
            case "Jungle":
                themePreviewPanel.setBackground(new Color(32, 48, 32));
                break;
            case "Ethnic":
                themePreviewPanel.setBackground(new Color(139, 69, 19));
                break;
            case "Aquamarine":
                themePreviewPanel.setBackground(new Color(178, 229, 237, 255));
                break;
            case "Lemon":
                themePreviewPanel.setBackground(new Color(239, 249, 218, 255));
                break;
            case "Apple":
                themePreviewPanel.setBackground(new Color(249, 216, 214, 255));
                break;
        }
        themePreviewPanel.revalidate();
        themePreviewPanel.repaint();
    }
}
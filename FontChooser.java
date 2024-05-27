import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FontChooser extends JDialog implements ActionListener {
    private JComboBox<String> fontComboBox;
    private JComboBox<Integer> sizeComboBox;
    private JTextField sampleTextField;
    private int styleFont;
    private Font selectedFont;

    public FontChooser(Frame parent, Font initialFont) {
        super(parent, "Font Chooser", true);
        setResizable(false);
        setSize(490, 300);
        setLocationRelativeTo(parent);
        setLayout(null);
        styleFont = initialFont.getStyle();

        selectedFont = initialFont;

        JRadioButton plainButton = new JRadioButton("Plain");
        plainButton.addActionListener(this);
        plainButton.setActionCommand("plain");

        JRadioButton boldButton = new JRadioButton("Bold");
        boldButton.addActionListener(this);
        boldButton.setActionCommand("bold");

        JRadioButton italicButton = new JRadioButton("Italic");
        italicButton.addActionListener(this);
        italicButton.setActionCommand("italic");

        switch (initialFont.getStyle()) {
            case Font.PLAIN:
                plainButton.setSelected(true);
            case Font.BOLD:
                boldButton.setSelected(true);
            case Font.ITALIC:
                italicButton.setSelected(true);
        }

        add(plainButton);
        add(boldButton);
        add(italicButton);

        ButtonGroup bg = new ButtonGroup();
        bg.add(plainButton);
        bg.add(boldButton);
        bg.add(italicButton);

        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setSelectedItem(initialFont.getFamily());
        fontComboBox.addActionListener(this);
        fontComboBox.setActionCommand("Font");

        sizeComboBox = new JComboBox<>(new Integer[]{8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 40, 48, 56});
        sizeComboBox.setSelectedItem(initialFont.getSize());
        sizeComboBox.addActionListener(this);
        sizeComboBox.setActionCommand("Size");

        sampleTextField = new JTextField("AaBbCc");
        sampleTextField.setFont(initialFont);
        sampleTextField.setHorizontalAlignment(JTextField.CENTER);
        sampleTextField.setEditable(false);

        fontComboBox.setBounds(60, 10, 150, 20);
        sizeComboBox.setBounds(240, 10, 150, 20);
        sampleTextField.setBounds(110, 100, 230, 100);

        plainButton.setBounds(60, 50, 100, 20);
        boldButton.setBounds(200, 50, 100, 20);
        italicButton.setBounds(320, 50, 100, 20);

        add(plainButton);
        add(boldButton);
        add(italicButton);

        add(fontComboBox);
        add(sizeComboBox);
        add(sampleTextField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setActionCommand("Ok_Button");

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("Cancel_Button");

        okButton.setBounds(110, 210, 105, 25);
        cancelButton.setBounds(235, 210, 105, 25);

        add(okButton);
        add(cancelButton);

        updateSampleFont();
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("Font")) {
            updateSampleFont();
        } else if (command.equals("Size")) {
            updateSampleFont();
        } else if (command.equals("Ok_Button")) {
            selectedFont = new Font((String) fontComboBox.getSelectedItem(), styleFont, (int) sizeComboBox.getSelectedItem());
            setVisible(false);
        } else if (command.equals("Cancel_Button")) {
            selectedFont = null;
            setVisible(false);
        } else if (command.equals("plain")) {
            styleFont = Font.PLAIN;
            updateSampleFont();
        } else if (command.equals("bold")) {
            styleFont = Font.BOLD;
            updateSampleFont();
        } else if (command.equals("italic")) {
            styleFont = Font.ITALIC;
            updateSampleFont();
        }
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    private void updateSampleFont() {
        String fontName = (String) fontComboBox.getSelectedItem();
        int fontSize = (int) sizeComboBox.getSelectedItem();
        Font newFont = new Font(fontName, styleFont, fontSize);
        sampleTextField.setFont(newFont);
    }
}

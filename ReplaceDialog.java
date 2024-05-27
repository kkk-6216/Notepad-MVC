import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReplaceDialog extends JDialog implements ActionListener {
    private JTextField searchField;
    private JTextField replaceField;
    private JRadioButton caseSensitiveRadioButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JButton cancelButton;
    private JTextArea text;
    private String searchText = "";
    private String replaceText = "";
    private static String lastSearchText = "";
    private static String lastReplaceText = "";

    public ReplaceDialog(Frame parent, JTextArea textArea) {
        super(parent, "Replace", true);
        this.text = textArea;
        setSize(440, 190);
        setLocationRelativeTo(parent);
        setLayout(null);

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(15);
        searchField.setText(lastSearchText);

        JLabel replaceLabel = new JLabel("Replace with:");
        replaceField = new JTextField(15);
        replaceField.setText(lastReplaceText);

        caseSensitiveRadioButton = new JRadioButton("Case sensitive");

        replaceButton = new JButton("Replace");
        replaceAllButton = new JButton("Replace All");
        cancelButton = new JButton("Cancel");

        searchLabel.setBounds(10, 10, 100, 28);
        searchField.setBounds(120, 10, 150, 28);
        replaceLabel.setBounds(10, 40, 100, 28);
        replaceField.setBounds(120, 40, 150, 28);
        caseSensitiveRadioButton.setBounds(10, 105, 200, 28);

        replaceButton.setBounds(310, 10, 100, 30);
        replaceAllButton.setBounds(310, 50, 100, 30);
        cancelButton.setBounds(310, 90, 100, 30);

        add(searchLabel);
        add(searchField);
        add(replaceLabel);
        add(replaceField);
        add(caseSensitiveRadioButton);
        add(replaceButton);
        add(replaceAllButton);
        add(cancelButton);

        replaceButton.addActionListener(this);
        replaceAllButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setResizable(false);
    }

    public void actionPerformed(ActionEvent e) {
        searchText = searchField.getText();
        lastSearchText = searchText;
        replaceText = replaceField.getText();
        lastReplaceText = replaceText;

        boolean isCaseSensitive = caseSensitiveRadioButton.isSelected();
        String textContent = text.getText();
        String originalText = textContent;

        if (!isCaseSensitive) {
            searchText = searchText.toLowerCase();
            textContent = textContent.toLowerCase();
        }
        if (e.getSource() == replaceButton) {
            int start = textContent.indexOf(searchText);
            if (start >= 0) {
                textContent = originalText.substring(0, start) + replaceText + originalText.substring(start + searchText.length());
                text.setText(textContent);
            } else {
                JOptionPane.showMessageDialog(this, "Text '" + searchField.getText() + "' not found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == replaceAllButton) {
            if (textContent.contains(searchText)) {
                textContent = originalText.replaceAll(searchText, replaceText);
                text.setText(textContent);
            } else {
                JOptionPane.showMessageDialog(this, "Text '" + searchField.getText() + "' not found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }
}

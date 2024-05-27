import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Frame;

public class TextFinder extends JDialog implements ActionListener {
    int startIndex = 0;
    JLabel lab1;
    JTextField textF;
    JButton findBtn, findNext, findPrev;
    JCheckBox caseSensitiveCheckBox;
    private JTextArea txt;
    private String searchText = "";
    private static String lastSearchText = "";

    public TextFinder(Frame parent, JTextArea text) {
        super(parent, "Text Finder");
        setSize(400, 200);
        setLocationRelativeTo(parent);
        this.txt = text;
        lab1 = new JLabel("Find:");
        textF = new JTextField(40);
        textF.setText(lastSearchText);
        findBtn = new JButton("Find");
        findNext = new JButton("Find Next");
        findPrev = new JButton("Find Previous");
        caseSensitiveCheckBox = new JCheckBox("Case Sensitive");
        setLayout(null);

        int labWidth = 100;
        int labHeight = 40;

        lab1.setBounds(35, 25, labWidth, labHeight);
        add(lab1);

        textF.setBounds(70, 35, 120, 20);
        add(textF);

        findBtn.setBounds(225, 28, 115, 20);
        add(findBtn);
        findBtn.addActionListener(this);

        findNext.setBounds(225, 56, 115, 20);
        add(findNext);
        findNext.addActionListener(this);

        findPrev.setBounds(225, 82, 115, 20);
        add(findPrev);
        findPrev.addActionListener(this);

        caseSensitiveCheckBox.setBounds(30, 75, 115, 20);
        add(caseSensitiveCheckBox);
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == findBtn) {
            find();
        } else if (e.getSource() == findNext) {
            findNext();
        } else if (e.getSource() == findPrev) {
            findPrev();
        }
    }

    public void find() {
        searchText = textF.getText();
        startIndex = 0;
        lastSearchText = searchText;
        int select_start;

        if (caseSensitiveCheckBox.isSelected()) {
            select_start = txt.getText().indexOf(searchText);
        } else {
            select_start = txt.getText().toLowerCase().indexOf(searchText.toLowerCase());
        }

        if (select_start == -1) {
            JOptionPane.showMessageDialog(null, "Could not find \"" + textF.getText() + "\"!");
        } else {
            int select_end = select_start + searchText.length();
            txt.select(select_start, select_end);
        }
    }

    public void findNext() {
        if (lastSearchText.isEmpty()) {
            return;
        }
        int select_start;
        if (caseSensitiveCheckBox.isSelected()) {
            select_start = txt.getText().indexOf(lastSearchText, startIndex + 1);
        } else {
            select_start = txt.getText().toLowerCase().indexOf(lastSearchText.toLowerCase(), startIndex + 1);
        }
        if (select_start != -1) {
            int select_end = select_start + searchText.length();
            txt.select(select_start, select_end);
            startIndex = select_start;
        } else {
            select_start = txt.getText().indexOf(searchText);
            if (select_start != -1) {
                int select_end = select_start + searchText.length();
                txt.select(select_start, select_end);
                startIndex = select_start;
            } else {
                startIndex = 0;
            }
        }
    }

    public void findPrev() {
        if (lastSearchText.isEmpty()) {
            return;
        }
        int select_start;
        if (caseSensitiveCheckBox.isSelected()) {
            select_start = txt.getText().lastIndexOf(lastSearchText, startIndex - 1);
        } else {
            select_start = txt.getText().toLowerCase().lastIndexOf(lastSearchText.toLowerCase(), startIndex - 1);
        }

        if (select_start != -1) {
            int select_end = select_start + searchText.length();
            txt.select(select_start, select_end);
            startIndex = select_start;
        } else {
            select_start = txt.getText().lastIndexOf(searchText);
            if (select_start != -1) {
                int select_end = select_start + searchText.length();
                txt.select(select_start, select_end);
                startIndex = select_start;
            } else {
                startIndex = txt.getText().length();
            }
        }
    }
}

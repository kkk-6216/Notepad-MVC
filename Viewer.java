import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import java.awt.Font;
import java.io.File;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
import java.awt.Color;
import java.io.IOException;
import java.net.URISyntaxException;
import java.awt.Desktop;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Viewer {
    private JTextArea textArea;
    private JFrame frame;
    private JFileChooser fileChooser;
    private Font fontTextArea;
    private JLabel statusLabel;
    private boolean statusLabelVisible;
    private Controller controller;
    public TextFinder finder;
    private boolean fileSaved = false;
    private UndoManager undoManager;
    private String fileName = "New document.txt";
    private JMenuBar menuBar;
    private JDialog dialog;
    private double zoomFactor = 1.0;
    private JMenuItem autoSave;

    public Viewer() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        undoManager = new UndoManager();
        controller = new Controller(this);
        menuBar = getJMenuBar(controller);
        statusLabelVisible = false;
        fontTextArea = new Font("Arial", Font.PLAIN, 32);

        textArea = new JTextArea();
        textArea.setFont(fontTextArea);
        textArea.getDocument().addUndoableEditListener(undoManager);

        UIManager.put("control", new Color(178, 229, 237, 255));
        UIManager.put("nimbusBase", new Color(120, 213, 229, 123));
        UIManager.put("nimbusSelectedText", new Color(234, 217, 212));
        UIManager.put("nimbusFocus", new Color(178, 229, 237, 255));

        textArea.setBackground(new Color(178, 229, 237, 255));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {

                    double scaleFactor = 1.05;
                    double newZoomFactor = zoomFactor * (e.getWheelRotation() > 0 ? 1.0 / scaleFactor : scaleFactor);
                    if (newZoomFactor >= 0.5 && newZoomFactor <= 2.0) {
                        zoomFactor = newZoomFactor;
                        updateZoomInfo();
                    }
                } else {
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    int unitsToScroll = e.getUnitsToScroll();
                    int newValue = verticalScrollBar.getValue() - unitsToScroll;
                    newValue = Math.max(0, Math.min(newValue, verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount()));
                    verticalScrollBar.setValue(newValue);
                }
            }
        });
        statusLabel = new JLabel("Row:   Column:  ");
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        Image image = Toolkit.getDefaultToolkit().getImage("images/icon.png");
        frame = new JFrame("Notepad MVC Pattern    " + getFileName());

        dialog = new JDialog(frame);
        ((java.awt.Frame) dialog.getOwner()).setIconImage(image);
        dialog.setModal(true);

        frame.setLocation(300, 100);
        frame.setSize(800, 800);
        frame.setJMenuBar(menuBar);

        frame.add("Center", scrollPane);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                callExitDialog();
            }
        });
    }

    private JMenuBar getJMenuBar(Controller controller) {
        JMenu fileMenu = getFileMenu(controller);
        JMenu editMenu = getEditMenu(controller);
        JMenu formatMenu = getFormatMenu(controller);
        JMenu viewMenu = getViewMenu(controller);
        JMenu helpMenu = getHelpMenu(controller);

        Font fontMenu = new Font("Neutral Face", Font.PLAIN, 12);
        fileMenu.setFont(fontMenu);
        editMenu.setFont(fontMenu);
        formatMenu.setFont(fontMenu);
        viewMenu.setFont(fontMenu);
        helpMenu.setFont(fontMenu);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JMenu getFileMenu(Controller controller) {
        JMenuItem newDocument = new JMenuItem("New Document", new ImageIcon("images/new.gif"));
        newDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newDocument.addActionListener(controller);
        newDocument.setActionCommand("New_Document");

        JMenuItem openDocument = new JMenuItem("Open ...", new ImageIcon("images/open.gif"));
        openDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openDocument.addActionListener(controller);
        openDocument.setActionCommand("Open_Document");

        JMenuItem saveDocument = new JMenuItem("Save", new ImageIcon("images/save.gif"));
        saveDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveDocument.addActionListener(controller);
        saveDocument.setActionCommand("Save_Document");

        JMenuItem saveAsDocument = new JMenuItem("Save As ...", new ImageIcon("images/save_as.gif"));
        saveAsDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
        saveAsDocument.addActionListener(controller);
        saveAsDocument.setActionCommand("Save_As_Document");

        autoSave = new JMenuItem("AutoSave", new ImageIcon("images/krest.png"));
        autoSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
        autoSave.addActionListener(controller);
        autoSave.setActionCommand("Auto_Save");

        JMenuItem printDocument = new JMenuItem("Print ...", new ImageIcon("images/print.gif"));
        printDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        printDocument.addActionListener(controller);
        printDocument.setActionCommand("Print");

        JMenuItem closeProgram = new JMenuItem("Exit", new ImageIcon("images/exit.gif"));
        closeProgram.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        closeProgram.addActionListener(controller);
        closeProgram.setActionCommand("Exit");

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(newDocument);
        fileMenu.add(openDocument);
        fileMenu.add(saveDocument);
        fileMenu.add(saveAsDocument);
        fileMenu.add(autoSave);
        fileMenu.add(new JSeparator());
        fileMenu.add(printDocument);
        fileMenu.add(new JSeparator());
        fileMenu.add(closeProgram);

        return fileMenu;
    }


    private JMenu getEditMenu(Controller controller) {
        JMenuItem undo = new JMenuItem("Undo", new ImageIcon("images/back.gif"));
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undo.addActionListener(controller);
        undo.setActionCommand("Undo");

        JMenuItem redo = new JMenuItem("Redo", new ImageIcon("images/redo.png"));
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redo.addActionListener(controller);
        redo.setActionCommand("Redo");

        JMenuItem cut = new JMenuItem("Cut", new ImageIcon("images/cut.gif"));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        cut.addActionListener(controller);
        cut.setActionCommand("Cut");

        JMenuItem copy = new JMenuItem("Copy", new ImageIcon("images/copy.gif"));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copy.addActionListener(controller);
        copy.setActionCommand("Copy");

        JMenuItem paste = new JMenuItem("Paste", new ImageIcon("images/past.gif"));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        paste.addActionListener(controller);
        paste.setActionCommand("Paste");

        JMenuItem delete = new JMenuItem("Delete", new ImageIcon("images/delete.gif"));
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        delete.addActionListener(controller);
        delete.setActionCommand("Delete");

        JMenuItem find = new JMenuItem("Find", new ImageIcon("images/find.gif"));
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        find.addActionListener(controller);
        find.setActionCommand("Find");

        JMenuItem findPrevious = new JMenuItem("Find Previous", new ImageIcon("images/findprevious.gif"));
        findPrevious.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
        findPrevious.addActionListener(controller);
        findPrevious.setActionCommand("Find_Previous");

        JMenuItem findNext = new JMenuItem("Find Next", new ImageIcon("images/findnext.gif"));
        findNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
        findNext.addActionListener(controller);
        findNext.setActionCommand("Find_Next");

        JMenuItem replace = new JMenuItem("Replace", new ImageIcon("images/replace.gif"));
        replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
        replace.addActionListener(controller);
        replace.setActionCommand("Replace");

        JMenuItem goTo = new JMenuItem("Go to", new ImageIcon("images/goto.gif"));
        goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        goTo.addActionListener(controller);
        goTo.setActionCommand("Go_to");

        JMenuItem selectAll = new JMenuItem("Select All", new ImageIcon("images/selectall.gif"));
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        selectAll.addActionListener(controller);
        selectAll.setActionCommand("Select_All");

        JMenuItem timeDate = new JMenuItem("Time/Date", new ImageIcon("images/time.gif"));
        timeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        timeDate.addActionListener(controller);
        timeDate.setActionCommand("Time/Date");

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);
        editMenu.add(delete);
        editMenu.add(new JSeparator());
        editMenu.add(find);
        editMenu.add(findPrevious);
        editMenu.add(findNext);
        editMenu.add(replace);
        editMenu.add(goTo);
        editMenu.add(new JSeparator());
        editMenu.add(selectAll);
        editMenu.add(timeDate);
        return editMenu;
    }

    private JMenu getFormatMenu(Controller controller) {
        JMenuItem font = new JMenuItem("Font", new ImageIcon("images/font.gif"));
        font.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        font.addActionListener(controller);
        font.setActionCommand("Font");

        JMenuItem themes = new JMenuItem("Themes", new ImageIcon("images/themes.png"));
        themes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
        themes.addActionListener(controller);
        themes.setActionCommand("Themes");

        JMenu formatMenu = new JMenu("Format");
        formatMenu.setMnemonic('W');
        formatMenu.add(themes);
        formatMenu.add(font);
        return formatMenu;
    }

    private JMenu getViewMenu(Controller controller) {
        JMenuItem statusBar = new JMenuItem("Status Bar", new ImageIcon("images/statusbar.gif"));
        statusBar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        statusBar.addActionListener(controller);
        statusBar.setActionCommand("Status_Bar");

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        viewMenu.add(statusBar);
        return viewMenu;
    }

    private JMenu getHelpMenu(Controller controller) {
        JMenuItem checkInfo = new JMenuItem("Check Info", new ImageIcon("images/info.gif"));
        checkInfo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        checkInfo.addActionListener(controller);
        checkInfo.setActionCommand("Check_Info");

        JMenuItem aboutProgramm = new JMenuItem("About Programm", new ImageIcon("images/about.gif"));
        aboutProgramm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        aboutProgramm.addActionListener(controller);
        aboutProgramm.setActionCommand("About_Program");

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        helpMenu.add(checkInfo);
        helpMenu.add(new JSeparator());
        helpMenu.add(aboutProgramm);
        return helpMenu;
    }

    public void setAutoSaveIcon(Icon icon) {
        autoSave.setIcon(icon);
    }

    public void updateFrameTitle(String newTitle) {
        frame.setTitle("Notepad MVC Pattern - " + newTitle);
    }

    private void updateZoomInfo() {
        int zoomPercentage = (int) (zoomFactor * 100);
        statusLabel.setText(zoomPercentage + "%");
        textArea.setFont(textArea.getFont().deriveFont((float) (zoomFactor * fontTextArea.getSize2D())));
        textArea.revalidate();
        textArea.repaint();
    }

    public File showFileDialog(String value) {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        int returnVal = Integer.MIN_VALUE;
        File selectedFile = null;

        if (value.equals("Open")) {
            if (!fileSaved) {
                showUnsavedFile();
            }
            returnVal = fileChooser.showOpenDialog(frame);
        } else if (value.equals("Save")) {
            controller.saveCommand(getFilePath(), getFileData());
            updateFrameTitle(fileName);
        } else if (value.equals("Save_As")) {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setSelectedFile(new File(getFileName()));
            returnVal = fileChooser.showSaveDialog(frame);
        }
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileName = selectedFile.getName();
            updateFrameTitle(fileName);
        }
        return selectedFile;
    }

    public void showErrorMessage() {
        JOptionPane.showMessageDialog(frame, "We have problem with read file!", "Information", JOptionPane.ERROR_MESSAGE);
    }

    public void checkInfo() {
        try {
            URI url = new URI("https://go.microsoft.com/fwlink/?LinkId=834783");
            Desktop.getDesktop().browse(url);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void showReplaceDialog() {
        ReplaceDialog replaceDialog = new ReplaceDialog(frame, textArea);
        replaceDialog.setVisible(true);
    }

    public void update(String text) {
        textArea.setText(text);
    }

    public void updateCaret(int dot) {
        try {
            int row = textArea.getLineOfOffset(dot) + 1;
            int col = dot - textArea.getLineStartOffset(row - 1) + 1;
            statusLabel.setText("Row: " + row + " Column: " + col);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    public void applySelectedTheme() {
        ThemeChooser themeChooser = new ThemeChooser(frame, textArea, menuBar);
        themeChooser.setVisible(true);
    }

    public void statusBar() {
        if (statusLabelVisible) {
            frame.remove(statusLabel);
            textArea.removeCaretListener(controller);
            statusLabelVisible = false;
        } else if (!statusLabelVisible) {
            int caretPosition = textArea.getCaretPosition();
            int lineNumber = -1;
            int columnNumber = -1;
            try {
                lineNumber = textArea.getLineOfOffset(caretPosition);
                int startOffset = textArea.getLineStartOffset(lineNumber);
                columnNumber = caretPosition - startOffset;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            lineNumber++;
            columnNumber++;
            statusLabel = new JLabel("Row: " + lineNumber + " Column: " + columnNumber);
            frame.add(statusLabel, BorderLayout.SOUTH);
            textArea.addCaretListener(controller);
            statusLabelVisible = true;
        }
        frame.revalidate();
        frame.repaint();
    }

    public void changeFont() {
        FontChooser dialog = new FontChooser(frame, fontTextArea);
        dialog.setVisible(true);
        Font selectedFont = dialog.getSelectedFont();
        if (selectedFont != null) {
            fontTextArea = selectedFont;
            textArea.setFont(selectedFont);
        }
    }

    public void find() {
        finder = new TextFinder(frame, textArea);
        finder.setVisible(true);
    }

    public void findNext() {
        finder.findNext();
    }

    public void findPrev() {
        finder.findPrev();
    }

    public void textAreaCut() {
        textArea.cut();
    }

    public void textAreaCopy() {
        textArea.copy();
    }

    public void textAreaPaste() {
        textArea.paste();
    }

    public void textAreaDelete() {
        textArea.replaceSelection("");
    }

    public void textAreaSelectAll() {
        textArea.selectAll();
    }

    public String getContentTextArea() {
        return textArea.getText();
    }

    public Font getFontTextArea() {
        return textArea.getFont();
    }

    public void callExitDialog() {
        if (!fileSaved) {
            int option = JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == 0) {
                if (!controller.getFileExists()) {
                    controller.saveAsCommand();
                } else {
                    controller.saveCommand(getFilePath(), getFileData());
                    frame.dispose();
                }
            } else if (option == 1) {
                frame.dispose();
            }
        } else {
            frame.dispose();
        }
    }

    public void showUnsavedFile() {
        int option = JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Save", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            if (!controller.getFileExists()) {
                controller.saveAsCommand();
            } else {
                controller.saveCommand(getFilePath(), getFileData());
            }
        }
    }

    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    public void openNewWindow() {
        new Viewer();
    }

    public void goTo() {
        try {
            String input = JOptionPane.showInputDialog(frame, "Line number:", "Go To Line", JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return;
            }
            int inputInt = Integer.parseInt(input);
            int goLine = textArea.getLineStartOffset(inputInt - 1);
            textArea.setCaretPosition(goLine);
        } catch (NumberFormatException | BadLocationException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid line number (numeric input).");
            goTo();
        }
    }

    public void showTimeDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        int caretPosition = textArea.getCaretPosition();
        textArea.insert(dateString, caretPosition);
    }

    public void aboutProgram() {
        JDialog dialog = new JDialog(frame, "Notepad: Documentation", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(460, 450);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(frame);

        String imagesDir = System.getProperty("user.dir") + "\\images\\";

        JLabel logoLabel = new JLabel(new ImageIcon(imagesDir + "O.png"));
        logoLabel.setBounds(90, 4, 60, 64);

        JLabel versionLabel = new JLabel("BM-2");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        versionLabel.setBounds(150, 10, 300, 50);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setOpaque(false);
        editorPane.setBackground(Color.WHITE);
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        String htmlText = "<html>" +
                "<head>\n" +
                "<meta charset=\"UTF-8\"></head>" +
                "<body style='font-family: Arial; font-size: 8px;'>" +
                "<br/><br/>" +
                "<strong>BM-2</strong> <br>" +
                "Version 1.0.0 <br/>" +
                "&#169; KTMU Corporation(BM-2). All rights <br/>" +
                "<br/><br/>" +
                "<strong>Notepad MVC Desktop Application Software</strong> <br/>" +
                "The user interface therein is protected by<br/>" +
                "trademarks and other intellectual property rights <br/>" +
                "in Kyrgyzstan and other countries." +
                "<br/><br/><br/><br/><br/><br/>" +
                "The product is licensed under the terms of the <br/>" +
                "<a href=\"https://www.microsoft.com/ru-ru/servicesagreement/\"> Intern Labs 5.0 Software License Agreement.</a>";
        editorPane.setText(htmlText);
        editorPane.setBounds(45, 75, 400, 260);
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JLabel logoNotepadLabel = new JLabel(new ImageIcon(imagesDir + "icon.png"));
        logoNotepadLabel.setVerticalAlignment(SwingConstants.TOP);
        logoNotepadLabel.setBounds(15, 105, 40, 40);

        JButton okButton = new JButton("OK");
        okButton.setBounds(350, 370, 80, 30);
        okButton.setFocusPainted(false);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        JPanel mainPanel = new JPanel();

        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        mainPanel.add(logoLabel);
        mainPanel.add(versionLabel);
        mainPanel.add(editorPane);
        mainPanel.add(logoNotepadLabel);
        mainPanel.add(okButton);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public String getFilePath() {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        return filePath;
    }

    public String getFileData() {
        String fileData = textArea.getText();
        return fileData;
    }

    public boolean getFileSaved() {
        return fileSaved;
    }

    public void setFileSaved(boolean temp) {
        fileSaved = temp;
    }

    public String getFileName() {
        if (controller.getFileExists()) {
            fileName = fileChooser.getSelectedFile().getName();
        }
        return fileName;
    }
}

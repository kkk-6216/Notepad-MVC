import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.CaretListener;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;

public class Controller implements ActionListener, CaretListener {
    private Viewer viewer;
    private File currentFile;
    private boolean isDarkTheme = false;
    private File file;
    private boolean fileExists = false;
    private boolean isAutoSaveEnabled = false;
    private Map<String, Runnable> commandMap;
    private Timer autoSaveTimer;

    public Controller(Viewer viewer) {
        this.viewer = viewer;
        this.commandMap = new HashMap<>();
        autoSaveTimer = new Timer(8000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileExists && currentFile != null) {
                    String data = viewer.getContentTextArea();
                    saveCommand(currentFile.getAbsolutePath(), data);
                    viewer.setFileSaved(true);
                }
            }
        });
        autoSaveTimer.setRepeats(true);
        initCommandMap();
    }

    private void initCommandMap() {
        commandMap.put("New_Document", this::newDocument);
        commandMap.put("Open_Document", this::openDocument);
        commandMap.put("Save_Document", this::saveDocument);
        commandMap.put("Save_As_Document", this::saveAsCommand);
        commandMap.put("Auto_Save", this::autoSave);
        commandMap.put("Print", this::printDocument);
        commandMap.put("Exit", this::exit);

        commandMap.put("Undo", this::undo);
        commandMap.put("Redo", this::redo);
        commandMap.put("Cut", this::textAreaCut);
        commandMap.put("Copy", this::textAreaCopy);
        commandMap.put("Paste", this::textAreaPaste);
        commandMap.put("Delete", this::textAreaDelete);
        commandMap.put("Find", this::find);
        commandMap.put("Find_Next", this::findNext);
        commandMap.put("Find_Previous", this::findPrev);
        commandMap.put("Replace", this::showReplaceDialog);
        commandMap.put("Go_to", this::goTo);
        commandMap.put("Select_All", this::textAreaSelectAll);
        commandMap.put("Time/Date", this::showTimeDate);

        commandMap.put("Themes", this::theme);
        commandMap.put("Font", this::changeFont);

        commandMap.put("Status_Bar", this::statusBar);

        commandMap.put("Check_Info", this::checkInfo);
        commandMap.put("About_Program", this::aboutProgram);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        Runnable action = commandMap.get(command);
        if (action != null) {
            action.run();
        } else {
            viewer.showErrorMessage();
        }
    }

    private boolean saveDataToFile(File file, String data) {
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(file));
            out.println(data);
            out.flush();
            out.close();
            return true;
        } catch (IOException ioe) {
            System.out.println("Error " + ioe);
            return false;
        }
    }

    private String readFile(File file) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            List<String> list = new ArrayList<String>();
            String line;
            while ((line = in.readLine()) != null) {
                list.add(line);
            }
            String content = String.join("\n", list);
            return content;
        } catch (FileNotFoundException fne) {
            System.out.println("Error " + fne);
            return null;
        } catch (IOException ioe) {
            System.out.println("Error " + ioe);
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    System.out.println("Error " + ioe);
                }
            }
        }
    }

    public void caretUpdate(CaretEvent e) {
        int dot = e.getDot();
        viewer.updateCaret(dot);
    }

    public void saveDocument() {
        if (fileExists) {
            String data = viewer.getContentTextArea();
            if (currentFile != null) {
                saveCommand(currentFile.getAbsolutePath(), data);
            } else {
                saveAsCommand();
            }
        } else {
            saveAsCommand();
        }
    }

    public void saveCommand(String filePath, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileExists = true;
        viewer.setFileSaved(true);
    }

    public void saveAsCommand() {
        File file = viewer.showFileDialog("Save_As");
        if (file != null) {
            String data = viewer.getContentTextArea();
            if (saveDataToFile(file, data)) {
                fileExists = true;
                viewer.setFileSaved(true);
                currentFile = file;
            } else {
                viewer.showErrorMessage();
            }
        }
    }


    public void autoSave() {
        if (!isAutoSaveEnabled) {
            isAutoSaveEnabled = true;
            autoSaveTimer.start();
            viewer.setAutoSaveIcon(new ImageIcon("images/galo.png"));
        } else {
            isAutoSaveEnabled = false;
            autoSaveTimer.stop();
            viewer.setAutoSaveIcon(new ImageIcon("images/krest.png"));
        }
    }

    public boolean getFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean temp) {
        fileExists = temp;
    }

    public void openDocument() {
        File newFile = viewer.showFileDialog("Open");
        autoSaveTimer.stop();
        isAutoSaveEnabled = false;
        viewer.setAutoSaveIcon(new ImageIcon("images/krest.png"));

        if (newFile != null) {
            String content = readFile(newFile);
            if (content != null) {
                viewer.update(content);
                currentFile = newFile;

            } else {
                viewer.showErrorMessage();
            }
            fileExists = true;
            viewer.setFileSaved(false);
        }
    }


    public void newDocument() {
        viewer.openNewWindow();
    }

    public void printDocument() {
        String data = viewer.getContentTextArea();
        java.awt.Font font = viewer.getFontTextArea();
        PrintDocument document = new PrintDocument(data, font);
        if (fileExists) {
            file = viewer.showFileDialog("Save");
            document.setName(viewer.getFileName());
            System.out.println("document is saved");
            document.printDocument();
        } else {
            saveAsCommand();
            document.setName(viewer.getFileName());
            document.printDocument();
            System.out.println("document is saved for the first time");
        }
    }

    public void changeFont() {
        viewer.changeFont();
    }

    public void textAreaCut() {
        viewer.textAreaCut();
    }

    public void textAreaCopy() {
        viewer.textAreaCopy();
    }

    public void textAreaPaste() {
        viewer.textAreaPaste();
    }

    public void textAreaDelete() {
        viewer.textAreaDelete();
    }

    public void showReplaceDialog() {
        viewer.showReplaceDialog();
    }

    public void find() {
        viewer.find();
    }

    public void findNext() {
        viewer.findNext();
    }

    public void findPrev() {
        viewer.findPrev();
    }

    public void statusBar() {
        viewer.statusBar();
    }

    public void undo() {
        viewer.undo();
    }

    public void exit() {
        viewer.callExitDialog();
    }

    public void textAreaSelectAll() {
        viewer.textAreaSelectAll();
    }

    public void redo() {
        viewer.redo();
    }

    public void theme() {
        viewer.applySelectedTheme();
    }

    public void aboutProgram() {
        viewer.aboutProgram();
    }

    public void checkInfo() {
        viewer.checkInfo();
    }

    public void goTo() {
        viewer.goTo();
    }

    public void showTimeDate() {
        viewer.showTimeDate();
    }
}

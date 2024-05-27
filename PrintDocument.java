import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;

public class PrintDocument implements Printable {
    private final String data;
    private int[] pageBreaks;
    private String[] textLines;
    private ArrayList<String> textLinesFinal;
    private Font font;
    private String name = "New document";

    public PrintDocument(String data, Font font) {
        this.data = data;
        this.font = font;
    }

    private String[] subStringAndRemainder(FontMetrics metrics, String line, Integer widthArea) {
        String[] result = new String[2];
        String text = "";
        int lineWidth = 0;
        for (int i = 0; i < line.length(); i++) {
            char symbol = line.charAt(i);
            int widthSymbol = metrics.charWidth(symbol);
            lineWidth += widthSymbol;
            if (lineWidth > widthArea) {
                result[0] = text;
                result[1] = line.substring(i);
                return result;
            }
            text += symbol;
        }
        result[0] = text;
        result[1] = "";
        return result;
    }

    private void initializationText(FontMetrics metrics, PageFormat pf) {
        String[] subStrAndRemainder = new String[2];
        int widthArea = (int) pf.getImageableWidth() - 140;
        textLinesFinal = new ArrayList<>();

        if (textLines == null) {
            textLines = data.split("\n");

            for (String line : textLines) {
                int lineSize = metrics.stringWidth(line);

                if (line.equals("")) {
                    textLinesFinal.add("\n");
                    continue;
                }

                while (lineSize > 0) {
                    lineSize = metrics.stringWidth(line);
                    subStrAndRemainder = subStringAndRemainder(metrics, line, widthArea);
                    if (!subStrAndRemainder[0].equals("")) {
                        textLinesFinal.add(subStrAndRemainder[0]);
                    }
                    line = subStrAndRemainder[1];
                }
            }
        }
    }

    public void printDocument() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
                javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(), "Print Success!");
            } catch (PrinterException ex) {
                javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(), "Error!");
            }
        }
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) {
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        int lineHeight = metrics.getHeight();

        if (pageBreaks == null) {
            initializationText(metrics, pf);
            int linesPerPage = ((int) pf.getImageableHeight() - 160) / lineHeight;
            int numBreaks = (textLinesFinal.size() - 1) / linesPerPage;
            pageBreaks = new int[numBreaks];

            for (int b = 0; b < numBreaks; b++) {
                pageBreaks[b] = (b + 1) * linesPerPage;
            }
        }

        if (pageIndex > pageBreaks.length) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        int y = 100;
        int x = 70;
        int widthPage = (int) pf.getImageableWidth();
        int heightPage = (int) pf.getImageableHeight();
        int width = (int) pf.getImageableWidth() - 2 * x;
        int start = (pageIndex == 0) ? 0 : pageBreaks[pageIndex - 1];
        int end = (pageIndex == pageBreaks.length) ? textLinesFinal.size() : pageBreaks[pageIndex];

        for (int i = start; i < end; i++) {
            g.drawString(textLinesFinal.get(i), x, y);
            y += lineHeight;
        }

        Font fontColumnar = new Font("Arial", Font.ITALIC, 12);
        g.setFont(fontColumnar);

        FontMetrics fntmetr = g.getFontMetrics(fontColumnar);

        String page = "Page " + (pageIndex + 1);
        int columnUpSize = fntmetr.stringWidth(name);
        int columnDownSize = fntmetr.stringWidth(page);

        g.drawString(name, (widthPage - columnUpSize) / 2, 50);
        g.drawString(page, (widthPage - columnDownSize) / 2, heightPage - 50);

        return PAGE_EXISTS;
    }

    public void setName(String fileName) {
        name = fileName;
    }
}

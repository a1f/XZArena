import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditorPanel extends JPanel implements ActionListener {

    public EditorPanel(XZArena xzArena) {
        this.xzArena = xzArena;

        interactiveWindow = new JTextPane();
        interactiveWindow.setEditable(false);
        interactiveWindow.setMargin(new Insets(10, 10, 10, 10));
        interactiveWindow.setBackground(Color.BLACK);
        interactiveWindow.setForeground(Color.GREEN);
        interactiveWindow.setFont(new Font("Monospaced", Font.PLAIN, 14));
        interactiveWindow.setMinimumSize(new Dimension(150, 60));

        normalStyle = interactiveWindow.addStyle("Normal Style", null);
        StyleConstants.setForeground(normalStyle, Color.WHITE);
        StyleConstants.setBold(normalStyle, false);
        errorStyle = interactiveWindow.addStyle("Error Style", null);
        StyleConstants.setForeground(errorStyle, Color.RED);
        StyleConstants.setBold(errorStyle, true);

        JScrollPane scrollPane = new JScrollPane(interactiveWindow);

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridwidth = 5;
        constraints.gridheight = 4;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.8;
        constraints.weighty = 0.5;
        constraints.fill = GridBagConstraints.BOTH;
        this.add(scrollPane, constraints);

        reloadConfigButton = new JButton("Reload configuration");
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 5;
        constraints.gridy = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.25;
        constraints.ipady = 5;
        constraints.insets = new Insets(10, 10, 3, 0);
        reloadConfigButton.setPreferredSize(new Dimension(135, 20));
        reloadConfigButton.setMaximumSize(new Dimension(150, 20));
        reloadConfigButton.setMargin(new Insets(0, 0, 0, 0));
        reloadConfigButton.addActionListener(this);
        this.add(reloadConfigButton, constraints);

        regenerateButton = new JButton("Regenerate code");
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 2;
        constraints.insets = new Insets(3, 10, 10, 0);
        regenerateButton.setPreferredSize(new Dimension(135, 20));
        regenerateButton.setMaximumSize(new Dimension(150, 20));
        regenerateButton.setMargin(new Insets(0, 0, 0, 0));
        regenerateButton.addActionListener(this);
        this.add(regenerateButton, constraints);

        TitledBorder border = new TitledBorder(new EmptyBorder(5, 3, 3, 1), "XZ Arena");
        border.setTitleColor(Color.decode("0xccff99"));
        this.setBorder(border);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == reloadConfigButton) {
            this.showLine("Reload configuration");
            xzArena.initialize();
        } else if (src == regenerateButton) {
            this.showLine("Generate code");
             //xzArena.generateCode(true);
        }
    }

    public void showLine(String message) {
        appendText(message + "\n", normalStyle);
    }

    private void appendText(String text, Style style) {
        StyledDocument doc = interactiveWindow.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
        }
    }

    public void clear() {
        interactiveWindow.setText("");
    }

    private Style normalStyle, errorStyle;
    private XZArena xzArena;
    private JTextPane interactiveWindow;
    private JButton reloadConfigButton;
    private JButton regenerateButton;
}
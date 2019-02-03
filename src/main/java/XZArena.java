import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.util.syntaxhighlighter.Language;

import javax.swing.*;

public class XZArena {
    final static String path = "/Users/alf/acm/alf/_/";
    private EditorPanel talkingWindow;
    private boolean initialized;
    private String problemName;

    private void setProblemName(int score) {
//        this.problemName = "tc_" + CodeGenerator.getProblemLetter(score);
    }

    public XZArena() {
        talkingWindow = new EditorPanel(this);
        this.initialized = false;
    }

    public String getSignature() {
        return "Powered by Alex Fetisov";
    }

    public JPanel getEditorPanel() {
        return talkingWindow;
    }

    public String getSource() {
        talkingWindow.showLine("Getting source code. Make sure you have compiled the code for " + this.problemName);
//        String result = CodeGenerator.generateSubmit(problemName);
        String result = "";
        if (result == null) {
            talkingWindow.showLine("CANNOT GENERATE SOLUTION!");
        }
        return result;
    }

    public void setProblemComponent(ProblemComponentModel componentModel, Language language, com.topcoder.shared.problem.Renderer renderer) {
        if (!initialized) {
            return;
        }
        this.setProblemName(componentModel.getPoints().intValue());
        talkingWindow.showLine("Changing problem name to " + this.problemName);
        talkingWindow.showLine("Generating code...");
//        CodeGenerator.generate(componentModel);
        talkingWindow.showLine("Done!");

    }

    public void setSource(String source) {
        talkingWindow.showLine("set source is not implemented");
    }

    public void startUsing() {
        talkingWindow.clear();
        talkingWindow.showLine("startUsing...");
        if (!initialized) {
            talkingWindow.showLine(String.format("Greetings from XZArena"));
            initialize();
        } else {
            talkingWindow.showLine(String.format("Hello again :)"));
        }
    }

    private void initialize() {
        try {
            talkingWindow.showLine("Initializing... ");
            this.initialized = true;
            talkingWindow.showLine("Done!");
        } catch (Throwable e) {
            talkingWindow.showLine("Failed!");
        } finally {
            talkingWindow.showLine("finally...");
        }

    }
}
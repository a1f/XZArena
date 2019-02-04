import javax.swing.*;

public class XZArena {
    private EditorPanel talkingWindow;
    private boolean initialized;
    private String problemName;

    private void setProblemName(int score) {
        this.problemName = "tc_" + CodeGenerator.getProblemLetter(score);
    }

    public XZArena() {
        MyLogger.getInstance().log("Version 2");
        talkingWindow = new EditorPanel(this);
        this.initialized = false;
    }

    public String getSignature() {
        MyLogger.getInstance().log("Signature");
        return "Powered by Alex Fetisov";
    }

    public JPanel getEditorPanel() {
        return talkingWindow;
    }

    public String getSource() {
        talkingWindow.showLine("Getting source code. Make sure you have compiled the code for " + this.problemName);
        String result = CodeGenerator.generateSubmit(problemName);
        if (result == null) {
            talkingWindow.showLine("CANNOT GENERATE SOLUTION!");
        }
        return result;
    }

    public void setProblemComponent(com.topcoder.client.contestant.ProblemComponentModel componentModel, com.topcoder.shared.language.Language language, com.topcoder.shared.problem.Renderer renderer) {
        talkingWindow.showLine("setProblemComponent begin...");
        if (!initialized) {
            return;
        }
        this.setProblemName(componentModel.getPoints().intValue());
        talkingWindow.showLine("Changing problem name to " + this.problemName);
        talkingWindow.showLine("Generating code...");
        CodeGenerator.generate(componentModel);
        talkingWindow.showLine("Done!");

    }

    public void setSource(String source) {
        talkingWindow.showLine("set source is not implemented");
    }

    public void startUsing() {
        talkingWindow.clear();
        talkingWindow.showLine("startUsing...");
        if (!initialized) {
            talkingWindow.showLine("Greetings from XZArena");
            initialize();
        } else {
            talkingWindow.showLine("Hello again :)");
        }
    }

    void initialize() {
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
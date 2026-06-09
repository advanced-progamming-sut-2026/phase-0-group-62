import controller.Controller;
import model.Model;
import view.TerminalView;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        TerminalView view = new TerminalView();
        Controller controller = new Controller(model, view);

        controller.start();
    }
}


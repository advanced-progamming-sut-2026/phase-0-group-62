package view.greenhouse;

import model.greenhouse.Greenhouse;
import view.View;

public class GreenhouseView extends View {
    public void showGreenhouse(Greenhouse greenhouse) {
        showMessage("Greenhouse pots: " + greenhouse.getPots().size());
    }
}


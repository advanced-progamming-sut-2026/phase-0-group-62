package model.greenhouse;

import java.util.ArrayList;
import java.util.List;

public class Greenhouse {
    private final List<Pot> pots;

    public Greenhouse() {
        this.pots = new ArrayList<>();
    }

    public void addPot(Pot pot) {
        pots.add(pot);
    }

    public List<Pot> getPots() {
        return pots;
    }
}


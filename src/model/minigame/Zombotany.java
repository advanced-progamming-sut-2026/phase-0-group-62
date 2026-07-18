package model.minigame;

import model.Game;
import model.entities.zombie.Zombie;
import model.entities.plant.Plant;
import java.util.ArrayList;
import java.util.List;

public class Zombotany extends MiniGame {
    private int stageDifficulty;

    public Zombotany() {
        super("Zombotany");
        this.stageDifficulty = 1;
    }

    public int getStageDifficulty() {
        return stageDifficulty;
    }

    public void setStageDifficulty(int stageDifficulty) {
        this.stageDifficulty = stageDifficulty;
    }

    public void updateMiniGame(Game game) {
        for (Zombie zombie : new ArrayList<>(game.getActiveZombies())) {
            if (zombie.getName().equalsIgnoreCase("PeashooterZombie") && game.getTickCount() % 15 == 0) {
                for (int col = (int) zombie.getX(); col >= 0; col--) {
                    Plant p = game.getPlantAt(col, zombie.getY());
                    if (p != null && p.isAlive()) {
                        p.takeDamage(20);
                        game.getScoreGame().onDamageTaken(20);
                        System.out.println("Zombotany: PeashooterZombie shot a pea and hit " + p.getName() + " at (" + col + ", " + zombie.getY() + ")");
                        if (!p.isAlive()) {
                            game.getActivePlants().remove(p);
                            game.getBoard().getTile(p.getY(), p.getX()).setPlant(null);
                        }
                        break;
                    }
                }
            }
            if (zombie.getName().equalsIgnoreCase("JalapenoZombie")) {
                zombie.incrementJalapenoTimer();
                if (zombie.getZombotanyJalapenoTimer() >= 100) {
                    System.out.println("Zombotany: JalapenoZombie exploded and incinerated lane " + zombie.getY() + "!");
                    List<Plant> toBurn = new ArrayList<>();
                    for (Plant p : game.getActivePlants()) {
                        if (p.getY() == zombie.getY()) toBurn.add(p);
                    }
                    game.getActivePlants().removeAll(toBurn);
                    for (Plant bp : toBurn) {
                        game.getBoard().getTile(bp.getY(), bp.getX()).setPlant(null);
                    }
                    game.getActiveZombies().remove(zombie);
                    game.getBoard().getTile(zombie.getY(), (int) zombie.getX()).setZombie(null);
                    continue;
                }
            }
            if (!zombie.hasEffect(model.entities.zombie.ZombieEffect.FROZEN)) {
                Plant targetPlant = game.getPlantAt((int) zombie.getX(), zombie.getY());
                if (zombie.getName().equalsIgnoreCase("SquashZombie") && targetPlant != null) {
                    System.out.println("Zombotany: SquashZombie squashed " + targetPlant.getName() + " انتحاری!");
                    game.getActivePlants().remove(targetPlant);
                    game.getBoard().getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                    game.getActiveZombies().remove(zombie);
                    game.getBoard().getTile(zombie.getY(), (int) zombie.getX()).setZombie(null);
                }
            }
        }
    }
}
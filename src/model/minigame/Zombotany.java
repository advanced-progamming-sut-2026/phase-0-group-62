package model.minigame;

import model.Game;
import model.entities.zombie.Zombie;
import model.entities.plant.Plant;
import model.entities.zombie.factory.ZombieFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Zombotany extends MiniGame {
    private int stageDifficulty;
    private int maxStage;
    private int zombiesDefeated;
    private int targetZombies;

    public Zombotany() {
        super("Zombotany");
        this.stageDifficulty = 1;
        this.maxStage = 3;
        this.zombiesDefeated = 0;
        this.targetZombies = 15;
    }

    public int getStageDifficulty() {
        return stageDifficulty;
    }

    public void setStageDifficulty(int stageDifficulty) {
        this.stageDifficulty = Math.min(stageDifficulty, maxStage);
        updateStageParameters();
    }

    private void updateStageParameters() {
        switch (stageDifficulty) {
            case 1:
                targetZombies = 15;
                break;
            case 2:
                targetZombies = 25;
                break;
            case 3:
                targetZombies = 35;
                break;
            default:
                targetZombies = 15;
        }
    }

    public void updateMiniGame(Game game) {
        // First tick setup
        if (game.getTickCount() == 1) {
            // Ensure we have all hybrid zombie types
            // Remove any existing zombies and spawn initial wave
            for (Zombie z : new ArrayList<>(game.getActiveZombies())) {
                game.getBoard().getTile(z.getY(), (int) z.getX()).setZombie(null);
                game.removeZombie(z);
            }
            spawnHybridWave(game);
            game.getGameLogMessages().add("Zombotany: Stage " + stageDifficulty + " started! Target: " + targetZombies + " kills");
        }

        // Check win condition
        if (zombiesDefeated >= targetZombies) {
            if (stageDifficulty < maxStage) {
                completeLevel(stageDifficulty, zombiesDefeated);
                stageDifficulty++;
                updateStageParameters();
                zombiesDefeated = 0;
                game.getGameLogMessages().add("Zombotany: Stage " + (stageDifficulty - 1) + " complete! Moving to Stage " + stageDifficulty);
                // Spawn new wave
                for (Zombie z : new ArrayList<>(game.getActiveZombies())) {
                    game.getBoard().getTile(z.getY(), (int) z.getX()).setZombie(null);
                    game.removeZombie(z);
                }
                spawnHybridWave(game);
                return;
            } else {
                game.setWon(true);
                game.stop();
                game.getGameLogMessages().add("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
                return;
            }
        }

        // Process hybrid zombie behaviors
        for (Zombie zombie : new ArrayList<>(game.getActiveZombies())) {
            // Peashooter Zombie - shoots peas
            if (zombie.getName().equalsIgnoreCase("PeashooterZombie") && game.getTickCount() % 15 == 0) {
                for (int col = (int) zombie.getX(); col >= 0; col--) {
                    Plant p = game.getPlantAt(col, zombie.getY());
                    if (p != null && p.isAlive()) {
                        p.takeDamage(20);
                        game.getScoreGame().onDamageTaken(20);
                        game.getGameLogMessages().add("Zombotany: PeashooterZombie shot a pea and hit " + p.getName() + " at (" + col + ", " + zombie.getY() + ")");
                        if (!p.isAlive()) {
                            game.getActivePlants().remove(p);
                            game.getBoard().getTile(p.getY(), p.getX()).setPlant(null);
                        }
                        break;
                    }
                }
            }

            // Jalapeno Zombie - explodes after 100 ticks (10 seconds)
            if (zombie.getName().equalsIgnoreCase("JalapenoZombie")) {
                zombie.incrementJalapenoTimer();
                if (zombie.getZombotanyJalapenoTimer() >= 100) {
                    game.getGameLogMessages().add("Zombotany: JalapenoZombie exploded and incinerated lane " + zombie.getY() + "!");
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

            // Squash Zombie - kills adjacent plant
            if (!zombie.hasEffect(model.entities.zombie.ZombieEffect.FROZEN)) {
                Plant targetPlant = game.getPlantAt((int) zombie.getX(), zombie.getY());
                if (zombie.getName().equalsIgnoreCase("SquashZombie") && targetPlant != null) {
                    game.getGameLogMessages().add("Zombotany: SquashZombie squashed " + targetPlant.getName() + " at (" + targetPlant.getX() + ", " + targetPlant.getY() + ")!");
                    game.getActivePlants().remove(targetPlant);
                    game.getBoard().getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                    game.getActiveZombies().remove(zombie);
                    game.getBoard().getTile(zombie.getY(), (int) zombie.getX()).setZombie(null);
                    continue;
                }
            }

            // Wall-nut Zombie - just has lots of HP, moves slow (handled by stats)
        }

        // Count defeated zombies and remove dead ones
        List<Zombie> toRemove = new ArrayList<>();
        for (Zombie z : game.getActiveZombies()) {
            if (!z.isAlive()) {
                toRemove.add(z);
                zombiesDefeated++;
                game.getScoreGame().onZombieKilled(z, game);
                game.getGameLogMessages().add("Zombotany: " + z.getName() + " defeated! (" + zombiesDefeated + "/" + targetZombies + ")");
            }
        }
        for (Zombie z : toRemove) {
            game.getActiveZombies().remove(z);
            game.getBoard().getTile(z.getY(), (int) z.getX()).setZombie(null);
        }

        // Spawn new hybrid zombies periodically
        if (game.getTickCount() % 150 == 0 && game.getActiveZombies().size() < 5) {
            spawnHybridWave(game);
        }

        // Check loss condition - zombies reach house
        for (Zombie z : game.getActiveZombies()) {
            if (z.getX() <= 0) {
                game.setLost(true);
                game.stop();
                game.getGameLogMessages().add("The zombie ate your brain; LOSER!!!");
                return;
            }
        }
    }

    private void spawnHybridWave(Game game) {
        Random rand = new Random();
        String[] hybridTypes = {"PeashooterZombie", "WallnutZombie", "JalapenoZombie", "SquashZombie"};
        int numZombies = 2 + stageDifficulty + rand.nextInt(stageDifficulty + 1);

        for (int i = 0; i < numZombies; i++) {
            String type = hybridTypes[rand.nextInt(hybridTypes.length)];
            int lane = rand.nextInt(game.getBoard().getRows());
            int spawnCol = game.getBoard().getColumns() - 1;
            
            Zombie z = ZombieFactory.createZombieAtColumn(type, lane, spawnCol);
            if (z == null) {
                // Fallback creation
                int hp = 200;
                double speed = 0.5;
                int damage = 20;
                if (type.equalsIgnoreCase("WallnutZombie")) {
                    hp = 600;
                    speed = 0.2;
                } else if (type.equalsIgnoreCase("SquashZombie")) {
                    speed = 2.0;
                    damage = 50;
                } else if (type.equalsIgnoreCase("JalapenoZombie")) {
                    hp = 150;
                    speed = 0.6;
                }
                z = new Zombie(type, hp, speed, damage);
                z.setX(spawnCol);
                z.setY(lane);
            }
            
            // Scale with stage difficulty
            if (stageDifficulty >= 2) {
                z.setHealth(z.getMaxHealth() + 50);
            }
            if (stageDifficulty >= 3) {
                z.setDamage(z.getDamage() + 10);
            }
            
            game.addZombie(z);
            game.getBoard().getTile(lane, spawnCol).setZombie(z);
        }
        game.getGameLogMessages().add("Zombotany: Hybrid zombie wave spawned!");
    }
}

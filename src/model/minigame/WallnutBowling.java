package model.minigame;

import model.Game;
import model.entities.plant.Plant;
import model.entities.zombie.Zombie;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WallnutBowling extends MiniGame {
    private int deadlineColumn;
    private int redLineX;

    public WallnutBowling() {
        super("WallnutBowling");
        this.deadlineColumn = 3;
        this.redLineX = 3;
    }

    public int getDeadlineColumn() {
        return deadlineColumn;
    }

    public void setDeadlineColumn(int deadlineColumn) {
        this.deadlineColumn = deadlineColumn;
    }

    public int getRedLineX() {
        return redLineX;
    }

    public void setRedLineX(int redLineX) {
        this.redLineX = redLineX;
    }

    public void updateMiniGame(Game game) {
        if (game.getTickCount() == 1 || game.getTickCount() % 120 == 0) {
            Random rand = new Random();
            String roll = rand.nextInt(100) < 70 ? "Bowling Wallnut" : (rand.nextInt(100) < 50 ? "Explode O' Nut" : "Giant Wallnut");
            game.getConveyorBeltPlants().add(roll);
        }

        for (Plant ball : new ArrayList<>(game.getActivePlants())) {
            if (ball.isBowlingBall()) {
                if (game.getTickCount() % 5 == 0) {
                    game.getBoard().getTile(ball.getY(), ball.getX()).setPlant(null);
                    ball.setX(ball.getX() + ball.getDx());
                    ball.setY(ball.getY() + ball.getDy());

                    if (ball.getY() < 0) {
                        ball.setY(0);
                        ball.setDy(-ball.getDy());
                    } else if (ball.getY() >= game.getBoard().getRows()) {
                        ball.setY(game.getBoard().getRows() - 1);
                        ball.setDy(-ball.getDy());
                    }

                    if (ball.getX() >= game.getBoard().getColumns()) {
                        game.getActivePlants().remove(ball);
                        continue;
                    }

                    game.getBoard().getTile(ball.getY(), ball.getX()).setPlant(ball);

                    Zombie target = null;
                    for (Zombie z : game.getActiveZombies()) {
                        if (z.getY() == ball.getY() && (int) z.getX() == ball.getX()) {
                            target = z;
                            break;
                        }
                    }

                    if (target != null) {
                        if (ball.getName().equalsIgnoreCase("Giant Wallnut")) {
                            target.takeDamage(target.getMaxHealth(), false);
                        } else if (ball.getName().equalsIgnoreCase("Explode O' Nut")) {
                            List<Zombie> blastTargets = new ArrayList<>();
                            for (Zombie az : game.getActiveZombies()) {
                                if (Math.abs(az.getY() - ball.getY()) <= 1 && Math.abs((int) az.getX() - ball.getX()) <= 1) {
                                    blastTargets.add(az);
                                }
                            }
                            for (Zombie bt : blastTargets) {
                                bt.takeDamage(500, false);
                                if (!bt.isAlive()) {
                                    game.getActiveZombies().remove(bt);
                                    game.getBoard().getTile(bt.getY(), (int) bt.getX()).setZombie(null);
                                }
                            }
                            game.getActivePlants().remove(ball);
                            game.getBoard().getTile(ball.getY(), ball.getX()).setPlant(null);
                        } else {
                            target.takeDamage(200, false);
                            ball.incrementHitCount();
                            if (ball.getHitCount() == 1) {
                                ball.setDy(new Random().nextBoolean() ? 1 : -1);
                            } else {
                                ball.setDy(-ball.getDy());
                            }
                        }

                        if (target != null && !target.isAlive()) {
                            game.getActiveZombies().remove(target);
                            game.getBoard().getTile(target.getY(), (int) target.getX()).setZombie(null);
                            game.incrementZombiesKilled();
                        }
                    }
                }
            }
        }
    }
}
package view.menu;

import controller.menu.CommandParser;
import controller.menu.MenuController;
import controller.menu.GreenhouseController;
import controller.menu.ShopController;
import model.greenhouse.Greenhouse;
import model.shop.Shop;
import view.greenhouse.GreenhouseView;
import util.ParsedCommand;

public class GreenhouseMenu extends Menu {
    private final Greenhouse greenhouse;
    private final GreenhouseView greenhouseView;
    private final GreenhouseController greenhouseController;
    private final Shop shop;
    private final ShopController shopController;
    private boolean inShopMode;

    public GreenhouseMenu(MenuController controller) {
        super(controller);
        this.greenhouse = new Greenhouse();
        this.greenhouseView = new GreenhouseView();
        this.greenhouseController = new GreenhouseController(this.greenhouse, this.greenhouseView);
        this.shop = new Shop();
        this.shopController = new ShopController(this.shop, this.greenhouse);
        this.inShopMode = false;
    }

    @Override
    public void runMenu() {
        CommandParser parser = new CommandParser();
        while (true) {
            String prompt = inShopMode ? "ShopMenu" : "GreenhouseMenu";
            String input = view.getInput(prompt);

            if (input.equalsIgnoreCase("back")) {
                if (inShopMode) {
                    inShopMode = false;
                    view.showMessage("Returned to the greenhouse.");
                } else {
                    manager.setCurrentMenu(new PlayMenu(controller));
                    break;
                }
                continue;
            }

            ParsedCommand cmd = parser.parse(input);
            String action = cmd.getAction();

            if (!inShopMode) {
                if (action.equalsIgnoreCase("enter shop")) {
                    inShopMode = true;
                    view.showMessage("Welcome to the shop! Permanent items and daily offers are available.");
                } else if (action.equalsIgnoreCase("show greenhouse")) {
                    greenhouseController.showGreenhouse();
                } else if (action.equalsIgnoreCase("grow")) {
                    String rowStr = cmd.getArg("-x");
                    String colStr = cmd.getArg("-y");
                    if (rowStr == null || colStr == null) {
                        view.showMessage("Usage: grow -x <row> -y <col>");
                        continue;
                    }
                    try {
                        int r = Integer.parseInt(rowStr);
                        int c = Integer.parseInt(colStr);
                        view.showMessage(greenhouseController.plantPot(r, c));
                    } catch (NumberFormatException e) {
                        view.showMessage("Error: Coordinates must be integers.");
                    }
                } else if (action.equalsIgnoreCase("collect")) {
                    String rowStr = cmd.getArg("-x");
                    String colStr = cmd.getArg("-y");
                    if (rowStr == null || colStr == null) {
                        view.showMessage("Usage: collect -x <row> -y <col>");
                        continue;
                    }
                    try {
                        int r = Integer.parseInt(rowStr);
                        int c = Integer.parseInt(colStr);
                        view.showMessage(greenhouseController.collectPot(r, c));
                    } catch (NumberFormatException e) {
                        view.showMessage("Error: Coordinates must be integers.");
                    }
                } else if (action.equalsIgnoreCase("cheat add") || action.equalsIgnoreCase("menu cheat add")) {
                    view.showMessage(controller.processPlay(cmd, "cheat add"));
                } else {
                    String rowStr = cmd.getArg("-x");
                    String colStr = cmd.getArg("-y");
                    if (input.toLowerCase().startsWith("unlock") && rowStr != null && colStr != null) {
                        try {
                            int r = Integer.parseInt(rowStr);
                            int c = Integer.parseInt(colStr);
                            view.showMessage(greenhouseController.unlockPot(r, c));
                        } catch (NumberFormatException e) {
                            view.showMessage("Error: Coordinates must be integers.");
                        }
                    } else if (input.toLowerCase().startsWith("accelerate") && rowStr != null && colStr != null) {
                        try {
                            int r = Integer.parseInt(rowStr);
                            int c = Integer.parseInt(colStr);
                            view.showMessage(greenhouseController.acceleratePot(r, c));
                        } catch (NumberFormatException e) {
                            view.showMessage("Error: Coordinates must be integers.");
                        }
                    } else {
                        view.showMessage("Unknown or incomplete command in Greenhouse Menu.");
                    }
                }
            } else {
                if (action.equalsIgnoreCase("shop list")) {
                    view.showMessage(shopController.showShopList());
                } else if (action.equalsIgnoreCase("shop daily")) {
                    view.showMessage(shopController.showDailyOffer());
                } else if (action.equalsIgnoreCase("shop buy")) {
                    view.showMessage(shopController.buyItem(cmd));
                } else if (action.equalsIgnoreCase("cheat add") || action.equalsIgnoreCase("menu cheat add")) {
                    view.showMessage(controller.processPlay(cmd, "cheat add"));
                } else {
                    view.showMessage("Unknown command in Shop Mode. Available commands: 'shop list', 'shop daily', 'shop buy', 'back'.");
                }
            }
        }
    }
}
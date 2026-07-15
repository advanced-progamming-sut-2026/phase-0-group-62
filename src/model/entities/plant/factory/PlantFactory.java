package model.entities.plant.factory;

import model.entities.plant.Plant;
import model.entities.plant.loader.PlantLoader;
import java.util.List;

public class PlantFactory {
    private static final List<Plant> templates = PlantLoader.loadPlants();

    public static Plant createPlant(String type) {
        for (Plant template : templates) {
            if (template.getName().equalsIgnoreCase(type)) {
                Plant p = new Plant(
                        template.getId(),
                        template.getName(),
                        template.getCategory(),
                        template.getTags(),
                        template.getCost(),
                        template.getBaseHp(),
                        template.getDamage(),
                        template.getActionInterval(),
                        template.getRecharge(),
                        template.getAbilityType(),
                        template.getAbilityValue(),
                        template.getPlantFoodType(),
                        template.getPlantFoodValue()
                );
                p.initHealth();
                return p;
            }
        }
        return null;
    }
}
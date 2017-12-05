package com.unciv.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.unciv.civinfo.CityInfo;
import com.unciv.civinfo.TileInfo;



public class WorldTileGroup extends TileGroup {

    WorldTileGroup(TileInfo tileInfo) {
        super(tileInfo);
    }

    void setIsViewable(boolean isViewable) {
        if (isViewable) setColor(1, 1, 0, 1); // Only alpha really changes anything
        else setColor(0, 0, 0, 0.3f);
    }


    void update(WorldScreen worldScreen) {
        super.update();

        if(tileInfo.workingCity != null && populationImage==null) addPopulationIcon();
        if(tileInfo.workingCity == null && populationImage!=null) removePopulationIcon();


        if (tileInfo.owner != null && hexagon == null) {
            hexagon = ImageGetter.getImageByFilename("TerrainIcons/Hexagon.png");
            float imageScale = terrainImage.getWidth() * 1.3f / hexagon.getWidth();
            hexagon.setScale(imageScale);
            hexagon.setPosition((getWidth() - hexagon.getWidth() * imageScale) / 2,
                    (getHeight() - hexagon.getHeight() * imageScale) / 2);
            addActor(hexagon);
            hexagon.setZIndex(0);
        }


        final CityInfo city = tileInfo.getCity();
        if (tileInfo.isCityCenter()) {
            float buttonScale = 0.7f;
            if (cityButton == null) {
                cityButton = new Container<TextButton>();
                cityButton.setActor(new TextButton("", worldScreen.skin));

                cityButton.getActor().getLabel().setFontScale(buttonScale);

                final UnCivGame game = worldScreen.game;
                cityButton.getActor().addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.civInfo.currentCity = game.civInfo.cities.indexOf(city);
                        game.setScreen(new CityScreen(game));
                    }
                });

                addActor(cityButton);
//                cityButton.setColor(1,1,1,0.9f);
                setZIndex(getParent().getChildren().size); // so this tile is rendered over neighboring tiles
            }

            String cityButtonText = city.name +" ("+city.population+")";
//                    +" ("+city.population+")" + "\r\n" + city.cityBuildings.getCityProductionText();
            TextButton button = cityButton.getActor();
            button.setText(cityButtonText);
            button.setSize(button.getPrefWidth(), button.getPrefHeight());

            cityButton.setPosition((getWidth() - cityButton.getWidth()) / 2,
                    getHeight() * 0.9f);
            cityButton.setZIndex(cityButton.getParent().getChildren().size); // so city button is rendere over oeverything else in this tile
        }

    }
}

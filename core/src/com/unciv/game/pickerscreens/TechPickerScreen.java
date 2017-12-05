package com.unciv.game.pickerscreens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.unciv.game.UnCivGame;
import com.unciv.models.gamebasics.GameBasics;
import com.unciv.models.gamebasics.Technology;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class TechPickerScreen extends PickerScreen {

    HashMap<String, TextButton> techNameToButton = new HashMap<String, TextButton>();
    Technology SelectedTech;
    com.unciv.civinfo.CivilizationTech civTech = game.civInfo.tech;
    ArrayList<String> TechsToResearch = new ArrayList<String>(civTech.techsToResearch);

    public void SetButtonsInfo() {

        for (String techName : techNameToButton.keySet()) {
            TextButton TB = techNameToButton.get(techName);
            TB.getStyle().checkedFontColor = Color.BLACK;
            if (civTech.isResearched(techName)) TB.setColor(Color.GREEN);
            else if (TechsToResearch.contains(techName)) TB.setColor(Color.BLUE);
            else if (civTech.canBeResearched(techName)) TB.setColor(Color.WHITE);
            else TB.setColor(Color.GRAY);

            TB.setChecked(false);
            TB.setText(techName);

            if (SelectedTech != null) {
                Technology thisTech = GameBasics.Technologies.get(techName);
                if (techName.equals(SelectedTech.name)) {
                    TB.setChecked(true);
                    TB.setColor(TB.getColor().lerp(Color.LIGHT_GRAY, 0.5f));
                }

                if (thisTech.prerequisites.contains(SelectedTech.name)) TB.setText("*" + techName);
                else if (SelectedTech.prerequisites.contains(techName)) TB.setText(techName + "*");
            }
            if (TechsToResearch.contains(techName)) {
                TB.setText(TB.getText() + " (" + TechsToResearch.indexOf(techName) + ")");
            }

            if(!civTech.isResearched(techName)) TB.setText(TB.getText() + "\r\n" + game.civInfo.turnsToTech(techName) + " turns");
        }
    }

    public void selectTechnology(Technology tech) {
        SelectedTech = tech;
        descriptionLabel.setText(tech.description);

        if (civTech.isResearched(tech.name)) {
            rightSideButton.setText("Research");
            rightSideButton.setTouchable(Touchable.disabled);
            rightSideButton.setColor(Color.GRAY);
            SetButtonsInfo();
            return;
        }

        rightSideButton.setTouchable(Touchable.enabled);
        rightSideButton.setColor(Color.WHITE);

        if (civTech.canBeResearched(tech.name)) {
            TechsToResearch.clear();
            TechsToResearch.add(tech.name);
        } else {
            Stack<String> Prerequisites = new Stack<String>();
            ArrayDeque<String> CheckPrerequisites = new ArrayDeque<String>();
            CheckPrerequisites.add(tech.name);
            while (!CheckPrerequisites.isEmpty()) {
                String techNameToCheck = CheckPrerequisites.pop();
                if (civTech.isResearched(techNameToCheck))
                    continue; //no need to add or check prerequisites
                Technology techToCheck = GameBasics.Technologies.get(techNameToCheck);
                for (String str : techToCheck.prerequisites)
                    if (!CheckPrerequisites.contains(str)) CheckPrerequisites.add(str);
                Prerequisites.add(techNameToCheck);
            }
            TechsToResearch.clear();
            while (!Prerequisites.isEmpty()) TechsToResearch.add(Prerequisites.pop());
        }

        rightSideButton.setText("Research \r\n" + TechsToResearch.get(0));
        SetButtonsInfo();
    }

    public TechPickerScreen(final UnCivGame game) {
        super(game);

        Technology[][] techMatrix = new Technology[10][5]; // Divided into columns, then rows
        for (int i = 0; i < techMatrix.length; i++) {
            techMatrix[i] = new Technology[10];
        }

        for (Technology technology : GameBasics.Technologies.linqValues()) {
            techMatrix[technology.column.columnNumber][technology.row - 1] = technology;
        }

//        Table topTable = new Table();
        for (int i = 0; i < 10; i++) {
            topTable.row().pad(5);

            for (int j = 0; j < 8; j++) {
                final Technology tech = techMatrix[j][i];
                if (tech == null) topTable.add(); // empty cell
                else {
                    final TextButton TB = new TextButton("", skin);
                    techNameToButton.put(tech.name, TB);
                    TB.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            selectTechnology(tech);
                        }
                    });
                    topTable.add(TB);
                }
            }
            SetButtonsInfo();
        }

        rightSideButton.setText("Research");
        rightSideButton.setTouchable(Touchable.disabled);
        rightSideButton.setColor(Color.GRAY);
        rightSideButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                civTech.techsToResearch = TechsToResearch;
                game.setWorldScreen();
                dispose();
            }
        });
    }
}
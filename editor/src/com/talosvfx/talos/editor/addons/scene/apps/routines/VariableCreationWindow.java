package com.talosvfx.talos.editor.addons.scene.apps.routines;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.talosvfx.talos.editor.addons.scene.apps.routines.nodes.RoutineExposedVariableNodeWidget;
import com.talosvfx.talos.editor.addons.scene.apps.routines.runtime.RoutineInstance;
import com.talosvfx.talos.editor.addons.scene.utils.scriptProperties.PropertyWrapper;
import com.talosvfx.talos.editor.nodes.NodeListPopup;
import com.talosvfx.talos.editor.nodes.widgets.ValueWidget;
import com.talosvfx.talos.editor.notifications.Notifications;
import com.talosvfx.talos.editor.notifications.events.NodeCreatedEvent;
import com.talosvfx.talos.editor.project2.SharedResources;
import com.talosvfx.talos.editor.widgets.ui.common.SquareButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableCreationWindow extends Table {

    private static final Logger logger = LoggerFactory.getLogger(VariableCreationWindow.class);
    private DragAndDrop dragAndDrop;
    private Array<VariableTemplateRow<?>> templateRowArray = new Array<>();

    private RoutineStage routineStage;

    public VariableCreationWindow (RoutineStage routineStage) {
        this.routineStage = routineStage;
        setBackground(routineStage.skin.getDrawable("window-bg"));
        dragAndDrop = new DragAndDrop();
    }

    public void reloadWidgets() {
        clear();
        setSize(420, 300);
        defaults().pad(5);
        bottom().left();

        Table mainContent = new Table();
        mainContent.setSkin(routineStage.skin);
        mainContent.setBackground("background-fill");

        Table contentTable = new Table();
        contentTable.top().left();
        ScrollPane scrollPane = new ScrollPane(contentTable);
        scrollPane.setScrollingDisabled(true, false);

        RoutineInstance routineInstance = routineStage.routineInstance;
        Array<PropertyWrapper<?>> propertyWrappers = routineInstance.getPropertyWrappers();
        templateRowArray.clear();
        for (int i = 0; i < propertyWrappers.size; i++) {
            PropertyWrapper<?> propertyWrapper = propertyWrappers.get(i);
            VariableTemplateRow<?> variableTemplateRow = new VariableTemplateRow<>(propertyWrapper, routineStage);
            if (i == 0) {
                variableTemplateRow.textValueWidget.setType(ValueWidget.Type.TOP);
            } else if (i == propertyWrappers.size - 1) {
                variableTemplateRow.textValueWidget.setType(ValueWidget.Type.BOTTOM);
            } else {
                variableTemplateRow.textValueWidget.setType(ValueWidget.Type.MID);
            }

            if (propertyWrappers.size == 1) {
                variableTemplateRow.textValueWidget.setType(ValueWidget.Type.NORMAL);
            }

            contentTable.add(variableTemplateRow).growX();
            contentTable.row();
            templateRowArray.add(variableTemplateRow);
        }

        mainContent.add(scrollPane).grow();
        add(mainContent).grow();
        row();

        addButton();

        configureDragAndDrop();
    }

    private void configureDragAndDrop() {
        dragAndDrop.clear();
        for (VariableTemplateRow variableTemplateRow : templateRowArray) {
            dragAndDrop.addSource(new DragAndDrop.Source(variableTemplateRow) {
                @Override
                public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(routineStage.routineInstance.getPropertyWrapperWithIndex(variableTemplateRow.propertyWrapper.index));
                    Table payloadTable = new Table();
                    float width = variableTemplateRow.getWidth();
                    float height = variableTemplateRow.getHeight();
                    payloadTable.setSize(width, height);
                    payloadTable.setSkin(routineStage.skin);
                    payloadTable.setBackground("button-over");
                    payloadTable.getColor().a = 0.5f;
                    dragAndDrop.setDragActorPosition(width / 2f, -height / 2f);
                    payload.setDragActor(payloadTable);
                    return payload;
                }
            });
        }

        dragAndDrop.addTarget(new DragAndDrop.Target(routineStage.routineEditorApp.routineStageWrapper) {

            private Vector2 temp = new Vector2();

            @Override
            public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                temp.set(x, y);
                // x and y are in routineStageWrapper coordinate system
                // we need to convert them to nodeBoard's stage coordinate

                RoutineExposedVariableNodeWidget exposedVariable = ((RoutineExposedVariableNodeWidget) routineStage.createNode("ExposedVariableNode", temp.x, temp.y));
                PropertyWrapper<?> propertyWrapper = (PropertyWrapper<?>) payload.getObject();
                if (exposedVariable != null) {
                    NodeListPopup nodeListPopup = routineStage.getNodeListPopup();
                    exposedVariable.constructNode(nodeListPopup.getModuleByName("ExposedVariableNode"));
                    Notifications.fireEvent(Notifications.obtainEvent(NodeCreatedEvent.class).set(exposedVariable));
                    exposedVariable.update(propertyWrapper);
                }
            }
        });
    }

    private void addButton() {
        Skin skin = routineStage.skin;
        SquareButton squareButton = new SquareButton(skin, new Label("New Variable", skin), "Add a new Variable");
        squareButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                squareButton.setChecked(false);
                routineStage.routineEditorApp.createNewVariable();
            }
        });
        add(squareButton).growX();
    }
}

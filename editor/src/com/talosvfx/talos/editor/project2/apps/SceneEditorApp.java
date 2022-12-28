package com.talosvfx.talos.editor.project2.apps;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.talosvfx.talos.editor.addons.scene.SceneEditorWorkspace;
import com.talosvfx.talos.editor.addons.scene.assets.GameAsset;
import com.talosvfx.talos.editor.addons.scene.events.commands.GONameChangeCommand;
import com.talosvfx.talos.editor.addons.scene.logic.Scene;
import com.talosvfx.talos.editor.layouts.DummyLayoutApp;
import com.talosvfx.talos.editor.notifications.EventHandler;
import com.talosvfx.talos.editor.notifications.Notifications;
import com.talosvfx.talos.editor.notifications.Observer;
import com.talosvfx.talos.editor.project2.AppManager;
import com.talosvfx.talos.editor.project2.SharedResources;


@SingletonApp
public class SceneEditorApp extends AppManager.BaseApp<Scene> implements GameAsset.GameAssetUpdateListener, Observer {

	private final SceneEditorWorkspace workspaceWidget;

	public SceneEditorApp () {
		this.singleton = true;

		workspaceWidget = new SceneEditorWorkspace();
		workspaceWidget.disableListeners();

		DummyLayoutApp sceneEditorWorkspaceApp = new DummyLayoutApp(SharedResources.skin, getAppName()) {
			@Override
			public Actor getMainContent () {
				return workspaceWidget;
			}

			@Override
			public void onInputProcessorAdded () {
				super.onInputProcessorAdded();
				workspaceWidget.restoreListeners();
				SharedResources.stage.setScrollFocus(workspaceWidget);
			}

			@Override
			public void onInputProcessorRemoved () {
				super.onInputProcessorRemoved();
				workspaceWidget.disableListeners();
			}
		};

		this.gridAppReference = sceneEditorWorkspaceApp;
	}

	@Override
	public void updateForGameAsset (GameAsset<Scene> gameAsset) {
		super.updateForGameAsset(gameAsset);

		if (!gameAsset.listeners.contains(this, true)) {
			gameAsset.listeners.add(this);
		}


		workspaceWidget.loadFromScene(gameAsset);
		Notifications.registerObserver(this);
	}

	@Override
	public String getAppName () {
		if (gameAsset != null) {
			return "Scene - " + gameAsset.nameIdentifier;
		} else {
			return "Scene - ";
		}
	}

	@Override
	public void onRemove () {
		gameAsset.listeners.removeValue(this, true);

		Notifications.unregisterObserver(this);
	}

	@Override
	public void onUpdate () {
		workspaceWidget.loadFromScene(gameAsset);
	}

	@EventHandler
	public void GONameChangeCommand(GONameChangeCommand command) {
		workspaceWidget.changeGOName(command.getGo(), command.getSuggestedName());
	}

	@Override
	public void applyPreferences(Object appPreferences) {
		super.applyPreferences(appPreferences);
		SceneEditorAppPrefs prefs = (SceneEditorAppPrefs) appPreferences;
		workspaceWidget.setCameraPos(prefs.cameraPos);
		workspaceWidget.setCameraZoom(prefs.cameraZoom);
	}

	@Override
	public Object getCurrentPreference() {
		SceneEditorAppPrefs prefs = (SceneEditorAppPrefs) super.getCurrentPreference();
		prefs.cameraPos = workspaceWidget.getCameraPos();
		prefs.cameraZoom = workspaceWidget.getCameraZoom();
		return prefs;
	}

	@AppPreference
	private static class SceneEditorAppPrefs {
		private Vector3 cameraPos;
		private float cameraZoom;
	}
}


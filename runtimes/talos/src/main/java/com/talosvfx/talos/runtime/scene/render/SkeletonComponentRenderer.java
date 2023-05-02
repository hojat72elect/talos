package com.talosvfx.talos.runtime.scene.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.TalosSkeletonRenderer;
import com.talosvfx.talos.runtime.assets.GameAsset;
import com.talosvfx.talos.runtime.scene.GameObject;
import com.talosvfx.talos.runtime.scene.GameObjectRenderer;
import com.talosvfx.talos.runtime.scene.components.SpineRendererComponent;
import com.talosvfx.talos.runtime.scene.components.TransformComponent;

public class SkeletonComponentRenderer extends ComponentRenderer<SpineRendererComponent> {

	private final TalosSkeletonRenderer skeletonRenderer;

	public SkeletonComponentRenderer (GameObjectRenderer gameObjectRenderer) {
		super(gameObjectRenderer);
		skeletonRenderer = new TalosSkeletonRenderer();
	}
	@Override
	public void render (Batch batch, Camera camera, GameObject gameObject, SpineRendererComponent rendererComponent) {
		TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
		SpineRendererComponent spineRendererComponent = gameObject.getComponent(SpineRendererComponent.class);

		GameAsset<SkeletonData> gameResource = rendererComponent.getGameResource();
		if (gameResource.isBroken()) {
			GameObjectRenderer.renderBrokenComponent(batch, gameObject, transformComponent);
			return;
		}


		spineRendererComponent.skeleton.setPosition(transformComponent.worldPosition.x, transformComponent.worldPosition.y);
		spineRendererComponent.skeleton.setScale(transformComponent.worldScale.x * spineRendererComponent.scale, transformComponent.worldScale.y * spineRendererComponent.scale);
		spineRendererComponent.skeleton.getRootBone().setRotation(transformComponent.rotation);

		if (!gameObjectRenderer.isSkipUpdates()) {
			spineRendererComponent.animationState.update(Gdx.graphics.getDeltaTime());
			spineRendererComponent.animationState.apply(spineRendererComponent.skeleton);
		}
		spineRendererComponent.skeleton.updateWorldTransform();

		spineRendererComponent.skeleton.getColor().set(spineRendererComponent.finalColor);
		skeletonRenderer.draw(batch, spineRendererComponent.skeleton);

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}

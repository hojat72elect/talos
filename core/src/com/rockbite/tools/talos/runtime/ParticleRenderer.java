package com.rockbite.tools.talos.runtime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParticleRenderer {

    ParticleSystem particleSystem;

    TextureRegion textureRegion;

    Color color = new Color(Color.WHITE);

    private Vector2 position = new Vector2();

    public ParticleRenderer() {
        textureRegion = new TextureRegion(new Texture("fire.png"));
    }

    public ParticleRenderer(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
        textureRegion = new TextureRegion(new Texture("fire.png"));
    }

    public void render(Batch batch) {
        if(particleSystem == null) return;

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        for(int i = 0; i < particleSystem.emitters.size; i++) {
            for(int j = 0; j < particleSystem.emitters.get(i).activeParticles.size; j++) {
                renderParticle(batch, particleSystem.emitters.get(i).activeParticles.get(j));
            }
        }

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void renderParticle(Batch batch, Particle particle) {
        color.a = particle.transparency;
        float rotation = particle.rotation;
        float size = particle.size;
        batch.setColor(color);
        batch.draw(textureRegion,
                position.x + particle.position.x-size/2f,
                position.y + particle.position.y-size/2f,
                size/2f,
                size/2f,
                size,
                size,
                1f,
                1f,
                rotation);
    }

    public void dispose() {

    }

    public void setParticleSystem(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }
}

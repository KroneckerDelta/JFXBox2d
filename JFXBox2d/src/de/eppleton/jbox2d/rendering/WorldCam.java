/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.jbox2d.rendering;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jbox2d.common.Vec2;

/**
 *
 * @author antonepple
 */
public class WorldCam {

    private float scale;
    private Vec2 translate;

    public WorldCam(Vec2 translate, float scale) {
        this.scale = scale;
        this.translate = translate;
    }

    public Vec2 worldToScreen(float x, float y) {
        return worldToScreen(new Vec2(x, y));
    }

    public Vec2 worldToScreen(Vec2 world) {
        Vec2 screen = new Vec2();
        screen.x = (world.x + translate.x) * scale;
        screen.y = (-world.y + translate.y) * scale;

        return screen;
    }

    public float scale(float x) {
        return x * scale;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vec2 getTranslate() {
        return translate;
    }

    public void setTranslate(Vec2 translate) {
        this.translate = translate;
    }

    
    
}

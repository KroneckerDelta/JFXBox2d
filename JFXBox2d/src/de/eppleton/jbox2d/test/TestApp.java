/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.jbox2d.test;

import de.eppleton.jbox2d.rendering.WorldCam;
import de.eppleton.jbox2d.rendering.WorldView;
import javafx.animation.Interpolator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.CircleBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jbox2d.builders.BoxBuilder;
import org.jbox2d.builders.CircleShapeBuilder;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

/**
 *
 * @author antonepple
 */
public class TestApp extends Application {
    // -Dprism.dirtyopts=false

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        World world = new World(new Vec2(0, -7));
        Body circle = new CircleShapeBuilder(world).position(4, 6).type(BodyType.DYNAMIC).radius(1).density(1).restitution(0.5f).build();
        new BoxBuilder(world).type(BodyType.STATIC).position(4, 0).halfHeight(1).halfWidth(5).density(1).build();
        WorldView worldView = new WorldView(world, 800, 507);
        WorldCam camera = worldView.getCamera();
        camera.setScale(50);
        camera.setTranslate(new Vec2(2,6));
        Scene scene = new Scene(worldView, 800, 507);
        primaryStage.setScene(scene);
        primaryStage.show();
        worldView.play();
        worldView.updateBodies();
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.jbox2d.rendering;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 *
 * @author antonepple
 */
public class ImagePolygonProvider implements PolygonProvider<ImageView> {

    double width;
    double height;
    double centerX;
    double centerY;

    @Override
    public ImageView configureNode(ImageView imageView, Body body, PolygonShape shape, WorldCam camera) {//, Transform[] transform) {
        if (imageView == null) {
            Image image = new Image((String) body.getUserData());
            imageView = new ImageView(image);
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (int i = 0; i < shape.getVertexCount(); i++) {
                Vec2 vec2 = shape.getVertex(i);
                Vec2 transformed = camera.worldToScreen(org.jbox2d.common.Transform.mul(body.m_xf, vec2));

                double x = (double) transformed.x;
                double y = (double) transformed.y;

                if (x > maxX) {
                    maxX = x;
                }
                if (y > maxY) {
                    maxY = y;
                }
                if (x < minX) {
                    minX = x;
                }
                if (y < minY) {
                    minY = y;
                }
            }

            width = image.getWidth();
            height = image.getHeight();
            
            double scaleX = (maxX - minX) / width;
            double scaleY = (maxY - minY) / height;
            Rotate rotate = new Rotate(Math.toDegrees(-body.getAngle()));
            imageView.getTransforms().add(rotate);
            Scale scale = new Scale(scaleX, scaleY);
            imageView.getTransforms().add(scale);
        }
        Vec2 worldCenter = camera.worldToScreen(body.getWorldCenter());
        double centerX = worldCenter.x;
        double centerY = worldCenter.y;
        imageView.setRotate(Math.toDegrees(-body.getAngle()));
        Bounds boundsInParent = imageView.getBoundsInParent();
        double oldCenterX = boundsInParent.getMinX() + (boundsInParent.getWidth() / 2);
        double oldCenterY = boundsInParent.getMinY() + (boundsInParent.getHeight() / 2);
        double deltaX = centerX - oldCenterX;
        double deltaY = centerY - oldCenterY;
        double newX = imageView.getLayoutX() + deltaX;
        double newY = imageView.getLayoutY() + deltaY;
        imageView.relocate(newX, newY);
        return imageView;
    }

    @Override
    public boolean providesNodeFor(Body body, PolygonShape shape) {
        return shape instanceof PolygonShape && (body.getUserData() instanceof String) && ((String) body.getUserData()).endsWith(".png");
    }
}

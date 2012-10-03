/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.jbox2d.rendering;

import javafx.scene.Node;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;

/**
 *
 * @author eppleton
 */
public interface NodeProvider< S extends Shape, N extends Node> {

    public Node configureNode(N n, Body body, S shape, WorldCam camera);//, Transform [] transforms);

    public boolean providesNodeFor(Body body, S shape);

}

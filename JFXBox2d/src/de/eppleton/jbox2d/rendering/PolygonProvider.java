/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.jbox2d.rendering;

import javafx.scene.Node;
import org.jbox2d.collision.shapes.PolygonShape;

/**
 *
 * @author eppleton
 */
public interface PolygonProvider<n extends Node> extends NodeProvider<PolygonShape, n> {
    
}

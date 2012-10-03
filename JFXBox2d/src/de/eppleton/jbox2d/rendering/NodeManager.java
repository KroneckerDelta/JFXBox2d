 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.jbox2d.rendering;

import java.util.ArrayList;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * NodeManager gives you a NodeProvider, which will create and configure JavaFX
 * Nodes for you. Add your own Providers for custom rendering.
 *
 * @author eppleton
 */
public class NodeManager {

    private static Logger LOGGER = Logger.getLogger(NodeManager.class.getName());
    private static ArrayList<CircleProvider> circleProviders = new ArrayList<CircleProvider>();
    private static ArrayList<PolygonProvider> polygonProviders = new ArrayList<PolygonProvider>();
    private static ArrayList<ChainProvider> chainProviders = new ArrayList<ChainProvider>();
    private static PolygonProvider DEFAULT_POLYGON_PROVIDER;
    private static ChainProvider DEFAULT_CHAIN_PROVIDER;
    private static CircleProvider DEFAULT_CIRCLE_PROVIDER;

    public static void addCircleProvider(CircleProvider provider) {
        circleProviders.add(provider);
    }

    public static void removeProvider(CircleProvider provider) {
        circleProviders.remove(provider);
    }

    public static void addPolygonProvider(PolygonProvider provider) {
        polygonProviders.add(provider);
    }

    public static void removeProvider(PolygonProvider provider) {
        polygonProviders.remove(provider);
    }

    public static void addChainProvider(ChainProvider provider) {
        chainProviders.add(provider);
    }

    public static void removeProvider(ChainProvider provider) {
        chainProviders.remove(provider);
    }

    public static NodeProvider getNodeProvider(Body body, Shape shape) {

        if (shape instanceof CircleShape) {
            for (NodeProvider provider : circleProviders) {
                if (provider.providesNodeFor(body, shape)) {
                    return provider;
                }
            }
            if (DEFAULT_CIRCLE_PROVIDER == null) {
                DEFAULT_CIRCLE_PROVIDER = new DefaultCircleProvider();
            }
            return DEFAULT_CIRCLE_PROVIDER;
        } else if (shape instanceof PolygonShape) {
            for (NodeProvider provider : polygonProviders) {
                if (provider.providesNodeFor(body, shape)) {
                    return provider;
                }
            }
            if (DEFAULT_POLYGON_PROVIDER == null) {
                DEFAULT_POLYGON_PROVIDER = new DefaultPolygonProvider();
            }
            return DEFAULT_POLYGON_PROVIDER;
        } else if (shape instanceof ChainShape) {
            for (NodeProvider provider : chainProviders) {
                if (provider.providesNodeFor(body, shape)) {
                    return provider;
                }
            }
            if (DEFAULT_CHAIN_PROVIDER == null) {
                DEFAULT_CHAIN_PROVIDER = new DefaultChainProvider();
            }
            return DEFAULT_CHAIN_PROVIDER;
        }
        return null;
    }

    public static class DefaultPolygonProvider implements PolygonProvider<Polygon> {

        @Override
        public Polygon configureNode(Polygon polygon, Body body, PolygonShape shape, WorldCam camera) {//, Transform[] transform) {
            if (polygon == null) {
                Color fill = "hidden".equals(body.getUserData()) ? Color.TRANSPARENT : Color.BLACK;
                Color stroke = "hidden".equals(body.getUserData()) ? Color.TRANSPARENT : Color.GREEN;
                polygon = javafx.scene.shape.PolygonBuilder.create() //.transforms(transform)
                        .fill(fill).stroke(stroke).strokeWidth(0.1).build();
                for (int i = 0; i < shape.getVertexCount(); i++) {
                    Vec2 vec2 = shape.getVertex(i);
                    Vec2 transformed = camera.worldToScreen(org.jbox2d.common.Transform.mul(body.m_xf, vec2));
                    polygon.getPoints().add((double) transformed.x);
                    polygon.getPoints().add((double) transformed.y);
                }

            } else {
                for (int i = 0; i < shape.getVertexCount(); i++) {
                    Vec2 vec2 = shape.getVertex(i);
                    Vec2 transformed = camera.worldToScreen(org.jbox2d.common.Transform.mul(body.m_xf, vec2));
                    polygon.getPoints().set(i * 2, (double) transformed.x);
                    polygon.getPoints().set((i * 2) + 1, (double) transformed.y);
                }
            }
            return polygon;
        }

        @Override
        public boolean providesNodeFor(Body body, PolygonShape shape) {
            // dummy, because this will never be asked for
            return shape instanceof PolygonShape;
        }
    }

    public static class DefaultCircleProvider implements CircleProvider<Circle> {

        @Override
        public Circle configureNode(Circle circle, Body body, CircleShape shape, WorldCam camera) {//, Transform[] transform) {
            if (circle == null) {
                circle = javafx.scene.shape.CircleBuilder.create().stroke(Color.RED).strokeWidth(0.05d).
                        //.transforms(transform)
                        build();
            }
            Vec2 worldToScreen = camera.worldToScreen(body.getPosition());
            circle.setCenterX(worldToScreen.x);
            circle.setCenterY(worldToScreen.y);
            circle.setRadius(camera.scale(shape.m_radius));
            return circle;
        }

        @Override
        public boolean providesNodeFor(Body body, CircleShape shape) {
            // dummy, because this will never be asked for
            return shape instanceof CircleShape;
        }
    }

    public static class DefaultChainProvider implements ChainProvider<Polyline> {

        @Override
        public Node configureNode(Polyline node, Body body, ChainShape shape, WorldCam camera) {
            if (node == null) {
                node = javafx.scene.shape.PolylineBuilder.create() //.transforms(transform)
                        .stroke(Color.BLACK).fill(Color.TRANSPARENT).build();
                for (int i = 0; i < shape.m_vertices.length; i++) {
                    Vec2 vec2 = shape.m_vertices[i];
                    Vec2 transformed = camera.worldToScreen(org.jbox2d.common.Transform.mul(body.m_xf, vec2));
                    node.getPoints().add((double) transformed.x);
                    node.getPoints().add((double) transformed.y);
                }

            } else {
                for (int i = 0; i < shape.m_vertices.length; i++) {
                    Vec2 vec2 = shape.m_vertices[i];
                    Vec2 transformed = camera.worldToScreen(org.jbox2d.common.Transform.mul(body.m_xf, vec2));

                    node.getPoints().set(i * 2, (double) transformed.x);
                    node.getPoints().set((i * 2) + 1, (double) transformed.y);

                }
            }
            return node;
        }

        @Override
        public boolean providesNodeFor(Body body, ChainShape shape) {
// dummy, because this will never be asked for
            return shape instanceof ChainShape;
        }
    }

}

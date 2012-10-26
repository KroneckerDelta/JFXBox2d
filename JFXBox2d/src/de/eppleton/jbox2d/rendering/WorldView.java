/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.jbox2d.rendering;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.util.Duration;
import org.jbox2d.builders.BoxBuilder;
import org.jbox2d.callbacks.DestructionListener;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;

/**
 *
 * @author eppleton
 */
public class WorldView extends Parent implements DestructionListener {

    private HashMap<Body, Node> nodesForBodies = new HashMap<Body, Node>();
    private WorldCam camera;
    protected World world;
    private Timeline timeline;
    private final double frames = 60d;

    public WorldView(final World world, double width, double height) {
        enableDragAndDrop();
        Rectangle build = RectangleBuilder
                .create()
                .height(height)
                .width(width)
                .fill(Color.TRANSPARENT)
                .build();
        getChildren().add(build);

        this.world = world;
        initCamera(world, width, height);
        getStyleClass().add("background");
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        Duration duration = Duration.seconds(1d / frames);
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                world.step(1.0f / (float) (frames * 3), 1, 1);
                updateBodies();
            }
        };

        KeyFrame keyFrame = new KeyFrame(duration, onFinished, null, null);
        timeline.getKeyFrames().add(keyFrame);

    }

    private void initCamera(World world, double width, double height) {
        Vec2 min = WorldMetrics.min(world);
        Vec2 max = WorldMetrics.max(world);
        float worldWidth = max.x - min.x;
        float worldHeight = max.y - min.y;
        float scaleX = (float) width / worldWidth;
        float scaleY = (float) height / worldHeight;
        float scale = scaleX > scaleY ? scaleY : scaleX; // fit screen
        scale = scale * .8f; // add a little extra space 
        float centerX = min.x + (worldWidth / 2);
        float centerY = min.y + (worldHeight / 2);
        float targetX = ((float) width / 2) / scale;
        float targetY = ((float) height / 2) / scale;
        this.camera = new WorldCam(new Vec2(targetX - centerX, max.y + (targetY - centerY)), scale);
    }

    private void enableDragAndDrop() {
        setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data is dragged over the target */
                System.out.println("onDragOver");

                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != this
                        && event.getDragboard().hasString()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                System.out.println("onDragEntered");
                /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != this
                        && event.getDragboard().hasString()) {
                }

                event.consume();
            }
        });

        setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */


                event.consume();
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data dropped */
                System.out.println("onDragDropped");
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;

                Vec2 vec2 = new Vec2((float) event.getSceneX(), (float) event.getSceneY());
                Vec2 targetPos = getCamera().screenToWorld(vec2);
                new BoxBuilder(world).type(BodyType.DYNAMIC)
                        .position(targetPos)
                        .halfHeight(.1f)
                        .halfWidth(.1f)
                        .density(.2f)
                        .build();
                updateBodies();
                //setText(db.getString());
                success = true;

                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });
    }

    public void init() {
        updateBodies();
    }

    public void pause() {
        timeline.pause();
    }

    public void stop() {
        timeline.stop();
    }

    public void play() {
        timeline.play();
    }
    
    boolean stationary = false;

    public void updateBodies() {
        stationary = true;
        Body nextBody = world.getBodyList();
        while (nextBody != null) {
            if (nextBody.isAwake() && nextBody.getType() != BodyType.STATIC) {
                stationary = false;
            }
            Shape shape = nextBody.getFixtureList().getShape();
            NodeProvider nodeProvider = NodeManager.getNodeProvider(nextBody, shape);
            Node n = nodesForBodies.get(nextBody);
            n = nodeProvider.configureNode(n, nextBody, shape, camera);//, transform);

            if (n != null && n.getParent() == null) {
                addChild(n, nextBody);
            }/*
             if (followMe != null && followMe.equals(nextBody.getUserData())) {
             double layoutY = n.getLayoutY();
             double scaleY = getScaleY();
             double maxVisibleY = getHeight() / scaleY;
             double distanceFromLowerBound = getHeight() - layoutY;
             if (distanceFromLowerBound > getHeight()) {
             double newScale = (getHeight() / distanceFromLowerBound);
             setScaleX(newScale);
             setScaleY(newScale);
             maxVisibleY = getHeight() / newScale;
             double d = getHeight() - (newScale * getHeight());
             setTranslateY(d / 2);
             }
             double layoutX = n.getLayoutX();
             double width = getScene().getWindow().getWidth();
             double transX = ((width / 2) - layoutX) / getScaleX();
             if (transX < 0) {
             if (getTranslateX() != transX) {
             setTranslateX(transX);
             }
             }
             }*/
            nextBody = nextBody.getNext();
        }
    }

    public WorldCam getCamera() {
        return camera;
    }

    public void removeBody(Body toRemove) {
        if (nodesForBodies.containsKey(toRemove)) {
            Node orphan = nodesForBodies.get(toRemove);
            getChildren().remove(orphan);
            nodesForBodies.remove(toRemove);
        }
    }

    public void addChild(Node n) {
        getChildren().add(n);
    }

    public void addChild(Node n, Body nextBody) {
        // check if this node is new
        Node get = nodesForBodies.get(nextBody);
        if (get== null)getChildren().add(n);
        nodesForBodies.put(nextBody, n);
    }

    public Node getNodeForBody(Body b) {
        return nodesForBodies.get(b);
    }

    @Override
    public void sayGoodbye(Joint joint) {
    }

    @Override
    public void sayGoodbye(Fixture fixture) {
        removeBody(fixture.m_body);
    }
}

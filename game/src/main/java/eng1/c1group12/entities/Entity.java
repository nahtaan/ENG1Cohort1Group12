package eng1.c1group12.entities;

import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.CenterQuad;
import com.jme3.texture.Texture;
import eng1.c1group12.Main;

import java.io.InputStreamReader;
import java.nio.FloatBuffer;

/**
 * Abstract class for all types of entities to extend. Manages creating geometry and a Node for the entity.
 */
public abstract class Entity {

    protected final String name;
    protected final Texture texture;
    protected final AnimationHandler animationHandler;
    protected final Geometry geom;
    protected final Node node;
    public float width;
    public float height;

    public Entity(AssetManager assetManager, String name, String texture_name) {
        this.name = name;
        this.texture = assetManager.loadTexture("Textures/Entities/"+texture_name+"/sheet.png");
        texture.setMagFilter(Texture.MagFilter.Nearest);
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        this.animationHandler = new AnimationHandler(texture_name);

        width = (float) animationHandler.info.res /animationHandler.info.pixels_per_unit;
        height = (float) animationHandler.info.res /animationHandler.info.pixels_per_unit;
        this.geom = new Geometry(name, new CenterQuad(width, height));

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", texture);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        // Very important, makes all entities render transparency properly!
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        geom.setMaterial(material);
        this.node = new Node(name);

        // default to top left cell so that the whole sheet doesn't get shown to the player
        animationHandler.showCell(0, 0);
        node.attachChild(geom);
    }

    /**
     * Called when the entity logic needs to be updated.
     */
    public abstract void tick(float tpf);

    public String getName() {
        return name;
    }

    public void move(float x, float y, float z) {
        getNode().move(x, y, z);
    }


    public Node getNode() {
        return node;
    }

    public class AnimationHandler {
        private final AnimationInfo info;

        private int currentCol = -1;
        private int currentRow = -1;
        private Direction dir = Direction.DOWN;
        private float timer = 0;
        private int frame;



        public AnimationHandler(String texture_name) {
            //noinspection ConstantConditions
            info = new Gson().fromJson(new InputStreamReader(Main.class.getResourceAsStream("/Textures/Entities/" + texture_name + "/animation.json")), AnimationInfo.class);
        }

        public void update(float tpf, float vx, float vy) {
            boolean moving = vx != 0 || vy != 0;

            if(moving) {
                // prioritise horizontal animations over vertical ones
                if(Math.abs(vx) > Math.abs(vy)) {
                    dir = vx > 0 ? Direction.RIGHT : Direction.LEFT;
                }else {
                    dir = vy > 0 ? Direction.UP : Direction.DOWN;
                }

                timer += tpf;
                while(timer >= info.frame_time) {
                    timer -= info.frame_time;
                    frame = (frame + 1) % 4;
                }
                showCell(frame, rowFor(dir));
            }else {
                timer = 0;
                frame = 0;
                showCell(0, rowFor(Direction.DOWN));
            }
        }

        private int rowFor(Direction dir) {
            return switch (dir) {
                case UP -> info.up;
                case DOWN -> info.down;
                case LEFT -> info.left;
                case RIGHT -> info.right;
            };
        }


        public void showCell(int col, int row) {
            if (col == currentCol && row == currentRow) return;
            currentCol = col;
            currentRow = row;

            float u0 = (float) col / info.columns;
            float u1 = (float) (col+1) / info.columns;

            float v1 = 1f - (float) row / 4; // 4 directions
            float v0 = 1f - (float) (row+1) / 4;

            Mesh mesh = geom.getMesh();
            VertexBuffer vb = mesh.getBuffer(VertexBuffer.Type.TexCoord);
            FloatBuffer fb = (FloatBuffer) vb.getData();
            fb.rewind();
            // vertex order: bottom-left, bottom-right, top-right, top-left
            fb.put(u0).put(v0)
                .put(u1).put(v0)
                .put(u1).put(v1)
                .put(u0).put(v1);
            vb.updateData(fb);
        }

        private record AnimationInfo(
                int res,
                int pixels_per_unit,
                float frame_time,
                int columns,
                int down,
                int left,
                int right,
                int up
        ){}

        public enum Direction {
            UP,
            DOWN,
            LEFT,
            RIGHT
        }

    }

}

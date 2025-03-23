package steve6472.planetoid.aimguihax;

import imgui.*;
import imgui.flag.ImGuiCol;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.type.ImBoolean;
import steve6472.planetoid.Main;
import steve6472.planetoid.SystemEntry;
import steve6472.planetoid.Systems;
import steve6472.planetoid.angine.UserInput;
import steve6472.planetoid.api.Input;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.ColorUtil;
import steve6472.core.util.Profiler;
import steve6472.planetoid.sound.GameSound;
import steve6472.planetoid.world.Universe;

public class Debug extends Application
{
    private static final Logger LOGGER = Log.getLogger(Debug.class);

    public record ProfilerEntry(Profiler profiler, String name) {}
    public final List<ProfilerEntry> profilers = new ArrayList<>();

    public final Universe universe;
    private final Input input;

    public Debug(Input input, Universe universe)
    {
        this.input = input;
        this.universe = universe;
    }

    @Override
    protected void initImGui(Configuration config) 
    {
        super.initImGui(config);
        final ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    }

    private int selectedEntity = -1;
    private Key selectedSystemKey;
    private String selectedProfilerId;
    private String selectedPreparedKey;
    private ImBoolean ignoreParticles = new ImBoolean(true);
    private int[] mainUPS = new int[] {60};
    private int[] chunksMapZoom = new int[] {1};
    private ImBoolean ENABLE_DEBUG = new ImBoolean(true);
    private int mapPreviewMode = -1; // -1 for all, others correspond to ordinal of ChunkLayer

    // Static debug 'cause I'm lazy lul
    public static final ImBoolean CHUNK_RENDER_DEBUG_INSIDE = new ImBoolean(false);
    public static final ImBoolean CHUNK_RENDER_DEBUG_OUTSIDE = new ImBoolean(false);
    public static final ImBoolean CHUNK_RENDER_DEBUG_EDGE = new ImBoolean(false);
    public static final ImBoolean CHUNK_RENDER_DEBUG_FULL = new ImBoolean(false);
    public static final ImBoolean FRAME = new ImBoolean(false);
    public static final ImBoolean EMISSION = new ImBoolean(true);
    public static final ImBoolean FANCY_EMISSION = new ImBoolean(false);

    private Profiler debugProfiler = new Profiler(10);

    @Override
    public void process()
    {
        debugProfiler.start();

        GLFW.glfwWindowHint(GLFW.GLFW_MOUSE_PASSTHROUGH, GLFW.GLFW_FALSE);

        if (ImGui.button("Exit"))
        {
            ImGui.saveIniSettingsToDisk(ImGui.getIO().getIniFilename());
            dispose();
            System.exit(0);
        }

        ImGui.sameLine();
        ImGui.checkbox("Enable debug", ENABLE_DEBUG);

        ImGui.sameLine();
        ImGui.text("Last debug processing took: %.5f ms".formatted(debugProfiler.averageMilli()));
        ImGui.text("Main scheduler: %.5f ms".formatted(Main.mainSchedulerProfiler.averageMilli()));
        
        ImGui.beginTabBar("tabs");

        if (ImGui.beginTabItem("Performance"))
        {
            performanceTab();
            ImGui.endTabItem();
        }

        if (ImGui.beginTabItem("Profilers"))
        {
            profilersTab();
            ImGui.endTabItem();
        }

        if (ImGui.beginTabItem("Client"))
        {
            clientTab();
            ImGui.endTabItem();
        }

        if (ImGui.beginTabItem("Systems"))
        {
            systemsTab();
            ImGui.endTabItem();
        }

        ImGui.endTabBar();

        debugProfiler.end();
    }

    private void profilersTab()
    {
        if (profilers.isEmpty())
        {
            ImGui.text("No schedulers registered!");
            return;
        }

        ImGui.beginChild("left pane", 220, 0, true);
        ProfilerEntry selectedEntry = null;

        ImGui.text("Average %s: %.5f ms".formatted(profilers.getFirst().name(), profilers.getFirst().profiler().averageMilli()));
        ImGui.separator();

        for (ProfilerEntry profilerEntry : profilers)
        {
            if (profilerEntry.name().equals(selectedProfilerId))
                selectedEntry = profilerEntry;

            ImVec2 cursorScreenPos = ImGui.getCursorScreenPos();
            float x = cursorScreenPos.x - 5;
            float y = cursorScreenPos.y - 2;
            float w = 220;
            float h = 17;
            float target = 1000f / 60f;
            float milli = Math.clamp((float) profilerEntry.profiler.averageMilli(), 0f, target) / target;

            ImGui.getWindowDrawList().addRectFilled(x, y, x + w, y + h, ColorUtil.getColor(0f, 1f - milli, milli, 0.2f));

            ImGui.pushStyleColor(ImGuiCol.Header, 0);
            if (ImGui.selectable(profilerEntry.name, profilerEntry.name().equals(selectedProfilerId)))
            {
                selectedEntry = profilerEntry;
                selectedProfilerId = profilerEntry.name();
            }
            ImGui.popStyleColor();
        }
        ImGui.endChild();
        ImGui.sameLine();

        if (selectedEntry == null)
        {
            ImGui.text("Select Profiler");
            return;
        }

        ImGui.beginGroup();
        ImGui.beginChild("item view", 0, -ImGui.getFrameHeightWithSpacing());

        ImFont font = ImGui.getFont();
        float scale = font.getScale();
        font.setScale(3f);
        ImGui.pushFont(font);
        ImGui.text(selectedEntry.name);
        font.setScale(scale);
        ImGui.popFont();

        ImGui.separator();
        ImGui.text("Average: %.5f ms".formatted(selectedEntry.profiler.averageMilli()));
        ImGui.separator();
        ImGui.text("Last: %.5f ms".formatted(selectedEntry.profiler.lastMilli()));
        ImGui.text("Max: %.5f ms".formatted(selectedEntry.profiler.maxMilli()));
        ImGui.text("Max Ever: %.5f ms".formatted(selectedEntry.profiler.maxEverMilli()));

        //        Configs.systemConfig(selectedEntry.system, world);

        ImGui.endChild();
        ImGui.endGroup();
    }

    private void ecsTab()
    {/*
        ImGui.checkbox("Ignore Particles", ignoreParticles);

        var iterator = world.ecs().findEntitiesWith(All.class).iterator();

        Object[] selectedComponents = null;
        Entity selectedEntityObj = null;

        while (iterator.hasNext())
        {
            var entity = iterator.next().entity();
            if (entity == null) continue;

            if (ignoreParticles.get())
                if (entity.get(All.class).id().contains("particle"))
                    continue;

            var intEntity = (IntEntity) entity;
            //String id = intEntity.getComposition().getIdSchema().idToString(intEntity.getId());

            if (intEntity.hashCode() == selectedEntity)
            {
                selectedComponents = intEntity.getComponentArray();
                selectedEntityObj = entity;
            }

            ImGui.beginChild("left pane", 150, 0, true);
            if (ImGui.selectable(entity.get(All.class).id() + "##" + intEntity.hashCode(), selectedEntity == intEntity.hashCode()))
            {
                selectedComponents = intEntity.getComponentArray();
                selectedEntity = intEntity.hashCode();
                selectedEntityObj = entity;
            }
            ImGui.endChild();
        }
        ImGui.sameLine();

        ImGui.beginGroup();
        ImGui.beginChild("item view", 0, -ImGui.getFrameHeightWithSpacing());
        if (selectedComponents != null)
        {
            if (ImGui.button("SAVE"))
            {
                Map<Component<?>, Object> map = new HashMap<>();
                for (Object selectedComponent : selectedComponents)
                {
                    for (Component<?> value : Registries.COMPONENT.getMap().values())
                    {
                        if (value.componentClass() == selectedComponent.getClass())
                        {
                            LOGGER.fine(value.componentClass() + " " + selectedComponent);
                            LOGGER.finer("" + ((Encoder<Object>) value.codec()).encodeStart(JsonOps.INSTANCE, selectedComponent).getOrThrow());
                            map.put(value, selectedComponent);
                            break;
                        }
                    }
                }

                JsonElement json = Registries.COMPONENT.valueMapCodec().encodeStart(JsonOps.INSTANCE, map).getOrThrow();
                System.out.println(json);
            }
            ImGui.spacing();

            Set<Pair<Object, Object>> changedComponents = new HashSet<>();

            for (Object component : selectedComponents)
            {
                ImGui.textWrapped(prettyString(component.toString()));
                ImGui.newLine();

                Object change = Configs.systemConfig(component, world);
                if (change != component)
                {
                    changedComponents.add(new Pair<>(component, change));
                }
            }

            for (Pair<Object, Object> pair : changedComponents)
            {
                Object oldComponent = pair.a();
                Object newComponent = pair.b();
                selectedEntityObj.remove(oldComponent);
                selectedEntityObj.add(newComponent);
            }
        }

        ImGui.endChild();
        ImGui.endGroup();*/
    }

    private void systemsTab()
    {
        ImGui.beginTabBar("systems_tabs");
        {
//            if (ImGui.beginTabItem("Client"))
//            {
//                systemsTab(client.clientSystems);
//                ImGui.endTabItem();
//            }

//            if (ImGui.beginTabItem("Universe - Entities"))
//            {
//                systemsTab(universe.systems);
//                ImGui.endTabItem();
//            }
        }
        ImGui.endTabBar();
    }

    private <T> void systemsTab(Systems systems)
    {
        ImGui.beginChild("left pane", 200, 0, true);
        SystemEntry selectedEntry = null;

        ImGui.text("Average: %.5f ms".formatted(systems.profiler.averageMilli()));
        ImGui.separator();

        Collection<SystemEntry> systemEntries = systems.systemEntries.values();

        List<SystemEntry> sortedSystems = systemEntries.stream().sorted(Comparator.comparing(ob -> ob.debug)).toList();

        boolean reachedDebug = false;
        ImVec4 oldColor = ImGui.getStyle().getColor(ImGuiCol.Text);
        for (SystemEntry system : sortedSystems)
        {
            if (system.key.equals(selectedSystemKey))
                selectedEntry = system;

            if (system.debug && !reachedDebug)
            {
                reachedDebug = true;
                ImGui.separator();
                ImGui.getStyle().setColor(ImGuiCol.Text, oldColor.x, oldColor.y, oldColor.z, oldColor.w);
                ImGui.text("DEBUG");
                ImGui.separator();
            }

            ImGui.getStyle().setColor(ImGuiCol.Text, system.enabled ? 0xff33ff33 : 0xff3333ff);

            ImVec2 cursorScreenPos = ImGui.getCursorScreenPos();
            float x = cursorScreenPos.x - 5;
            float y = cursorScreenPos.y - 2;
            float w = 200;
            float h = 17;
            float milli = Math.clamp((float) system.profiler.averageMilli(), 0f, 1f);

            ImGui.getWindowDrawList().addRectFilled(x, y, x + w, y + h, ColorUtil.getColor(0f, 1f - milli, milli, 0.2f));

            ImGui.pushStyleColor(ImGuiCol.Header, 0);
            if (ImGui.selectable(system.name, system.key.equals(selectedSystemKey)))
            {
                selectedEntry = system;
                selectedSystemKey = system.key;
            }
            ImGui.popStyleColor();
        }
        ImGui.getStyle().setColor(ImGuiCol.Text, oldColor.x, oldColor.y, oldColor.z, oldColor.w);
        ImGui.endChild();
        ImGui.sameLine();

        if (selectedEntry == null)
        {
            ImGui.text("Select System");
            return;
        }

        ImGui.beginGroup();
        ImGui.beginChild("item view", 0, -ImGui.getFrameHeightWithSpacing());

        ImGui.text(selectedEntry.name);
        ImGui.text("id: " + selectedEntry.key);
        ImGui.text("Last took: %.5f ms".formatted(selectedEntry.profiler.averageMilli()));
        ImGui.separator();

        ImBoolean enabledBool = new ImBoolean(selectedEntry.enabled);
        ImGui.checkbox("Enabled", enabledBool);
        selectedEntry.enabled = enabledBool.get();
        ImGui.separator();

        ImGui.text(selectedEntry.description);
        ImGui.separator();

//        Configs.systemConfig(selectedEntry.system, world);

        ImGui.endChild();
        ImGui.endGroup();
    }

    private static class Tree<V>
    {
        private List<V> leaves;
        private Map<String, Tree<V>> branches;
        private Tree<V> parent;

        private final String id;

        private Tree(String id)
        {
            this.id = id;
        }

        private Tree(String id, Tree<V> parent)
        {
            this(id);
            this.parent = parent;
        }

        private void addLeaf(V value)
        {
            if (leaves == null)
                leaves = new ArrayList<>();
            
            leaves.add(value);
        }

        private void addBranch(Tree<V> branch)
        {
            if (branches == null)
                branches = new HashMap<>();
            
            branches.put(branch.id, branch);
        }

        private void add(String[] path, V value)
        {
            if (parent != null)
                throw new RuntimeException("Can not be called on a node");

            Tree<V> lastNode = this;

            if (!lastNode.id.equals(path[0]))
                throw new RuntimeException("Root node mismatch, expected " + lastNode.id + ", got " + path[0]);

            for (int i = 1; i < path.length; i++)
            {
                String s = path[i];

                if (lastNode.branches == null)
                {
                    Tree<V> branch = new Tree<>(s, lastNode);
                    lastNode.addBranch(branch);
                    lastNode = branch;
                } else
                {
                    Tree<V> branch = lastNode.branches.get(s);
                    if (branch == null)
                    {
                        branch = new Tree<>(s, lastNode);
                        lastNode.addBranch(branch);
                    }
                    lastNode = branch;
                }
            }
            lastNode.addLeaf(value);
        }

        private String path()
        {
            if (parent == null)
                return id;
            else
                return parent.path() + "/" + id;
        }

        @Override
        public String toString()
        {
            return "Tree{" + "id=" + id + ", parent=" + (parent == null ? "none" : "exists") + ", path=" + path() + ", branches=" + branches + ", leaves=" + leaves + "}";
        }
    }

    private void treeGui(Tree<String> tree)
    {
        if (tree.branches != null)
        {
            for (Tree<String> branch : tree.branches.values()) 
            {
                if (ImGui.treeNode(branch.id))
                {
                    treeGui(branch);
                    ImGui.treePop();
                }
            }
        }

        if (tree.leaves != null)
        {
            for (String value : tree.leaves) 
            {
                if (ImGui.selectable(value + "##" + tree.path(), (tree.path() + "/" + value).equals(selectedPreparedKey)))
                {
                    selectedPreparedKey = tree.path() + "/" + value;
                }
            }
        }
    }

    private static final String TAB = "  ";
    public static String prettyString(String string)
    {
        StringBuilder builder = new StringBuilder();
        int depth = 0;

        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(string));
        tokenizer.wordChars('\'', '\'');
        tokenizer.ordinaryChar(' ');
        tokenizer.ordinaryChar('/');

        int token;
        while (true)
        {
            try
            {
                if ((token = tokenizer.nextToken()) == StreamTokenizer.TT_EOF)
                    break;
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            if (token == '{')
            {
                builder.append('\n').append(TAB.repeat(depth)).append("{");
                depth++;
                builder.append('\n').append(TAB.repeat(depth));
            } else if (token == '}')
            {
                depth--;
                builder.append('\n').append(TAB.repeat(depth)).append("}");
            } else if (token == '[')
            {
                builder.append('\n').append(TAB.repeat(depth)).append("[");
                depth++;
                builder.append('\n').append(TAB.repeat(depth));
            } else if (token == ']')
            {
                depth--;
                builder.append('\n').append(TAB.repeat(depth)).append("]");
            } else if (token == StreamTokenizer.TT_NUMBER)
            {
                builder.append(tokenizer.nval);
            } else if (token == StreamTokenizer.TT_WORD)
            {
                builder.append(tokenizer.sval);
            } else
            {
                builder.append((char) token);
            }
        }

        return builder.toString();
    }

    private void performanceTab()
    {
        /*if (ImGui.sliderInt("Client Scheduler", mainUPS, 0, 70))
        {
            mainScheduler.tickAtFixedRate(mainUPS[0]);
        }*/
//        ImGui.text("Delta Time: " + mainScheduler.deltaTime());
//        ImGui.text(String.format("Load: %.2f", mainScheduler.deltaTime() * mainUPS[0]));

        ImGui.separator();
//        ImGui.text("World tick: %.5f ms".formatted(world.worldTickProfiler.averageMilli()));

        ImGui.separator();
//        ImGui.text("World render to screen: %.5f ms".formatted(client.worldRenderProfiler.lastMilli()));
//        ImGui.text("UI render: %.5f ms".formatted(client.clientRenderProfiler.lastMilli()));
        if (ImGui.isItemHovered())
        {
            ImGui.beginTooltip();
            ImGui.text("Image to screen");
            ImGui.endTooltip();
        }

        /*if (ImGui.sliderInt("World Updates Per Second", worldUPS, 0, 120))
        {
            world.scheduler().tickAtFixedRate(worldUPS[0]);
        }
        ImGui.text("Delta Time: " + world.scheduler().deltaTime());
        ImGui.text(String.format("Load: %.2f", world.scheduler().deltaTime() * worldUPS[0]));*/

        ImGui.separator();
        ImGui.text("Chunk Render Debug");
        ImGui.checkbox("Inside", CHUNK_RENDER_DEBUG_INSIDE); ImGui.sameLine();
        ImGui.checkbox("Outside", CHUNK_RENDER_DEBUG_OUTSIDE); ImGui.sameLine();
        ImGui.checkbox("Edge", CHUNK_RENDER_DEBUG_EDGE);
        ImGui.checkbox("Render Full Chunk", CHUNK_RENDER_DEBUG_FULL);

        ImGui.separator();
//        if (ImGui.checkbox("Emission", EMISSION))
//        {
//            client.emissionImageSettings.enabled = EMISSION.get();
//        }

        if (EMISSION.get())
        {
//            if (ImGui.checkbox("Fancy Emission", FANCY_EMISSION))
//            {
//                client.emissionImageSettings.interpolation = FANCY_EMISSION.get() ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR;
//            }
        }

        ImGui.separator();
        float globalVolume = (float) GameSound.getGlobalVolume();
        float[] volume = new float[] {globalVolume};
        ImGui.sliderFloat("Master Volume", volume, 0, 1);
        if (volume[0] != globalVolume)
        {
            GameSound.setGlobalVolume(volume[0]);
        }
    }

    // TODO: optimize this
    private final List<Integer> textures = new ArrayList<>();

    private void clientTab()
    {
        Vector2i rawMouseLoc = input.getRawMouseLoc();
        Vector2i pixelScreenMouseLoc = input.getPixelMouseLocOnScreen();
        Vector2i pixelWorldMouseLoc = input.getPixelMouseLocInWorld();
        Vector2i mouseTileLoc = input.getMouseTileLoc();
        ImGui.separator();
        ImGui.text("Raw Mouse Loc: " + rawMouseLoc.x + " / " + rawMouseLoc.y);
        ImGui.text("Pixel Screen Mouse Loc: " + pixelScreenMouseLoc.x + " / " + pixelScreenMouseLoc.y);
        ImGui.text("Pixel World Mouse Loc: " + pixelWorldMouseLoc.x + " / " + pixelWorldMouseLoc.y);
        ImGui.text("Mouse Tile Loc: " + mouseTileLoc.x + " / " + mouseTileLoc.y);
        ImGui.text("Mouse in window: " + input.isMouseInWindow());
//        ImGui.text("Render Offset: " + (int) Math.floor(client.getRender().offsetX()) + " / " + (int) Math.floor(client.getRender().offsetY()));
        if (input instanceof UserInput userInput)
        {
            ImGui.separator();
            ImGui.text("Pressed keys: " + userInput.pressedKeys);
            ImGui.text("Pressed mouse buttons: " + userInput.pressedMouseButtons);
        }
    }


    public static int loadTexture(int width, int height, int[] pixels) 
    {
        // Create a new OpenGL texture
        int textureID = GL11.glGenTextures();
        
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        
        // Convert the pixel array into a ByteBuffer
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4); // 4 for RGBA
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
                buffer.put((byte) (pixel & 0xFF));         // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
            }
        }
        
        buffer.flip(); // Flip the buffer to read
        
        // Set texture parameters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
        // Load texture data
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        
        return textureID;
    }
}

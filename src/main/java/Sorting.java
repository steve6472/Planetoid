import steve6472.planetoid.angine.RenderFrame;
import steve6472.planetoid.api.Render;

import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by steve6472
 * Date: 5/30/2024
 * Project: Domin <br>
 */
@SuppressWarnings("ALL")
class Sorting
{
    /*
     * Window constants
     */
    public static final int PIXEL_SIZE = 3;
    public static final int WINDOW_WIDTH = 16 * 70 * PIXEL_SIZE;
    public static final int WINDOW_HEIGHT = 9 * 70 * PIXEL_SIZE;
    public static final int TARGET_FPS = 60;

    /*
     * Rendering constants
     */
    public static final int X_OFFSET = 10;
    public static final int Y_OFFSET = 10;
    public static final int NUMBER_WIDTH = 2;
    public static final int SPACING = 1;

    public static final int BACKGROUND = 0xff303030;
    public static final IntToIntFunction NUMBER_COLOR = i -> 0xff00ff00;
    public static final int COMPARE_COLOR = 0xffff0000;
    public static final int SWAP_COLOR = 0xffff00ff;

    /*
     * Animation constatns
     */
    public static final double ANIMATION_SECONDS = 0.01;
    public static final long ANIMATION_NANOS = (long) ((ANIMATION_SECONDS) * 1e9);

    /*
     * Array constants
     */
    public static final int NUMBER_COUNT = 80;

    private RenderFrame frame;
    private Render render;
    private ArrayList<Object> eventQueue;

    private void setup()
    {
        frame = new RenderFrame("Sorting", WINDOW_WIDTH / PIXEL_SIZE, WINDOW_HEIGHT / PIXEL_SIZE, PIXEL_SIZE);
        frame.createAndAddImage(WINDOW_WIDTH / PIXEL_SIZE, WINDOW_HEIGHT / PIXEL_SIZE, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        frame.getFrame().setLocationRelativeTo(null);
        render = frame.render;
        eventQueue = new ArrayList<>();

        start();
        startLoop();
    }

    private void startLoop()
    {
        final long targetNano = (long) (1d / (double) TARGET_FPS * 1e9);

        long last = System.nanoTime();
        long timeoutNanos = 0;

        while (true)
        {
            if (System.nanoTime() - last >= timeoutNanos)
            {
                long startTime = System.nanoTime();
                last = startTime;

                timeoutNanos = System.nanoTime() - startTime;

                tick(timeoutNanos / (double) TARGET_FPS);

                timeoutNanos = targetNano - timeoutNanos;
            }
        }
    }

    private void tick(double deltaTime)
    {
        render.fillRectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, BACKGROUND);
        render(deltaTime);
        frame.render();
        eventQueue.clear();
    }

    private ActionQueue queue;
    private Animation currentAnimation;

    private void start()
    {
        SortingStrategy sortingStrategy = new BubbleSort();
        int[] randomArray = getRandomArray();
        queue = new ActionQueue(randomArray);

        sortingStrategy.sort(randomArray, queue);
    }

    private void render(double deltaTime)
    {
        render.setPixel(5, 5, 0xff00ff00);

        if (queue == null || queue.countActions() == 0)
        {
            if (queue != null)
            {
                for (int i = 0; i < NUMBER_COUNT; i++)
                {
                    int n = queue.simulatedArray[i];
                    drawNumber(render, i, n);
                }
            }
            return;
        }

        if (currentAnimation == null || currentAnimation.animationAtEnd())
        {
            currentAnimation = new LerpAnimation(queue.getNextAction(), System.nanoTime());
        }

        main: for (int i = 0; i < NUMBER_COUNT; i++)
        {
            int[] toHide = currentAnimation.action.indicesToHide();

            for (int k : toHide)
            {
                if (k == i)
                {
                    continue main;
                }
            }

            int simulatedNumber = queue.simulatedArray[i];
            drawNumber(render, i, simulatedNumber);
        }

        currentAnimation.animate(render, queue.simulatedArray, deltaTime);
        currentAnimation.action.finishAction(queue.simulatedArray);
    }

    /*
     * Misc methods
     */

    int[] getRandomArray()
    {
        List<Integer> list = new ArrayList<>(NUMBER_COUNT);

        for (int i = 1; i < NUMBER_COUNT + 1; i++)
        {
            list.add(i);
        }

        Collections.shuffle(list);

        return list.stream().mapToInt(i -> i).toArray();
    }

    /*
     * Rendering methods
     */

    static void drawNumber(Render render, int index, int number)
    {
        render.fillRectangle(X_OFFSET + index * (NUMBER_WIDTH + SPACING), Y_OFFSET + NUMBER_COUNT - number, NUMBER_WIDTH, number, NUMBER_COLOR.apply(number));
    }

    static void drawColoredNumber(Render render, int index, int number, int color)
    {
        render.fillRectangle(X_OFFSET + index * (NUMBER_WIDTH + SPACING), Y_OFFSET + NUMBER_COUNT - number, NUMBER_WIDTH, number, color);
    }

    /*
     * Main
     */

    public static void main(String[] args)
    {
        new Sorting().setup();
    }

    /*
     * Sorting
     */

    interface SortingStrategy
    {
        void sort(int[] array, ActionQueue queue);
    }

    static class BubbleSort implements SortingStrategy
    {
        @Override
        public void sort(int[] array, ActionQueue queue)
        {
            int i, j, temp, n = array.length;
            boolean swapped;

            for (i = 0; i < n - 1; i++)
            {
                swapped = false;
                for (j = 0; j < n - i - 1; j++)
                {
                    queue.addAction(new CompareAction(j, j + 1));
                    if (array[j] > array[j + 1])
                    {
                        queue.addAction(new SwapAction(j, j + 1));
                        // Swap arr[j] and arr[j+1]
                        temp = array[j];
                        array[j] = array[j + 1];
                        array[j + 1] = temp;
                        swapped = true;
                    }
                }

                // If no two elements were
                // swapped by inner loop, then break
                if (!swapped)
                    break;
            }
        }
    }

    static class InsertionSort implements SortingStrategy
    {
        @Override
        public void sort(int[] array, ActionQueue queue)
        {
            int n = array.length;
            for (int i = 1; i < n; ++i)
            {
                int key = array[i];
                int j = i - 1;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */
                queue.addAction(new CompareAction(j, i));
                while (j >= 0 && array[j] > key)
                {
                    queue.addAction(new SwapAction(j, j + 1));
                    array[j + 1] = array[j];
                    j = j - 1;
                }
                array[j + 1] = key;
            }
        }
    }

    /*
     * Animations
     */

    static abstract class Animation
    {
        Action action;

        Animation(Action action)
        {
            this.action = action;
        }

        abstract void animate(Render render, int[] simulatedArray, double deltaTime);

        abstract boolean animationAtEnd();
    }

    static class LerpAnimation extends Animation
    {
        final long start;
        final long timeout;

        LerpAnimation(Action action, long start, long timeout)
        {
            super(action);
            this.start = start;
            this.timeout = timeout;
        }

        LerpAnimation(Action action, long start)
        {
            this(action, start, ANIMATION_NANOS);
        }

        @Override
        void animate(Render render, int[] simulatedArray, double deltaTime)
        {
            action.drawAction(render, simulatedArray, 0);
        }

        @Override
        public boolean animationAtEnd()
        {
            return System.nanoTime() - start >= timeout;
        }
    }

    /*
     * Action Queue
     */

    static class ActionQueue
    {
        List<Action> queue;
        int[] startingArray;
        int[] simulatedArray;

        ActionQueue(int[] startingArray)
        {
            this.queue = new ArrayList<>();
            this.startingArray = new int[startingArray.length];
            this.simulatedArray = new int[startingArray.length];
            System.arraycopy(startingArray, 0, this.startingArray, 0, startingArray.length);
            System.arraycopy(startingArray, 0, this.simulatedArray, 0, startingArray.length);
        }

        void addAction(Action action)
        {
            queue.add(action);
        }

        Action getNextAction()
        {
            return queue.remove(0);
        }

        int countActions()
        {
            return queue.size();
        }
    }

    /*
     * Actions
     */

    interface Action
    {
        int[] indicesToHide();

        void drawAction(Render render, int[] simulatedArray, double time);

        void finishAction(int[] simulatedArray);
    }

    static abstract class DoubleIndexAction implements Action
    {
        public final int index1, index2;
        private final int[] toHide;

        DoubleIndexAction(int index1, int index2)
        {
            this.index1 = index1;
            this.index2 = index2;
            toHide = new int[] {index1, index2};
        }

        @Override
        public int[] indicesToHide()
        {
            return toHide;
        }
    }

    static class SwapAction extends DoubleIndexAction
    {
        SwapAction(int index1, int index2)
        {
            super(index1, index2);
        }

        @Override
        public void drawAction(Render render, int[] simulatedArray, double time)
        {
            // TODO: add moving ?
            drawColoredNumber(render, index1, simulatedArray[index1], SWAP_COLOR);
            drawColoredNumber(render, index2, simulatedArray[index2], SWAP_COLOR);
        }

        @Override
        public void finishAction(int[] simulatedArray)
        {
            int temp = simulatedArray[index1];
            simulatedArray[index1] = simulatedArray[index2];
            simulatedArray[index2] = temp;
        }
    }

    static class CompareAction extends DoubleIndexAction
    {
        CompareAction(int index1, int index2)
        {
            super(index1, index2);
        }

        @Override
        public void drawAction(Render render, int[] simulatedArray, double time)
        {
            drawColoredNumber(render, index1, simulatedArray[index1], COMPARE_COLOR);
            drawColoredNumber(render, index2, simulatedArray[index2], COMPARE_COLOR);
        }

        @Override
        public void finishAction(int[] simulatedArray) { }
    }

    /*
     * Misc
     */

    @FunctionalInterface
    public interface IntToIntFunction
    {
        int apply(int value);
    }
}

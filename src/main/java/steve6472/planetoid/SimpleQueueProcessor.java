package steve6472.planetoid;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleQueueProcessor<T, R>
{
    private final ExecutorService executorService;
    private final BlockingQueue<T> inputQueue;
    private final BlockingQueue<ProcessedItem<T, R>> resultQueue;
    private final Function<T, R> workFunction;

    public SimpleQueueProcessor(Function<T, R> workFunction)
    {
        this.executorService = Executors.newSingleThreadExecutor();
        this.inputQueue = new LinkedBlockingQueue<>();
        this.resultQueue = new LinkedBlockingQueue<>();
        this.workFunction = workFunction;

        startProcessingThread();
    }

    private void startProcessingThread()
    {
        executorService.execute(() ->
        {
            try
            {
                while (true)
                {
                    T input = takeFromInputQueue(); // Blocking call
                    R result = workFunction.apply(input);
                    putInResultQueue(new ProcessedItem<>(input, result)); // Blocking call
                }
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void addWorkToQueue(T input)
    {
        inputQueue.add(input);
    }

    public void addWorkToQueue(T input, Predicate<T> removePredicate)
    {
        inputQueue.removeIf(removePredicate);
        inputQueue.add(input);
    }

    public Optional<ProcessedItem<T, R>> takeResultFromQueue()
    {
        ProcessedItem<T, R> processedItem = resultQueue.poll(); // Non-blocking call
        return Optional.ofNullable(processedItem);
    }

    private T takeFromInputQueue() throws InterruptedException
    {
        return inputQueue.take(); // Blocking call
    }

    private void putInResultQueue(ProcessedItem<T, R> processedItem) throws InterruptedException
    {
        resultQueue.put(processedItem); // Blocking call
    }

    public void shutdown()
    {
        executorService.shutdown();
    }

    public record ProcessedItem<T, R>(T input, R result) { }
}
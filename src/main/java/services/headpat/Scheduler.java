package services.headpat;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scheduler {
    @Getter
    private long tick = 0;
    private final HashMap<Long, ArrayList<Task>> activeTasks = new HashMap<>();

    public Scheduler()
    {

    }

    public void tick()
    {
        if (activeTasks.containsKey(this.tick))
        {
            ArrayList<Task> cloneList = new ArrayList<>(this.activeTasks.get(this.tick));
            for (Task task : cloneList)
            {
                if (task.isCancelled())
                    continue;

                if (task.getNextTick() == this.tick)
                {
                    task.getConsumer().accept(task);
                    if (task.isCancelled())
                        continue;

                    long nextTick = task.incrementNextTick();
                    if (!this.activeTasks.containsKey(nextTick))
                        this.activeTasks.put(nextTick, new ArrayList<>());

                    this.activeTasks.get(nextTick).add(task);
                }
            }
            activeTasks.remove(this.tick);
        }
        this.tick += 1;
    }

    public synchronized void addTask(Task task)
    {
        task.setNextTick(this.tick + task.getDelay());
        if (!this.activeTasks.containsKey(task.getNextTick()))
            this.activeTasks.put(task.getNextTick(), new ArrayList<>());

        this.activeTasks.get(task.getNextTick()).add(task);
    }
}

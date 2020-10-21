package services.headpat.jlibschedule;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Task {
    @Getter private final boolean async;
    @Getter private final long delay;
    @Getter private Consumer<Task> consumer;
    @Getter private final long interval;
    @Getter private final String taskName;
    @Getter private boolean cancelled = false;
    @Getter @Setter private long nextTick;
    @Getter private int currentIteration;
    @Getter private final int maxTimes;

    public static class Factory
    {
        private boolean _async = false;
        private long _delay = 0;
        private long _interval = 0;
        private String _name = "";
        private Consumer<Task> _consumer;
        private int _times = 1;

        public Factory async(boolean async)
        {
            this._async = async;
            return this;
        }

        public Factory delay(long delay, TimeUnit unit)
        {
            this._delay = unit.toMillis(delay) / 50;
            return this;
        }

        public Factory delayTicks(long ticks)
        {
            this._delay = ticks;
            return this;
        }

        public Factory execute(Runnable runnable)
        {
            return this.execute(task -> runnable.run());
        }

        public Factory execute(Consumer<Task> executor)
        {
            this._consumer = executor;
            return this;
        }

        public Factory interval(long interval, TimeUnit unit)
        {
            this._interval = unit.toMillis(interval) / 50;
            return this;
        }

        public Factory intervalTicks(long ticks)
        {
            this._interval = ticks;
            return this;
        }

        public Factory name(String name)
        {
            this._name = name;
            return this;
        }

        public Factory times(int times)
        {
            this._times = times;
            return this;
        }

        public Task submit(Scheduler scheduler)
        {
            Task task = new Task(this._async, this._delay, this._consumer, this._interval, this._name, this._times);
            scheduler.addTask(task);
            return task;
        }
    }


    private Task(boolean async, long delay, Consumer<Task> task, long interval, String taskName, int maxTimes)
    {
        this.async = async;
        this.delay = delay;
        this.consumer = task;
        this.interval = interval;
        this.taskName = taskName;
        this.maxTimes = maxTimes;
    }

    public void cancel()
    {
        this.cancelled = true;
        this.consumer = null;
    }

    public long incrementNextTick()
    {
        this.nextTick += this.interval;
        this.currentIteration++;
        if (this.maxTimes >= this.currentIteration)
            this.cancel();
        return this.nextTick;
    }

    public static Factory factory()
    {
        return new Factory();
    }
}

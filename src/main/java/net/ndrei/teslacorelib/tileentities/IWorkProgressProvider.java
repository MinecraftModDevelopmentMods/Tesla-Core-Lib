package net.ndrei.teslacorelib.tileentities;

/**
 * Created by CF on 2016-12-03.
 */
public interface IWorkProgressProvider {
    int getJobTicks();
    float getJobProgress();
    boolean hasJob();
}

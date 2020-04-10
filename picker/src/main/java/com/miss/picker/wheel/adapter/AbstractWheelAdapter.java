package com.miss.picker.wheel.adapter;

import java.util.LinkedList;
import java.util.List;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * Abstract Wheel adapter.
 */
public abstract class AbstractWheelAdapter implements WheelViewAdapter
{
    // Observers
    private List<DataSetObserver> datasetObservers;

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent)
    {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer)
    {
        if (datasetObservers == null)
        {
            datasetObservers = new LinkedList<DataSetObserver>();
        }
        datasetObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer)
    {
        if (datasetObservers != null)
        {
            datasetObservers.remove(observer);
        }
    }

    /**
     * Notifies observers about data changing
     */
    public void notifyDataChangedEvent()
    {
        if (datasetObservers != null)
        {
            for (DataSetObserver observer : datasetObservers)
            {
                observer.onChanged();
            }
        }
    }

    /**
     * Notifies observers about invalidating data
     */
    public void notifyDataInvalidatedEvent()
    {
        if (datasetObservers != null)
        {
            for (DataSetObserver observer : datasetObservers)
            {
                observer.onInvalidated();
            }
        }
    }
}

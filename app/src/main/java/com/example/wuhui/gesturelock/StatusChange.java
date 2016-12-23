package com.example.wuhui.gesturelock;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by wuhui on 2016/12/3.
 */

public class StatusChange {
    private int status=0;
    private PropertyChangeSupport changeSupport=new PropertyChangeSupport(this);

    public void setStatus(int status) {
        int oldStatus=this.status;
        this.status=status;
        changeSupport.firePropertyChange("status",oldStatus,status);
    }

    public int getStatus() {
        return status;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}

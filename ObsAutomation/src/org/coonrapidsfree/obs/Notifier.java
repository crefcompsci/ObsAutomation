package org.coonrapidsfree.obs;

import java.util.ArrayList;
import java.util.List;

public class Notifier {

    private static Notifier instance = null;

    private List<Observer> observers = new ArrayList<Observer> ();
    
    private Notifier() {

    }

    public static Notifier getInstance() {
        if (instance == null) {
            instance = new Notifier();

        }
        return instance;
    }
    
    public synchronized void notify(CamEnum shot) {
        for(Observer o: observers) {
            o.notify(shot);
        }
    }
    
    public void addObserver(Observer o) {
        observers.add(o);
    }

    public synchronized void setTransition(boolean on) {
        for(Observer o: observers) {
            o.setTransition(on);
        }
    }
}

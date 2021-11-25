package org.coonrapidsfree.obs;

public interface Observer {

    public void notify(CamEnum shot);

    public void setTransition(boolean on);

}

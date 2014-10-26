package model;

public interface Observable {
	public void addObserver(Observer observer);
	public void removeObserver(Observer observer);
	public void notifyObservers();
	public void notifyObservers(Object obj);
}

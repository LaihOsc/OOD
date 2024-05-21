package view;
import model.Album;

public interface MusicOrganizerObserver {
	boolean update(Album changed);
	void notifyClose();
}

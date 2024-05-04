package view;
import model.Album;

public interface MusicOrganizerObserver {
	void update(Album album, Album rootAlbum);
}

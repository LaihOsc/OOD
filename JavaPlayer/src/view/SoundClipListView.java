package view;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import model.Album;
import model.SoundClip;

public class SoundClipListView extends ListView<SoundClip> {

	private List<SoundClip> clips;
	
	public SoundClipListView() {
		super();
		clips = new ArrayList<>();
	}

	public SoundClipListView(ObservableList<SoundClip> arg0) {
		super(arg0);
		clips = new ArrayList<>();
	}
	
	public void getSubAlbumSongsRecursively(Album album)
	{
		for (SoundClip song: album.getSongs()) {
			if (!clips.contains(song)) {
				clips.add(song);
			}
		}
		
		if (album.getSubAlbums().size() > 0) {
			for (Album subAlbum: album.getSubAlbums()) {
				getSubAlbumSongsRecursively(subAlbum);
			}
			
		}
	}
	
	/**
	 * Displays the contents of the specified album and all subalbums
	 * @param album - the album which contents are to be displayed
	 */
	public void display(Album album)
	{
		this.getItems().clear();
//		clips = album.getSongs();
		getSubAlbumSongsRecursively(album);
		
		ObservableList<SoundClip> temp = FXCollections.observableList(clips);
		this.setItems(temp);
	}

	public List<SoundClip> getSelectedClips(){
		ObservableList<SoundClip> items = this.getSelectionModel().getSelectedItems();
		List<SoundClip> clips = new ArrayList<>(items);
		return clips;
	}
}

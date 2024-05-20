package controller;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javafx.scene.control.Alert;
import model.Album;
import model.SoundClip;
import model.SoundClipBlockingQueue;
import model.SoundClipLoader;
import model.SoundClipPlayer;
import view.MusicOrganizerWindow;
import view.AlbumWindow;
import view.MusicOrganizerObserver;

public class MusicOrganizerController {

	private MusicOrganizerWindow view;
	private SoundClipBlockingQueue queue;
	private Album root;
	private List<AlbumWindow> albumWindows;
	private List<MusicOrganizerObserver> observers;
	
	public MusicOrganizerController() {

		// TODO: Create the root album for all sound clips
		root = Album.createAlbum("All Sound Clips");
		
		// Create the blocking queue
		queue = new SoundClipBlockingQueue();
				
		// Create a separate thread for the sound clip player and start it
		
		(new Thread(new SoundClipPlayer(queue))).start();
		
		// Create a list for album windows.
		albumWindows = new ArrayList<>();
		
		// Create a list for the observers.
		observers = new ArrayList<>();
	}
	
	public void registerObserver(MusicOrganizerObserver observer) {
		observers.add(observer);
	}
	
	private void notifyObservers(Album album) {
		for (MusicOrganizerObserver observer: observers) {
			observer.update(album, root);
		}
	}
	
	/**
	 * Load the sound clips found in all subfolders of a path on disk. If path is not
	 * an actual folder on disk, has no effect.
	 */
	public Set<SoundClip> loadSoundClips(String path) {
		Set<SoundClip> clips = SoundClipLoader.loadSoundClips(path);
		for(SoundClip c : clips)
		{
			root.addSong(c);
		}
		return clips;
	}
	
	public void registerView(MusicOrganizerWindow view) {
		this.view = view;
	}
	
	/**
	 * Returns the root album
	 */
	public Album getRootAlbum(){
		return root;
	}
	
	/**
	 * Adds an album to the Music Organizer
	 */
	public void addNewAlbum(Album parentAlbum, String albumName)
	{
		Album newAlbum = Album.createSubAlbum(albumName, parentAlbum);
		view.displayMessage("Created new album '"+albumName+"' under '"+parentAlbum.getAlbumName()+"'");
		view.onAlbumAdded(parentAlbum, newAlbum);
		notifyObservers(parentAlbum);
	}
	
	/**
	 * Removes an album from the Music Organizer
	 */
	public void deleteAlbum(Album targetAlbum)
	{
		Album parent = targetAlbum.getParentAlbum();
		if(parent!=null)
		{
			view.displayMessage("Removed album '"+targetAlbum.getAlbumName()+"'");
			parent.removeSubAlbum(targetAlbum);
			view.onAlbumRemoved(targetAlbum);
			view.onClipsUpdated();
			notifyObservers(targetAlbum);
		}
	}
	
	/**
	 * Adds sound clips to an album
	 */
	public void addSoundClips(Album targetAlbum, List<SoundClip> songs)
	{
		boolean bModified = false;
		for(SoundClip song : songs)
		{
			if(targetAlbum.containsSong(song))
				continue;
			bModified = true;
			targetAlbum.addSong(song);
			view.displayMessage("Added song '"+song+"' to album '"+targetAlbum.getAlbumName()+"'");
		}
		if( bModified )
			view.onClipsUpdated();
			notifyObservers(targetAlbum);
	}
	
	/**
	 * Removes sound clips from an album
	 */
	public void removeSoundClips(Album targetAlbum, List<SoundClip> songs)
	{
		boolean bModified = false;
		for(SoundClip song : songs)
		{
			if(!targetAlbum.containsSong(song))
				continue;
			bModified = true;
			targetAlbum.removeSong(song);
			view.displayMessage("Removed song '"+song+"' from album '"+targetAlbum.getAlbumName()+"'");
		}
		if( bModified )
			view.onClipsUpdated();
			notifyObservers(targetAlbum);
	}
	
	/**
	 * Puts the selected sound clips on the queue and lets
	 * the sound clip player thread play them. Essentially, when
	 * this method is called, the selected sound clips in the 
	 * SoundClipTable are played.
	 */
	public void playSoundClips(){
		List<SoundClip> l = view.getSelectedSoundClips();
		queue.enqueue(l);
		for(int i=0;i<l.size();i++) {
			view.displayMessage("Playing " + l.get(i));
		}
	}
	
	
	/**
	 * Method for opening a new album window.
	 */
	public void openAlbumWindow(Album album) {
		if (album != null) {
			AlbumWindow albumWindow = new AlbumWindow();
			registerObserver(albumWindow);
			albumWindow.openAlbum(album, queue, view);
			albumWindows.add(albumWindow);
			// Alerts the user is no album is selected.
		} else {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No album selected");
			alert.setHeaderText(null);
			alert.setContentText("You need to select an album before opening a new window...");
			alert.showAndWait();
		}
	}
}

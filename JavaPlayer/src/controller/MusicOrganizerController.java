package controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Album;
import model.SoundClip;
import model.SoundClipBlockingQueue;
import model.SoundClipLoader;
import model.SoundClipPlayer;
import view.MusicOrganizerWindow;

public class MusicOrganizerController {

	private MusicOrganizerWindow view;
	private SoundClipBlockingQueue queue;
	private Album root;
	
	public MusicOrganizerController() {

		// TODO: Create the root album for all sound clips
		root = Album.createAlbum("All Sound Clips");
		
		// Create the blocking queue
		queue = new SoundClipBlockingQueue();
				
		// Create a separate thread for the sound clip player and start it
		
		(new Thread(new SoundClipPlayer(queue))).start();
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
		view.onAlbumAdded(newAlbum);
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
			view.onAlbumRemoved();
			view.onClipsUpdated();
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
}

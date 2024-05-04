package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import controller.MusicOrganizerController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;

import model.Album;
import model.SoundClip;
import model.SoundClipBlockingQueue;
import model.SoundClipPlayer;

public class AlbumWindow extends Stage implements MusicOrganizerObserver {
	
	private static MusicOrganizerController controller;
	private SoundClipListView soundClipTable;
	private Album currentAlbum;
	
	
	
	@Override
	public void update(Album album, Album rootAlbum) {
		//Checks if update concerns the window.
		
		if (!rootAlbum.containsAlbum(album)) {
			close();
		}
		
		if (album.getAlbumName() == currentAlbum.getAlbumName()) {
			soundClipTable.display(album);
		}
		
	}
    
    public void openAlbum(Album album, SoundClipBlockingQueue queue, MusicOrganizerWindow view) {
    	
    	currentAlbum = album;
         
         // Create a layout
         BorderPane layout = new BorderPane();
         
         
         // Set the scene with the layout
         Scene scene = new Scene(layout, 600, 400);
         
         setTitle(album.getAlbumName());
         
		 // Create the list in the right of the GUI
		 soundClipTable = createSoundClipListView(album, queue, view);
		 layout.setCenter(soundClipTable);
		 
		 scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

         setScene(scene);
         
         
         //Show the window
         show();
    }
 
	private SoundClipListView createSoundClipListView(Album album, SoundClipBlockingQueue queue, MusicOrganizerWindow view) {
		SoundClipListView v = new SoundClipListView();
		v.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		v.display(album);
		
		v.setOnMouseClicked(e->{
			if(e.getClickCount() == 2) {
				// This code gets invoked whenever the user double clicks in the sound clip table
				List<SoundClip> l = v.getSelectedClips();
				queue.enqueue(l);
				for(int i=0;i<l.size();i++) {
					view.displayMessage("Playing " + l.get(i));
				}
				
			}
		});
		
		return v;
	}
	
//	public void playSoundClips(){
//		List<SoundClip> l = view.getSelectedSoundClips();
//		queue.enqueue(l);
//		for(int i=0;i<l.size();i++) {
//			view.displayMessage("Playing " + l.get(i));
//		}
//	}
    
}
    
    
//	
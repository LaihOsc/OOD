package view;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import controller.MusicOrganizerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Album;
import model.SoundClip;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class MusicOrganizerWindow extends Application {
	
	private BorderPane bord;
	private static MusicOrganizerController controller;
	private TreeItem<Album> rootNode;
	private TreeView<Album> tree;
	private ButtonPaneHBox buttons;
	private SoundClipListView soundClipTable;
	private TextArea messages;
	private MenuBar menuBar;
	
	public static void main(String[] args) {
		controller = new MusicOrganizerController();
		if (args.length == 0) {
			controller.loadSoundClips("sample-sound");
		} else if (args.length == 1) {
			controller.loadSoundClips(args[0]);
		} else {
			System.err.println("too many command-line arguments");
			System.exit(0);
		}
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
				
		try {
			controller.registerView(this);
			primaryStage.setTitle("Music Organizer");
			
			bord = new BorderPane();
			
			// Create menubar
			MenuItem menuItem1 = new MenuItem("Save As...");
			menuItem1.setOnAction(e->{
				FileChooser saveChooser = new FileChooser();
				saveChooser.setTitle("Save album as...");
				saveChooser.getExtensionFilters().addAll(
				         new ExtensionFilter("Album Hierarchy", "*.ahl"),
				         new ExtensionFilter("Export HTML", "*.html"));
				File selectedFile = saveChooser.showSaveDialog(primaryStage);
				if (selectedFile!=null)
					saveAlbumsTo(selectedFile);
			});
			MenuItem menuItem2 = new MenuItem("Load Hierarchy");
			menuItem2.setOnAction(e->{
				FileChooser loadChooser = new FileChooser();
				loadChooser.setTitle("Load album...");
				loadChooser.getExtensionFilters().addAll(
				         new ExtensionFilter("Album Hierarchy", "*.ahl"));
				File selectedFile = loadChooser.showOpenDialog(primaryStage);
				if (selectedFile!=null)
					loadAlbumsFrom(selectedFile);
			});
			MenuItem menuItem3 = new MenuItem("Exit");
			menuItem3.setOnAction(e->{
				Platform.exit();
				System.exit(0);
			});
			Menu menu = new Menu("File");
			menu.getItems().addAll(menuItem1,menuItem2,menuItem3);
			menuBar = new MenuBar(menu);
			
			// Create buttons in the top of the GUI
			buttons = new ButtonPaneHBox(controller, this);
			VBox vBox = new VBox(menuBar, buttons);
			bord.setTop(vBox);

			// Create the tree in the left of the GUI
			tree = createTreeView();
			bord.setLeft(tree);
			
			// Create the list in the right of the GUI
			soundClipTable = createSoundClipListView();
			bord.setCenter(soundClipTable);
						
			// Create the text area in the bottom of the GUI
			bord.setBottom(createBottomTextArea());
			
			Scene scene = new Scene(bord);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					Platform.exit();
					System.exit(0);
					
				}
				
			});
			
			primaryStage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private TreeView<Album> createTreeView(){
		rootNode = new TreeItem<>(controller.getRootAlbum());
		TreeView<Album> v = new TreeView<>(rootNode);
		
		v.setOnMouseClicked(e->{
			if(e.getClickCount()==2) {
				// This code gets invoked whenever the user double clicks in the TreeView
				onClipsUpdated();
			}
		});

		
		return v;
	}
	
	private SoundClipListView createSoundClipListView() {
		SoundClipListView v = new SoundClipListView();
		v.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		v.display(controller.getRootAlbum());
		
		v.setOnMouseClicked(e->{
			if(e.getClickCount() == 2) {
				// This code gets invoked whenever the user double clicks in the sound clip table
				controller.playSoundClips();
			}
		});
		
		return v;
	}
	
	private ScrollPane createBottomTextArea() {
		messages = new TextArea();
		messages.setPrefRowCount(3);
		messages.setWrapText(true);
		messages.prefWidthProperty().bind(bord.widthProperty());
		messages.setEditable(false); // don't allow user to edit this area
		
		// Wrap the TextArea in a ScrollPane, so that the user can scroll the 
		// text area up and down
		ScrollPane sp = new ScrollPane(messages);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		return sp;
	}
	
	/**
	 * Displays the message in the text area at the bottom of the GUI
	 * @param message the message to display
	 */
	public void displayMessage(String message) {
		messages.appendText(message + "\n");
	}
	
	public Album getSelectedAlbum() {
		TreeItem<Album> selectedItem = getSelectedTreeItem();
		return selectedItem == null ? null : selectedItem.getValue();
	}
	
	private TreeItem<Album> getSelectedTreeItem(){
		return tree.getSelectionModel().getSelectedItem();
	}
	
	
	
	/**
	 * Pop up a dialog box prompting the user for a name for a new album.
	 * Returns the name, or null if the user pressed Cancel
	 */
	public String promptForAlbumName() {
		TextInputDialog dialog = new TextInputDialog();
		
		dialog.setTitle("Enter album name");
		dialog.setHeaderText(null);
		dialog.setContentText("Please enter the name for the album");
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
	}
	
	/**
	 * Return all the sound clips currently selected in the clip table.
	 */
	public List<SoundClip> getSelectedSoundClips(){
		return soundClipTable.getSelectedClips();
	}
	
	private void saveAlbumsTo(File outFile)
	{
		try {
			FileOutputStream fout = new FileOutputStream(outFile, true);
			fout.close();
		} catch (FileNotFoundException e) {
			displayMessage("Failed to write output file: "+outFile.toString());
			return;
		} catch (IOException e) {
		}
		displayMessage("Exported hierarchy to: "+outFile.toString());
	}
	private void loadAlbumsFrom(File outFile)
	{
		displayMessage("Imported hierarchy from: "+outFile.toString());
	}
	
	/**
	 * *****************************************************************
	 * Methods to be called in response to events in the Music Organizer
	 * *****************************************************************
	 */	
	
	
	
	/**
	 * Updates the album hierarchy with a new album
	 * @param newAlbum
	 */
	public void onAlbumAdded(Album parent, Album newAlbum){
		TreeItem<Album> root = tree.getRoot();
		TreeItem<Album> parentNode = findAlbumNode(parent, root);
		parentNode.getChildren().add(new TreeItem<>(newAlbum));
		parentNode.setExpanded(true); // automatically expand the parent node in the tree	
	}
	
	/**
	 * Updates the album hierarchy by removing an album from it
	 */
	public void onAlbumRemoved(Album toRemove){
		TreeItem<Album> root = tree.getRoot();
		TreeItem<Album> nodeToRemove = findAlbumNode(toRemove, root);
		nodeToRemove.getParent().getChildren().remove(nodeToRemove);
	}
	
	private TreeItem<Album> findAlbumNode(Album albumToFind, TreeItem<Album> root) {
		// recursive method to locate a node that contains a specific album in the TreeView
		if(root.getValue().equals(albumToFind)) {
			return root;
		}
			
		for(TreeItem<Album> node : root.getChildren()) {
			TreeItem<Album> item = findAlbumNode(albumToFind, node);
			if(item != null)
				return item;
		}
		
		return null;
	}
	
	/**
	 * Refreshes the clipTable in response to the event that clips have
	 * been modified in an album
	 */
	public void onClipsUpdated(){
		Album a = getSelectedAlbum();
		soundClipTable.display(a);
	}
	
}

package view;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;

import model.Album;
import model.SoundClip;

import java.io.IOException;

public class TestRun
{
	public static void mainX(String[] args) throws IOException
	{
		Album root = Album.createAlbum("Root Album");
		Album albA = Album.createSubAlbum("Album A", root);
		Album albB = Album.createSubAlbum("Album B", root);
		Album albC = Album.createSubAlbum("Album C", albB);
		assertNotNull(root);
		assertNotNull(albA);
		assertNotNull(albB);
		assertNotNull(albC);
		assertTrue(root.containsAlbum(albA));
		assertTrue(root.containsAlbum(albB));
		assertTrue(root.containsAlbum(albC));
		assertTrue(albB.containsAlbum(albC));
		assertFalse(albA.containsAlbum(albC));
		
		albB.removeSubAlbum(albC);
		albA.addSubAlbum(albC);
		assertFalse(albB.containsAlbum(albC));
		assertTrue(albA.containsAlbum(albC));
		
		Random rand = new Random();
		Files.list(new File("").toPath())
        .limit(15)
        .forEach(path -> {
            File fn = new File(path.toString());
            SoundClip newClip = new SoundClip(fn);
            Album toAlbum = root;
            switch(rand.nextInt() % 4)
            {
            case 0:
            	toAlbum = albA;
            	break;
            case 1:
            	toAlbum = albB;
            	break;
            case 2:
            	toAlbum = albC;
            	break;
            }
            toAlbum.addSong(newClip);
            assertTrue(toAlbum.containsSong(newClip));
        });
		
		root.DumpAlbum("*");
	}
}

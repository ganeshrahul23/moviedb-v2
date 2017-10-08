package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class FileUtils {
	private static ArrayList<String> movieFilePaths = new ArrayList<>();
	
	public static ArrayList<String> getMovieNames(String path)
	{   
        movieFilePaths.clear();
		ArrayList<String> movieNames = new ArrayList<>();
		//String path = "F:\\Videos\\Movies\\English";	                                 
		File f =  new File(path);			
		displayDirectoryContents(f);		
		String[] movieFilePathsArray = movieFilePaths.toArray(new String[movieFilePaths.size()]);		
		for(int i=0; i<movieFilePathsArray.length; i++) 
		{
			String [] temparray = movieFilePathsArray[i].split("\\\\");
			movieNames.add(temparray[temparray.length - 1]);
		}					
		return movieNames;
	}
	
	public static void displayDirectoryContents(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				Path myPath = Paths.get(file.getPath());
				try {
					boolean fa = Files.list(myPath).anyMatch(x -> Files.isDirectory(x));
					if(!fa) 
					{
						movieFilePaths.add(file.getPath());
					}
				} catch (IOException e) 
				{
					e.printStackTrace();
				}																
				displayDirectoryContents(file);
			} 
		}		
	}
}

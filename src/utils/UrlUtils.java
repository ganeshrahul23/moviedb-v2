package utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.net.*;
import javax.swing.JOptionPane;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import org.json.*;

public class UrlUtils 
{
	
	private static final String api = "70b2ffcbde2a148d3168f35ac66ce32f";   
	private static final CloseableHttpClient httpclient = HttpClients.createDefault();
	private static HttpGet httpget;
	public static final HashMap<String, String> genresHM = getGenreIds();
	private static String baseImagePath = "F:\\Academic\\JAVA\\moviedDB Data\\" ;
                	
	public static HashMap<String,String> getMovieDetailsByName(String moviename)
	{
    	HashMap<String,String> hm = new HashMap<>();
        String movieFolderName = moviename;
    	moviename = moviename.replace(" ", "+");
    	String starturl = "https://api.themoviedb.org/3/search/movie?api_key=";
    	String middleurl = "&language=en-US&query=";
    	String endurl = "&page=1&include_adult=false";
    	String url = starturl + api + middleurl + moviename + endurl;
    	httpget = new HttpGet(url);
    	String id,title,overview,posterPath,genres;
    	StringBuilder temp = new StringBuilder();
    	try {
			CloseableHttpResponse response = httpclient.execute(httpget);			
			HttpEntity entity = response.getEntity();

			InputStream instream = entity.getContent();
			String result = convertStreamToString(instream);

			JSONObject mainjson = new JSONObject(result);
			boolean flag = Objects.equals(mainjson.get("total_results").toString(), "0");
			if(!flag)
			{
				JSONArray resultsArray = mainjson.getJSONArray("results");
				JSONObject resultOne = resultsArray.getJSONObject(0);

				String [] genreIdsArray = resultOne.get("genre_ids").toString().replace("[", "").replace("]", "").split(",");
				for(String s : genreIdsArray) 
				{				
					temp.append(genresHM.get(s));
					temp.append(", ");
				}
				temp.deleteCharAt(temp.length() - 1);
				temp.deleteCharAt(temp.length() - 1);
				genres = temp.toString();					
				id = resultOne.get("id").toString();
				title = resultOne.get("title").toString();
				overview = resultOne.get("overview").toString();                                                                 
				posterPath = resultOne.get("poster_path").toString();
				hm.put("Title", title);
				hm.put("Id", id);
				hm.put("Overview", overview);
				hm.put("Genres", genres);			
				hm.put("PosterPath", posterPath);
				hm.put("FolderName",movieFolderName);
				hm.put("Cast", getCastByID(id));
				saveImgByPosterPath(id, hm.get("PosterPath"));
			}else {
				return hm = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No Internet Connection");
		} catch (JSONException e) {
			e.printStackTrace();
		} 
    	return hm;
    }
	
	
    public static String getCastByID(String movieID){
    	String cast = null;
    	String starturl = "https://api.themoviedb.org/3/movie/";
    	String endurl = "/credits?api_key=";
    	String url = starturl + movieID + endurl + api;
    	httpget = new HttpGet(url);
    	StringBuilder temp = new StringBuilder();
    	try {
			CloseableHttpResponse response = httpclient.execute(httpget);			
			HttpEntity entity = response.getEntity();

			InputStream instream = entity.getContent();
			String result = convertStreamToString(instream);

			JSONObject mainjson = new JSONObject(result);
			JSONArray castArr = mainjson.getJSONArray("cast");

			int len = castArr.length();
			//len = len >= 5 ? 5:len;
			JSONObject [] casts = new JSONObject[len];
			for(int i = 0; i<len ; i++) 
			{
				casts[i] = castArr.getJSONObject(i);
				if((casts[i].get("name").toString().length() + temp.length()) <= 2000){
						temp.append(casts[i].get("name").toString());
						temp.append(", ");                                                                                  
				}else{
					break;
				}
			}
			if(temp.length() == 0) {
					cast = "";
			}else {
				temp.deleteCharAt(temp.length() - 1);
				temp.deleteCharAt(temp.length() - 1);
				cast = temp.toString();					
			}		
    	}catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}   	
    	return cast;    	
    }
    
    
    public static void saveImgByPosterPath(String id, String posterPath) {
    	String baseurl = "http://image.tmdb.org/t/p/w185/";
    	String url = baseurl + posterPath;
    	String imagePath = baseImagePath + id +".jpg";
		File temp = new File(baseImagePath);
		if(!temp.exists())
		{
			temp.mkdir();
		}
		temp = new File(imagePath);
		if(!temp.exists())
		{
			try {
				URL imageurl = new URL(url);
				InputStream in = new BufferedInputStream(imageurl.openStream());
				OutputStream out = new BufferedOutputStream(new FileOutputStream(imagePath));
				for ( int i; (i = in.read()) != -1; ) 
				{
					out.write(i);
				}
				in.close();
				out.close();
			 } catch (MalformedURLException e) {
				 e.printStackTrace();
			 } catch (IOException e) {
				 e.printStackTrace();
			}
        }        
    }
     
    private static HashMap<String,String> getGenreIds(){
		HashMap<String,String> hm = new HashMap<>();
		hm.put("99","Documentary");
		hm.put("12","Adventure");
		hm.put("35","Comedy");
		hm.put("14","Fantasy");
		hm.put("36","History");
		hm.put("37","Western");
		hm.put("16","Animation");
		hm.put("27","Horror");
		hm.put("28","Action");
		hm.put("18","Drama");
		hm.put("10749","Romance");
		hm.put("80","Crime");
		hm.put("10770","TV Movie");
		hm.put("878","Science Fiction");
		hm.put("9648","Mystery");
		hm.put("10752","War");
		hm.put("10751","Family");
		hm.put("10402","Music");
		hm.put("53","Thriller");   	
		return hm;   	
    }
    
    private static String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) 
			{
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}      

    public static void setBaseImagePath(String aBaseImagePath) {
        baseImagePath = aBaseImagePath;
	}

    public static String getBaseImagePath() 
	{
        return baseImagePath;
    }	
}
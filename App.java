package api.RestAPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class App 
{
    public static void main( String[] args ) throws Exception {
    	
    	List<String> list = new ArrayList<String>();
    	list.add("https://keithito.github.io/audio-samples/eval-441000-0.wav");
    	list.add("https://keithito.github.io/audio-samples/eval-441000-1.wav");
    	list.add("https://keithito.github.io/audio-samples/eval-441000-2.wav");
    	list.add("https://keithito.github.io/audio-samples/eval-441000-3.wav");
    	list.add("https://keithito.github.io/audio-samples/eval-441000-4.wav");
    	list.add("https://keithito.github.io/audio-samples/eval-441000-5.wav");
    	
    	Transcript transcript = new Transcript();
    	transcript.setAudio_url(list.get(0));
    	
    	Gson gson = new Gson();
    	String jsonRequest = gson.toJson(transcript);
    	
    	HttpRequest postRequest = HttpRequest.newBuilder()
    			.uri(new URI(Constants.URI_LINK))
    			.header("Authorization", Constants.API_KEY)
    			.POST(BodyPublishers.ofString(jsonRequest))
    			.build();
    	
    	HttpClient client = HttpClient.newHttpClient();
    	HttpResponse<String> postResponse =  client.send(postRequest, BodyHandlers.ofString());
    	
    	transcript = gson.fromJson(postResponse.body(), Transcript.class);
    	
    	HttpRequest getRequest = HttpRequest.newBuilder()
    			.uri(new URI(Constants.URI_LINK + "/" + transcript.getId()))
    			.header("Authorization", Constants.API_KEY)
    			.GET()
    			.build();
 	
    	while (true) {
			
    		HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
			transcript = gson.fromJson(getResponse.body(), Transcript.class);
			
			if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
				break;
			}
			
			System.out.println(transcript.getStatus() + (" (please wait)"));
			
			Thread.sleep(2000);
			
		}
    	
    	Path path = Paths.get("transcript.txt");
    	
    	if ("completed".equals(transcript.getStatus())){
    		Files.writeString(path, transcript.getText(), StandardCharsets.UTF_8);
    		System.out.println("Transcript saved succesfully!");

    	} else System.out.println("\nError: No transcript available!");
        
    }
    
}

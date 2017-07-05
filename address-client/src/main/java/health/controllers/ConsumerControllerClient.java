package health.controllers;


import java.io.IOException;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Controller
public class ConsumerControllerClient {
	
	@Autowired
	private LoadBalancerClient loadBalancer;
	
	public void getService() throws RestClientException, IOException {
		
		ServiceInstance serviceInstance=loadBalancer.choose("address-register");
		
		System.out.println(serviceInstance.getUri());
		
		String baseUrl=serviceInstance.getUri().toString();
		
		baseUrl=baseUrl+"/service";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	   
		JSONObject responseJson = (JSONObject) JSONValue.parse(response.getBody());
	    String address = (String) responseJson.get("address");
	    String port = (String) responseJson.get("port");

        //System.out.println("The address is " + address);
        PythonExecution(address,port);
        //PythonExecution("localhost","1883");

       
	}

	private void PythonExecution(String address, String port) {

		try {
			
			ProcessBuilder pb = new ProcessBuilder("python", "python/TemperatureLevels.py","20", "kostis", "10", address, port, "mosquitto-kostis");
			//ProcessBuilder pb = new ProcessBuilder("python", "python/hello.py 20  10");
			Process p = pb.start();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
}

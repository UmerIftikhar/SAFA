package client_sock;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;



public class Client_Sock implements IOCallback {
	//private SocketIO socket;
        public SocketIO socket;
        
	public Client_Sock() throws Exception {
		socket = new SocketIO();
		socket.connect("http://localhost:5000/", this);

                //JSONObject myObj = new JSONObject();
                //myObj.put("name", "foo");
                //myObj.put("num", new Integer(100));
                //myObj.put("balance", new Double(1000.21));
                //myObj.put("is_vip", new Boolean(true));
                
		// Sends a string to the server.
		//socket.send("Hello Server");

		// Sends a JSON object to the server.
		//socket.send(new JSONObject().put("key", "value").put("key2","another value"));

		// Emits an event to the server.
		//socket.emit("event", "argument1", "argument2", 13.37);
                //socket.emit("event", myObj);

	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		try {
			System.out.println("Server said:" + json.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("Server said: " + data);
	}

	@Override
	public void onError(SocketIOException socketIOException) {
		System.out.println("an Error occured");
		socketIOException.printStackTrace();
	}

	@Override
	public void onDisconnect() {
		System.out.println("Connection terminated.");
	}

	@Override
	public void onConnect() {
		System.out.println("Connection established");
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
            
		System.out.println("Server triggered event '" + event + "'");
	}
        
//{"name":"newEvent","args":[{"time":49903,"location":{"Zone":"Z4","WS":"WS8"},"palletID":"1468320544202"}]}        
        
}

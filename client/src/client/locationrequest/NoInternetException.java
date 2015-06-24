package client.locationrequest;

public class NoInternetException extends Exception {

	private static final long serialVersionUID = -2924932146778259090L;
	
	public NoInternetException (String message){
		super(message);
	}

}
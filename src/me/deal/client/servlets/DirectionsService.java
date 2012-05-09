package me.deal.client.servlets;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.  DirectionsService is the
 * RPC service used to get directions between two locations.
 */
@RemoteServiceRelativePath("directions")
public interface DirectionsService extends RemoteService {
	
}

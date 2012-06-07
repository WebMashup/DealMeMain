package me.deal.client.view.menubar;

import java.util.ArrayList;

import me.deal.client.events.DealsEvent;
import me.deal.client.events.DealsEventHandler;
import me.deal.client.model.Deals;
import me.deal.client.servlets.DealServiceAsync;
import me.deal.client.servlets.GeocodingServiceAsync;
import me.deal.shared.Deal;
import me.deal.shared.LatLngCoor;
import me.deal.shared.Location;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.base.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class LocationWidget extends Composite {

	interface LocationWidgetUiBinder extends UiBinder<Widget, LocationWidget> {
    }
	
    private static LocationWidgetUiBinder uiBinder = GWT
            .create(LocationWidgetUiBinder.class);
    
    private static final Integer LOADING_DELAY = 100;
    
    private final GeocodingServiceAsync geocodingService;
    private final DealServiceAsync dealService;
    private final HandlerManager eventBus;
    
    private Integer loadingState = 0;
    private String[] loadingStrings = {
            "Acquiring location", "Acquiring location.",
            "Acquiring location..", "Acquiring location..."};
    private Boolean locationInitialized = false;
    private boolean mapView = false;
    
    @UiField
    ListBox radius;
    
    @UiField
    Label addressLine1;
    
    @UiField
    TextBox address;
    
    @UiField
    TextBox city;
    
    @UiField
    TextBox state;
    
    @UiField
    TextBox zip;
    
    @UiField
    Button changeLocationButton;
    
 // On click handler for submit button
    @UiHandler("changeLocationButton")
    void handleClick(ClickEvent e) {
    	
        // Get all location inputs
        String addressValue = address.getValue();
        String cityValue = city.getValue();
        String stateValue = state.getValue();
        String zipValue = zip.getValue();
        
        // Create new location with inputs
        Location loc = new Location();
        loc.setAddress(addressValue);
        loc.setCity(cityValue);
        loc.setState(stateValue);
        loc.setZipCode(zipValue);
        
        // Convert address into a coordinate using the geocodingService
        geocodingService.convertAddressToLatLng(loc, new AsyncCallback<LatLngCoor>() {
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(LatLngCoor result) {
            	
            	// Get instance of Deals and update the location coordinates
            	Deals deals = Deals.getInstance();
                deals.getLocation().setLatLng((LatLngCoor) result);

                // Update radius of Deals instance
                String radiusString = radius.getItemText(radius.getSelectedIndex());
                double radiusDouble = Double.parseDouble(radiusString);
                deals.setRadius(radiusDouble);

                // Do a new deals search with updated location info
                dealService.getYipitDeals(deals.getLocation().getLatLng(),
                        deals.getRadius(),
                        Deals.DEFAULT_NUM_DEALS,
                        deals.getOffset(),
                        deals.getTags(),
                        new AsyncCallback<ArrayList<Deal>>() {
                	
                            @Override
                            public void onFailure(Throwable caught) {
                                Window.alert(caught.getMessage());
                            }

                            @Override
                            public void onSuccess(ArrayList<Deal> result) {
                                // Update the Deals instance with the retrieved deals
                            	Deals deals = Deals.getInstance();
                                deals.setDeals(result);
                                deals.setOffset(result.size());

                                // Tell all other widgets that deals have been updated
                                eventBus.fireEvent(new DealsEvent());
                            }
                });
                
                // Find the corresponding address for updated coordinate to display to user
                geocodingService.convertLatLngToAddress(result, new AsyncCallback<Location>() {

                    @Override
                    public void onFailure(Throwable caught) {
                    	Window.alert(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Location result) {
                    	String line1 = "Current address: " + 
                        		result.getAddress() + ", " + 
                        		result.getCity() + ", " + 
                        		result.getState() + " " + 
                        		result.getZipCode();
                        addressLine1.setText(line1);
                        
                        Deals deals = Deals.getInstance();
                        deals.setLocation(result);
                        deals.setLocation(result);
                    }
                });
            }
        });    
    }
    
    // Called when LocationWidget is created, binds XML UI to this widget
    public @UiConstructor LocationWidget(final GeocodingServiceAsync geocodingService,
            		final DealServiceAsync dealService,
            		final HandlerManager eventBus)
    {
        initWidget(uiBinder.createAndBindUi(this));
        this.geocodingService = geocodingService;
        this.dealService = dealService;
        this.eventBus = eventBus;
        
        initialize();
    }
    
    // Updates the map
    public void setMapSize(boolean mapView)
    {
        this.mapView = mapView;
    }
    
    // Initializes loading of deals
    private void initialize() {
    	// Disable submit button
        changeLocationButton.setEnabled(false);
        
        // Control the loading ellipses
        addressLine1.setText("Acquiring location");
        loadingState++;
        loadingState %= 3;
        
        // Create timer and add delay
        final Timer t = new Timer() {
            public void run() {
                loadingState++;
                loadingState %= 3;
                addressLine1.setText(loadingStrings[loadingState]);
            }
        };
        t.schedule(LOADING_DELAY);
        
        // Add handler to allow user to change the location after DealsEvent is received
        eventBus.addHandler(DealsEvent.TYPE, new DealsEventHandler() {
            @Override
            public void onDeals(DealsEvent event) {
                if(locationInitialized.equals(false)) {
                    t.cancel();
                    Location userLoc = Deals.getInstance().getLocation();
                    Deals.getInstance().setLocation(userLoc);
                    String line1 = "Current address: " + 
                    		userLoc.getAddress() + ", " + 
                    		userLoc.getCity() + ", " + 
                    		userLoc.getState() + " " + 
                    		userLoc.getZipCode();
                    addressLine1.setText(line1);
                    changeLocationButton.setEnabled(true);
                    locationInitialized = true;
                }
            }
        });
    }
}

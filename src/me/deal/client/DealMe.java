package me.deal.client;

import java.util.ArrayList;

import me.deal.client.events.DealsEvent;
import me.deal.client.model.Deals;
import me.deal.client.servlets.DealService;
import me.deal.client.servlets.DealServiceAsync;
import me.deal.client.servlets.DirectionsService;
import me.deal.client.servlets.DirectionsServiceAsync;
import me.deal.client.servlets.GeocodingService;
import me.deal.client.servlets.GeocodingServiceAsync;
import me.deal.client.view.main.GoogleMapWidget;
import me.deal.client.view.main.ListWidget;
import me.deal.client.view.menubar.FilterWidget;
import me.deal.client.view.menubar.LocationWidget;
import me.deal.client.view.menubar.MapFilterWidget;
import me.deal.client.view.menubar.MapLocationWidget;
import me.deal.shared.Deal;
import me.deal.shared.LatLngCoor;
import me.deal.shared.Location;

import com.github.gwtbootstrap.client.ui.Brand;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Nav;
import com.github.gwtbootstrap.client.ui.ResponsiveNavbar;
import com.github.gwtbootstrap.client.ui.constants.Alignment;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.Position.Coordinates;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
* Entry point classes define <code>onModuleLoad()</code>.
*/
public class DealMe implements EntryPoint {
    
    interface MyUiBinder extends UiBinder<Widget,DealMe> {
        MyUiBinder INSTANCE = GWT.create(MyUiBinder.class);
}


    private final DealServiceAsync dealService =  GWT.create(DealService.class);
    private final DirectionsServiceAsync directionsService = GWT.create(DirectionsService.class);
    private final GeocodingServiceAsync geocodingService = GWT.create(GeocodingService.class);
    
    private final HandlerManager eventBus = new HandlerManager(null);
        
    /**ListView**/
    
    @UiField (provided=true)
    ScrollPanel mainScrollPanel;
    
    @UiField (provided=true)
    FilterWidget filterWidget;
    
    @UiField (provided=true)
    LocationWidget locationWidget;
    
    @UiField (provided=true)
    ListWidget listWidget;
    
    @UiField (provided=true)
    GoogleMapWidget googleMapWidget;
    
    @UiField 
    FlowPanel filterPanel;
    
    @UiField
    FlowPanel locationPanel;
    
    @UiField
    Button filterButton;
    
    @UiField
    Button locationButton;
    
    /*@UiField
    com.google.gwt.user.client.ui.Label scrollPopupLabel;*/
    
    Widget w;
    
    /** NavBar **/ 
    HorizontalPanel navBarPanel;
    boolean mapViewFlag = false;    
    boolean listViewFlag = true;
    
    /** MapView **/    
    GoogleMapWidget newMapWidget;
    
    /** PopupPanel **/
    FlowPanel listViewPanel;    
    FlowPanel mapViewPanel;    
    PopupPanel popup;    
    FlowPanel popupMainPanel;    
    MapFilterWidget mapFilterWidget;    
    MapLocationWidget mapLocationWidget;
    
    /** NavBar Buttons **/
    @UiHandler("filterButton")
    void handleClick1(ClickEvent e) {
        if (filterPanel.isVisible())
            filterPanel.setVisible(false);
        else {
            if (locationPanel.isVisible()) {
                locationPanel.setVisible(false);
                filterPanel.setVisible(true);
            }
            else
                filterPanel.setVisible(true);
        }
    }
    
    @UiHandler("locationButton")
    void handleClick(ClickEvent e) {
        if (locationPanel.isVisible())
            locationPanel.setVisible(false);
        else {
            if (filterPanel.isVisible()) {
                filterPanel.setVisible(false);
                locationPanel.setVisible(true);
            }
            else
                locationPanel.setVisible(true);
        }
    }
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

    	
    	/** Initialize User Location **/
    	getUserLocation();
    	
    	/** ListView **/
        mainScrollPanel = new ScrollPanel();
        filterWidget = new FilterWidget(dealService, eventBus);
        locationWidget = new LocationWidget(geocodingService, dealService, eventBus);
        listWidget = new ListWidget(mainScrollPanel, dealService, directionsService, eventBus);
        googleMapWidget = new GoogleMapWidget(dealService, eventBus, false);
               
        filterPanel = new FlowPanel();
        locationPanel = new FlowPanel();
        
        filterButton = new Button("Filters");
        locationButton = new Button("Location");   
        
        /** NAVIGATION BAR **/
        Brand brand = new Brand("Deal.Me");
        brand.setStylePrimaryName("logoStyle");
        Button listLink = new Button("List View");
        Button mapLink = new Button("Map View");
        Button contactLink = new Button("Contact Us");
        
        listLink.setStylePrimaryName("navBarButtonReplace");
        mapLink.setStylePrimaryName("navBarButtonReplace");
        contactLink.setStylePrimaryName("navBarButtonReplace");
        contactLink.setHref("mailto:dealmedev@gmail.com");
        
        Nav nav = new Nav();
        nav.setAlignment(Alignment.RIGHT);
        nav.add(contactLink);
        
        FlowPanel innerNav = new FlowPanel();
        innerNav.setStylePrimaryName("innerNav");
        
        innerNav.add(brand);
        innerNav.add(listLink);
        innerNav.add(mapLink);
        innerNav.add(nav);
        
        navBarPanel = new HorizontalPanel();
        navBarPanel.setStylePrimaryName("navBarStyle");
        navBarPanel.setWidth("100%");
        navBarPanel.add(innerNav);
        navBarPanel.setHeight("60px");
         
        /*
        mainScrollPanel.addScrollHandler(new ScrollHandler() {

			@Override
			public void onScroll(ScrollEvent event) {
				// TODO Auto-generated method stub

	        	scrollPopupLabel.setText("currPos = " + mainScrollPanel.getVerticalScrollPosition() + ", maxPos = " + mainScrollPanel.getMaximumVerticalScrollPosition());
			}
        });
        */
        
        mapLink.addClickHandler(new ClickHandler() {
              public void onClick(ClickEvent event) {
            	  
                  if (!mapViewFlag) {
                      setMapView();
                      
                      mapViewFlag = true;
                      listViewFlag = false;                      
                      googleMapWidget.setMapSize(mapViewFlag);
                      filterWidget.setMapSize(mapViewFlag);
                      locationWidget.setMapSize(mapViewFlag);
                      
                      popup.show();
                      
                      Deals deals = Deals.getInstance();
                      deals.setOffset(0);
                      
                      dealService.getYipitDeals(deals.getLocation().getLatLng(),
                          deals.getRadius(),
                          deals.DEFAULT_NUM_DEALS,
                          deals.getOffset(),
                          deals.getTags(),
                          new AsyncCallback<ArrayList<Deal>>() {
                              @Override
                              public void onFailure(Throwable caught) {
                                  Window.alert("Failed to load deals.");
                              }
          
                              @Override
                              public void onSuccess(ArrayList<Deal> result) {
                                  Deals deals = Deals.getInstance();
                                  deals.setOffset(result.size());
                                  deals.setDeals(result);
                                  eventBus.fireEvent(new DealsEvent());
                              }
                          });
                  } 
              }
          });
        
        listLink.addClickHandler(new ClickHandler() {
              public void onClick(ClickEvent event) {
                  if (!listViewFlag) {
                      setListView();
                      
                      listViewFlag = true;
                      mapViewFlag = false;
                      googleMapWidget.setMapSize(mapViewFlag);
                      filterWidget.setMapSize(mapViewFlag);
                      locationWidget.setMapSize(mapViewFlag);
                      
                      Deals deals = Deals.getInstance();
                      deals.setOffset(0);
                      dealService.getYipitDeals(deals.getLocation().getLatLng(),
                          deals.getRadius(),
                          deals.DEFAULT_NUM_DEALS,
                          deals.getOffset(),
                          deals.getTags(),
                          new AsyncCallback<ArrayList<Deal>>() {
                              @Override
                              public void onFailure(Throwable caught) {
                                  Window.alert("Failed to load deals.");
                              }
          
                              @Override
                              public void onSuccess(ArrayList<Deal> result) {
                                  Deals deals = Deals.getInstance();
                                  deals.setOffset(result.size());
                                  deals.setDeals(result);
                                  eventBus.fireEvent(new DealsEvent());
                              }
                          });
                  } 
              }
          });
        
        /**POPUPPANEL FOR LARGEMAP FILTER/LOCATION**/
        newMapWidget = new GoogleMapWidget(dealService, eventBus, true);
        newMapWidget.setStylePrimaryName("mapStyle");
        
        mapFilterWidget = new MapFilterWidget(dealService, eventBus);
        mapLocationWidget = new MapLocationWidget(geocodingService, dealService, eventBus);
        
        popup = new PopupPanel();
        popupMainPanel = new FlowPanel();
        
        popup.setHeight("300px");
        popup.add(popupMainPanel);
        
        popupMainPanel.add(mapLocationWidget);
        popupMainPanel.add(mapFilterWidget);
        
        int left = (20);
        int top = (Window.getClientHeight() - 20);        
        popup.setPopupPosition(left, top);

        /** Final Bindings **/
        w = MyUiBinder.INSTANCE.createAndBindUi(this);        

        listViewPanel = new FlowPanel();
        listViewPanel.setWidth("100%");
        listViewPanel.add(navBarPanel);
        listViewPanel.add(w);
        
        mapViewPanel = new FlowPanel();
        mapViewPanel.setWidth("100%");
        w.setHeight("100%");
        
        //First display list view
        RootLayoutPanel.get().add(listViewPanel);
        
        //Hide the filter and location panels for now
        filterPanel.setVisible(false);
        locationPanel.setVisible(false);
    }
    
    private void setListView()
    {
        RootLayoutPanel.get().clear();
        
        listViewPanel.clear();
        listViewPanel.add(navBarPanel);
        listViewPanel.add(w);
        
        RootLayoutPanel.get().add(listViewPanel);
        
        //Reset and hide option panels
        filterPanel.setVisible(false);
        locationPanel.setVisible(false);
        popup.setVisible(false);
    }
    
    private void setMapView()
    {
    	//Reset user screen
        RootLayoutPanel.get().clear();
        
        mapViewPanel.clear();
        mapViewPanel.add(navBarPanel);
        mapViewPanel.add(newMapWidget);
        
        RootLayoutPanel.get().add(mapViewPanel);
        
        popupMainPanel.clear();
        popupMainPanel.add(new Label("Location"));
        popupMainPanel.add(mapLocationWidget);
        popupMainPanel.add(new Label("Filters:"));
        popupMainPanel.add(mapFilterWidget);
        popup.setStylePrimaryName("popupPlacement");
        popup.setVisible(true);
        popup.show();
        
    }
    
    private void getUserLocation() {
        Geolocation geo;
        if((geo = Geolocation.getIfSupported()) != null) {
            geo.getCurrentPosition(
            new Callback<Position, PositionError>() {
                    @Override
                    public void onFailure(PositionError reason) {
                        Window.alert(reason.toString());
                    }

                    @Override
                    public void onSuccess(Position result) {
                        Coordinates userCoor = result.getCoordinates();
                        LatLngCoor userLatLng = new LatLngCoor(userCoor.getLatitude(), userCoor.getLongitude());
                        geocodingService.convertLatLngToAddress(userLatLng, new AsyncCallback<Location>() {
                            @Override
                            public void onFailure(Throwable caught) {
                            	Window.alert(caught.toString());
                                Window.alert("Failed to geocode!");
                            }

                            @Override
                            public void onSuccess(final Location result) {
                                Deals.getInstance().setLocation(result);
                                Deals deals = Deals.getInstance();
                                dealService.getYipitDeals(deals.getLocation().getLatLng(),
                                        deals.getRadius(),
                                        deals.DEFAULT_NUM_DEALS,
                                        deals.getOffset(),
                                        deals.getTags(),
                                        new AsyncCallback<ArrayList<Deal>>() {
                                            @Override
                                            public void onFailure(Throwable caught) {
                                                Window.alert("Failed to load deals.");
                                            }

                                            @Override
                                            public void onSuccess(ArrayList<Deal> result) {
                                                Deals deals = Deals.getInstance();
                                                deals.setDeals(result);
                                                deals.setOffset(result.size());
                                                eventBus.fireEvent(new DealsEvent());
                                            }
                                });
                            }
                        });
                    }
            });
        }
    }
}
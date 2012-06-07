package me.deal.client.model;

import java.util.ArrayList;

import me.deal.shared.Category;
import me.deal.shared.Deal;
import me.deal.shared.LatLngCoor;
import me.deal.shared.Location;

public class Deals {

    public static final Integer DEFAULT_NUM_DEALS = 20;
	
    private static final Deals INSTANCE = new Deals();
    private ArrayList<Deal> deals;
    private Location location;
    private Location userLocation;
    private Double radius;
    private Integer offset;
    private ArrayList<Category> tags;
    private Boolean reset;
    private Integer dealIndex;
    private int duplicates;
    private boolean resize;

    private Deals() {
        deals = new ArrayList<Deal>();
        location = new Location();
        radius = new Double(10);
        offset = new Integer(0);
        tags = new ArrayList<Category>();
        reset = new Boolean(false);
        duplicates = 0;
        resize = true;
        dealIndex = 0;
    }
    
    public static Deals getInstance() {
        return INSTANCE;
    }
    
    public ArrayList<Deal> getDeals() {
        return deals;
    }
    
    private void setIDUrl(Integer dealIndex)
    {
    	Deal current = deals.get(dealIndex);
    	Integer beginCheck = (((dealIndex - DEFAULT_NUM_DEALS) < 0) ? 0 : (dealIndex - DEFAULT_NUM_DEALS));
        LatLngCoor test = current.getBusinessAddress().getLatLng();
        for(int i = beginCheck; i < dealIndex; i++)
        {
            double lat1 = test.getLatitude();
            double lat2 = deals.get(i).getBusinessAddress().getLatLng().getLatitude();
            double lon1 = test.getLongitude();
            double lon2 = deals.get(i).getBusinessAddress().getLatLng().getLongitude();
            
            if(lat1 == lat2 && lon1 == lon2) {
            	current.setIDUrl(deals.get(i).getIDUrl());
                return;
            }
        }
        if(dealIndex == 0) {
        	current.setIDUrl('A');
        } else {
        	Character prevLetter = deals.get(dealIndex - 1).getIDUrl();
        	Character currLetter = (char) ((prevLetter.equals('Z')) ? 'A' : (prevLetter+1));
        	current.setIDUrl(currLetter);
        }
    }
    
    public void setDeals(final ArrayList<Deal> deals) {
    	for(int i = this.deals.size() - 1; i >= 0; i--) {
    		this.deals.remove(i);
    	}
    	for(Deal deal : deals) {
    		this.deals.add(deal);
    		setIDUrl(this.deals.size()-1);
    	}
    	this.dealIndex = 0;
        this.reset = true;
    }
    
    public void addDeal(final Deal deal) {
    	this.deals.add(deal);
    	setIDUrl(this.deals.size()-1);
    }
    
    public void addDeals(final ArrayList<Deal> deals) {
    	for(Deal deal : deals) {
    		addDeal(deal);
    	}
    }
    
    public void setDealIndex(final Integer dealIndex) {
    	this.dealIndex = dealIndex;
    }
    
    public Integer getDealIndex() {
    	return this.dealIndex;
    }
    
    public void incrementDealIndex() {
    	this.dealIndex++;
    }
    
    public void decrementDealIndex() {
    	this.dealIndex = ((this.dealIndex-1)<0) ? 0 : (this.dealIndex-1);
    }
    
    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(final Double radius) {
        this.radius = radius;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(final Integer offset) {
        this.offset = offset;
    }

    public ArrayList<Category> getTags() {
        return tags;
    }

    public void setTags(final ArrayList<Category> tags) {
        this.tags = tags;
    }
    
    public void addTag(final Category tag) {
        this.tags.add(tag);
    }

    public void acknowledgeReset() {
    	this.reset = false;
    }
    
    public Boolean isReset() {
    	return this.reset;
    }
    
    public int getDuplicates()
    {
        return this.duplicates;
    }
    
    public void setDuplicates(int dup)
    {
        this.duplicates = dup;
    }
    
    public boolean getResize()
    {
        return this.resize;
    }
    
    public void setResize(boolean bool)
    {
        this.resize = bool;
    }
}
package me.deal.client.view.menubar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class MenuWidget extends Composite {

	private static MenuWidgetUiBinder uiBinder = GWT
			.create(MenuWidgetUiBinder.class);

	interface MenuWidgetUiBinder extends UiBinder<Widget, MenuWidget> {
	}

	private final HandlerManager eventBus;
	
	public MenuWidget(final HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		initialize();
	}
	
	private void initialize() {
		
	}
}
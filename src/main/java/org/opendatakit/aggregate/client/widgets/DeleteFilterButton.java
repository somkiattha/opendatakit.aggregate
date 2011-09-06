/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.aggregate.client.widgets;

import org.opendatakit.aggregate.client.FilterSubTab;
import org.opendatakit.aggregate.client.filter.Filter;
import org.opendatakit.aggregate.client.popups.HelpBalloon;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class DeleteFilterButton extends AbstractButtonBase implements ClickHandler {

	private static final String TOOLTIP_TEXT = "Remove filter";

	private static final String HELP_BALLOON_TXT = "This deletes the filter from the filter group.  In " +
			"order for this change to remain, you must save the filter group.";

	private FilterSubTab parentSubTab;
	private Filter remove;

	public DeleteFilterButton(Filter remove, FilterSubTab parentSubTab) {
		super("<img src=\"images/red_x.png\" />", TOOLTIP_TEXT);
		this.remove = remove;
		this.parentSubTab = parentSubTab;
		addStyleDependentName("negative");
		helpBalloon = new HelpBalloon(this, HELP_BALLOON_TXT);
	}

	@Override
	public void onClick(ClickEvent event) {
		super.onClick(event);
		parentSubTab.getDisplayedFilterGroup().removeFilter(remove);
		parentSubTab.update();
	}
}
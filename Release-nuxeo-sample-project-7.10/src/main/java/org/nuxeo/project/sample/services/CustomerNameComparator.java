package org.nuxeo.project.sample.services;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import org.nuxeo.project.sample.beans.Invoice2;

public class CustomerNameComparator implements Comparator<Invoice2>  {
	
	private static Collator COLLATOR = Collator.getInstance(Locale.FRENCH);

	
	public CustomerNameComparator() {
	}
	@Override 
	public int compare(Invoice2 f1, Invoice2 f2) {
			int c = COLLATOR.compare(f1.getCustomerName(), f2.getCustomerName());
//			if (c == 0) {
//				return COLLATOR.compare(p1.getPrenom(), p2.getPrenom());
//			} else {
				return c;
//			}
		
	}
}

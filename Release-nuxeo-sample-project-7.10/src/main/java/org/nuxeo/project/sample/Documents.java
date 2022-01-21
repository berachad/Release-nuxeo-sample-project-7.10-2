package org.nuxeo.project.sample;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
public class Documents {

			 private String name ="";
			 private String id = "";
			 private String path = "";
			 private String primaryType;  
			 public String getPrimarType() {
				return primaryType;
			}

			public void setPrimarType(String primaryType) {
				this.primaryType = primaryType;
			}

			public String getPath() {
				return path;
			}
			 
			public void setPath(String path) {
				this.path = path;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}

			public Documents(String name, String id, String path) {
				super();
				this.name = name;
				this.id = id;
				this.path = path;
			 
			}
			public Documents(String name, String id, String path, String primaryType) {
				super();
				this.name = name;
				this.id = id;
				this.path = path;
				this.primaryType = primaryType;
			}

			@Override
			public String toString() {
				return "Documents [name=" + name + ", id=" + id + ", path=" + path + ", primaryType=" + primaryType + "]";
			}
			 
		 
	    
	}



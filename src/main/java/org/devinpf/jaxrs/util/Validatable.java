package org.devinpf.jaxrs.util;

import javax.xml.bind.annotation.XmlTransient;

public interface Validatable {

	@XmlTransient
	boolean isValid();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public abstract class Representation {
    public static final String RELATIONS_URI = "http://relations.gradebook.com/";
    public static final String RESTBUCKS_NAMESPACE = "http://schemas.gradebook.com";
    public static final String DAP_NAMESPACE = RESTBUCKS_NAMESPACE + "/dap";
    public static final String RESTBUCKS_MEDIA_TYPE = "application/vnd.gradebook+xml";
    public static final String SELF_REL_VALUE = "self";

    @XmlElement(name = "link", namespace = DAP_NAMESPACE)
    protected List<Link> links;

    protected Link getLinkByName(String uriName) {
        if (links == null) {
            return null;
        }

        for (Link l : links) {
            if (l.getRelValue().toLowerCase().equals(uriName.toLowerCase())) {
                return l;
            }
        }
        return null;
    }
}

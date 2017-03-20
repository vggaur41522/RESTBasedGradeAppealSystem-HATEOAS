/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server;

/**
 *
 * @author Varun_Gaur
 */

import javax.xml.bind.annotation.XmlEnumValue;


public enum AppealState {
    @XmlEnumValue(value="ungraded")
    UNGRADED,
    @XmlEnumValue(value="appeal_expected")
    EXPECTED,
    @XmlEnumValue(value="appeal_pending_submission")
    PEND_SUBMISSION, 
    @XmlEnumValue(value="appeal_pending_review")
    PEND_REVIEW, 
    @XmlEnumValue(value="appeal_acknowledged")
    ACKNOWLEDGE,
    @XmlEnumValue(value="appeal_abandoned")
    ABANDON,
    @XmlEnumValue(value="appeal_followed_up")
    FOLLOWUP
}


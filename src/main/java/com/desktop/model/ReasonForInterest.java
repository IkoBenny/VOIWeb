/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desktop.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author benny
 */
@Entity
@Table(name = "REASON_FOR_INTEREST")
@NamedQueries({
    @NamedQuery(name = "ReasonForInterest.findAll", query = "SELECT r FROM ReasonForInterest r"),
    @NamedQuery(name = "ReasonForInterest.findByReason", query = "SELECT r FROM ReasonForInterest r WHERE r.reason = :reason"),
    @NamedQuery(name = "ReasonForInterest.findByDescription", query = "SELECT r FROM ReasonForInterest r WHERE r.description = :description")})
public class ReasonForInterest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "REASON")
    private String reason;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reason")
    private Collection<VehicleOfInterest> vehicleOfInterestCollection;

    public ReasonForInterest() {
    }

    public ReasonForInterest(String reason) {
        this.reason = reason;
    }

    public ReasonForInterest(String reason, String description) {
        this.reason = reason;
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<VehicleOfInterest> getVehicleOfInterestCollection() {
        return vehicleOfInterestCollection;
    }

    public void setVehicleOfInterestCollection(Collection<VehicleOfInterest> vehicleOfInterestCollection) {
        this.vehicleOfInterestCollection = vehicleOfInterestCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reason != null ? reason.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReasonForInterest)) {
            return false;
        }
        ReasonForInterest other = (ReasonForInterest) object;
        if ((this.reason == null && other.reason != null) || (this.reason != null && !this.reason.equals(other.reason))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.desktop.model.ReasonForInterest[ reason=" + reason + " ]";
    }
    
}

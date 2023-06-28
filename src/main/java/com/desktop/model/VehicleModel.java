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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author benny
 */
@Entity
@Table(name = "VEHICLE_MODEL")
@NamedQueries({
    @NamedQuery(name = "VehicleModel.findAll", query = "SELECT v FROM VehicleModel v"),
    @NamedQuery(name = "VehicleModel.findByModel", query = "SELECT v FROM VehicleModel v WHERE v.model = :model")})
public class VehicleModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "MODEL")
    private String model;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "model")
    private Collection<VehicleOfInterest> vehicleOfInterestCollection;
    @JoinColumn(name = "MAKE", referencedColumnName = "MAKE")
    @ManyToOne(optional = false)
    private VehicleMake make;

    public VehicleModel() {
    }

    public VehicleModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Collection<VehicleOfInterest> getVehicleOfInterestCollection() {
        return vehicleOfInterestCollection;
    }

    public void setVehicleOfInterestCollection(Collection<VehicleOfInterest> vehicleOfInterestCollection) {
        this.vehicleOfInterestCollection = vehicleOfInterestCollection;
    }

    public VehicleMake getMake() {
        return make;
    }

    public void setMake(VehicleMake make) {
        this.make = make;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (model != null ? model.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VehicleModel)) {
            return false;
        }
        VehicleModel other = (VehicleModel) object;
        if ((this.model == null && other.model != null) || (this.model != null && !this.model.equals(other.model))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.desktop.model.VehicleModel[ model=" + model + " ]";
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desktop.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author benny
 */
@Entity
@Table(name = "VEHICLE_OF_INTEREST")
@NamedQueries({
    @NamedQuery(name = "VehicleOfInterest.findAll", query = "SELECT v FROM VehicleOfInterest v"),
    @NamedQuery(name = "VehicleOfInterest.findByLicensePlate", query = "SELECT v FROM VehicleOfInterest v WHERE v.licensePlate = :licensePlate"),
    @NamedQuery(name = "VehicleOfInterest.findByVehYear", query = "SELECT v FROM VehicleOfInterest v WHERE v.vehYear = :vehYear"),
    @NamedQuery(name = "VehicleOfInterest.findByColor", query = "SELECT v FROM VehicleOfInterest v WHERE v.color = :color"),
    @NamedQuery(name = "VehicleOfInterest.findByOwnersName", query = "SELECT v FROM VehicleOfInterest v WHERE v.ownersName = :ownersName"),
    @NamedQuery(name = "VehicleOfInterest.findByOwnersPhone", query = "SELECT v FROM VehicleOfInterest v WHERE v.ownersPhone = :ownersPhone")})
public class VehicleOfInterest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "LICENSE_PLATE")
    private String licensePlate;
    @Column(name = "VEH_YEAR")
    private String vehYear;
    @Column(name = "COLOR")
    private String color;
    @Column(name = "OWNERS_NAME")
    private String ownersName;
    @Column(name = "OWNERS_PHONE")
    private String ownersPhone;
    @JoinColumn(name = "REASON", referencedColumnName = "REASON")
    @ManyToOne(optional = false)
    private ReasonForInterest reason;
    @JoinColumn(name = "MAKE", referencedColumnName = "MAKE")
    @ManyToOne(optional = false)
    private VehicleMake make;
    @JoinColumn(name = "MODEL", referencedColumnName = "MODEL")
    @ManyToOne(optional = false)
    private VehicleModel model;

    public VehicleOfInterest() {
    }

    public VehicleOfInterest(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehYear() {
        return vehYear;
    }

    public void setVehYear(String vehYear) {
        this.vehYear = vehYear;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwnersName() {
        return ownersName;
    }

    public void setOwnersName(String ownersName) {
        this.ownersName = ownersName;
    }

    public String getOwnersPhone() {
        return ownersPhone;
    }

    public void setOwnersPhone(String ownersPhone) {
        this.ownersPhone = ownersPhone;
    }

    public ReasonForInterest getReason() {
        return reason;
    }

    public void setReason(ReasonForInterest reason) {
        this.reason = reason;
    }

    public VehicleMake getMake() {
        return make;
    }

    public void setMake(VehicleMake make) {
        this.make = make;
    }

    public VehicleModel getModel() {
        return model;
    }

    public void setModel(VehicleModel model) {
        this.model = model;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (licensePlate != null ? licensePlate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VehicleOfInterest)) {
            return false;
        }
        VehicleOfInterest other = (VehicleOfInterest) object;
        if ((this.licensePlate == null && other.licensePlate != null) || (this.licensePlate != null && !this.licensePlate.equals(other.licensePlate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.desktop.model.VehicleOfInterest[ licensePlate=" + licensePlate + " ]";
    }
    
}

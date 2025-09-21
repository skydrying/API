/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.api.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author oneju
 */
@Entity
@Table(name = "embroidery_kit")
@NamedQueries({
    @NamedQuery(name = "EmbroideryKit.findAll", query = "SELECT e FROM EmbroideryKit e"),
    @NamedQuery(name = "EmbroideryKit.findById", query = "SELECT e FROM EmbroideryKit e WHERE e.id = :id"),
    @NamedQuery(name = "EmbroideryKit.findByName", query = "SELECT e FROM EmbroideryKit e WHERE e.name = :name"),
    @NamedQuery(name = "EmbroideryKit.findByPrice", query = "SELECT e FROM EmbroideryKit e WHERE e.price = :price"),
    @NamedQuery(name = "EmbroideryKit.findByStockQuantity", query = "SELECT e FROM EmbroideryKit e WHERE e.stockQuantity = :stockQuantity"),
    @NamedQuery(name = "EmbroideryKit.findByImage", query = "SELECT e FROM EmbroideryKit e WHERE e.image = :image")})
public class EmbroideryKit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Name")
    private String name;
    @Lob
    @Column(name = "Description")
    private String description;
    @Column(name = "Price")
    private String price;
    @Column(name = "StockQuantity")
    private String stockQuantity;
    @Column(name = "Image")
    private String image;

    public EmbroideryKit() {
    }

    public EmbroideryKit(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmbroideryKit)) {
            return false;
        }
        EmbroideryKit other = (EmbroideryKit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.EmbroideryKit[ id=" + id + " ]";
    }
    
}

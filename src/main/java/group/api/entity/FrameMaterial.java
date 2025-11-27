package group.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "frame_material")
@NamedQueries({
    @NamedQuery(name = "FrameMaterial.findAll", query = "SELECT f FROM FrameMaterial f"),
    @NamedQuery(name = "FrameMaterial.findById", query = "SELECT f FROM FrameMaterial f WHERE f.id = :id"),
    @NamedQuery(name = "FrameMaterial.findByName", query = "SELECT f FROM FrameMaterial f WHERE f.name = :name"),
    @NamedQuery(name = "FrameMaterial.findByPricePerMeter", query = "SELECT f FROM FrameMaterial f WHERE f.pricePerMeter = :pricePerMeter"),
    @NamedQuery(name = "FrameMaterial.findByStockQuantity", query = "SELECT f FROM FrameMaterial f WHERE f.stockQuantity = :stockQuantity"),
    @NamedQuery(name = "FrameMaterial.findByColor", query = "SELECT f FROM FrameMaterial f WHERE f.color = :color"),
    @NamedQuery(name = "FrameMaterial.findByWidth", query = "SELECT f FROM FrameMaterial f WHERE f.width = :width")})
public class FrameMaterial implements Serializable {

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
    @Column(name = "PricePerMeter")
    private Integer pricePerMeter;
    @Column(name = "StockQuantity")
    private Integer stockQuantity;
    @Column(name = "Color")
    private String color;
    @Column(name = "Width")
    private Integer width;
    @JsonIgnore
    @OneToMany(mappedBy = "frameMaterialID")
    private Collection<CustomFrameOrder> customFrameOrderCollection;

    public FrameMaterial() {
    }

    public FrameMaterial(Integer id) {
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

    public Integer getPricePerMeter() {
        return pricePerMeter;
    }

    public void setPricePerMeter(Integer pricePerMeter) {
        this.pricePerMeter = pricePerMeter;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Collection<CustomFrameOrder> getCustomFrameOrderCollection() {
        return customFrameOrderCollection;
    }

    public void setCustomFrameOrderCollection(Collection<CustomFrameOrder> customFrameOrderCollection) {
        this.customFrameOrderCollection = customFrameOrderCollection;
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
        if (!(object instanceof FrameMaterial)) {
            return false;
        }
        FrameMaterial other = (FrameMaterial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.FrameMaterial[ id=" + id + " ]";
    }
    
}

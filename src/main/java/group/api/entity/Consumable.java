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

@Entity
@Table(name = "consumable")
@NamedQueries({
    @NamedQuery(name = "Consumable.findAll", query = "SELECT c FROM Consumable c"),
    @NamedQuery(name = "Consumable.findById", query = "SELECT c FROM Consumable c WHERE c.id = :id"),
    @NamedQuery(name = "Consumable.findByName", query = "SELECT c FROM Consumable c WHERE c.name = :name"),
    @NamedQuery(name = "Consumable.findByPrice", query = "SELECT c FROM Consumable c WHERE c.price = :price"),
    @NamedQuery(name = "Consumable.findByStockQuantity", query = "SELECT c FROM Consumable c WHERE c.stockQuantity = :stockQuantity"),
    @NamedQuery(name = "Consumable.findByUnit", query = "SELECT c FROM Consumable c WHERE c.unit = :unit")})
public class Consumable implements Serializable {

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
    private Integer price;
    @Column(name = "StockQuantity")
    private Integer stockQuantity;
    @Column(name = "Unit")
    private String unit;

    public Consumable() {
    }

    public Consumable(Integer id) {
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
        if (!(object instanceof Consumable)) {
            return false;
        }
        Consumable other = (Consumable) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.Consumable[ id=" + id + " ]";
    }
    
}

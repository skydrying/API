
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
@Table(name = "frame_component")
@NamedQueries({
    @NamedQuery(name = "FrameComponent.findAll", query = "SELECT f FROM FrameComponent f"),
    @NamedQuery(name = "FrameComponent.findById", query = "SELECT f FROM FrameComponent f WHERE f.id = :id"),
    @NamedQuery(name = "FrameComponent.findByName", query = "SELECT f FROM FrameComponent f WHERE f.name = :name"),
    @NamedQuery(name = "FrameComponent.findByPrice", query = "SELECT f FROM FrameComponent f WHERE f.price = :price"),
    @NamedQuery(name = "FrameComponent.findByStockQuantity", query = "SELECT f FROM FrameComponent f WHERE f.stockQuantity = :stockQuantity"),
    @NamedQuery(name = "FrameComponent.findByType", query = "SELECT f FROM FrameComponent f WHERE f.type = :type")})
public class FrameComponent implements Serializable {

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
    @Column(name = "Type")
    private String type;

    public FrameComponent() {
    }

    public FrameComponent(Integer id) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if (!(object instanceof FrameComponent)) {
            return false;
        }
        FrameComponent other = (FrameComponent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.FrameComponent[ id=" + id + " ]";
    }
    
}

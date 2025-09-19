package group.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "custom_frame_order")
@NamedQueries({
    @NamedQuery(name = "CustomFrameOrder.findAll", query = "SELECT c FROM CustomFrameOrder c"),
    @NamedQuery(name = "CustomFrameOrder.findById", query = "SELECT c FROM CustomFrameOrder c WHERE c.id = :id"),
    @NamedQuery(name = "CustomFrameOrder.findByWidth", query = "SELECT c FROM CustomFrameOrder c WHERE c.width = :width"),
    @NamedQuery(name = "CustomFrameOrder.findByHeight", query = "SELECT c FROM CustomFrameOrder c WHERE c.height = :height"),
    @NamedQuery(name = "CustomFrameOrder.findByColor", query = "SELECT c FROM CustomFrameOrder c WHERE c.color = :color"),
    @NamedQuery(name = "CustomFrameOrder.findByStyle", query = "SELECT c FROM CustomFrameOrder c WHERE c.style = :style"),
    @NamedQuery(name = "CustomFrameOrder.findByMountType", query = "SELECT c FROM CustomFrameOrder c WHERE c.mountType = :mountType"),
    @NamedQuery(name = "CustomFrameOrder.findByGlassType", query = "SELECT c FROM CustomFrameOrder c WHERE c.glassType = :glassType"),
    @NamedQuery(name = "CustomFrameOrder.findByEstimatedMaterialUsage", query = "SELECT c FROM CustomFrameOrder c WHERE c.estimatedMaterialUsage = :estimatedMaterialUsage"),
    @NamedQuery(name = "CustomFrameOrder.findByActualMaterialUsage", query = "SELECT c FROM CustomFrameOrder c WHERE c.actualMaterialUsage = :actualMaterialUsage")})
public class CustomFrameOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Width")
    private Integer width;
    @Column(name = "Height")
    private Integer height;
    @Column(name = "Color")
    private String color;
    @Column(name = "Style")
    private String style;
    @Column(name = "MountType")
    private String mountType;
    @Column(name = "GlassType")
    private String glassType;
    @Lob
    @Column(name = "Notes")
    private String notes;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "EstimatedMaterialUsage")
    private BigDecimal estimatedMaterialUsage;
    @Column(name = "ActualMaterialUsage")
    private BigDecimal actualMaterialUsage;
    @JsonIgnore
    @JoinColumn(name = "FrameMaterialID", referencedColumnName = "ID")
    @ManyToOne
    private FrameMaterial frameMaterialID;
    @JsonIgnore
    @JoinColumn(name = "OrderID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Order orderID;
    @JsonIgnore
    @JoinColumn(name = "ProductionMasterID", referencedColumnName = "ID")
    @ManyToOne
    private User productionMasterID;

    public CustomFrameOrder() {
    }

    public CustomFrameOrder(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getMountType() {
        return mountType;
    }

    public void setMountType(String mountType) {
        this.mountType = mountType;
    }

    public String getGlassType() {
        return glassType;
    }

    public void setGlassType(String glassType) {
        this.glassType = glassType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getEstimatedMaterialUsage() {
        return estimatedMaterialUsage;
    }

    public void setEstimatedMaterialUsage(BigDecimal estimatedMaterialUsage) {
        this.estimatedMaterialUsage = estimatedMaterialUsage;
    }

    public BigDecimal getActualMaterialUsage() {
        return actualMaterialUsage;
    }

    public void setActualMaterialUsage(BigDecimal actualMaterialUsage) {
        this.actualMaterialUsage = actualMaterialUsage;
    }

    public FrameMaterial getFrameMaterialID() {
        return frameMaterialID;
    }

    public void setFrameMaterialID(FrameMaterial frameMaterialID) {
        this.frameMaterialID = frameMaterialID;
    }

    public Order getOrderID() {
        return orderID;
    }

    public void setOrderID(Order orderID) {
        this.orderID = orderID;
    }

    public User getProductionMasterID() {
        return productionMasterID;
    }

    public void setProductionMasterID(User productionMasterID) {
        this.productionMasterID = productionMasterID;
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
        if (!(object instanceof CustomFrameOrder)) {
            return false;
        }
        CustomFrameOrder other = (CustomFrameOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.CustomFrameOrder[ id=" + id + " ]";
    }
    
}

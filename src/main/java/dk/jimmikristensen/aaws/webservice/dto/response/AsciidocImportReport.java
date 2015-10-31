package dk.jimmikristensen.aaws.webservice.dto.response;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import dk.jimmikristensen.aaws.webservice.dto.response.adaptor.DateAdapter;

@XmlRootElement(name = "asciidocImportReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class AsciidocImportReport {

    private int deleted;
    private int inserted;
    private int updated;
    private int resourcesDownloaded;
    
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date reportDate;
    
    public int getDeleted() {
        return deleted;
    }
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
    public int getInserted() {
        return inserted;
    }
    public void setInserted(int inserted) {
        this.inserted = inserted;
    }
    public int getUpdated() {
        return updated;
    }
    public void setUpdated(int updated) {
        this.updated = updated;
    }
    public int getResourcesDownloaded() {
        return resourcesDownloaded;
    }
    public void setResourcesDownloaded(int resourcesDownloaded) {
        this.resourcesDownloaded = resourcesDownloaded;
    }
    public Date getReportDate() {
        return reportDate;
    }
    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
    
    
}

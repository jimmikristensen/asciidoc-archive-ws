package dk.jimmikristensen.aaws.persistence.dao.entity;

import java.util.Date;

public class ImportReportEntity {

    private Date reportDate = new Date();
    private int inserted = 0;
    private int updated = 0;
    private int resourcesDownloaded = 0;
    
    public Date getReportDate() {
        return reportDate;
    }
    public void setReportDate(Date reportTime) {
        this.reportDate = reportTime;
    }
    public int getInserted() {
        return inserted;
    }
    public void setInserted(int inserted) {
        this.inserted = inserted;
    }
    public void increaseInserted() {
        inserted++;
    }
    public int getUpdated() {
        return updated;
    }
    public void setUpdated(int updated) {
        this.updated = updated;
    }
    public void increaseUpdated() {
        updated++;
    }
    public int getResourcesDownloaded() {
        return resourcesDownloaded;
    }
    public void setResourcesDownloaded(int resourcesDownloaded) {
        this.resourcesDownloaded = resourcesDownloaded;
    }
    public void increaseResourcesDownloaded() {
        resourcesDownloaded++;
    }

}

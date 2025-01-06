package com.example.streamliner.home.Item;

import java.util.Date;
import java.util.Objects;

public class FeatureItem {
    private String id;
    private String title;
    private String description;
    private String status;
    private Date expectedRelease;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpectedRelease() {
        return expectedRelease;
    }

    public void setExpectedRelease(Date expectedRelease) {
        this.expectedRelease = expectedRelease;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FeatureItem that = (FeatureItem) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(status, that.status) &&
                Objects.equals(expectedRelease, that.expectedRelease);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, expectedRelease);
    }
}

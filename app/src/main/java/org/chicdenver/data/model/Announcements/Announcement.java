package org.chicdenver.data.model.Announcements;

import org.chicdenver.data.model.Classification;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Announcement {

    private Date date;
    private String announcement;

    private String title;

    private List<Classification> audience;

    public Announcement() {}

    public void setAnnouncement(String title, String announcement, Date date, List<Classification> audience) {

        this.title = title;
        this.announcement = announcement;
        this.date = date;
        this.audience = audience;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public String getPrettyDate() {

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy", Locale.US);

        return formatter.format(date);

    }

    public void setAudience(List<Classification> audience) {
        this.audience = audience;
    }

    public Date getDate() {

        return date;

    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAnnouncement(String announcement)
    {
        this.announcement = announcement;
    }


    public List<Classification> getAudience() {
        return audience;
    }
}

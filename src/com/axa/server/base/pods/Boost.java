package com.axa.server.base.pods;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.google.gson.annotations.Expose;


@Entity
public class Boost {

    public static enum Status {

        NEW, ONGOING, ACCOMPLISHED, FAILED;

    };

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Expose private Long boostId;
    @Expose private String title;
    @Expose private String picture;
    @Expose private String goal;
    @Expose private Long ownerId;
    @Expose private String status = Status.NEW.toString();
    @Expose private Date creation;
    @Expose private Date start;
    @Expose private Date end;

	
    public Boost() {
    }

    public Boost(Long boostId, String title, String picture, String goal, Long ownerId,
                 String status, Date creation, Date start, Date end) {
        this.boostId = boostId;
        this.title = title;
        this.picture = picture;
        this.goal = goal;
        this.ownerId = ownerId;
        this.status = status;
        this.creation = creation;
        this.start = start;
        this.end = end;
    }


	/**
     *
     * @return
     * The boostId
     */
    public Long getBoostId() {
        return boostId;
    }

    /**
     *
     * @param boostId
     * The boostId
     */
    public void setBoostId(Long boostId) {
        this.boostId = boostId;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     *
     * @param picture
     * The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     *
     * @return
     * The goal
     */
    public String getGoal() {
        return goal;
    }

    /**
     *
     * @param goal
     * The goal
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     *
     * @return
     * The ownerId
     */
    public Long getOwnerId() {
        return ownerId;
    }

    /**
     *
     * @param ownerId
     * The ownerId
     */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The creation
     */
    public Date getCreation() {
        return creation;
    }

    /**
     *
     * @param creation
     * The creation
     */
    public void setCreation(Date creation) {
        this.creation = creation;
    }

    /**
     *
     * @return
     * The start
     */
    public Date getStart() {
        return start;
    }

    /**
     *
     * @param start
     * The start
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     *
     * @return
     * The end
     */
    public Date getEnd() {
        return end;
    }

    /**
     *
     * @param end
     * The end
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Boost{");
        sb.append("boostId='").append(boostId).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", picture='").append(picture).append('\'');
        sb.append(", goal=").append(goal);
        sb.append(", ownerId='").append(ownerId).append('\'');
        sb.append(", status=").append(status);
        sb.append(", creation='").append(creation).append('\'');
        sb.append(", start='").append(start).append('\'');
        sb.append(", end='").append(end).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Date getRemaining() {
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(start);

        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(end);

        Calendar result = (Calendar) cEnd.clone();
        result.add(Calendar.YEAR, -cStart.get(Calendar.YEAR));
        result.add(Calendar.MONTH, -(cStart.get(Calendar.MONTH) + 1)); // Months are zero-based!
        result.add(Calendar.DATE, -cStart.get(Calendar.DATE));
        result.add(Calendar.HOUR_OF_DAY, -cStart.get(Calendar.HOUR_OF_DAY));
        result.add(Calendar.MINUTE, -cStart.get(Calendar.MINUTE));
        result.add(Calendar.SECOND, -cStart.get(Calendar.SECOND));
        result.add(Calendar.MILLISECOND, -cStart.get(Calendar.MILLISECOND));

        return result.getTime();
    }

}
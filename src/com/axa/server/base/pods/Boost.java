package com.axa.server.base.pods;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rrodriguez on 17/02/2015.
 */

public class Boost {

    public static enum Status {

        NEW, ONGOING, ACCOMPLISHED, FAILED;

    };

    private String boostId;
    private String title;
    private String picture;
    private String goal;
    private String ownerId;
    private String status = Status.NEW.toString();
    private Date creation;
    private Date start;
    private Date end;

    public Boost() {
    }

    public Boost(String boostId, String title, String picture, String goal, String ownerId,
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
    public String getBoostId() {
        return boostId;
    }

    /**
     *
     * @param boostId
     * The boostId
     */
    public void setBoostId(String boostId) {
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
    public String getOwnerId() {
        return ownerId;
    }

    /**
     *
     * @param ownerId
     * The ownerId
     */
    public void setOwnerId(String ownerId) {
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
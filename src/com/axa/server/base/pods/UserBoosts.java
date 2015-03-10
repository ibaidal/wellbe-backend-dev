package com.axa.server.base.pods;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.google.gson.annotations.Expose;

public class UserBoosts {

    @Expose private List<Boost> boosts = new ArrayList<Boost>();
    @Expose private List<User> people = new ArrayList<User>();

    public UserBoosts() {
    }

    public UserBoosts(List<Boost> boosts, List<User> people) {
        this.boosts = boosts;
        this.people = people;
    }

    public List<Boost> getBoosts() {
        return boosts;
    }

    public void setBoosts(List<Boost> boosts) {
        this.boosts = boosts;
    }

    public List<User> getPeople() {
        return people;
    }

    public void setPeople(List<User> people) {
        this.people = people;
    }

}
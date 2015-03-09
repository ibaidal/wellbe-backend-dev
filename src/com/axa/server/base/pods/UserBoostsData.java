package com.axa.server.base.pods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rrodriguez on 26/02/2015.
 */
public class UserBoostsData {

    private List<Boost> boosts = new ArrayList<Boost>();
    private List<User> people = new ArrayList<User>();

    public UserBoostsData() {
    }

    public UserBoostsData(List<Boost> boosts, List<User> people) {
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

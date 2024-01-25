package it.unibo.jurassiko.model.territory.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.unibo.jurassiko.model.territory.api.Territory;

public class TerritoryImpl implements Territory {

    private String name;
    private String continent;
    @JsonProperty("neighbours")
    private Set<String> neighbourNames;

    @JsonIgnore
    private Set<Territory> neighbours;
    @JsonIgnore
    private int dinoAmount = 0;

    private TerritoryImpl() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContinent() {
        return continent;
    }

    @Override
    public Set<String> getNeighbourNames() {
        return Set.copyOf(neighbourNames);
    }

    @Override
    public void setNeighbours(final Set<Territory> neighbours) {
        this.neighbours = new HashSet<>(neighbours);
    }

    @Override
    public Set<Territory> getNeighbours() {
        return Set.copyOf(neighbours);
    }

    @Override
    public boolean isNeighbour(final String territoryName) {
        return this.neighbours.stream()
                .map(t -> t.getName())
                .collect(Collectors.toSet())
                .contains(territoryName);
    }

    @Override
    public void changeDinoAmount(final int delta) {
        this.dinoAmount += delta;
    }

    @Override
    public int getDinoAmount() {
        return dinoAmount;
    }

}

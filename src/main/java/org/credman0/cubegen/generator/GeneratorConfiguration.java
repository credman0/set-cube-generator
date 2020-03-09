package org.credman0.cubegen.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeneratorConfiguration implements Serializable {
    protected List<String> exclusions = new ArrayList<String>();

    public List<String> getExclusions() {
        return exclusions;
    }

    protected int rerollCommons = 1;

    public int getRerollCommons() {
        return rerollCommons;
    }

    public void setRerollCommons(int rerollCommons) {
        this.rerollCommons = rerollCommons;
    }

    protected int rerollUncommons = 1;

    public int getRerollUncommons() {
        return rerollUncommons;
    }

    public void setRerollUncommons(int rerollUncommons) {
        this.rerollUncommons = rerollUncommons;
    }

    protected int rerollRares = 3;

    public int getRerollRares() {
        return rerollRares;
    }

    public void setRerollRares(int rerollRares) {
        this.rerollRares = rerollRares;
    }

    protected int packSize = 15;

    public int getPackSize() {
        return packSize;
    }

    protected int commonsPer = 11;

    public int getCommonsPer() {
        return commonsPer;
    }

    public void setCommonsPer(int commonsPer) {
        this.commonsPer = commonsPer;
    }

    protected int uncommonsPer = 3;

    public int getUncommonsPer() {
        return uncommonsPer;
    }

    public void setUncommonsPer(int uncommonsPer) {
        this.uncommonsPer = uncommonsPer;
    }

    public GeneratorConfiguration() {
    }
}
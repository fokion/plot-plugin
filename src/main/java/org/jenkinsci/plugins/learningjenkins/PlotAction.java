/*
 * Copyright (c) 2007 Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the MIT License.
 */
package org.jenkinsci.plugins.learningjenkins;

import hudson.Functions;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.collections.CollectionUtils;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Project action to display plots.
 *
 * @author Nigel Daley
 */
public class PlotAction implements Action, StaplerProxy, SimpleBuildStep.LastBuildAction{

    private static final Logger LOGGER = Logger.getLogger(PlotAction.class
            .getName());
    private final Job<?, ?> job;
    private final PlotPublisher publisher;

    public PlotAction(Job<?, ?> job, PlotPublisher publisher) {
        this.job = job;
        this.publisher = publisher;
    }

    public Job<?, ?> getJob() {
        return job;
    }

    public String getDisplayName() {
        return Messages.Plot_Action_DisplayName();
    }

    public String getIconFileName() {
        return Functions.getResourcePath() + "/plugin/learningjenkins/graph.gif";
    }

    public String getUrlName() {
        return Messages.Plot_UrlName();
    }

    // called from PlotAction/index.jelly
    public boolean hasPlots() throws IOException {
        return CollectionUtils.isNotEmpty(publisher.getPlots());
    }

    // called from PlotAction/index.jelly
    public List<String> getOriginalGroups() {
        return publisher.getOriginalGroups();
    }

    // called from PlotAction/index.jelly
    public String getUrlGroup(String originalGroup) {
        return publisher.originalGroupToUrlEncodedGroup(originalGroup);
    }

    // called from href created in PlotAction/index.jelly
    public PlotReport getDynamic(String group, StaplerRequest req,
            StaplerResponse rsp) throws IOException {
        return new PlotReport(job,
                publisher.urlGroupToOriginalGroup(getUrlGroup(group)),
                publisher.getPlots(getUrlGroup(group)));
    }

    /**
     * If there's only one plot category, simply display that category of
     * reports on this view.
     */
    public Object getTarget() {
        List<String> groups = getOriginalGroups();
        if (groups != null && groups.size() == 1) {
            return new PlotReport(job, groups.get(0),
                    publisher.getPlots(getUrlGroup(groups.get(0))));
        } else {
            return this;
        }
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return Collections.singleton(this);
    }
}

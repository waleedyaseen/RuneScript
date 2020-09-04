/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import me.waliedyassen.runescript.editor.settings.state.SettingsState;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a single settings page.
 *
 * @author Walied K. Yassen
 */
public abstract class Settings<S extends SettingsState> {

    /**
     * The current state of the settings.
     */
    @Getter
    @Setter
    protected S persistentState;

    /**
     * The temporary state of the settings.
     */
    @Getter
    @Setter
    protected S temporaryState;

    /**
     * Constructs a new {@link Settings} type object instance.
     */
    public Settings() {
        persistentState = createDefaultState();
    }

    /**
     * Creates the component panel of the settings.
     *
     * @return the component panel of the settings.
     */
    public abstract JComponent createComponent();

    /**
     * Apply the current changes in the temporary state to the persistent state
     * and make the changes take effect on the editor.
     */
    public void apply() {
        apply(persistentState, temporaryState);
        persistentState = temporaryState;
        temporaryState = null;
    }

    /**
     * Find the different properties between two specified state objects of type {@link S} then apply these changed
     * properties effect on the editor.
     *
     * @param oldState the old state of the settings.
     * @param newState the new state of the settings.
     */
    protected abstract void apply(S oldState, S newState);

    /***
     * Returns the name of the settings which is used for serialization
     * and deserialization.
     *
     * @return the name of the settings.
     */
    public abstract String getName();

    /**
     * Returns the title of the settings which is used for displaying.
     *
     * @return the title of the settings.
     */
    public abstract String getTitle();

    /**
     * Creates a new state object of type {@link S} that holds all of the default values for the
     * settings.
     *
     * @return the state object of type {@link S}.
     */
    public abstract S createDefaultState();

    /**
     * Loads the state object of type {@link S} from the specified {@link JsonNode node}.
     *
     * @param mapper the JSON mapper to use to map JSON to objects.
     * @param node   the settings JSON node object.
     * @return the loaded state object of type {@link S}.
     */
    public S loadState(ObjectMapper mapper, JsonNode node) throws IOException {
        return mapper.treeToValue(node, getStateClass());
    }

    /**
     * Stores the specified {@code state} object of type {@link S} in a {@link JsonNode node} and return
     * it.
     *
     * @param mapper the mapper to use to map objects to JSON node objects.
     * @param state  the state that we want to store.
     * @return the mapped {@link JsonNode} object.
     */
    public JsonNode storeState(ObjectMapper mapper, S state) {
        return mapper.valueToTree(state);
    }

    /**
     * Returns the class type of the state object.
     *
     * @return the class type of the state object.
     */
    public abstract Class<S> getStateClass();

    /**
     * Checks whether or not the settings has been modified.
     *
     * @return <code>true</code> if it is modified otherwise <code>false</code>.
     */
    public boolean isModified() {
        if (temporaryState == null) {
            return false;
        }
        return !temporaryState.equals(persistentState);
    }

    /***
     * Checks whether or not the objects provided by the specified {@code provider} is equal for the specified
     * state objects.
     * @param oldState the old state object of type {@link S} which we will grab the old value from.
     * @param newState the new state object of type {@link S} which we will grab the new value from.
     * @param provider the provider which takes a state object and provides the object we want to compare.
     * @return <code>true</code> if the provided objects are not equal otherwise <code>false</code>.
     */
    protected boolean isModified(S oldState, S newState, Function<S, Object> provider) {
        final var oldValue = provider.apply(oldState);
        final var newValue = provider.apply(newState);
        return !Objects.equals(oldValue, newValue);
    }
}

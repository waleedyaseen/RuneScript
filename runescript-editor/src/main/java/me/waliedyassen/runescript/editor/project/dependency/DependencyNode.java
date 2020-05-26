/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.dependency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * A single dependency node in the dependency tree that holds information about all of the dependencies and about other
 * nodes that use this node a dependency.
 *
 * @param <K> the type of key value this node holds.
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class DependencyNode<K> {

    /**
     * A map of all the nodes that depend on this dependency.
     */
    @Getter
    private final Map<K, DependencyNode<K>> dependsOn = new HashMap<>();

    /**
     * A map of all the nodes that depend on this dependency.
     */
    @Getter
    private final Map<K, DependencyNode<K>> usedBy = new HashMap<>();

    /**
     * The parent {@link DependencyTree} of this node.
     */
    @Getter
    private final DependencyTree<K> tree;

    /**
     * The key value of this node.
     */
    @Getter
    private final K key;

    /**
     * Adds the dependency node with the specified {@link K key} as a dependency to this node.
     *
     * @param key the key of the dependency that we want to add.
     */
    public void add(K key) {
        if (isChildFor(key)) {
            throw new CircularDependencyException("Circular dependency found in between " + this.key + " and " + key);
        }
        if (key.equals(this.key)) {
            throw new CircularDependencyException("A dependency node cannot depend on itself");
        }
        var dependency = tree.findOrCreate(key);
        dependsOn.put(key, dependency);
        dependency.usedBy.put(this.key, this);
    }

    /**
     * Checks whether or not this dependency node is a child node for the node with the specified {@code key}.
     *
     * @param key the key of the dependency node that we want to check.
     * @return <code>true</code> if this dependency node is child of the dependency node with the specified {@link K key}.
     */
    private boolean isChildFor(K key) {
        if (usedBy.containsKey(key)) {
            return true;
        }
        for (var node : usedBy.values()) {
            if (node.isChildFor(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the {@link DependencyNode} with the specified {@link K key}.
     *
     * @param key the key which we want the associated dependency node from.
     * @return the {@link DependencyNode} if found otherwise {@code null}.
     */
    public DependencyNode<K> get(K key) {
        return dependsOn.get(key);
    }

    /**
     * Removes the dependency of this dependency node on any dependency node.
     */
    public void clearDependsOn() {
        dependsOn.values().forEach(node -> node.getUsedBy().remove(key));
        dependsOn.clear();
    }
}

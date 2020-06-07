/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.dependency;

import lombok.var;

import java.util.*;

/**
 * A very basic implementation of a dependency tree.
 *
 * @param <K> the type of the key-value this dependency tree is going to use for the dependency nodes.
 * @author Walied K. Yassen
 */
public final class DependencyTree<K> {

    /**
     * A map of all the dependency nodes that live in this tree associated by their {@link K key}.
     */
    private final Map<K, DependencyNode<K>> nodesByKey = new HashMap<>();

    /**
     * Evaluates the order of all the dependency tree.
     *
     * @return a list of the evaluated order (ascending).
     */
    public List<K> evaluateOrder() {
        var order = new ArrayList<K>();
        var visited = new HashSet<K>(nodesByKey.size());
        for (var node : nodesByKey.values()) {
            evaluateOrder(node, order, visited);
        }
        return order;
    }

    /**
     * Evaluates the dependency order for the dependency the specified {@link K key}.
     *
     * @param key the key to evaluate the order for.
     * @return a list of the evaluated order (ascending).
     */
    public List<K> evaluate(K key) {
        var node = nodesByKey.get(key);
        if (node == null) {
            throw new IllegalArgumentException("The specified key does not belong to any dependency node");
        }
        var order = new ArrayList<K>();
        var visited = new HashSet<K>();
        evaluateOrder(node, order, visited);
        return order;
    }

    /**
     * Evaluates the order of the specified dependency node.
     *
     * @param node    the dependency node to evaluate the order for.
     * @param order   the list to place the ordered node keys in.
     * @param visited the set of all the visited keys so far.
     */
    private void evaluateOrder(DependencyNode<K> node, List<K> order, Set<K> visited) {
        if (!visited.add(node.getKey())) {
            return;
        }
        for (var dependency : node.getDependsOn().values()) {
            evaluateOrder(dependency, order, visited);
        }
        order.add(node.getKey());
    }

    /**
     * Checks if there is already a node with the specified {@link K key} and if there is, it will be returned
     * otherwise a new {@link DependencyNode} will be created a returned.
     *
     * @param key the key value of the dependency node that we want to find to create.
     * @return the cached or created {@link DependencyNode} object instance.
     */
    public DependencyNode<K> findOrCreate(K key) {
        var node = nodesByKey.get(key);
        if (node != null) {
            return node;
        }
        node = new DependencyNode<>(this, key);
        nodesByKey.put(key, node);
        return node;
    }

    /**
     * Finds the {@link DependencyNode} with the specified {@link K key}.
     *
     * @param key the key of the dependency node.
     * @return the {@link DependencyNode} if found otherwise {@code null}.
     */
    public DependencyNode<K> find(K key) {
        return nodesByKey.get(key);
    }

    /**
     * Removes the dependency node with the specified {@link K key} from the dependency tree.
     *
     * @param key the key of the dependency node to remove.
     */
    public void remove(K key) {
        var node = nodesByKey.get(key);
        if (node == null) {
            return;
        }
        node.clearDependsOn();
        if (node.getUsedBy().isEmpty()) {
            nodesByKey.remove(key);
        }
    }

    /**
     * Returns the key set of the dependency tree.
     *
     * @return the key set of the dependency tree.
     */
    public Collection<K> keySet() {
        return nodesByKey.keySet();
    }

    /**
     * Returns the value set of the dependency tree.
     *
     * @return the value set of the dependency tree.
     */
    public Collection<DependencyNode<K>> valueSet() {
        return nodesByKey.values();
    }

    /**
     * Returns the size of the dependency tree.
     *
     * @return the sizze of the dependency tree.
     */
    public int size() {
        return nodesByKey.size();
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import lombok.*;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the smallest unit in the Abstract Syntax Tree (AST).
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class AstNodeBase implements Element {

    /**
     * The node source code range.
     */
    @Getter
    private final Range range;

    /**
     * A list of all the children nodes of this node.
     */
    @Getter
    private final List<AstNodeBase> children = new ArrayList<>();

    /**
     * The parent node of this node.
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    @EqualsAndHashCode.Exclude
    private AstNodeBase parent;

    /**
     * Adds all of the specified nodes of type {@link T} to this node as children.
     *
     * @param nodes the nodes to add to this node as children.
     * @param <T>   the type of the nodes we are adding.
     * @return the same array reference that was passed to this method.
     */
    public <T extends AstNodeBase> T[] addChild(T[] nodes) {
        for (var node : nodes) {
            if (node.getParent() != null) {
                throw new IllegalStateException("One or more of the specified nodes is already a children of another node");
            }
        }
        for (var node : nodes) {
            node.setParent(this);
            children.add(node);
        }
        return nodes;
    }

    /**
     * Adds all of the specified nodes of type {@link T} to this node as children.
     *
     * @param nodes the nodes to add to this node as children.
     * @param <T>   the type of the nodes we are adding.
     * @return the same {@link List} reference that was passed to this method.
     */
    public <T extends AstNodeBase> List<T> addChild(List<T> nodes) {
        for (var node : nodes) {
            if (node.getParent() != null) {
                throw new IllegalStateException("One or more of the specified nodes is already a children of another node");
            }
        }
        for (var node : nodes) {
            node.setParent(this);
            children.add(node);
        }
        return nodes;
    }

    /**
     * Adds the specified node of type {@link T} to this node as children.
     *
     * @param node the children node to add to this node.
     * @param <T>  the type of the children node.
     * @return the same reference of type {@link T} that was passed to this method.
     */
    public <T extends AstNodeBase> T addChild(T node) {
        if (node.getParent() != null) {
            throw new IllegalStateException("The specified node is already a children of another node");
        }
        node.setParent(this);
        children.add(node);
        return node;
    }

    /**
     * Selects one of the parent nodes to this node that passes the test of the specified {@link Predicate}.
     *
     * @param predicate the predicate which will test the nodes.
     * @return the selected {@link AstNodeBase} if any has passed otherwise {@code null}.
     */
    public AstNodeBase selectParent(Predicate<AstNodeBase> predicate) {
        var parent = this.parent;
        while (parent != null) {
            if (predicate.test(parent)) {
                return parent;
            }
            parent = parent.parent;
        }
        return null;
    }
}

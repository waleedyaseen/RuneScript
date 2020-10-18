/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.*;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents the smallest unit in the Abstract Syntax Tree (AST).
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class SyntaxBase implements Element {

    /**
     * A list of all the children nodes of this node.
     */
    @Getter
    private final List<SyntaxBase> children = new ArrayList<>();

    /**
     * The attributes attached to the syntax.
     */
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * The node source code range.
     */
    @Getter
    private final Range range;

    /**
     * The parent node of this node.
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    @EqualsAndHashCode.Exclude
    private SyntaxBase parent;

    /**
     * Adds all of the specified nodes of type {@link T} to this node as children.
     *
     * @param nodes the nodes to add to this node as children.
     * @param <T>   the type of the nodes we are adding.
     * @return the same array reference that was passed to this method.
     */
    public <T extends SyntaxBase> T[] addChild(T[] nodes) {
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
    public <T extends SyntaxBase> List<T> addChild(List<T> nodes) {
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
    public <T extends SyntaxBase> T addChild(T node) {
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
     * @return the selected {@link SyntaxBase} if any has passed otherwise {@code null}.
     */
    public SyntaxBase selectParent(Predicate<SyntaxBase> predicate) {
        var parent = this.parent;
        while (parent != null) {
            if (predicate.test(parent)) {
                return parent;
            }
            parent = parent.parent;
        }
        return null;
    }

    /**
     * Returns the attribute that is stored in the attributes map under the specified
     * {@code attributeName}.
     *
     * @param attributeName the name of the attribute that we want to retrieve.
     * @param <T>           the type which the attribute will be casted to before returning.
     * @return the attribute value casted to type {@link T} or {@code null} if nothing is present.
     * @throws ClassCastException if the attribute cannot be casted to type {@link T}.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String attributeName) {
        return (T) attributes.get(attributeName);
    }

    /**
     * Adds a new attribute with the specified {@code value} to the attribute map.
     *
     * @param attributeName the name of the attribute to add.
     * @param value         the value of the attribute to add.
     * @param <T>           the type of the attribute we are adding.
     */
    public <T> T putAttribute(String attributeName, @NonNull T value) {
        attributes.put(attributeName, value);
        return value;
    }

    /**
     * Removes the attribute with the specified attribute name from the attributes map.
     *
     * @param attributeName the attribute name to remove from the attributes map.
     */
    public void removeAttribute(String attributeName) {
        attributes.remove(attributeName);
    }
}

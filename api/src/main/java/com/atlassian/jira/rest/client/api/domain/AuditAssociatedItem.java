package com.atlassian.jira.rest.client.api.domain;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * TODO: Document this class / interface here
 *
 * @since v2.0
 */
public class AuditAssociatedItem {

    @Nullable
    private final String id;

    @Nonnull
    private final String name;

    @Nonnull
    private final String typeName;

    @Nullable
    private final String parentId;

    @Nullable
    private final String parentName;

    public AuditAssociatedItem(final String id, final String name, final String typeName, final String parentId, final String parentName) {
        this.id = id;
        this.name = name;
        this.typeName = typeName;
        this.parentId = parentId;
        this.parentName = parentName;
    }

    protected Objects.ToStringHelper getToStringHelper() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("name", name).
                add("typeName", typeName).
                add("parentId", parentId).
                add("parentName", parentName);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AuditAssociatedItem) {
            final AuditAssociatedItem that = (AuditAssociatedItem) o;
            return Objects.equal(this.id, that.id)
                    && Objects.equal(this.name, that.name)
                    && Objects.equal(this.parentId, that.parentId)
                    && Objects.equal(this.parentName, that.parentName)
                    && Objects.equal(this.typeName, that.typeName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, typeName, typeName, parentId, parentName);
    }

}
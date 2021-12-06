/*
 * This file is generated by jOOQ.
 */
package com.github.vti.amcrm.db.tables.records;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;

import com.github.vti.amcrm.db.tables.User;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class UserRecord extends UpdatableRecordImpl<UserRecord>
        implements Record7<String, Long, String, Boolean, String, String, String> {

    private static final long serialVersionUID = 1L;

    /** Setter for <code>user.id</code>. */
    public void setId(String value) {
        set(0, value);
    }

    /** Getter for <code>user.id</code>. */
    public String getId() {
        return (String) get(0);
    }

    /** Setter for <code>user.version</code>. */
    public void setVersion(Long value) {
        set(1, value);
    }

    /** Getter for <code>user.version</code>. */
    public Long getVersion() {
        return (Long) get(1);
    }

    /** Setter for <code>user.name</code>. */
    public void setName(String value) {
        set(2, value);
    }

    /** Getter for <code>user.name</code>. */
    public String getName() {
        return (String) get(2);
    }

    /** Setter for <code>user.is_admin</code>. */
    public void setIsAdmin(Boolean value) {
        set(3, value);
    }

    /** Getter for <code>user.is_admin</code>. */
    public Boolean getIsAdmin() {
        return (Boolean) get(3);
    }

    /** Setter for <code>user.created_by</code>. */
    public void setCreatedBy(String value) {
        set(4, value);
    }

    /** Getter for <code>user.created_by</code>. */
    public String getCreatedBy() {
        return (String) get(4);
    }

    /** Setter for <code>user.updated_by</code>. */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /** Getter for <code>user.updated_by</code>. */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /** Setter for <code>user.deleted_by</code>. */
    public void setDeletedBy(String value) {
        set(6, value);
    }

    /** Getter for <code>user.deleted_by</code>. */
    public String getDeletedBy() {
        return (String) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, Long, String, Boolean, String, String, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<String, Long, String, Boolean, String, String, String> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return User.USER.ID;
    }

    @Override
    public Field<Long> field2() {
        return User.USER.VERSION;
    }

    @Override
    public Field<String> field3() {
        return User.USER.NAME;
    }

    @Override
    public Field<Boolean> field4() {
        return User.USER.IS_ADMIN;
    }

    @Override
    public Field<String> field5() {
        return User.USER.CREATED_BY;
    }

    @Override
    public Field<String> field6() {
        return User.USER.UPDATED_BY;
    }

    @Override
    public Field<String> field7() {
        return User.USER.DELETED_BY;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getVersion();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Boolean component4() {
        return getIsAdmin();
    }

    @Override
    public String component5() {
        return getCreatedBy();
    }

    @Override
    public String component6() {
        return getUpdatedBy();
    }

    @Override
    public String component7() {
        return getDeletedBy();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getVersion();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public Boolean value4() {
        return getIsAdmin();
    }

    @Override
    public String value5() {
        return getCreatedBy();
    }

    @Override
    public String value6() {
        return getUpdatedBy();
    }

    @Override
    public String value7() {
        return getDeletedBy();
    }

    @Override
    public UserRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public UserRecord value2(Long value) {
        setVersion(value);
        return this;
    }

    @Override
    public UserRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public UserRecord value4(Boolean value) {
        setIsAdmin(value);
        return this;
    }

    @Override
    public UserRecord value5(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public UserRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    @Override
    public UserRecord value7(String value) {
        setDeletedBy(value);
        return this;
    }

    @Override
    public UserRecord values(
            String value1,
            Long value2,
            String value3,
            Boolean value4,
            String value5,
            String value6,
            String value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** Create a detached UserRecord */
    public UserRecord() {
        super(User.USER);
    }

    /** Create a detached, initialised UserRecord */
    public UserRecord(
            String id,
            Long version,
            String name,
            Boolean isAdmin,
            String createdBy,
            String updatedBy,
            String deletedBy) {
        super(User.USER);

        setId(id);
        setVersion(version);
        setName(name);
        setIsAdmin(isAdmin);
        setCreatedBy(createdBy);
        setUpdatedBy(updatedBy);
        setDeletedBy(deletedBy);
    }
}
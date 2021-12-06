/*
 * This file is generated by jOOQ.
 */
package com.github.vti.amcrm.db.tables.records;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;

import com.github.vti.amcrm.db.tables.Customer;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class CustomerRecord extends UpdatableRecordImpl<CustomerRecord>
        implements Record11<
                String,
                Long,
                String,
                String,
                String,
                LocalDateTime,
                String,
                LocalDateTime,
                String,
                LocalDateTime,
                String> {

    private static final long serialVersionUID = 1L;

    /** Setter for <code>customer.id</code>. */
    public void setId(String value) {
        set(0, value);
    }

    /** Getter for <code>customer.id</code>. */
    public String getId() {
        return (String) get(0);
    }

    /** Setter for <code>customer.version</code>. */
    public void setVersion(Long value) {
        set(1, value);
    }

    /** Getter for <code>customer.version</code>. */
    public Long getVersion() {
        return (Long) get(1);
    }

    /** Setter for <code>customer.name</code>. */
    public void setName(String value) {
        set(2, value);
    }

    /** Getter for <code>customer.name</code>. */
    public String getName() {
        return (String) get(2);
    }

    /** Setter for <code>customer.surname</code>. */
    public void setSurname(String value) {
        set(3, value);
    }

    /** Getter for <code>customer.surname</code>. */
    public String getSurname() {
        return (String) get(3);
    }

    /** Setter for <code>customer.photo_location</code>. */
    public void setPhotoLocation(String value) {
        set(4, value);
    }

    /** Getter for <code>customer.photo_location</code>. */
    public String getPhotoLocation() {
        return (String) get(4);
    }

    /** Setter for <code>customer.created_at</code>. */
    public void setCreatedAt(LocalDateTime value) {
        set(5, value);
    }

    /** Getter for <code>customer.created_at</code>. */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(5);
    }

    /** Setter for <code>customer.created_by</code>. */
    public void setCreatedBy(String value) {
        set(6, value);
    }

    /** Getter for <code>customer.created_by</code>. */
    public String getCreatedBy() {
        return (String) get(6);
    }

    /** Setter for <code>customer.updated_at</code>. */
    public void setUpdatedAt(LocalDateTime value) {
        set(7, value);
    }

    /** Getter for <code>customer.updated_at</code>. */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(7);
    }

    /** Setter for <code>customer.updated_by</code>. */
    public void setUpdatedBy(String value) {
        set(8, value);
    }

    /** Getter for <code>customer.updated_by</code>. */
    public String getUpdatedBy() {
        return (String) get(8);
    }

    /** Setter for <code>customer.deleted_at</code>. */
    public void setDeletedAt(LocalDateTime value) {
        set(9, value);
    }

    /** Getter for <code>customer.deleted_at</code>. */
    public LocalDateTime getDeletedAt() {
        return (LocalDateTime) get(9);
    }

    /** Setter for <code>customer.deleted_by</code>. */
    public void setDeletedBy(String value) {
        set(10, value);
    }

    /** Getter for <code>customer.deleted_by</code>. */
    public String getDeletedBy() {
        return (String) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<
                    String,
                    Long,
                    String,
                    String,
                    String,
                    LocalDateTime,
                    String,
                    LocalDateTime,
                    String,
                    LocalDateTime,
                    String>
            fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<
                    String,
                    Long,
                    String,
                    String,
                    String,
                    LocalDateTime,
                    String,
                    LocalDateTime,
                    String,
                    LocalDateTime,
                    String>
            valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Customer.CUSTOMER.ID;
    }

    @Override
    public Field<Long> field2() {
        return Customer.CUSTOMER.VERSION;
    }

    @Override
    public Field<String> field3() {
        return Customer.CUSTOMER.NAME;
    }

    @Override
    public Field<String> field4() {
        return Customer.CUSTOMER.SURNAME;
    }

    @Override
    public Field<String> field5() {
        return Customer.CUSTOMER.PHOTO_LOCATION;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Customer.CUSTOMER.CREATED_AT;
    }

    @Override
    public Field<String> field7() {
        return Customer.CUSTOMER.CREATED_BY;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return Customer.CUSTOMER.UPDATED_AT;
    }

    @Override
    public Field<String> field9() {
        return Customer.CUSTOMER.UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return Customer.CUSTOMER.DELETED_AT;
    }

    @Override
    public Field<String> field11() {
        return Customer.CUSTOMER.DELETED_BY;
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
    public String component4() {
        return getSurname();
    }

    @Override
    public String component5() {
        return getPhotoLocation();
    }

    @Override
    public LocalDateTime component6() {
        return getCreatedAt();
    }

    @Override
    public String component7() {
        return getCreatedBy();
    }

    @Override
    public LocalDateTime component8() {
        return getUpdatedAt();
    }

    @Override
    public String component9() {
        return getUpdatedBy();
    }

    @Override
    public LocalDateTime component10() {
        return getDeletedAt();
    }

    @Override
    public String component11() {
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
    public String value4() {
        return getSurname();
    }

    @Override
    public String value5() {
        return getPhotoLocation();
    }

    @Override
    public LocalDateTime value6() {
        return getCreatedAt();
    }

    @Override
    public String value7() {
        return getCreatedBy();
    }

    @Override
    public LocalDateTime value8() {
        return getUpdatedAt();
    }

    @Override
    public String value9() {
        return getUpdatedBy();
    }

    @Override
    public LocalDateTime value10() {
        return getDeletedAt();
    }

    @Override
    public String value11() {
        return getDeletedBy();
    }

    @Override
    public CustomerRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public CustomerRecord value2(Long value) {
        setVersion(value);
        return this;
    }

    @Override
    public CustomerRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public CustomerRecord value4(String value) {
        setSurname(value);
        return this;
    }

    @Override
    public CustomerRecord value5(String value) {
        setPhotoLocation(value);
        return this;
    }

    @Override
    public CustomerRecord value6(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public CustomerRecord value7(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public CustomerRecord value8(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    public CustomerRecord value9(String value) {
        setUpdatedBy(value);
        return this;
    }

    @Override
    public CustomerRecord value10(LocalDateTime value) {
        setDeletedAt(value);
        return this;
    }

    @Override
    public CustomerRecord value11(String value) {
        setDeletedBy(value);
        return this;
    }

    @Override
    public CustomerRecord values(
            String value1,
            Long value2,
            String value3,
            String value4,
            String value5,
            LocalDateTime value6,
            String value7,
            LocalDateTime value8,
            String value9,
            LocalDateTime value10,
            String value11) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** Create a detached CustomerRecord */
    public CustomerRecord() {
        super(Customer.CUSTOMER);
    }

    /** Create a detached, initialised CustomerRecord */
    public CustomerRecord(
            String id,
            Long version,
            String name,
            String surname,
            String photoLocation,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime updatedAt,
            String updatedBy,
            LocalDateTime deletedAt,
            String deletedBy) {
        super(Customer.CUSTOMER);

        setId(id);
        setVersion(version);
        setName(name);
        setSurname(surname);
        setPhotoLocation(photoLocation);
        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
        setUpdatedAt(updatedAt);
        setUpdatedBy(updatedBy);
        setDeletedAt(deletedAt);
        setDeletedBy(deletedBy);
    }
}

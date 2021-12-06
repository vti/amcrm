/*
 * This file is generated by jOOQ.
 */
package com.github.vti.amcrm.db.tables;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import com.github.vti.amcrm.db.DefaultSchema;
import com.github.vti.amcrm.db.Keys;
import com.github.vti.amcrm.db.tables.records.EventRecord;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Event extends TableImpl<EventRecord> {

    private static final long serialVersionUID = 1L;

    /** The reference instance of <code>event</code> */
    public static final Event EVENT = new Event();

    /** The class holding records for this type */
    @Override
    public Class<EventRecord> getRecordType() {
        return EventRecord.class;
    }

    /** The column <code>event.id</code>. */
    public final TableField<EventRecord, Integer> ID =
            createField(
                    DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /** The column <code>event.created_at</code>. */
    public final TableField<EventRecord, LocalDateTime> CREATED_AT =
            createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(0), this, "");

    /** The column <code>event.name</code>. */
    public final TableField<EventRecord, String> NAME =
            createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    /** The column <code>event.origin_id</code>. */
    public final TableField<EventRecord, String> ORIGIN_ID =
            createField(DSL.name("origin_id"), SQLDataType.CLOB.nullable(false), this, "");

    /** The column <code>event.user_id</code>. */
    public final TableField<EventRecord, String> USER_ID =
            createField(DSL.name("user_id"), SQLDataType.CLOB.nullable(false), this, "");

    /** The column <code>event.payload</code>. */
    public final TableField<EventRecord, String> PAYLOAD =
            createField(DSL.name("payload"), SQLDataType.CLOB, this, "");

    private Event(Name alias, Table<EventRecord> aliased) {
        this(alias, aliased, null);
    }

    private Event(Name alias, Table<EventRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /** Create an aliased <code>event</code> table reference */
    public Event(String alias) {
        this(DSL.name(alias), EVENT);
    }

    /** Create an aliased <code>event</code> table reference */
    public Event(Name alias) {
        this(alias, EVENT);
    }

    /** Create a <code>event</code> table reference */
    public Event() {
        this(DSL.name("event"), null);
    }

    public <O extends Record> Event(Table<O> child, ForeignKey<O, EventRecord> key) {
        super(child, key, EVENT);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<EventRecord, Integer> getIdentity() {
        return (Identity<EventRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<EventRecord> getPrimaryKey() {
        return Keys.PK_EVENT;
    }

    @Override
    public List<UniqueKey<EventRecord>> getKeys() {
        return Arrays.<UniqueKey<EventRecord>>asList(Keys.PK_EVENT);
    }

    @Override
    public Event as(String alias) {
        return new Event(DSL.name(alias), this);
    }

    @Override
    public Event as(Name alias) {
        return new Event(alias, this);
    }

    /** Rename this table */
    @Override
    public Event rename(String name) {
        return new Event(DSL.name(name), null);
    }

    /** Rename this table */
    @Override
    public Event rename(Name name) {
        return new Event(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, LocalDateTime, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}

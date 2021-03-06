/*
 * This file is generated by jOOQ.
 */
package com.github.vti.amcrm.db;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import com.github.vti.amcrm.db.tables.Customer;
import com.github.vti.amcrm.db.tables.Event;
import com.github.vti.amcrm.db.tables.Session;
import com.github.vti.amcrm.db.tables.SqliteSequence;
import com.github.vti.amcrm.db.tables.User;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /** The reference instance of <code>DEFAULT_SCHEMA</code> */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /** The table <code>customer</code>. */
    public final Customer CUSTOMER = Customer.CUSTOMER;

    /** The table <code>event</code>. */
    public final Event EVENT = Event.EVENT;

    /** The table <code>session</code>. */
    public final Session SESSION = Session.SESSION;

    /** The table <code>sqlite_sequence</code>. */
    public final SqliteSequence SQLITE_SEQUENCE = SqliteSequence.SQLITE_SEQUENCE;

    /** The table <code>user</code>. */
    public final User USER = User.USER;

    /** No further instances allowed */
    private DefaultSchema() {
        super("", null);
    }

    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
                Customer.CUSTOMER,
                Event.EVENT,
                Session.SESSION,
                SqliteSequence.SQLITE_SEQUENCE,
                User.USER);
    }
}

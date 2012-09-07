/*
  $Id: $

  Copyright (C) 2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.catalina.valves;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;

import org.apache.catalina.AccessLog;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 * Writes Tomcat access logs to a database via JDBC using asynchronous IO to minimize the impact
 * on Tomcat connector threads.  The interface of the valve conforms generally to
 * <code>org.apache.catalina.valves.JDBCAccessLogValve</code>.  The implementation uses a single
 * worker thread with an unbounded work queue to perform database writes in batches via the
 * {@link java.sql.PreparedStatement#executeBatch()} API.  All database writes happen on a single
 * database connection that is closed on errors and reopened as needed.
 * <p>
 * The database table and helpful indices can be created with SQL statements like the following:
 * </p>
 * <pre>
 * CREATE TABLE access (
 *   id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
 *   remoteHost CHAR(15) NOT NULL,
 *   userName VARCHAR(64),
 *   timestamp TIMESTAMP NOT NULL,
 *   virtualHost VARCHAR(64) NOT NULL,
 *   method VARCHAR(8) NOT NULL,
 *   query VARCHAR(512) NOT NULL,
 *   status SMALLINT UNSIGNED NOT NULL,
 *   bytes INT UNSIGNED NOT NULL,
 *   referer VARCHAR(512),
 *   userAgent VARCHAR(256)
 * );
 * CREATE INDEX i_access_time ON access (access_time);
 * CREATE INDEX i_access_remote_host ON access (remote_host);
 * CREATE INDEX i_access_virtual_hostON access (virtual_host);
 * CREATE INDEX i_access_user_agent ON access (user_agent);
 * </pre>
 * <p>
 * Note that the remoteHost field should be VARCHAR(64) or better if hostname resolution is
 * enabled.
 * </p>
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public class AsyncJDBCAccessLogValve extends ValveBase implements AccessLog {

    public static final String COMMON_PATTERN = "common";

    public static final String COMBINED_PATTERN = "combined";

    public static final String DEFAULT_TABLE_NAME = "access";

    public static final String DEFAULT_REMOTE_HOST_FIELD_NAME = "remoteHost";

    public static final String DEFAULT_USER_FIELD_NAME = "userName";

    public static final String DEFAULT_TIMESTAMP_FIELD_NAME = "timestamp";

    public static final String DEFAULT_VIRTUAL_HOST_FIELD_NAME = "virtualHost";

    public static final String DEFAULT_METHOD_FIELD_NAME = "method";

    public static final String DEFAULT_QUERY_FIELD_NAME = "query";

    public static final String DEFAULT_STATUS_FIELD_NAME = "status";

    public static final String DEFAULT_BYTES_FIELD_NAME = "bytes";

    public static final String DEFAULT_REFERER_FIELD_NAME = "referer";

    public static final String DEFAULT_USER_AGENT_FIELD_NAME = "userAgent";

    public static final String DEFAULT_PATTERN = COMMON_PATTERN;

    public static final int DEFAULT_RETRY_COUNT = 2;

    public static final int DEFAULT_REMOTE_HOST_FIELD_LENGTH = 15;

    public static final int DEFAULT_USER_FIELD_LENGTH = 64;

    public static final int DEFAULT_VIRTUAL_HOST_FIELD_LENGTH = 64;

    public static final int DEFAULT_QUERY_FIELD_LENGTH = 512;

    public static final int DEFAULT_REFERER_FIELD_LENGTH = 512;

    public static final int DEFAULT_USER_AGENT_FIELD_LENGTH = 256;

    private static final String EXCEPTION_MESSAGE_KEY = "jdbcAccessLogValve.exception";

    /**
     * The descriptive information about this implementation.
     */
    protected static final String info =
            "edu.vt.middleware.catalina.valves.AsyncJDBCAccessLogValve/1.1";

    /**
     * Use long contentLength as you have more 4 GB output.
     * @since 6.0.15
     */
    private boolean useLongContentLength = false ;

    /**
     * The connection username to use when trying to connect to the database.
     */
    private String connectionName = null;


    /**
     * The connection URL to use when trying to connect to the database.
     */
    private String connectionPassword = null;

    /**
     * Instance of the JDBC Driver class we use as a connection factory.
     */
    private Driver driver = null;


    private String connectionURL;

    private String tableName = DEFAULT_TABLE_NAME;
    private String remoteHostField = DEFAULT_REMOTE_HOST_FIELD_NAME;
    private String userField = DEFAULT_USER_FIELD_NAME;
    private String timestampField = DEFAULT_TIMESTAMP_FIELD_NAME;
    private String virtualHostField = DEFAULT_VIRTUAL_HOST_FIELD_NAME;
    private String methodField = DEFAULT_METHOD_FIELD_NAME;
    private String queryField = DEFAULT_QUERY_FIELD_NAME;
    private String statusField = DEFAULT_STATUS_FIELD_NAME;
    private String bytesField = DEFAULT_BYTES_FIELD_NAME;
    private String refererField = DEFAULT_REFERER_FIELD_NAME;
    private String userAgentField = DEFAULT_USER_AGENT_FIELD_NAME;
    private String pattern = DEFAULT_PATTERN;
    private int remoteHostSize = DEFAULT_REMOTE_HOST_FIELD_LENGTH;
    private int userSize = DEFAULT_USER_FIELD_LENGTH;
    private int virtualHostSize = DEFAULT_VIRTUAL_HOST_FIELD_LENGTH;
    private int querySize = DEFAULT_QUERY_FIELD_LENGTH;
    private int refererSize = DEFAULT_REFERER_FIELD_LENGTH;
    private int userAgentSize = DEFAULT_USER_AGENT_FIELD_LENGTH;
    private int retryCount = DEFAULT_RETRY_COUNT;
    private boolean resolveHosts = false;


    private Connection conn;
    private PreparedStatement ps;

    /**
     * @see #setRequestAttributesEnabled(boolean)
     */
    private boolean requestAttributesEnabled = true;

    /** Worker thread that drives batched database writes. */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /** Shared work queue. */
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<LogEntry>();

    /** Performs batched database writes at request of worker thread. */
    private final BatchWriter writer = new BatchWriter();


    /**
     * Class constructor. Initializes the fields with the default values.
     * The defaults are:
     * <pre>
     *      driverName = null;
     *      connectionURL = null;
     *      tableName = "access";
     *      remoteHostField = "remoteHost";
     *      userField = "userName";
     *      timestampField = "timestamp";
     *      virtualHostField = "virtualHost";
     *      methodField = "method";
     *      queryField = "query";
     *      statusField = "status";
     *      bytesField = "bytes";
     *      refererField = "referer";
     *      userAgentField = "userAgent";
     *      pattern = "common";
     *      resolveHosts = false;
     *      retryCount = 2;
     * </pre>
     */
    public AsyncJDBCAccessLogValve() {
        super(true);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getRequestAttributesEnabled() {
        return requestAttributesEnabled;
    }

    /**
     * Return the username to use to connect to the database.
     *
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * Set the username to use to connect to the database.
     *
     * @param connectionName Username
     */
    public void setConnectionName(final String connectionName) {
        this.connectionName = connectionName;
    }

    /**
     * Sets the database driver name.
     *
     * @param driverName The complete name of the database driver class.
     */
    public void setDriverName(final String driverName) {
        try {
            this.driver = (Driver) Class.forName(driverName).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create driver " + driverName, e);
        }
    }

    /**
     * Return the password to use to connect to the database.
     *
     */
    public String getConnectionPassword() {
        return connectionPassword;
    }

    /**
     * Set the password to use to connect to the database.
     *
     * @param connectionPassword User password
     */
    public void setConnectionPassword(final String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    /**
     * Sets the JDBC URL for the database where the log is stored.
     *
     * @param connectionURL The JDBC URL of the database.
     */
    public void setConnectionURL(final String connectionURL) {
        this.connectionURL = connectionURL;
    }


    /**
     * Sets the name of the table where the logs are stored.
     *
     * @param tableName The name of the table.
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }


    /**
     * Sets the name of the field containing the remote host.
     *
     * @param remoteHostField The name of the remote host field.
     */
    public void setRemoteHostField(final String remoteHostField) {
        this.remoteHostField = remoteHostField;
    }


    /**
     * Sets the name of the field containing the remote user name.
     *
     * @param userField The name of the remote user field.
     */
    public void setUserField(final String userField) {
        this.userField = userField;
    }


    /**
     * Sets the name of the field containing the server-determined timestamp.
     *
     * @param timestampField The name of the server-determined timestamp field.
     */
    public void setTimestampField(final String timestampField) {
        this.timestampField = timestampField;
    }


    /**
     * Sets the name of the field containing the virtual host information
     * (this is in fact the server name).
     *
     * @param virtualHostField The name of the virtual host field.
     */
    public void setVirtualHostField(final String virtualHostField) {
        this.virtualHostField = virtualHostField;
    }


    /**
     * Sets the name of the field containing the HTTP request method.
     *
     * @param methodField The name of the HTTP request method field.
     */
    public void setMethodField(final String methodField) {
        this.methodField = methodField;
    }


    /**
     * Sets the name of the field containing the URL part of the HTTP query.
     *
     * @param queryField The name of the field containing the URL part of
     * the HTTP query.
     */
    public void setQueryField(final String queryField) {
        this.queryField = queryField;
    }


    /**
     * Sets the name of the field containing the HTTP response status code.
     *
     * @param statusField The name of the HTTP response status code field.
     */
    public void setStatusField(final String statusField) {
        this.statusField = statusField;
    }


    /**
     * Sets the name of the field containing the number of bytes returned.
     *
     * @param bytesField The name of the returned bytes field.
     */
    public void setBytesField(final String bytesField) {
        this.bytesField = bytesField;
    }


    /**
     * Sets the name of the field containing the referer.
     *
     * @param refererField The referer field name.
     */
    public void setRefererField(final String refererField) {
        this.refererField = refererField;
    }


    /**
     * Sets the name of the field containing the user agent.
     *
     * @param userAgentField The name of the user agent field.
     */
    public void setUserAgentField(final String userAgentField) {
        this.userAgentField = userAgentField;
    }


    /**
     * Sets the logging pattern. The patterns supported correspond to the
     * file-based "common" and "combined". These are translated into the use
     * of tables containing either set of fields.
     *
     * @param pattern Either "common" or "combined".
     */
    public void setPattern(final String pattern) {
        if (COMMON_PATTERN.equals(pattern) || COMBINED_PATTERN.equals(pattern)) {
            this.pattern = pattern;
        } else {
            throw new IllegalArgumentException("Unsupported pattern " + pattern);
        }
    }


    /**
     * Determines whether IP host name resolution is done.
     *
     * @param resolveHosts "true" or "false", if host IP resolution
     * is desired or not.
     */
    public void setResolveHosts(final String resolveHosts) {
        this.resolveHosts = Boolean.valueOf(resolveHosts).booleanValue();
    }

    /**
     * get useLongContentLength
     */
    public boolean getUseLongContentLength() {
        return this.useLongContentLength ;
    }

    /**
     * @param useLongContentLength the useLongContentLength to set
     */
    public void setUseLongContentLength(boolean useLongContentLength) {
        this.useLongContentLength = useLongContentLength;
    }

    /**
     * Sets the number of times a failed batched database write will be attempted
     * before giving up.
     *
     * @param  count  Number of times a failed write will be reattempted before giving up.
     *                MUST be non-negative.
     */
    public void setRetryCount(final String count) {
        this.retryCount = parseNonNegativeInt(count, "Retry count");
    }

    public void setRemoteHostSize(final String size) {
        this.remoteHostSize = parseNonNegativeInt(size, "Remote host field size");
    }

    public void setUserSize(final String size) {
        this.userSize = parseNonNegativeInt(size, "User field size");
    }

    public void setVirtualHostSize(final String size) {
        this.virtualHostSize = parseNonNegativeInt(size, "Virtual host field size");
    }

    public void setQuerySize(final String size) {
        this.querySize = parseNonNegativeInt(size, "Query field size");
    }

    public void setRefererSize(final String size) {
        this.refererSize = parseNonNegativeInt(size, "Referer field size");
    }

    public void setUserAgentSize(final String size) {
        this.userAgentSize = parseNonNegativeInt(size, "User agent field size");
    }

    /**
     * This method is invoked by Tomcat on each query.
     *
     * @param request The Request object.
     * @param response The Response object.
     *
     * @exception java.io.IOException Should not be thrown.
     * @exception javax.servlet.ServletException Database SQLException is wrapped
     * in a ServletException.
     */
    @Override
    public void invoke(Request request, Response response) throws IOException,
            ServletException {
        getNext().invoke(request, response);
    }


    @Override
    public void log(Request request, Response response, long time) {
        if (!getState().isAvailable()) {
            return;
        }

        LogEntry entry = new LogEntry();
        if(resolveHosts) {
            if (requestAttributesEnabled) {
                Object host = request.getAttribute(REMOTE_HOST_ATTRIBUTE);
                if (host == null) {
                    entry.setRemoteHost(request.getRemoteHost());
                } else {
                    entry.setRemoteHost((String) host);
                }
            } else {
                entry.setRemoteHost(request.getRemoteHost());
            }
        } else {
            if (requestAttributesEnabled) {
                Object addr = request.getAttribute(REMOTE_ADDR_ATTRIBUTE);
                if (addr == null) {
                    entry.setRemoteHost(request.getRemoteAddr());
                } else {
                    entry.setRemoteHost((String) addr);
                }
            } else {
                entry.setRemoteHost(request.getRemoteAddr());
            }
        }
        entry.setUser(request.getRemoteUser());
        entry.setQuery(request.getRequestURI());

        entry.setBytes(response.getBytesWritten(true));
        if(entry.getBytes() < 0) {
            entry.setBytes(0);
        }
        entry.setStatus(response.getStatus());
        if (COMBINED_PATTERN.equals(this.pattern)) {
            entry.setVirtualHost(request.getServerName());
            entry.setMethod(request.getMethod());
            entry.setReferer(request.getHeader("referer"));
            entry.setUserAgent(request.getHeader("user-agent"));
        }
        this.queue.add(entry);
        this.executor.submit(this.writer);
    }


    /**
     * Start this component and implement the requirements
     * of {@link org.apache.catalina.util.LifecycleBase#startInternal()}.
     *
     * @exception org.apache.catalina.LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        open();
        setState(LifecycleState.STARTING);
    }


    /**
     * Stop this component and implement the requirements
     * of {@link org.apache.catalina.util.LifecycleBase#stopInternal()}.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        close();
        this.executor.shutdown();
    }


    /**
     * Create and open a database connection and prepared statement if needed.
     */
    private void open() {

        // Do nothing if there is a database connection already open
        if (conn != null) {
            return;
        }

        // Open a new connection
        Properties props = new Properties();
        props.put("autoReconnect", "true");
        if (connectionName != null) {
            props.put("user", connectionName);
        }
        if (connectionPassword != null) {
            props.put("password", connectionPassword);
        }
        try {
            conn = driver.connect(connectionURL, props);
            conn.setAutoCommit(true);
            if (COMBINED_PATTERN.equals(pattern)) {
                ps = conn.prepareStatement
                        ("INSERT INTO " + tableName + " ("
                                + remoteHostField + ", " + userField + ", "
                                + timestampField + ", " + queryField + ", "
                                + statusField + ", " + bytesField + ", "
                                + virtualHostField + ", " + methodField + ", "
                                + refererField + ", " + userAgentField
                                + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            } else {
                // Default to common logging pattern
                ps = conn.prepareStatement
                        ("INSERT INTO " + tableName + " ("
                                + remoteHostField + ", " + userField + ", "
                                + timestampField +", " + queryField + ", "
                                + statusField + ", " + bytesField
                                + ") VALUES(?, ?, ?, ?, ?, ?)");
            }
        } catch (SQLException e) {
            container.getLogger().error(sm.getString(EXCEPTION_MESSAGE_KEY), e);
            close();
            throw new IllegalStateException("Cannot open database connection.", e);
        }
    }

    /**
     * Close the database connection and related resources if needed.
     */
    private void close() {
        if (ps != null) {
            try {
                ps.close();
            } catch (Throwable f) {
                container.getLogger().error(sm.getString(EXCEPTION_MESSAGE_KEY), f);
            } finally {
                this.ps = null;
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                container.getLogger().error(sm.getString(EXCEPTION_MESSAGE_KEY), e);
            } finally {
                this.conn = null;
            }
        }

    }

    /**
     * Parses a string representation of a non-negative integer.
     *
     * @param value String representation of integer.
     * @param errPrefix Prefix appended to error messages raised when parsed value is negative.
     *
     * @return Non-negative integer.
     */
    private int parseNonNegativeInt(final String value, final String errPrefix) {
        final Integer n = Integer.parseInt(value);
        if (n < 0) {
            throw new IllegalArgumentException(errPrefix + " must be non-negative.");
        }
        return n.intValue();
    }


    /**
     * Trims a string to the given size when its length exceeds the specified size.
     *
     * @param value String to trim if needed.
     * @param size Maximum size of string to be returned.
     *
     * @return Original string if its length is less than or equal to size,
     * otherwise a substring of the given size.
     */
    private String trimToSize(final String value, final int size) {
        if (value != null && value.length() > size) {
            return value.substring(0, size - 1);
        }
        return value;
    }


    /**
     * Performs batched database writes. The implementation assumes that it is the only
     * consumer of the work queue.
     */
    class BatchWriter implements Runnable {

        /** Writes a single batch including all available items in the shared work queue. */
        public void run() {
            if (queue.isEmpty()) {
                return;
            }
            LogEntry entry;
            boolean hasMore = true;
            synchronized(this) {
                open();
                while (hasMore) {
                    try {
                        entry = queue.remove();
                        ps.setString(1, trimToSize(entry.getRemoteHost(), remoteHostSize));
                        ps.setString(2, trimToSize(entry.getUser(), userSize));
                        ps.setTimestamp(3, entry.getTimestamp());
                        ps.setString(4, trimToSize(entry.getQuery(), querySize));
                        ps.setInt(5, entry.getStatus());

                        if(useLongContentLength) {
                            ps.setLong(6, entry.getBytes());
                        } else {
                            if (entry.getBytes() > Integer.MAX_VALUE) {
                                entry.setBytes(-1);
                            }
                            ps.setInt(6, (int) entry.getBytes());
                        }
                        if (COMBINED_PATTERN.equals(pattern)) {
                            ps.setString(7, trimToSize(entry.getVirtualHost(), virtualHostSize));
                            ps.setString(8, entry.getMethod());
                            ps.setString(9, trimToSize(entry.getReferer(), refererSize));
                            ps.setString(10, trimToSize(entry.getUserAgent(), userAgentSize));
                        }
                        ps.addBatch();
                    } catch (NoSuchElementException e) {
                        hasMore = false;
                    } catch (Exception e) {
                        // Log the error and try to continue processing remaining entries
                        container.getLogger().error(sm.getString(EXCEPTION_MESSAGE_KEY), e);
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i <= retryCount; i++) {
                    open();
                    try {
                        ps.executeBatch();
                        return;
                    } catch (BatchUpdateException e) {
                        container.getLogger().error(sm.getString(EXCEPTION_MESSAGE_KEY), e);
                        final SQLException next = e.getNextException();
                        if (next != null) {
                            container.getLogger().error(sm.getString(EXCEPTION_MESSAGE_KEY), next);
                        }
                    } catch (SQLException e) {
                        container.getLogger().error(sm.getString(EXCEPTION_MESSAGE_KEY), e);

                        // Close the connection so that it gets reopened next time
                        close();
                    }
                }
            }
        }

    }

    /**
     * Describes the fields of an access log entry.
     */
    static class LogEntry {
        private static final String EMPTY = "";

        private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        private String remoteHost = EMPTY;

        private String user;

        private String query = EMPTY;

        private int status;

        private long bytes;

        private String virtualHost = EMPTY;

        private String method = EMPTY;

        private String referer;

        private String userAgent;

        public Timestamp getTimestamp() {
            return this.timestamp;
        }

        public String getRemoteHost() {
            return remoteHost;
        }

        public void setRemoteHost(final String remoteHost) {
            this.remoteHost = remoteHost;
        }

        public String getUser() {
            return user;
        }

        public void setUser(final String user) {
            this.user = user;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(final String query) {
            this.query = query;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(final int status) {
            this.status = status;
        }

        public long getBytes() {
            return bytes;
        }

        public void setBytes(final long bytes) {
            this.bytes = bytes;
        }

        public String getVirtualHost() {
            return virtualHost;
        }

        public void setVirtualHost(final String virtualHost) {
            this.virtualHost = virtualHost;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(final String method) {
            this.method = method;
        }

        public String getReferer() {
            return referer;
        }

        public void setReferer(final String referer) {
            this.referer = referer;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(final String userAgent) {
            this.userAgent = userAgent;
        }
    }
}


package com.yjy.mysql.driverManager;


import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * copy from @see{org.springframework.jdbc.datasource.AbstractDataSource}
 * Created by yjy on 2017/9/20.
 */
public abstract class AbstractDataSource implements DataSource {

    protected final Logger logger = Logger.getLogger(AbstractDataSource.class);

    public AbstractDataSource() {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLoginTimeout(int timeout) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("getLogWriter");
    }

    public void setLogWriter(PrintWriter pw) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }


}

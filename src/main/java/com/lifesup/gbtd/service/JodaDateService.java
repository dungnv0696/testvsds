package com.lifesup.gbtd.service;

import com.lifesup.gbtd.service.inteface.IDateService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Objects;
import java.util.TimeZone;

final public class JodaDateService implements IDateService {

    private final DateTimeZone timeZone;

    /**
     * Force system-wide timezone to ensure consistent
     * dates over all servers, independently from the region
     * the server is running.
     */
    public JodaDateService(final DateTimeZone timeZone) {
        super();
        this.timeZone = Objects.requireNonNull(timeZone);

        System.setProperty("user.timezone", timeZone.getID());
        TimeZone.setDefault(timeZone.toTimeZone());
        DateTimeZone.setDefault(timeZone);
    }

    @Override
    public DateTime now() {
        return DateTime.now(timeZone);
    }
}

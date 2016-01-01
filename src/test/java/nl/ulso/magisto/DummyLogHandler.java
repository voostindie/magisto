package nl.ulso.magisto;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

class DummyLogHandler extends Handler {

    private static DummyLogHandler handler;

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void publish(LogRecord record) {
        builder.append(getFormatter().formatMessage(record));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public static String getLog() {
        return handler.builder.toString();
    }

    public static void install() {
        handler = new DummyLogHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("%s%n", record.getMessage());
            }
        });
        Logger.getLogger("").addHandler(handler);

    }

    public static void uninstall() {
        Logger.getLogger("").removeHandler(handler);
    }
}

import 'dart:developer' as dev;

enum LogSeverity { debug, info, error }

class Logger {
  static void log(String message, {LogSeverity severity = LogSeverity.info, Object? error, StackTrace? stackTrace}) {
    final tag = severity.name.toUpperCase();
    final logMessage = '[$tag] $message';

    if (severity == LogSeverity.error) {
      dev.log(logMessage, name: 'APP_AUDIT', error: error, stackTrace: stackTrace);
      print(logMessage); // Also print for console visibility in some environments
    } else {
      dev.log(logMessage, name: 'APP_AUDIT');
      print(logMessage);
    }
  }

  static void debug(String message) => log(message, severity: LogSeverity.debug);
  static void info(String message) => log(message, severity: LogSeverity.info);
  static void error(String message, [Object? error, StackTrace? stackTrace]) =>
      log(message, severity: LogSeverity.error, error: error, stackTrace: stackTrace);
}

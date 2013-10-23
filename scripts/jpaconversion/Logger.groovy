/*
 This class is simply for logging basic messages.
 */
public class Logger {
	def logFile
	def lineNumber = 1

	def Logger() {
		this("errors.log")
	}

	def Logger(file) {
		logFile = new File(file)
		if (logFile.exists()) {
			logFile.delete()
		}
	}

	def log(message) {
		logFile << "${lineNumber}) ${message}"
		logFile << "\n"
		lineNumber++
	}
}

# Setup Instructions

## Prerequisites

1. **Java 11 or higher** - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
2. **Maven 3.6+** - Download from [Apache Maven](https://maven.apache.org/download.cgi)

## Environment Setup

### Windows

1. Set `JAVA_HOME` environment variable:
   ```powershell
   [System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-11", "User")
   ```

2. Add Maven to PATH:
   ```powershell
   [System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\apache-maven-3.9.0\bin", "User")
   ```

### Verify Installation

```bash
java -version
mvn -version
```

## Building the Project

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Run with a dataset
mvn exec:java -Dexec.mainClass="Main" -Dexec.args="data/small1.json"
```

## Git Repository Setup

```bash
# Initialize git repository
git init

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit: Smart City Scheduling Assignment 4"

# Add remote repository (replace with your GitHub URL)
git remote add origin https://github.com/yourusername/daa-4.git

# Push to GitHub
git push -u origin main
```

## Alternative: Run without Maven

If Maven is not available, compile manually:

```bash
# Download Gson JAR from https://mvnrepository.com/artifact/com.google.code.gson/gson/2.10.1
# Download JUnit JARs from https://mvnrepository.com/artifact/junit/junit/4.13.2

# Compile
javac -cp "gson-2.10.1.jar" -d target/classes src/main/java/**/*.java

# Run
java -cp "target/classes;gson-2.10.1.jar" Main data/small1.json
```

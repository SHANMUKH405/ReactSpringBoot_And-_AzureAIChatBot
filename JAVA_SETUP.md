# ☕ Java Setup Guide

Your project needs **Java 17 or higher**, but you currently have Java 8.

---

## 🚀 Quick Install (Recommended)

### Option 1: Using Homebrew (Easiest)

If you have Homebrew installed:

```bash
# Install Java 17 (Temurin - OpenJDK)
brew install openjdk@17

# Link it so it's available system-wide
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Set JAVA_HOME (add to ~/.zshrc)
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"' >> ~/.zshrc

# Reload shell configuration
source ~/.zshrc

# Verify installation
java -version
```

You should see: `openjdk version "17.x.x"`

---

### Option 2: Download from Oracle/Temurin

1. **Download Java 17**:
   - **Temurin (Recommended)**: https://adoptium.net/temurin/releases/?version=17
   - **Oracle**: https://www.oracle.com/java/technologies/downloads/#java17

2. **Install the .dmg file**:
   - Double-click the downloaded file
   - Follow installation wizard

3. **Verify installation**:
   ```bash
   /usr/libexec/java_home -V
   java -version
   ```

4. **Set JAVA_HOME** (if needed):
   ```bash
   # Add to ~/.zshrc
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   export PATH="$JAVA_HOME/bin:$PATH"
   
   # Reload
   source ~/.zshrc
   ```

---

### Option 3: Using SDKMAN (Java Version Manager)

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash

# Reload shell
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 17
sdk install java 17.0.9-tem

# Set as default
sdk default java 17.0.9-tem

# Verify
java -version
```

---

## ✅ After Installation

Verify Java 17 is installed:

```bash
java -version
```

You should see something like:
```
openjdk version "17.0.9" 2023-10-17
OpenJDK Runtime Environment Temurin-17.0.9+9 (build 17.0.9+9)
OpenJDK 64-Bit Server VM Temurin-17.0.9+9 (build 17.0.9+9, mixed mode, sharing)
```

---

## 🔧 If Multiple Java Versions

If you have multiple Java versions:

```bash
# List all installed versions
/usr/libexec/java_home -V

# Switch to Java 17 for current session
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

# Make it permanent (add to ~/.zshrc)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

---

## 🎯 Next Steps

After installing Java 17:

1. **Verify installation**:
   ```bash
   java -version  # Should show Java 17+
   ```

2. **Continue with QUICK_START.md**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

---

## 🐛 Troubleshooting

**Problem**: `java -version` still shows Java 8

**Solution**:
```bash
# Check which Java is being used
which java

# Set JAVA_HOME explicitly
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java -version
```

**Problem**: Command not found after installation

**Solution**:
- Make sure you've added Java to PATH
- Restart your terminal
- Check installation location: `/Library/Java/JavaVirtualMachines/`

---

## 💡 Quick Check

Run this command to see all installed Java versions:

```bash
/usr/libexec/java_home -V
```

If you see Java 17 in the list, you're good to go! 🎉

---

**Once Java 17 is installed, you can continue with the project setup!**


### Effective From: Sep 1, 2025 

Microblink is moving its Android SDK distribution from a custom Maven repository ([Microblink Maven](https://maven.microblink.com/)) to **Maven Central** ([Maven Central: Search](https://central.sonatype.com/search?q=com.microblink.blinkreceipt)).

### What’s Changing?

#### Old Setup (Before Migration):
```groovy
repositories {
    maven { url "https://maven.microblink.com" }
}
dependencies {
    implementation 'com.microblink.blinkreceipt:blinkreceipt-bom:[version]'
}
```

#### New Setup (After Migration):
```groovy
repositories {
    mavenCentral()
}
dependencies {
    implementation 'com.microblink.blinkreceipt:blinkreceipt-bom:[version]'
}
```
>
> 🔁 Only the repository source is changing. The SDK package name and usage remain the same.
>

### Step-by-Step Migration Instructions

#### 1. Remove the Custom Microblink Maven Repository
In your project’s **build.gradle** (Project-level or Module-level), remove:
```groovy
maven { url "https://maven.microblink.com" }
```

#### 2. Ensure Maven Central is Declared
If not already present, add this to your repositories block:
```groovy
repositories {
    mavenCentral()
}
```
>
> ℹ️ If you’re using google() make sure mavenCentral() is included as well.
>

#### 3. Keep Your Existing Dependency Declaration
No changes are required to the dependency itself. You can continue using:
```groovy
implementation 'com.microblink.blinkreceipt:blinkreceipt:[version]'
```
Replace [version] with the version you need. You can browse available versions here: [Maven Central: Search](https://central.sonatype.com/search?q=com.microblink.blinkreceipt)

### Post-Migration Checklist
- Removed the custom [Maven Microblink](https://maven.microblink.com/) repository
- Added or verified mavenCentral() is present
- Synced Gradle and verified no build errors
- Checked that the correct SDK version is downloaded from Maven Central

### FAQ

#### Q: Do I need to change the SDK version number?
- **A:** No, unless you’re upgrading to a newer version. The same artifact and versioning are used.

#### Q: What if I’m using a private or enterprise build system (e.g., Artifactory)?
- **A:** Ensure your internal repository proxies Maven Central. Contact your DevOps team if needed.

#### Q: Will older versions be available on Maven Central?
- **A:** Only selected versions are hosted. It’s recommended to use the latest version available on Maven Central.

# Permanent release signing for Learn-FR-DA

## Purpose

From version 0.5.2 onward, production APKs should be signed with one permanent release key. Android only accepts an APK as an update when the application ID and signing identity match the installed app.

Application ID: `dk.nenolink.learnfrda`

The keystore and passwords must never be committed to this public repository.

## Local key creation

Create the key once on Henrik's Windows computer and keep it permanently.

Recommended location outside the Git repository:

`C:\Users\henri\Nenolink-keys\nenoling-release.jks`

Use Android Studio or the JDK `keytool` to create a Java KeyStore. Suggested alias:

`nenoling-release`

Example command from a terminal where `keytool` is available:

```text
keytool -genkeypair -v -keystore C:\Users\henri\Nenolink-keys\nenoling-release.jks -alias nenoling-release -keyalg RSA -keysize 4096 -validity 10000
```

Choose strong passwords and store them in a password manager. Do not paste the passwords into source files, Git commits, issues or chat logs.

## Backup

Keep at least two secure copies of the `.jks` file in separate locations. Losing the signing key can prevent future APKs from updating installations signed with that key.

## Local release build

The release build should read signing values from local Gradle properties or environment variables, never from tracked files. The intended variables are:

- `NENOLING_KEYSTORE_FILE`
- `NENOLING_KEYSTORE_PASSWORD`
- `NENOLING_KEY_ALIAS`
- `NENOLING_KEY_PASSWORD`

The repository will only enable release signing when all required values are present.

## GitHub Actions later

For automated signed releases, encode the keystore and store it plus the passwords as GitHub Actions secrets. Do not upload the raw keystore to the repository. A dedicated release workflow can reconstruct the keystore during the job, sign the APK and remove the temporary file afterward.

## Migration rule

The first APK signed with this permanent key establishes the signing identity for this release line. All later production updates must use the same key.

Debug/test APKs may still use debug signing, but they must not be treated as upgrade-compatible production releases.

# Permanent release signing for Learn-FR-DA

## Purpose

From version 0.5.2 onward, production APKs are signed with one permanent release key. Android only accepts an APK as an update when the application ID and signing identity match the installed app.

Application ID: `dk.nenolink.learnfrda`

The keystore and passwords must never be committed to this public repository.

## Reference signing identity

The physically tested 0.5.2 release establishes the permanent signing identity for the Learn-FR-DA release line.

- Version: `0.5.2`
- Version code: `7`
- Key alias: `nenoling-release`
- Certificate SHA-256: `69:41:9B:98:62:FA:E6:CA:38:0E:52:CB:ED:85:B5:2C:F1:68:69:04:7B:E1:74:3A:52:84:D9:47:B6:2B:0A:08`
- Signature algorithm: `SHA256withRSA`
- Public key: `2048-bit RSA`

The certificate fingerprint above is public verification data. Passwords and the `.jks` file remain private.

## Local key location

Keep the permanent key outside the Git repository, for example:

`C:\Users\henri\Nenolink-keys\nenoling-release.jks`

Alias:

`nenoling-release`

Choose strong passwords and store them in a password manager. Do not paste the passwords into source files, Git commits, issues or chat logs.

## Backup

Keep at least two secure copies of the `.jks` file in separate locations. Losing the signing key can prevent future APKs from updating installations signed with that key.

## Local release build

The release build reads signing values from local Gradle properties or environment variables, never from tracked files. The supported variables are:

- `NENOLING_KEYSTORE_FILE`
- `NENOLING_KEYSTORE_PASSWORD`
- `NENOLING_KEY_ALIAS`
- `NENOLING_KEY_PASSWORD`

The repository only enables release signing when all required values are present.

## APK verification

A signed APK can be checked with Android SDK Build Tools:

```text
apksigner verify --print-certs app-release.apk
```

The signer certificate SHA-256 digest must match the reference fingerprint above.

## GitHub Actions later

For automated signed releases, encode the keystore and store it plus the passwords as GitHub Actions secrets. Do not upload the raw keystore to the repository. A dedicated release workflow can reconstruct the keystore during the job, sign the APK and remove the temporary file afterward.

## Migration rule

Version 0.5.2 is the first permanent-signing reference release. All later production updates must use the same key and application ID.

Debug/test APKs may still use debug signing, but they must not be treated as upgrade-compatible production releases.

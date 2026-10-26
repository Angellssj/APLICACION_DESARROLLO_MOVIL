#!/usr/bin/env bash
set -euo pipefail
export JAVA_HOME=/workspace/jdk-21
export ANDROID_HOME=/workspace/android-sdk
export ANDROID_USER_HOME=/workspace/.android
export GRADLE_USER_HOME=/workspace/.gradle
mkdir -p /workspace/downloads "$ANDROID_HOME" "$ANDROID_USER_HOME" "$GRADLE_USER_HOME"
if [ ! -x "$JAVA_HOME/bin/javac" ]; then
  curl -fsSL 'https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.9%2B10/OpenJDK21U-jdk_x64_linux_hotspot_21.0.9_10.tar.gz' -o /workspace/downloads/jdk.tar.gz
  echo '810d3773df7e0d6c4394e4e244b264c8b30e0b05a0acf542d065fd78a6b65c2f  /workspace/downloads/jdk.tar.gz' | sha256sum -c -
  mkdir -p "$JAVA_HOME"
  tar -xzf /workspace/downloads/jdk.tar.gz --strip-components=1 -C "$JAVA_HOME"
fi
cp /etc/ssl/certs/java/cacerts "$JAVA_HOME/lib/security/cacerts"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
if [ ! -x "$ANDROID_HOME/cmdline-tools/bin/sdkmanager" ]; then
  curl -fsSL https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip -o /workspace/downloads/android-tools.zip
  echo '5fdcc763663eefb86a5b8879697aa6088b041e70  /workspace/downloads/android-tools.zip' | sha1sum -c -
  unzip -q /workspace/downloads/android-tools.zip -d "$ANDROID_HOME"
fi
python3 - <<'PY'
import os,pathlib,urllib.parse
p=urllib.parse.urlparse(os.environ.get('HTTPS_PROXY',''))
f=pathlib.Path('/workspace/.gradle/gradle.properties')
lines=f.read_text().splitlines() if f.exists() else []
keys=['systemProp.'+s+'.proxy'+x for s in ['http','https'] for x in ['Host','Port']]
lines=[l for l in lines if not any(l.startswith(k+'=') for k in keys)]
if p.hostname:
    if p.username or p.password: raise RuntimeError('Authenticated proxy needs supported credential configuration')
    for s in ['http','https']:
        lines.extend([f'systemProp.{s}.proxyHost={p.hostname}',f'systemProp.{s}.proxyPort={p.port or 80}'])
f.write_text('\n'.join(lines)+'\n')
PY
python3 - <<'PY'
import os,subprocess,urllib.parse
p=urllib.parse.urlparse(os.environ.get('HTTPS_PROXY',''))
e=dict(os.environ)
if p.hostname:
    opts=' '.join(f'-D{s}.proxyHost={p.hostname} -D{s}.proxyPort={p.port or 80}' for s in ['http','https'])
    e['JAVA_TOOL_OPTIONS']=(e.get('JAVA_TOOL_OPTIONS','')+' '+opts).strip()
r=subprocess.run(['/workspace/android-sdk/cmdline-tools/bin/sdkmanager','--sdk_root=/workspace/android-sdk','platforms;android-36.1','build-tools;36.0.0','platform-tools'],env=e,input='y\n'*100,text=True)
raise SystemExit(r.returncode)
PY
cd /workspace/APLICACION_DESARROLLO_MOVIL
bash gradlew :app:dependencies --configuration debugRuntimeClasspath --max-workers=2 --console=plain

### gradlew 사용

```shell
$ chmod 744 gradlew
$ ./gradlew signingReport
```

### dev

```shell
$ keytool -list -v -alias androiddebugkey -keystore ./keys/debug.keystore -storepass android -keypass android
    별칭 이름: androiddebugkey
    생성 날짜: Oct 24, 2019
    항목 유형: PrivateKeyEntry
    인증서 체인 길이: 1
    인증서[1]:
    소유자: C=US, O=Android, CN=Android Debug
    발행자: C=US, O=Android, CN=Android Debug
    일련 번호: 1
    적합한 시작 날짜: Thu Oct 24 17:48:46 KST 2019 종료 날짜: Sat Oct 16 17:48:46 KST 2049
    인증서 지문:
         MD5:  9F:19:67:A6:C8:B5:03:35:D0:07:24:48:3E:B2:D0:7E
         SHA1: 38:C2:E6:11:88:7D:2E:45:FB:D9:A2:DB:7E:F0:FD:A9:48:64:69:AA
         SHA256: 2F:6D:15:A8:06:02:79:EE:03:EB:EB:E0:26:CA:65:52:72:3C:C1:4D:CF:2A:B7:17:35:A3:5B:CC:B9:E2:7E:FB
    서명 알고리즘 이름: SHA1withRSA
    주체 공용 키 알고리즘: 2048비트 RSA 키
    버전: 1
$ keytool -exportcert -alias androiddebugkey -keystore ./keys/debug.keystore -storepass android -keypass android | openssl sha1 -binary | openssl base64
    OMLmEYh9LkX72aLbfvD9qUhkaao=
```

### release

```shell
$ keytool -list -v -alias breakout_release_key -keystore ./keys/breakout.keystore -storepass breakout2019 -keypass breakout2019
    별칭 이름: breakout_release_key
    생성 날짜: Jan 14, 2020
    항목 유형: PrivateKeyEntry
    인증서 체인 길이: 1
    인증서[1]:
    소유자: CN=SungGue Choi, L=Korea
    발행자: CN=SungGue Choi, L=Korea
    일련 번호: 4be546fd
    적합한 시작 날짜: Tue Jan 14 15:22:41 KST 2020 종료 날짜: Wed Dec 21 15:22:41 KST 2118
    인증서 지문:
             MD5:  89:EE:F0:EA:5E:82:24:F1:3D:2A:F6:B3:83:DC:A7:AB
             SHA1: 90:C3:B6:E0:B1:88:75:D2:57:06:FA:A0:4D:73:E1:C7:09:1E:25:B7
             SHA256: 43:98:93:82:36:30:14:26:49:B7:88:83:D0:D8:BD:E6:60:5F:81:2B:9C:A6:6D:B7:6A:BA:03:5F:BC:AA:56:94
    서명 알고리즘 이름: SHA256withRSA
    주체 공용 키 알고리즘: 2048비트 RSA 키
    버전: 3
$ keytool -exportcert -alias breakout_release_key -keystore ./keys/breakout.keystore -storepass breakout2019 -keypass breakout2019 | openssl sha1 -binary | openssl base64
    kMO24LGIddJXBvqgTXPhxwkeJbc=
```

### playstore

```shell
    인증서 지문:
         MD5: 6A:97:8E:0C:23:DD:34:84:A7:6E:F7:C5:FE:45:F4:B4
         SHA1: 12:96:23:86:E6:96:B9:ED:81:52:09:E9:21:23:24:57:BD:C0:DA:CD
         SHA256: CA:30:66:D9:5A:48:27:E6:83:AB:4D:94:F1:95:82:C3:CC:52:64:14:BC:4B:4A:7D:9A:5C:6A:76:15:0C:9E:01
     key_hash: EpYjhuaWue2BUgnpISMkV73A2s0=
     
$ echo "12:96:23:86:E6:96:B9:ED:81:52:09:E9:21:23:24:57:BD:C0:DA:CD" | xxd -r -p | openssl base64
EpYjhuaWue2BUgnpISMkV73A2s0=
```

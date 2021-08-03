# android library


### 프로젝트 구조
- :file_folder:`guelib` :  라이브러리 모듈
- :file_folder:`gueSample` : 샘플 앱 모듈
- :file_folder:`keys` : 안드로이드 서명 관련 파일
  - `keys/keystore.properties` : keystore 설정


### 프로젝트 설정
- git clone
  ```shell
  # clone main-project
  $ git clone https://github.com/user/main-module.git
  $ cd main-module

  # add sub-module
  $ git submodule add https://github.com/sung-gue/gueLibrary.git gueLibrary

  # set sparse-checkout
  $ cd gueLibrary
  $ git sparse-checkout init
  $ git sparse-checkout set guelib
  $ git sparse-checkout reapply
  ```


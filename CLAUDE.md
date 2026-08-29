# StreamCompass

Kotlin Multiplatform(Compose Multiplatform, Android + JVM Desktop) 스트리밍 탐색 앱. Clean Architecture 3-module 구조(`:architecture:domain` / `:architecture:data` / `:architecture:presentation`) + `:core`(공용 유틸) + `:app:shared`/`:app:androidApp`/`:app:desktopApp`.

## 이 프로젝트에서 작업할 때 먼저 읽을 것

- **`README.md`** — 모듈 경계·의존성 규칙·데이터 흐름·설계 결정·알려진 제약사항을 정리한 최신 문서. `doc/handoff.md`/`doc/architecture.md`는 삭제되었고 그 내용은 여기로 이관되었다.

## 핵심 컨벤션

- Kotlin 함수 호출은 항상 **named argument**로 작성한다.
- 과도한 추상화·방어 코드를 추가하지 않는다(YAGNI). 실제로 반복되는 부분만 공용화하고, 사용처마다 다른 로직(예: 특정 화면 전용 empty-state 문구)까지 억지로 공용 컴포저블에 밀어넣지 않는다.
- Firebase API Key 등 secret은 git-tracked 파일에 하드코딩하지 않는다 — `local.properties` + Gradle codegen 패턴을 따른다.
- Room DB는 아직 버전 1(마이그레이션 인프라 없음) — 스키마를 바꿔야 하면 `README.md`의 "설계 결정"을 먼저 확인한다.

## 빌드/환경 참고

- WSL 환경에서 Kotlin 증분 컴파일 캐시가 가끔 깨져 `Expected absolute path but found relative path` 에러가 난다. 코드 문제가 아니라 캐시 문제이므로 `--rerun-tasks`로 재시도하면 해결된다.
- 빌드 확인은 최소한 `:architecture:domain:build`, `:architecture:data:build`, `:architecture:presentation:build`, `:app:shared:build`, `:app:desktopApp:build`(가능하면 `:app:androidApp:assembleDebug`까지)로 전체 체인을 확인한다.

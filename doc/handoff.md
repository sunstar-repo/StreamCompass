# 작업 이력 & 이어서 진행하기 (Session Handoff)

## 먼저 알아둘 것: 코드는 이미 git에 전부 있습니다

이 저장소는 **매 작업 단위로 커밋되고 있고, `origin/main`(GitHub: `sunstar-repo/StreamCompass`)까지 이미 push되어 있습니다.** 즉 코드 상태 자체는 `git log`/`git diff`만으로 어떤 계정·머신에서도 그대로 복원됩니다 — 이 문서를 코드 대신 쓸 필요는 없습니다.

이 문서가 대신하는 것은 **diff만 봐서는 안 보이는 것들**입니다: 왜 이렇게 결정했는지, 어떤 접근을 시도했다가 되돌렸는지, 뭐가 아직 안 끝났는지.

## 다른 Claude 계정에서 이어가는 법

1. 이 저장소를 clone(또는 이미 clone된 상태면 `git pull`) — `git@github.com:sunstar-repo/StreamCompass.git`, branch `main`.
2. 그 워킹 디렉토리 안에서 새 Claude Code 세션을 시작합니다 (계정은 무관 — 프로젝트 루트의 `CLAUDE.md`는 계정이 아니라 이 디렉토리에 묶여서 자동으로 로드됩니다).
3. 세션 시작 시 `CLAUDE.md`가 자동 로드되므로 별다른 설명 없이도 컨벤션은 파악되지만, 지금까지의 맥락(왜/무엇이 남았는지)이 필요하면 `doc/handoff.md`(이 문서)를 읽어달라고 한 번 요청하세요.
4. Claude Code의 대화 기록·auto-memory(`~/.claude/projects/.../memory/`)는 계정+머신+프로젝트 경로에 묶여 있어 **자동으로 넘어가지 않습니다.** 이 두 파일(`CLAUDE.md` + `doc/handoff.md`)이 그 공백을 메우는 용도입니다.

## 최근 커밋 흐름 (최신순, 참고용 — 정확한 diff는 `git log`/`git show` 참고)

```
#14 scroll 시 scaffold show or hide         Home/Movie/Tv 스크롤 시 top bar 자동 숨김
#13 Home - History Row 추가                  Movie/Tv History 테이블+화면+long-press 삭제
#12 Movie / Tv Tab Row 구성                  Movie/Tv 화면에 4개 카테고리 row
#11 Home Trending Row 추가                   Home Trending Carousel 최초 구현
#10 class renaming / Image load 안되는 현상   StreamDetail→Detail 리네이밍, backdrop url 버그
#9  Scaffold 활성/비활성 기능 추가
#8  SA Deeplink schema 개선
#6  Screen Navigation 추가
#3  Firebase Remote Config 추가
#1  Firebase(Firestore) DataSource 추가
#5  Local Caching Repository 추가 (Room)
#2  StreamAvailability API 추가
```

## 현재 화면 구조

- **Home** — Trending Carousel(`HorizontalCenteredHeroCarousel`, TMDB `/trending/all/day` movie+tv 혼합) + Movie History row + Tv History row. 각 row는 비어 있으면(로딩 중/이력 없음) 별도 placeholder를 보여줌.
- **Movie** — NowPlaying/Popular/TopRated/Upcoming 4개 poster row (paging).
- **Tv** — AiringToday/OnTheAir/Popular/TopRated 4개 backdrop row (paging).
- **Detail** — Movie만 지원. Tv 상세 화면이 없어서 Tv 아이템은 Home Carousel/Tv row/Tv History 어디서도 클릭 비활성(단, History long-press 삭제는 Tv도 가능).

## 주요 결정 사항과 이유

1. **History 테이블 설계** — `movie_history`/`tv_history` 두 테이블에 `Stream.MovieStream`/`TvStream` 필드를 그대로(+`visitedAt`) 저장. 기존 `movie_detail`/`tv_detail` 캐시와 조인하지 않음 — "detail 캐싱 로직이 완전히 구분되는 게 낫다"는 사용자 판단에 따른 설계. person 전용 테이블은 없음 — person 상세로 진입하는 경로 자체가 앱에 없어서 대상이 아님.
2. **Room DB version은 1 유지** — 아직 앱 미출시라 마이그레이션 인프라가 없음. `fallbackToDestructiveMigration`도 넣지 않기로 함(사용자 명시). 스키마 변경 시 기존 로컬 DB가 깨질 수 있는데, 현재는 감수하기로 한 상태.
3. **`presentation/core` 패키지** (`Constants.kt`, `Compose.kt`) — Movie/Tv/Home에서 반복되던 Row/Column을 `MediaRow`/`PosterCard`/`BackdropCard`로 통합. 단, **History 전용 empty-state 문구 같은 로직은 공용 `MediaRow`에 넣지 않고 `HomeScreen`에만 로컬로 둠** — "history에만 해당하는 사항을 공용 컴포저블에 넣는 건 부적절하다"는 사용자 피드백에 따른 경계.
4. **`HomeViewModel`을 MVI 패턴으로 전환** — `Event`/`Channel`/`runningFold` 구조. Room Flow(영화/TV 히스토리 관찰)를 `merge()`로 이벤트 스트림에 합쳐서, DB 변경이 생기면 자동으로 상태가 갱신되도록 함.
5. **Carousel 사이징** — 여러 번 시행착오 끝에 최종적으로 `height` 200dp 고정 + `maxItemWidth = height × 16/9`로 selected(hero) item의 width를 16:9 비율로 맞춤. `minSmallItemWidth`/`maxSmallItemWidth`를 24dp로 낮춰 중앙 정렬 여지를 최대한 늘렸지만, **좁은 폰(360dp대)에서는 여전히 완전한 대칭 중앙 정렬이 안 될 수 있음** — hero 폭(약 356dp) + peeking 2×24dp + spacing이 화면폭을 초과하는 경우가 있는 물리적 한계, 알려진 이슈로 남겨둠.
6. **Top bar auto-hide** — `ScrollState`를 각 화면 내부가 아니라 `MainScreen`으로 hoist(Home/Movie/Tv 각각 별도 인스턴스 → 탭 전환해도 스크롤 위치 유지). 현재 탭의 `scrollState.value == 0`이 아니면 top bar를 숨김.

## 미해결·보류 항목

- **JVM(Desktop) Firebase Remote Config 미초기화** — Android는 `google-services.json`으로 자동 초기화되지만 JVM은 `FirebasePlatform` 수동 초기화 + Firebase **Web app** 등록이 필요한데 아직 없음. 사용자가 "나중에 하자"로 명시적으로 보류한 상태 — 재개하려면 콘솔에서 Web app 등록 후 설정값을 받아야 함.
- **`doc/architecture.md`가 2026-08-05 기준으로 멈춰 있음** — Movie/Tv/Home tab, History 기능, `presentation/core` 등 최근 작업이 전혀 반영되지 않았음. 다음에 갱신 필요.
- **Tv Detail 화면 없음** — `RemoveTvHistoryUseCase` 등 관련 인프라는 이미 준비돼 있어서, Tv Detail 화면만 추가되면 바로 연결 가능한 상태.
- **Carousel 좁은 화면 중앙 정렬 한계** — 위 "주요 결정 사항 5" 참고. height/width 고정값을 조정하지 않는 한 근본 해결은 안 됨.

## 컨벤션 / 사용자 선호 (반복해서 지적받은 것들)

- Kotlin 함수 호출은 항상 **named argument**로.
- 과도한 추상화·방어 코드 금지 — 딱 필요한 만큼만(YAGNI). 공용화는 실제로 반복되는 부분만, 사용처가 다른 로직(예: history의 empty 문구)까지 억지로 공용 컴포저블에 넣지 않음.
- Firebase/API Key 등 secret은 git-tracked 파일에 하드코딩 금지 — `local.properties` + Gradle codegen 패턴.
- WSL에서 Kotlin 증분 컴파일 캐시가 가끔 깨져 `Expected absolute path but found relative path` 에러가 남 — 코드 문제가 아니라 캐시 문제이므로 `--rerun-tasks`로 재시도하면 해결됨.

# Changelog

All changes to this project will be documented in this file.

## [1.5.1] - 2024-07-12

- Release `MediaSession` when player is released
- Fix `isMuted` and `volume` API
- Rename `videoId` to `mediaId` in `VideoOptions`
- Simplify controller constructors
- Use /session endpoint for private live stream
- Tests: fix failed due to emulator timeout

## [1.5.0] - 2023-11-09

- Add a `compose-player` module to use the player with Jetpack Compose
- Add a specific component to hande full screen for view-based usage:
  see `ApiVideoPlayerFullScreenController`.
- Add an API to set/get how the video is displayed in its parent view: see `viewFit`.
- Move to gradle 8
- Upgrade to Kotlin 1.9

## [1.4.2] - 2023-10-09

- Add `isLive` API
- Fix a crash on ExoPlayer Analytics when position < 0.0f

## [1.4.1] - 2023-09-27

- Fix a crash with parsing of URL on old Android versions due to group named capture

## [1.4.0] - 2023-09-27

- Add support for Android 21 to 34
- Migrate ExoPlayer to `media3`

## [1.3.1] - 2023-07-24

- Upgrade dependencies
- Fix obfuscation on release builds by updating Kotlin serialization plugin

## [1.3.0] - 2023-06-05

- Add API to set playback speed
- Add API to use an api.video URL instead of a video ID (see `VideoOptions.fromUrl`)

## [1.2.0] - 2023-02-07

- Add support for live streaming
- Remove call to `player.json` endpoint
- Implements new VOD private video mechanism
- Add API for media controls and notifications
- Add extensions to set up videos for several Android players: ExoPlayer, MediaPlayer, VideoView.
- Fix 400 error on analytics

## [1.1.5] - 2022-12-12

- `ApiVideoPlayerController.duration` returns `0` when video is not loaded

## [1.1.4] - 2022-11-23

- Fix the display of the fullscreen button
- The `ApiVideoPlayerController.Listener` is not mandatory to simplify usage

## [1.1.3] - 2022-11-03

- Add an API to loop video. See `ApiVideoPlayerController.isLooping`.
- Add an API to automatically play video once it has been loaded.
  See `ApiVideoPlayerController.autoplay`.

## [1.1.2] - 2022-10-27

- Add an API to get the video size: see `videoSize`.
- Add an API to get player state: see `isPlaying`.
- Add an API to seek from an offset.

## [1.1.1] - 2022-10-21

- Add the `ApiVideoPlayerController` that takes a `Surface` as a parameter.

## [1.1.0] - 2022-10-03

- Split `ApiVideoPlayer` in 2 classes: `ApiVideoExoPlayerView` for player view
  and `ApiVideoPlayerController` for technical.
- Externalize analytics in a specific analytics module

## [1.0.0] - 2022-05-23

- First version

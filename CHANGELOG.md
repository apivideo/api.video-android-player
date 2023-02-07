# Changelog

All changes to this project will be documented in this file.

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

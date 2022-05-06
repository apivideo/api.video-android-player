[![badge](https://img.shields.io/twitter/follow/api_video?style=social)](https://twitter.com/intent/follow?screen_name=api_video)
&nbsp; [![badge](https://img.shields.io/github/stars/apivideo/api.video-android-player?style=social)](https://github.com/apivideo/api.video-android-player)
&nbsp; [![badge](https://img.shields.io/discourse/topics?server=https%3A%2F%2Fcommunity.api.video)](https://community.api.video)
![](https://github.com/apivideo/API_OAS_file/blob/master/apivideo_banner.png)
<h1 align="center">api.video Android player</h1>

[api.video](https://api.video) is the video infrastructure for product builders. Lightning fast
video APIs for integrating, scaling, and managing on-demand & low latency live streaming features in
your app.

# Table of contents

- [Table of contents](#table-of-contents)
- [Project description](#project-description)
- [Getting started](#getting-started)
    - [Installation](#installation)
        - [Gradle](#gradle)
    - [Permissions](#permissions)
    - [Code sample](#code-sample)
- [Sample application](#sample-application)
- [Documentation](#documentation)
- [Dependencies](#dependencies)

# Project description

Easily integrate a video player for videos from [api.video](https://api.video) in your Android
application.

# Getting started

## Installation

### Gradle

In your module `build.gradle`, add the following code in `dependencies`:

```groovy
dependencies {
    implementation 'video.api:android-player:1.0.0'
}
```

## Permissions

In your `AndroidManifest.xml`, add the following code in `<manifest>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Code sample

1. Add a `StyledPlayerView` to your Activity/Fragment layout:

```xml
<com.google.android.exoplayer2.ui.StyledPlayerView
    android:id="@+id/playerView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:show_subtitle_button="true" />
```

You can customize the `StyledPlayerView` according to your application design.

2. Implements the `Player.Listener` interface:

```kotlin
val playerListener = object : ApiVideoPlayer.Listener {
    override fun onError(error: Exception) {}
    override fun onReady() {}
}
```

3. Instantiate the `ApiVideoPlayer` in an your Activity/Fragment:

```kotlin
val playerView = findViewById<StyledPlayerView>(R.id.playerView)

val player = ApiVideoPlayer(
    this,
    "YOUR_VIDEO_ID",
    VideoType.VOD,
    playerListener,
    playerView
)
```

# Sample application

A demo application demonstrates how to use player.
See [`/example`](https://github.com/apivideo/api.video-android-player/tree/main/example)
folder.

# Documentation

* [API documentation](https://apivideo.github.io/api.video-android-player/)
* [api.video documentation](https://docs.api.video)

# Dependencies

We are using external library

| Plugin | README |
| ------ | ------ |
| [Exoplayer](https://github.com/google/ExoPlayer) | [README.md](https://github.com/google/ExoPlayer#readme) |

# FAQ

If you have any questions, ask us here: [https://community.api.video](https://community.api.video).
Or use [Issues].

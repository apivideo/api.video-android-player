[![badge](https://img.shields.io/twitter/follow/api_video?style=social)](https://twitter.com/intent/follow?screen_name=api_video)
&nbsp; [![badge](https://img.shields.io/github/stars/apivideo/api.video-android-player?style=social)](https://github.com/apivideo/api.video-android-player)
&nbsp; [![badge](https://img.shields.io/discourse/topics?server=https%3A%2F%2Fcommunity.api.video)](https://community.api.video)
![](https://github.com/apivideo/.github/blob/main/assets/apivideo_banner.png)
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
    - [Permissions](#permissions)
    - [Retrieve your video Id](#retrieve-your-video-id)
    - [Usage](#usage)
    - [Play your api.video video with ExoPlayer, MediaPlayer and VideoView](#play-your-apivideo-video-with-exoplayer-mediaplayer-and-videoview)
    - [Player views](#player-views)
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
    implementation 'video.api:android-player:1.4.2'
}
```

## Retrieve your video Id

At this point, you must have uploaded a least one video to your account. If you haven't
see [how to upload a video](https://docs.api.video/docs/upload-a-video-regular-upload). You'll need
a video Id to use this component and play a video from api.video. To get yours, follow these steps:

1. [Log into your account](https://dashboard.api.video/login) or create
   one [here](https://dashboard.api.video/register).
2. Copy your API key (sandbox or production if you are subscribed to one of
   our [plan](https://api.video/pricing)).
3. Go to [the official api.video documentation](https://docs.api.video/docs).
4. Log into your account in the top right corner. If it's already done, be sure it's the account you
   want to use.
5. Go to API Reference -> Videos -> [List all videos](https://docs.api.video/reference/list-videos)
6. On the right, be sure the "Authentication" section contains the API key you want to use.
7. Generate your upload token by clicking the "Try It!" button in the right section
8. Copy the "videoId" value of one of elements of the response in the right section.

Alternatively, you can find your video Id in the video details of
your [dashboard](https://dashboard.api.video).

## Usage

The api.video Android player will help you to play the HLS video from api.video. It also generates
analytics of [your viewers usage](https://api.video/product/video-analytics/).

1. Add a `ApiVideoExoPlayerView` to your Activity/Fragment layout:

```xml

<video.api.player.ApiVideoExoPlayerView
    android:id="@+id/playerView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:show_fullscreen_button="true"
    app:show_controls="true"
    app:show_subtitles="true" />
```

You can also use an ExoPlayer `StyledPlayerView` or a `SurfaceView` according to your requirements.
See [Player views](#player-views) for more details.

2. Implements the `ApiVideoPlayerController.Listener` interface:

```kotlin
val playerControllerListener = object : ApiVideoPlayerController.Listener {
    override fun onError(error: Exception) {
        Log.e(TAG, "An error happened", error)
    }
    override fun onReady() {
        Log.I(TAG, "Player is ready")
    }
}
```

3. Instantiate the `ApiVideoPlayerController` in an your Activity/Fragment:

```kotlin
val playerView = findViewById<ApiVideoExoPlayerView>(R.id.playerView)

val player = ApiVideoPlayerController(
    applicationContext,
    VideoOptions(
        "YOUR_VIDEO_ID",
        VideoType.VOD
    ), // For private video: VideoOptions("YOUR_VIDEO_ID", VideoType.VOD, "YOUR_PRIVATE_VIDEO_TOKEN")
    playerListener,
    playerView
)
```

4. Fullscreen video

If you requires a fullscreen video. You will have to implement
the `ApiVideoPlayerController.ViewListener` interface.
Check out for the implementation in the [Sample application](#sample-application).

## Play your api.video video with ExoPlayer, MediaPlayer and VideoView

If you want to use the ExoPlayer directly, you can use the api.video Android extensions:

1. Create a video

```kotlin
val videoOptions = VideoOptions("YOUR_VIDEO_ID", VideoType.VOD)
// For private video: VideoOptions("YOUR_VIDEO_ID", VideoType.VOD, "YOUR_PRIVATE_VIDEO_TOKEN")
```

2. Pass it to your player

For ExoPlayer:

```kotlin
val exoplayer = ExoPlayer.Builder(context).build() // You already have that in your code
exoplayer.addMediaSource(videoOptions)
// Or
exoplayer.setMediaSource(videoOptions)
```

For MediaPlayer:

```kotlin
val mediaPlayer = MediaPlayer() // You already have that in your code
mediaPlayer.setDataSource(context, videoOptions)
```

For VideoView:

```kotlin
val videoView = binding.videoView // You already have that in your code
videoView.setVideo(videoOptions)
```

## Player views

The api.video Android player comes with a specific view `ApiVideoExoPlayerView` to display the video
and its controller. If you require a customization of the view such as changing a button color,...,
you can contact [us](https://github.com/apivideo/api.video-android-player/issues).
Otherwise, you can also directly use the ExoPlayer views
from `media3`: [`PlayerView`](https://developer.android.com/reference/androidx/media3/ui/PlayerView).
The `ApiVideoPlayerController` also supports other type of views such
as [`SurfaceView`](https://developer.android.com/reference/android/view/SurfaceView)
and [`Surface`](https://developer.android.com/reference/android/view/Surface) but it requires far
more work.

# Sample application

A demo application demonstrates how to use player.
See [`/example`](https://github.com/apivideo/api.video-android-player/tree/main/example)
folder.

On the first run, you will have to set your video Id:

1. Click on the FloatingActionButton -> Settings
2. Replace the video Id with your own video Id

# Documentation

* [Player documentation](https://apivideo.github.io/api.video-android-player/)
* [api.video documentation](https://docs.api.video)

# Dependencies

We are using external library

| Plugin                                           | README                                                  |
|--------------------------------------------------|---------------------------------------------------------|
| [Exoplayer](https://github.com/google/ExoPlayer) | [README.md](https://github.com/google/ExoPlayer#readme) |

# FAQ

If you have any questions, ask us in the [community](https://community.api.video) or
use [issues](https://github.com/apivideo/api.video-android-player/issues).

## Impergram

based on [TelegramX](https://github.com/TGX-Android/Telegram-X)

## Author:

* [Gleb Obitotsky](https://t.me/oxxximif)

## Build instructions

### Prerequisites

* Repository must be fetched via `git`
* **JDK** or **[Android Studio](https://developer.android.com/studio/)** (with compatible bundled JDK)
* At least **8GB** of RAM
* At least **7,32GB** of free disk space when cloning with `--shallow-submodules --depth=1`
* At least **2-5x** times more disk space for files generated during build process.

#### macOS

* [Homebrew](https://brew.sh)
* git with LFS: `$ brew install git git-lfs && git lfs install`
* JDK: `$ brew install openjdk@25`

#### Ubuntu

* git with LFS: `# apt install git git-lfs`
* Run `$ git lfs install` if you just installed `git-lfs`
* JDK: `# apt install openjdk-25-jdk`
* If multiple JDK versions are installed:<br/>`# update-java-alternatives --list`<br/>`# update-java-alternatives --set java-1.25.0-openjdk-amd64` (or other compatible version)

#### Windows

* [MSYS2](https://www.msys2.org/#installation) (**UCRT64** environment)
* Update packages: `pacman -Syu`
* Run `pacman -S --needed git mingw-w64-ucrt-x86_64-git-lfs perl make diffutils`
* Run `git lfs install`
* Run `git config --global core.longpaths true`
* Clone the repository
* Set `msys2.dir` in `local.properties` after cloning

### Building

1. `$ git clone --recursive https://github.com/oxxx1mif/impergram-x.git tgx`
2. In case you forgot the `--recursive` flag, `cd` into `tgx` directory and run: `$ git submodule update --init --recursive`
3. Open project via **[Android Studio](https://developer.android.com/studio/)** or build manually from the command line: `./gradlew assembleLatestUniversalDebug`
4. If build fails, follow the instructions provided in the error message.

#### Publishing

1. [Obtain Telegram API credentials](https://core.telegram.org/api/obtaining_api_id)
2. Create `local.properties` file in the root project folder using any text editor:<br/><pre># Location where you have Android SDK installed
   sdk.dir=YOUR_ANDROID_SDK_FOLDER
   \# Telegram API credentials obtained at previous step
   telegram.api_id=YOUR_TELEGRAM_API_ID
   telegram.api_hash=YOUR_TELEGRAM_API_HASH</pre>
3. [Setup Firebase](https://firebase.google.com/docs/android/setup) and replace `google-services.json` with the one that's suitable for your `app.id`

## License

`Impergram` is licensed under the terms of the GNU General Public License v3.0.

For more information, see [LICENSE](/LICENSE) file.

License of components and third-party dependencies it relies on might differ, check `LICENSE` file in the corresponding folder.

